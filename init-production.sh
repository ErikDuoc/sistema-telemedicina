#!/bin/bash

# ============================================
# SCRIPT DE INICIALIZACIÓN PARA PRODUCCIÓN
# ============================================
# Uso: bash init-production.sh
# Prerequisitos: Java 21, Maven 3.9+

set -e  # Exit on error

echo "🚀 Inicializando Telemedicina en Producción"
echo "==========================================="
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Verificar Java
echo -e "${YELLOW}1. Verificando Java...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java no está instalado${NC}"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | grep version | awk -F'"' '{print $2}' | cut -d. -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo -e "${RED}❌ Se requiere Java 21+, tienes Java $JAVA_VERSION${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Java $JAVA_VERSION OK${NC}"
echo ""

# 2. Verificar Maven
echo -e "${YELLOW}2. Verificando Maven...${NC}"
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven no está instalado${NC}"
    exit 1
fi
MVN_VERSION=$(mvn -v | head -n 1 | awk '{print $3}')
echo -e "${GREEN}✅ Maven $MVN_VERSION OK${NC}"
echo ""

# 3. Verificar/Crear archivo .env.production
echo -e "${YELLOW}3. Verificando configuración de producción...${NC}"
if [ ! -f ".env.production" ]; then
    echo -e "${RED}⚠️  Archivo .env.production no encontrado${NC}"
    echo "Por favor crear .env.production basado en .env.production.example"
    echo ""
    echo "Pasos:"
    echo "1. cp .env.production.example .env.production"
    echo "2. Editar .env.production con valores reales"
    echo "3. Ejecutar este script nuevamente"
    exit 1
fi

# Verificar que no tiene valores de placeholder
if grep -q "your-secret\|change-in-prod\|secure-password" .env.production; then
    echo -e "${RED}❌ .env.production contiene valores de placeholder${NC}"
    echo "Por favor reemplazar todos los values antes de continuar"
    exit 1
fi

echo -e "${GREEN}✅ Configuración OK${NC}"
echo ""

# 4. Cargar variables de entorno
echo -e "${YELLOW}4. Cargando variables de entorno...${NC}"
export $(cat .env.production | grep -v '#' | xargs)
echo -e "${GREEN}✅ Variables cargadas${NC}"
echo ""

# 5. Limpiar build anterior
echo -e "${YELLOW}5. Limpiando build anterior...${NC}"
mvn clean -q
echo -e "${GREEN}✅ Limpieza completada${NC}"
echo ""

# 6. Compilar proyecto
echo -e "${YELLOW}6. Compilando proyecto...${NC}"
mvn install -q -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Compilación falló${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Compilación exitosa${NC}"
echo ""

# 7. Verificar características de seguridad
echo -e "${YELLOW}7. Verificando seguridad...${NC}"

# Check JWT Secret no está hardcoded
if grep -r "jwt.*secret.*=.*\"" target/classes --exclude-dir=node_modules 2>/dev/null | grep -v "placeholder\|example"; then
    echo -e "${YELLOW}⚠️  Advertencia: Posible JWT secret hardcoded${NC}"
fi

# Check CORS no es wildcard
if grep -r "cors.*allowed.*\*" target/classes 2>/dev/null; then
    echo -e "${YELLOW}⚠️  Advertencia: CORS configurado con wildcard${NC}"
fi

echo -e "${GREEN}✅ Verificación de seguridad completada${NC}"
echo ""

# 8. Crear estructura de directorios necesaria
echo -e "${YELLOW}8. Creando directorios necesarios...${NC}"
mkdir -p logs
mkdir -p data/backups
mkdir -p config
chmod 700 logs data config
echo -e "${GREEN}✅ Directorios creados${NC}"
echo ""

# 9. Generar JAR de cada servicio
echo -e "${YELLOW}9. Generando JARs ejecutables...${NC}"
if [ ! -d "target/services" ]; then
    mkdir -p target/services
fi

# Copiar JARs
for service in audit-service gateway-service patient-service doctor-service \
               appointment-service clinical-record-service payment-service \
               agenda-service laboratory-service notification-service \
               prescription-service video-consultation-service; do
    if [ -f "$service/target/*.jar" ]; then
        cp $service/target/*.jar target/services/
        echo "  ✅ $service"
    fi
done
echo -e "${GREEN}✅ JARs listos${NC}"
echo ""

# 10. Crear archivo systemd service (si es Linux)
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo -e "${YELLOW}10. Configurando systemd service...${NC}"

    if [ -f "systemd/telemedicina.service" ]; then
        sudo cp systemd/telemedicina.service /etc/systemd/system/
        sudo systemctl daemon-reload
        echo -e "${GREEN}✅ Systemd service configurado${NC}"
        echo ""
        echo "Para iniciar servicios:"
        echo "  sudo systemctl start telemedicina"
        echo "  sudo systemctl enable telemedicina"
    fi
fi

# 11. Resumen final
echo -e "${GREEN}==========================================${NC}"
echo -e "${GREEN}✅ INICIALIZACIÓN COMPLETADA${NC}"
echo -e "${GREEN}==========================================${NC}"
echo ""
echo "Próximos pasos:"
echo ""
echo "1. Configurar base de datos MySQL/PostgreSQL"
echo "   mysql -u root -p < database/initial-schema.sql"
echo ""
echo "2. Configurar backups automáticos"
echo "   crontab -e"
echo "   # Agregar: 0 2 * * * /opt/telemedicina/backup.sh"
echo ""
echo "3. Iniciar servicios"
echo "   sudo systemctl start telemedicina"
echo "   sudo systemctl status telemedicina"
echo ""
echo "4. Verificar salud de servicios"
echo "   curl http://localhost:8080/actuator/health"
echo ""
echo "5. Ver logs"
echo "   tail -f logs/application.log"
echo ""
echo "⚡ Para más información ver SEGURIDAD.md y README_PROFESIONAL.md"

