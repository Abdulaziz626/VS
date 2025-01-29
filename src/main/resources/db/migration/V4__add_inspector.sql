INSERT INTO users (full_name, email, password, role, region)
SELECT 'Ali Aljuhani',
       'ali@gmail.com',
       '$2a$10$z/Xw4WAWdCvtU7vDsq0iFuEfTchXBdP4Ddy3Bp69CRT.KXedjOHTa',
       'INSPECTOR',
       'Western'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'ali@gmail.com'
);
