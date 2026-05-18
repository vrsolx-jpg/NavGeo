-- Usuario administrador inicial para NavGeo.
-- Password en texto plano correspondiente a este hash: admin123
-- Cambia username/email/nombre si lo necesitas.

INSERT INTO usuarios (username, password, nombre, email, rol, activo, creado_en)
VALUES (
    'admin',
    '$2a$12$WLCvFkgOCgGmcAV3VERdreWesrH4hPe8J87IhqvPiuWh2DiX8pREy',
    'Administrador NavGeo',
    'admin@navgeo.local',
    'ROLE_ADMIN',
    true,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO UPDATE SET
    password = EXCLUDED.password,
    nombre = EXCLUDED.nombre,
    email = EXCLUDED.email,
    rol = EXCLUDED.rol,
    activo = EXCLUDED.activo;
