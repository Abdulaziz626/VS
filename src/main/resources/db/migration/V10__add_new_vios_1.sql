INSERT INTO violations (
    description,
    violation_location,
    plate_number,
    violation_type,
    inspector_id,
    inspector_name,
    inspector_notes,
    region,
    status
)
VALUES (
           'Speeding',
           'Highway Exit 12',
           'XYZ1234',
           'SPEEDING',
           '5',
           'Abdallah Hassan',
           'Driver was over the speed limit by 20km/h',
           'Southern',
           'PENDING'
       );
INSERT INTO violations (
    description,
    violation_location,
    plate_number,
    violation_type,
    inspector_id,
    inspector_name,
    inspector_notes,
    region,
    status
)
VALUES (
           'Signal Violation',
           'Downtown Intersection',
           'ABC6789',
           'RED_LIGHT_CROSSING',
           '6',
           'Khaled Omar',
           'Car passed a red light',
           'Northern',
           'PENDING'
       );
INSERT INTO violations (
    description,
    violation_location,
    plate_number,
    violation_type,
    inspector_id,
    inspector_name,
    inspector_notes,
    region,
    status
)
VALUES (
           'Using phone while driving',
           'Main Street 45',
           'LMN4567',
           'USING_PHONE',
           '7',
           'Saad Abogazala',
           'Driver shifted lanes without proper signal',
           'Middle',
           'PENDING'
       );