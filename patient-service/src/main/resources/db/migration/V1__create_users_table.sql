CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'PATIENT',
    active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO users (name, email, role, active, password) VALUES
  ('Administrador',     'admin@telemedicina.cl',    'ADMIN',   true, '$2a$10$gT.PsFi3xTq9xc3virQAfesYBesY5g53tQ5R7lgJGqgVdVMH0I8qa'),
  ('Dr. Roberto Silva', 'doctor@telemedicina.cl',   'DOCTOR',  true, '$2a$10$LAK58ME84bgotvy2eL.eWeobSCHMDsaD3BajXq/swyevMwfw8PW/m'),
  ('Ana Garcia',        'paciente@telemedicina.cl',  'PATIENT', true, '$2a$10$LAK58ME84bgotvy2eL.eWeobSCHMDsaD3BajXq/swyevMwfw8PW/m');
