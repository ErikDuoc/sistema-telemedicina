# ============================================================================
# SCRIPT DE PRUEBA PARA ENDPOINTS HATEOAS (PowerShell)
# ============================================================================
# 
# Este script prueba los endpoints de API con HATEOAS habilitado.
# Requiere que los servicios estén corriendo localmente.
#
# Uso (PowerShell):
#   .\test-hateoas.ps1
#
# ============================================================================

$BaseUrl = "http://localhost:8080"
$Headers = @{"Accept" = "application/json"}

Write-Host "================================================" -ForegroundColor Blue
Write-Host "TEST DE ENDPOINTS HATEOAS" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

# ============================================================================
# TEST 1: GET /api/patients - Colección
# ============================================================================
Write-Host "`nTEST 1: Obtener lista de pacientes (CollectionModel)" -ForegroundColor Yellow
Write-Host "GET $BaseUrl/api/patients"
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/patients" -Headers $Headers
    $json = $response.Content | ConvertFrom-Json
    Write-Host ($json | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "Error: No se pudo conectar al servidor" -ForegroundColor Red
}

# ============================================================================
# TEST 2: GET /api/patients/{id} - Individual
# ============================================================================
Write-Host "`nTEST 2: Obtener paciente por ID (EntityModel)" -ForegroundColor Yellow
Write-Host "GET $BaseUrl/api/patients/1"
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/patients/1" -Headers $Headers
    $json = $response.Content | ConvertFrom-Json
    Write-Host ($json | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "Error: No se pudo conectar al servidor" -ForegroundColor Red
}

# ============================================================================
# TEST 3: GET /api/doctors - Colección
# ============================================================================
Write-Host "`nTEST 3: Obtener lista de doctores (CollectionModel)" -ForegroundColor Yellow
Write-Host "GET $BaseUrl/api/doctors"
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/doctors" -Headers $Headers
    $json = $response.Content | ConvertFrom-Json
    Write-Host ($json | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "Error: No se pudo conectar al servidor" -ForegroundColor Red
}

# ============================================================================
# TEST 4: GET /api/appointments - Colección
# ============================================================================
Write-Host "`nTEST 4: Obtener citas de paciente (CollectionModel)" -ForegroundColor Yellow
Write-Host "GET $BaseUrl/api/appointments/patient/1"
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/appointments/patient/1" -Headers $Headers
    $json = $response.Content | ConvertFrom-Json
    Write-Host ($json | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "Error: No se pudo conectar al servidor" -ForegroundColor Red
}

# ============================================================================
# TEST 5: GET /api/prescriptions/{id} - Individual
# ============================================================================
Write-Host "`nTEST 5: Obtener receta por ID (EntityModel)" -ForegroundColor Yellow
Write-Host "GET $BaseUrl/api/prescriptions/1"
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/prescriptions/1" -Headers $Headers
    $json = $response.Content | ConvertFrom-Json
    Write-Host ($json | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "Error: No se pudo conectar al servidor" -ForegroundColor Red
}

# ============================================================================
# VERIFICACIÓN DE ESTRUCTURA HATEOAS
# ============================================================================
Write-Host "`n================================================" -ForegroundColor Blue
Write-Host "VERIFICACIÓN DE ESTRUCTURA HATEOAS" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue

Write-Host "`nVerificando que las respuestas incluyan _links:" -ForegroundColor Yellow

Write-Host -NoNewline "Pacientes (debe incluir _links): "
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/patients/1" -Headers $Headers
    $json = $response.Content | ConvertFrom-Json
    if ($json._links) {
        Write-Host "✓ OK" -ForegroundColor Green
    } else {
        Write-Host "✗ FALLO - No se encontraron _links" -ForegroundColor Red
    }
} catch {
    Write-Host "Error" -ForegroundColor Red
}

Write-Host -NoNewline "Doctores (debe incluir _links): "
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/doctors/1/profile" -Headers $Headers
    $json = $response.Content | ConvertFrom-Json
    if ($json._links) {
        Write-Host "✓ OK" -ForegroundColor Green
    } else {
        Write-Host "✗ FALLO - No se encontraron _links" -ForegroundColor Red
    }
} catch {
    Write-Host "Error" -ForegroundColor Red
}

Write-Host "`n================================================" -ForegroundColor Blue
Write-Host "Pruebas completadas" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Blue

# ============================================================================
# NOTAS
# ============================================================================
Write-Host @"

NOTAS:
- Estructura esperada de _links:
  {
    "self": { "href": "..." },
    "all": { "href": "..." },
    "update": { "href": "..." },  // Condicional
    "delete": { "href": "..." }   // Condicional
  }

- Los links condicionales aparecerán según el estado del recurso.
- Para visualizar en Swagger: http://localhost:8080/swagger-ui.html

"@
