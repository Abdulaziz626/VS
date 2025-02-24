-- V14: Add auditing fields to violations table
ALTER TABLE violations
    ADD
    updated_at DATETIME2 NULL,
    create_by NVARCHAR(255) NULL,
    updated_by NVARCHAR(255) NULL;