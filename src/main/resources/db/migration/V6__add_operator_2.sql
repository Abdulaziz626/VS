INSERT INTO users (full_name, email, password, role, region)
SELECT 'jamal aljuhani',
       'jamal@gmail.com',
       '$2a$10$wvm5cbncGOCxMxMzu7f8TudDxZZ7V6IpzrcuizMYBDoltekmCa38e',
       'OPERATOR',
       'Northern'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'jamal@gmail.com'
);
