-- V13: Add created_at and expired_at columns to violations table
ALTER TABLE violations
    ADD created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    expired_at DATETIME2 NOT NULL DEFAULT DATEADD(DAY, 30, GETDATE());