#!/bin/bash
# ============================================================================
# SCRIPT DE PRUEBA PARA ENDPOINTS HATEOAS
# ============================================================================
# 
# Este script prueba los endpoints de API con HATEOAS habilitado.
# Requiere que los servicios estén corriendo localmente.
#
# Uso:
#   bash test-hateoas.sh
#
# ============================================================================

BASE_URL="http://localhost:8080"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}TEST DE ENDPOINTS HATEOAS${NC}"
echo -e "${BLUE}================================================${NC}"

# ============================================================================
# TEST 1: GET /api/patients - Colección
# ============================================================================
echo -e "\n${YELLOW}TEST 1: Obtener lista de pacientes (CollectionModel)${NC}"
echo "GET $BASE_URL/api/patients"
curl -s -X GET "$BASE_URL/api/patients" \
  -H "Accept: application/json" \
  | jq '.' 2>/dev/null || echo "Error: No se pudo conectar al servidor"

# ============================================================================
# TEST 2: GET /api/patients/{id} - Individual
# ============================================================================
echo -e "\n${YELLOW}TEST 2: Obtener paciente por ID (EntityModel)${NC}"
echo "GET $BASE_URL/api/patients/1"
curl -s -X GET "$BASE_URL/api/patients/1" \
  -H "Accept: application/json" \
  | jq '.' 2>/dev/null || echo "Error: No se pudo conectar al servidor"

# ============================================================================
# TEST 3: GET /api/doctors - Colección
# ============================================================================
echo -e "\n${YELLOW}TEST 3: Obtener lista de doctores (CollectionModel)${NC}"
echo "GET $BASE_URL/api/doctors"
curl -s -X GET "$BASE_URL/api/doctors" \
  -H "Accept: application/json" \
  | jq '.' 2>/dev/null || echo "Error: No se pudo conectar al servidor"

# ============================================================================
# TEST 4: GET /api/appointments - Colección
# ============================================================================
echo -e "\n${YELLOW}TEST 4: Obtener citas de paciente (CollectionModel)${NC}"
echo "GET $BASE_URL/api/appointments/patient/1"
curl -s -X GET "$BASE_URL/api/appointments/patient/1" \
  -H "Accept: application/json" \
  | jq '.' 2>/dev/null || echo "Error: No se pudo conectar al servidor"

# ============================================================================
# TEST 5: GET /api/prescriptions/{id} - Individual
# ============================================================================
echo -e "\n${YELLOW}TEST 5: Obtener receta por ID (EntityModel)${NC}"
echo "GET $BASE_URL/api/prescriptions/1"
curl -s -X GET "$BASE_URL/api/prescriptions/1" \
  -H "Accept: application/json" \
  | jq '.' 2>/dev/null || echo "Error: No se pudo conectar al servidor"

# ============================================================================
# VERIFICACIÓN DE ESTRUCTURA HATEOAS
# ============================================================================
echo -e "\n${BLUE}================================================${NC}"
echo -e "${BLUE}VERIFICACIÓN DE ESTRUCTURA HATEOAS${NC}"
echo -e "${BLUE}================================================${NC}"

echo -e "\n${YELLOW}Verificando que las respuestas incluyan _links:${NC}"

echo -n "Pacientes (debe incluir _links): "
RESPONSE=$(curl -s -X GET "$BASE_URL/api/patients/1" -H "Accept: application/json")
if echo "$RESPONSE" | jq -e '._links' > /dev/null 2>&1; then
    echo -e "${GREEN}✓ OK${NC}"
else
    echo -e "✗ FALLO - No se encontraron _links"
fi

echo -n "Doctores (debe incluir _links): "
RESPONSE=$(curl -s -X GET "$BASE_URL/api/doctors/1/profile" -H "Accept: application/json")
if echo "$RESPONSE" | jq -e '._links' > /dev/null 2>&1; then
    echo -e "${GREEN}✓ OK${NC}"
else
    echo -e "✗ FALLO - No se encontraron _links"
fi

echo -e "\n${BLUE}================================================${NC}"
echo -e "${GREEN}Pruebas completadas${NC}"
echo -e "${BLUE}================================================${NC}"

# ============================================================================
# NOTAS
# ============================================================================
cat << 'EOF'

NOTAS:
- Si curl no está instalado, usa PowerShell en Windows:
  Invoke-WebRequest -Uri "http://localhost:8080/api/patients" -Headers @{"Accept"="application/json"}

- Estructura esperada de _links:
  {
    "self": { "href": "..." },
    "all": { "href": "..." },
    "update": { "href": "..." },  // Condicional
    "delete": { "href": "..." }   // Condicional
  }

- Los links condicionales aparecerán según el estado del recurso.
- Para mejor visualización, instala jq: https://stedolan.github.io/jq/

EOF
