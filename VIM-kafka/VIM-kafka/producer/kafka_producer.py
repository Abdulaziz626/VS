import logging
import time
import json
import os
import random
import base64
from confluent_kafka import Producer
from PIL import Image, ImageDraw
import io

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# Kafka Configuration
KAFKA_BROKER = os.getenv('KAFKA_BROKER', 'localhost:9092')  # Default to localhost if not set
KAFKA_TOPIC = os.getenv('KAFKA_TOPIC', 'test-topic')  # Ensure this is set properly

producer = Producer({'bootstrap.servers': KAFKA_BROKER})

def delivery_report(err, msg):
    """Kafka message delivery callback."""
    if err is not None:
        logging.error(f'Message delivery failed: {err}')
    else:
        logging.info(f'Message delivered to {msg.topic()} [{msg.partition()}]')

def generate_car_image():
    """
    Generate a simple car image with a random color as a placeholder.
    """
    img = Image.new('RGB', (500, 500), color=(255, 255, 255))  # White background
    draw = ImageDraw.Draw(img)

    # Random car color
    car_color = random.choice(['blue', 'red', 'green', 'yellow'])
    draw.rectangle([100, 300, 400, 350], fill=car_color)  # Car body
    draw.ellipse([120, 360, 180, 420], fill='black')  # Left wheel
    draw.ellipse([320, 360, 380, 420], fill='black')  # Right wheel

    return img

def image_to_hex(img):
    """
    Convert a PIL image to hex string.
    """
    byte_array = io.BytesIO()
    img.save(byte_array, format='PNG')
    base64_data = base64.b64encode(byte_array.getvalue()).decode('utf-8')  # Encode as base64 string
    hex_data = base64.b64decode(base64_data).hex()  # Convert base64 back to raw bytes and then to hex
    return hex_data

def generate_random_violation_request():
    """
    Generate a random violation request message.
    """
    violation_types = ['SPEEDING', 'USING_PHONE', 'RED_LIGHT_CROSSING', 'WRONG_PARKING']
    descriptions = [
        "Driver was caught speeding over the limit",
        "Driver was seen using their phone while driving",
        "Driver ran a red light at a traffic signal",
        "Driver parked in a restricted zone"
    ]
    locations = [
        "Main St and 5th Ave",
        "Highway 50 near exit 12",
        "Downtown Parking Lot",
        "Corner of Elm St and Oak Rd"
    ]
    plates = ["ABC1234", "XYZ9876", "LMN4321", "PQR5678"]
    inspector_notes = [
        "The driver appeared distracted.",
        "The car was speeding at 60 mph in a 40 mph zone.",
        "Driver ignored the red light and continued driving.",
        "No parking zone. Driver did not pay attention to signs."
    ]

    return {
        "description": random.choice(descriptions),
        "location": random.choice(locations),
        "plateNumber": random.choice(plates),
        "violationType": random.choice(violation_types),
        "inspectorNotes": random.choice(inspector_notes),
    }

counter = 0
while True:
    try:
        # Generate car image and convert to hex
        img = generate_car_image()
        image_data = image_to_hex(img)

        # Generate random violation request
        violation_request = generate_random_violation_request()

        # Create message payload
        message = {
            'counter': counter,
            'timestamp': time.time(),
            'image_data': image_data,  # Now in hex format
            'violation': violation_request
        }

        # Send message to Kafka
        producer.produce(
            KAFKA_TOPIC,
            value=json.dumps(message).encode('utf-8'),
            callback=delivery_report
        )
        producer.flush()  # Ensures messages are delivered

        logging.info(f'Sent violation message {counter}')
        counter += 1

        time.sleep(5)  # Send message every 5 seconds
    except Exception as e:
        logging.error(f"Error in main loop: {e}")
        time.sleep(5)  # Avoid crashing; retry after delay
