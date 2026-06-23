-- Script SQL para inicializar múltiples BDs y usuarios en MySQL

-- Crear BD para doctor-service
CREATE DATABASE IF NOT EXISTS doctors_db;

-- Crear BD para agenda-service
CREATE DATABASE IF NOT EXISTS agenda_db;

-- Crear usuario y otorgar permisos para doctors_db
CREATE USER IF NOT EXISTS 'doctors'@'%' IDENTIFIED BY 'doctors123';
GRANT ALL PRIVILEGES ON doctors_db.* TO 'doctors'@'%';

-- Crear usuario y otorgar permisos para agenda_db
CREATE USER IF NOT EXISTS 'agenda'@'%' IDENTIFIED BY 'agenda123';
GRANT ALL PRIVILEGES ON agenda_db.* TO 'agenda'@'%';

-- Aplicar cambios
FLUSH PRIVILEGES;

