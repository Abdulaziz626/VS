INSERT INTO users (full_name, email, password, role, region)
SELECT 'Abdulaziz Aljuhani',
       'a442020380@gmail.com',
       '$2a$10$XHgN548Li0lL90LbnxKvgOImoVt4NJ6dere7e/6lxjHz.PVztLcF2',
       'ADMIN',
       'Middle'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'a442020380@gmail.com'
);
