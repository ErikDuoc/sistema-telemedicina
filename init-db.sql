-- Script SQL para inicializar múltiples BDs y usuarios en MySQL

-- Crear BD para doctor-service
CREATE DATABASE IF NOT EXISTS doctors_db;

-- Crear BD para agenda-service
CREATE DATABASE IF NOT EXISTS agenda_db;

-- Crear BD para appointment-service
CREATE DATABASE IF NOT EXISTS appointment_db;

-- Crear usuario y otorgar permisos para doctors_db
CREATE USER IF NOT EXISTS 'doctors'@'%' IDENTIFIED BY 'doctors123';
GRANT ALL PRIVILEGES ON doctors_db.* TO 'doctors'@'%';

-- Crear usuario y otorgar permisos para agenda_db
CREATE USER IF NOT EXISTS 'agenda'@'%' IDENTIFIED BY 'agenda123';
GRANT ALL PRIVILEGES ON agenda_db.* TO 'agenda'@'%';

-- Crear usuario y otorgar permisos para appointment_db
CREATE USER IF NOT EXISTS 'appointment'@'%' IDENTIFIED BY 'appointment123';
GRANT ALL PRIVILEGES ON appointment_db.* TO 'appointment'@'%';

-- Aplicar cambios
FLUSH PRIVILEGES;

