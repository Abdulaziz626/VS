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
           'Illegal Parking in Restricted Area',
           'Southern Mall Parking Lot',
           'JKL1122',
           'WRONG_PARKING',
           '5',
           'Abdallah Hassan',
           'Car was parked in a no-parking zone',
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
           'Tailgating Another Vehicle',
           'Highway Northern Route 8',
           'NOP3345',
           'SPEEDING',
           '6',
           'Khaled Omar',
           'Vehicle was too close to another car while speeding',
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
           'Driving Without Headlights at Night',
           'Middle District Road 7',
           'QRS5698',
           'USING_PHONE',
           '7',
           'Saad Abogazala',
           'Driver was seen using the phone and ignoring headlight rules',
           'Middle',
           'PENDING'
       );