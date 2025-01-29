CREATE TABLE users (
   id INT IDENTITY(1,1) PRIMARY KEY, -- Auto-incrementing primary key
   full_name NVARCHAR(255) NOT NULL, -- Full name column
   email NVARCHAR(100) NOT NULL UNIQUE, -- Unique email column
   password NVARCHAR(255) NOT NULL, -- Password column
   role NVARCHAR(255) NOT NULL,
   region NVARCHAR(255) NOT NULL,
   created_at DATETIME DEFAULT GETDATE(), -- Automatically sets creation timestamp
   updated_at DATETIME DEFAULT GETDATE() -- Automatically updates on modification
);

-- Ensure unique constraints for the email column
ALTER TABLE users ADD CONSTRAINT UQ_users_email UNIQUE (email);