INSERT INTO users (full_name, email, password, role, region)
SELECT 'ahmad aljuhani',
       'ahmad@gmail.com',
       '$2a$10$I0oLkIqm2DBxEgvN22XqneECH.zEwuYAcDfMgXj2CNQLcsqtvbGZC',
       'OPERATOR',
       'Western'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'ahmad@gmail.com'
);
