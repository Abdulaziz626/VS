CREATE TABLE car_images (
    id INT IDENTITY(1,1) PRIMARY KEY,
    violation_id INT NOT NULL,
    image_path NVARCHAR(255) NOT NULL,
    FOREIGN KEY (violation_id) REFERENCES violations(id) ON DELETE CASCADE
);
