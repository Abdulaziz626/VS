CREATE TABLE violations (
    id INT IDENTITY(1,1) PRIMARY KEY,
    description NVARCHAR(255) NOT NULL,
    violation_location NVARCHAR(255) NOT NULL,
    plate_number NVARCHAR(50) NOT NULL,
    violation_type NVARCHAR(100) NOT NULL,
    inspector_id NVARCHAR(500) NOT NULL,
    inspector_name NVARCHAR(500) NOT NULL,
    inspector_notes NVARCHAR(500),
    region NVARCHAR(100) NOT NULL,
    violation_date DATETIME2 DEFAULT GETDATE() NOT NULL,
    status NVARCHAR(50)  NOT NULL,

    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);
