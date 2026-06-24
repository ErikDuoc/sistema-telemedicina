-- Script SQL para inicializar múltiples BDs y usuarios en MySQL

-- Crear BD para doctor-service
CREATE DATABASE IF NOT EXISTS doctors_db;

-- Crear BD para agenda-service
CREATE DATABASE IF NOT EXISTS agenda_db;

-- Crear BD para appointment-service
CREATE DATABASE IF NOT EXISTS appointment_db;

-- Crear BD para patient-service
CREATE DATABASE IF NOT EXISTS patients_db;

-- Crear BD para payment-service
CREATE DATABASE IF NOT EXISTS payments_db;

-- Crear BD para clinical-record-service
CREATE DATABASE IF NOT EXISTS clinical_records_db;

-- Crear BD para notification-service
CREATE DATABASE IF NOT EXISTS notifications_db;

-- Crear BD para prescription-service
CREATE DATABASE IF NOT EXISTS prescriptions_db;

-- Crear BD para video-consultation-service
CREATE DATABASE IF NOT EXISTS video_consultations_db;

-- Crear BD para laboratory-service
CREATE DATABASE IF NOT EXISTS lab_db;

-- Crear usuario y otorgar permisos para doctors_db
CREATE USER IF NOT EXISTS 'doctors'@'%' IDENTIFIED BY 'doctors123';
GRANT ALL PRIVILEGES ON doctors_db.* TO 'doctors'@'%';

-- Crear usuario y otorgar permisos para agenda_db
CREATE USER IF NOT EXISTS 'agenda'@'%' IDENTIFIED BY 'agenda123';
GRANT ALL PRIVILEGES ON agenda_db.* TO 'agenda'@'%';

-- Crear usuario y otorgar permisos para appointment_db
CREATE USER IF NOT EXISTS 'appointment'@'%' IDENTIFIED BY 'appointment123';
GRANT ALL PRIVILEGES ON appointment_db.* TO 'appointment'@'%';

-- Crear usuario y otorgar permisos para patients_db
CREATE USER IF NOT EXISTS 'patients'@'%' IDENTIFIED BY 'patients123';
GRANT ALL PRIVILEGES ON patients_db.* TO 'patients'@'%';

-- Crear usuario y otorgar permisos para payments_db
CREATE USER IF NOT EXISTS 'payments'@'%' IDENTIFIED BY 'payments123';
GRANT ALL PRIVILEGES ON payments_db.* TO 'payments'@'%';

-- Crear usuario y otorgar permisos para clinical_records_db
CREATE USER IF NOT EXISTS 'clinical_records'@'%' IDENTIFIED BY 'clinical123';
GRANT ALL PRIVILEGES ON clinical_records_db.* TO 'clinical_records'@'%';

-- Crear usuario y otorgar permisos para notifications_db
CREATE USER IF NOT EXISTS 'notifications'@'%' IDENTIFIED BY 'notifications123';
GRANT ALL PRIVILEGES ON notifications_db.* TO 'notifications'@'%';

-- Crear usuario y otorgar permisos para prescriptions_db
CREATE USER IF NOT EXISTS 'prescriptions'@'%' IDENTIFIED BY 'prescriptions123';
GRANT ALL PRIVILEGES ON prescriptions_db.* TO 'prescriptions'@'%';

-- Crear usuario y otorgar permisos para video_consultations_db
CREATE USER IF NOT EXISTS 'video_consultations'@'%' IDENTIFIED BY 'video_consultations123';
GRANT ALL PRIVILEGES ON video_consultations_db.* TO 'video_consultations'@'%';

-- Crear usuario y otorgar permisos para lab_db
CREATE USER IF NOT EXISTS 'lab'@'%' IDENTIFIED BY 'lab123';
GRANT ALL PRIVILEGES ON lab_db.* TO 'lab'@'%';

-- Aplicar cambios
FLUSH PRIVILEGES;

