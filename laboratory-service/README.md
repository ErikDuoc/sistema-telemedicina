# Laboratory Service

Puerto: 8086

Requiere:
- patient-service (8081)
- notification-service (8083)

Endpoints:

POST /api/lab/orders

PUT /api/lab/results/{orderId}

GET /api/lab/patient/{id}
