INSERT INTO users (full_name, email, password, role, region)
SELECT 'Abdallah hassan',
       'Ah@gmail.com',
       '$2a$10$WKKlgNdaK.tbB.lQ3p9oze62ENP1rzBncDFvbonk5TDjQiQAeUQ8u',
       'INSPECTOR',
       'Southern'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'Ah@gmail.com'
);
INSERT INTO users (full_name, email, password, role, region)
SELECT 'khaled omar',
       'ko@gmail.com',
       '$2a$10$VQ234B0b8gz7Mq.9NUtu9ueppiw0M.AaPlxw8234rJ.g9WIw6tp2u',
       'INSPECTOR',
       'Northern'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'ko@gmail.com'
);
INSERT INTO users (full_name, email, password, role, region)
SELECT 'Saad abogazala',
       'SA@gmail.com',
       '$2a$10$CIiNdy6b2erPHpa2pUddFOXvA2sXDUldl4e1i9IVIbPvMSVnX27l2',
       'INSPECTOR',
       'Middle'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'SA@gmail.com'
);