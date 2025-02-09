INSERT INTO users (full_name, email, password, role, region)
SELECT 'Hamad jasm',
       'Hj@gmail.com',
       '$2a$10$JYFf2FhhxCDZE8q16MpnNuIFxNIDvGOGeOQobtFsJthrhx2PSVKHa',
       'OPERATOR',
       'Middle'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'Hj@gmail.com'
);
INSERT INTO users (full_name, email, password, role, region)
SELECT 'Nasser tarq',
       'NTT@gmail.com',
       '$2a$10$JhC8/6wR.ZkGDUoB7XgR5OA/EUsF0Sv670imnRB.g6qxO3Pv3AGIa',
       'OPERATOR',
       'Southern'
    WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'NTT@gmail.com'
);