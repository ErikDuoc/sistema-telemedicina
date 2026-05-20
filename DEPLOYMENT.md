# GUÍA DE DEPLOYMENT - TELEMEDICINA

## Cómo Deployar en Diferentes Plataformas

---

## 1️⃣ HEROKU (Rápido y Simple)

### Ventajas
- ✅ Free tier disponible
- ✅ Deployment automático desde Git
- ✅ Escalado automático
- ✅ SSL/TLS incluído

### Setup

```bash
# 1. Instalar Heroku CLI
curl https://cli-assets.heroku.com/install.sh | sh

# 2. Login
heroku login

# 3. Crear app
heroku create telemedicina-app

# 4. Agregar buildpack de Java
heroku buildpacks:add heroku/java

# 5. Configurar variables de entorno
heroku config:set JWT_SECRET=$(openssl rand -base64 32)
heroku config:set CORS_ALLOWED_ORIGINS=https://myapp.herokuapp.com
heroku config:set DB_URL=postgresql://...  # Usar Heroku Postgres

# 6. Agregar Postgres
heroku addons:create heroku-postgresql:hobby-dev

# 7. Deploy
git push heroku main

# 8. Ver logs
heroku logs --tail
```

### Limitaciones
- ⚠️ Dyno sleeps after 30 min of inactivity (free tier)
- ⚠️ Memory limitado (512MB free)
- ⚠️ Costo puede aumentar con escala

---

## 2️⃣ AWS (Profesional y Escalable) 🔧 RECOMENDADO

### Opción A: Elastic Beanstalk (Simplest)

```bash
# 1. Instalar EB CLI
pip install awsebcli

# 2. Inicializar aplicación
eb init -p java-21 telemedicina

# 3. Crear environment
eb create telemedicina-prod

# 4. Deploy
eb deploy

# 5. Ver logs
eb logs
```

### Opción B: ECS + Fargate (Recomendado)

```bash
# 1. Crear Docker image
docker build -f Dockerfile -t telemedicina:1.0 .
docker tag telemedicina:1.0 123456789.dkr.ecr.us-east-1.amazonaws.com/telemedicina:1.0

# 2. Push a ECR
aws ecr get-login-password | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/telemedicina:1.0

# 3. Crear ECS Cluster (via AWS Console o CLI)
# 4. Crear Task Definition con imagen Docker
# 5. Crear Service en el cluster
# 6. Configure Load Balancer (ALB)
```

### Opción C: Lambda + API Gateway (Serverless)

**Nota:** No recomendado para este sistema (stateful, long-running)

### Configurar Secrets en AWS

```bash
# Guardar JWT Secret en Secrets Manager
aws secretsmanager create-secret \
  --name telemedicina/jwt-secret \
  --secret-string "$(openssl rand -base64 32)"

# Usar en aplicación
spring:
  cloud:
    aws:
      secretsmanager:
        prefix: telemedicina
```

---

## 3️⃣ GOOGLE CLOUD (Cloud Run)

### Setup

```bash
# 1. Autenticar
gcloud auth login
gcloud config set project my-project-id

# 2. Build image
gcloud builds submit --tag gcr.io/my-project/telemedicina:1.0

# 3. Deploy a Cloud Run
gcloud run deploy telemedicina \
  --image gcr.io/my-project/telemedicina:1.0 \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars JWT_SECRET=xxx,DB_URL=cloudsql://...

# 4. Configure Cloud SQL Proxy
cloud_sql_proxy -instances=project:region:instance=tcp:3306 &

# 5. Ver logs
gcloud run logs read telemedicina --limit 50 --follow
```

---

## 4️⃣ AZURE (App Service)

### Setup

```bash
# 1. Login
az login

# 2. Crear resource group
az group create --name telemedicina --location eastus

# 3. Crear App Service plan
az appservice plan create \
  --name telemedicina-plan \
  --resource-group telemedicina \
  --sku B1 \
  --is-linux

# 4. Crear web app
az webapp create \
  --resource-group telemedicina \
  --plan telemedicina-plan \
  --name telemedicina-app \
  --runtime "JAVA|21-java21"

# 5. Deploy JAR
az webapp deployment source config-zip \
  --resource-group telemedicina \
  --name telemedicina-app \
  --src telemedicina.jar

# 6. Configurar variables
az webapp config appsettings set \
  --resource-group telemedicina \
  --name telemedicina-app \
  --settings JWT_SECRET="xxx"
```

---

## 5️⃣ DOCKER + DOCKER COMPOSE (Local Dev)

### Dockerfile

```dockerfile
FROM openjdk:21-slim

WORKDIR /app

# Copiar todos los JARs
COPY target/services/*.jar ./

# Crear script para iniciar todos los servicios
RUN echo '#!/bin/bash\n\
java -jar audit-service*.jar &\n\
java -jar gateway-service*.jar &\n\
java -jar patient-service*.jar &\n\
java -jar doctor-service*.jar &\n\
wait' > start.sh && chmod +x start.sh

EXPOSE 8080-8092

CMD ["./start.sh"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: telemedicina
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  audit-service:
    build: ./audit-service
    ports:
      - "8092:8092"
    environment:
      JWT_SECRET: ${JWT_SECRET}
      DB_URL: jdbc:mysql://mysql:3306/telemedicina
      DB_USER: root
      DB_PASSWORD: root
    depends_on:
      - mysql

  gateway-service:
    build: ./gateway-service
    ports:
      - "8080:8080"
    environment:
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - audit-service

  patient-service:
    build: ./patient-service
    ports:
      - "8081:8081"
    environment:
      JWT_SECRET: ${JWT_SECRET}
      DB_URL: jdbc:mysql://mysql:3306/telemedicina
    depends_on:
      - mysql

  # ... rest of services

volumes:
  mysql-data:
```

**Ejecutar:**
```bash
docker-compose up -d
```

---

## 6️⃣ KUBERNETES (Producción Escalable) 🚀

### Setup Inicial

```bash
# 1. Instalar kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# 2. Crear cluster (GKE ejemplo)
gcloud container clusters create telemedicina \
  --zone us-central1-a \
  --num-nodes 3 \
  --machine-type n1-standard-2

# 3. Obtener credenciales
gcloud container clusters get-credentials telemedicina --zone us-central1-a

# 4. Crear namespace
kubectl create namespace telemedicina
kubectl config set-context --current --namespace=telemedicina
```

### Crear Secrets

```bash
# Crear secret para JWT y credenciales
kubectl create secret generic telemedicina-secrets \
  --from-literal=jwt-secret=$(openssl rand -base64 32) \
  --from-literal=db-password=secure-password-123 \
  -n telemedicina

# Ver secrets
kubectl get secrets -n telemedicina
```

### Deploy Services

```bash
# Crear ConfigMap
kubectl create configmap telemedicina-config \
  --from-literal=jwt-expiration=86400000 \
  --from-literal=cors-allowed-origins=https://myapp.com \
  -n telemedicina

# Deployments (yaml files en k8s/ directory)
kubectl apply -f k8s/audit-service-deployment.yaml
kubectl apply -f k8s/gateway-service-deployment.yaml
kubectl apply -f k8s/patient-service-deployment.yaml
# ... etc

# Ver deployments
kubectl get deployments -n telemedicina
kubectl get pods -n telemedicina

# Port forward for testing
kubectl port-forward svc/gateway-service 8080:80 -n telemedicina
```

### Scaling

```bash
# Auto-scaling
kubectl autoscale deployment patient-service \
  --min=2 --max=10 \
  -n telemedicina

# Manual scaling
kubectl scale deployment patient-service --replicas=5 -n telemedicina

# Ver HPA (Horizontal Pod Autoscaler)
kubectl get hpa -n telemedicina
```

---

## 🔄 CI/CD PIPELINES

### GitHub Actions

Crear `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up Java
      uses: actions/setup-java@v2
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
    
    - name: Login to Docker Hub
      uses: docker/login-action@v1
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}
    
    - name: Build and push Docker image
      run: |
        docker build -t myregistry/telemedicina:${{ github.sha }} .
        docker push myregistry/telemedicina:${{ github.sha }}
    
    - name: Deploy to Kubernetes
      uses: azure/k8s-deploy@v1
      with:
        manifests: |
          k8s/deployment.yaml
        images: myregistry/telemedicina:${{ github.sha }}
```

---

## 📊 COMPARISON TABLE

| Platform | Free Tier | Ease | Scalability | Cost | Recommendation |
|----------|-----------|------|-------------|------|---|
| Heroku | ✅ Limited | ⭐⭐⭐⭐⭐ | ⭐⭐ | Moderate | 🟡 Dev/Test |
| AWS EB | ✅ Limited | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Variable | 🟢 Prod (ECS) |
| Google Cloud | ✅ Limited | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Variable | 🟡 Prod |
| Azure | ✅ Limited | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Variable | 🟡 Prod |
| Docker | ✅ Yes | ⭐⭐⭐⭐⭐ | ⭐⭐ | Free | 🟢 Dev |
| Kubernetes | ❌ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Variable | 🟢 Enterprise Prod |

---

## ✅ PRE-DEPLOYMENT CHECKLIST

```bash
[ ] All tests passing
[ ] Security review completed
[ ] Secrets configured correctly
[ ] Database migrations tested
[ ] SSL/TLS certificate ready
[ ] Load balancer configured
[ ] Monitoring/logging setup
[ ] Backup strategy ready
[ ] Runbooks documented
[ ] Team trained
[ ] Rollback plan ready
[ ] Final approval obtained
```

---

**Para más información ver:**
- `SEGURIDAD.md` - Configuración segura
- `README_PROFESIONAL.md` - Inicio rápido
- `init-production.sh` - Script automático

**Última actualización:** Mayo 16, 2026

