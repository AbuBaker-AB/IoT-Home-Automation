# Smart Home Automation System

An IoT-based Smart Home Automation System developed using **ESP32-S3**, **Firebase Realtime Database**, and an **Android Mobile Application**. The system enables users to remotely monitor environmental conditions and control household devices in real time through a mobile application.

---

# Project Overview

This project implements a smart home automation solution that allows users to control electrical devices and monitor environmental conditions from anywhere using a mobile application.

The system uses an **ESP32-S3 microcontroller** as the central controller, which communicates with **Firebase Realtime Database** over Wi-Fi. Sensor data is continuously uploaded to Firebase, while device control commands from the mobile application are retrieved and executed by the ESP32.

The system currently supports:

* Remote control of 3 fans
* Remote control of 1 light
* Temperature monitoring
* Humidity monitoring
* Motion detection
* Real-time synchronization through Firebase

---

# Features

## Device Control

* Control Fan 1 (Living Room Fan)
* Control Fan 2 (Bedroom Fan)
* Control Fan 3 (Kitchen Fan)
* Control Main Light
* Quick Actions (All ON / All OFF)

## Sensor Monitoring

* Real-time Temperature Monitoring
* Real-time Humidity Monitoring
* Motion Detection Monitoring

## Cloud Connectivity

* Firebase Realtime Database Integration
* Real-time Data Synchronization
* Wi-Fi Connectivity

## User Interface

* Android Mobile Application
* Modern Dashboard UI
* Device Status Visualization
* Sensor Status Cards

---

# Hardware Components

| Component                           | Quantity |
| ----------------------------------- | -------- |
| ESP32-S3 Development Board          | 1        |
| DHT22 Temperature & Humidity Sensor | 1        |
| PIR Motion Sensor                   | 1        |
| OLED Display (SSD1306)              | 1        |
| 4-Channel Relay Module              | 1        |
| 5V DC Fan                           | 3        |
| LED Light                           | 1        |
| Li-ion 18650 Battery                | 1        |
| Mini Breadboard                     | 1        |
| Jumper Wires                        | Multiple |

---

# Software Technologies

## Firmware Development

* Arduino IDE
* ESP32 Arduino Framework

## Mobile Application

* Android Studio
* Kotlin

## Cloud Platform

* Firebase Realtime Database
* Firebase Authentication

## Libraries Used

* FirebaseESP32
* WiFi
* DHT Sensor Library
* Adafruit SSD1306
* Adafruit GFX

---

# System Architecture

The system follows a cloud-based IoT architecture.

```text
Mobile App
      │
      ▼
Firebase Realtime Database
      │
      ▼
ESP32-S3
├── DHT22 Sensor
├── PIR Motion Sensor
├── OLED Display
└── Relay Module
     ├── Fan 1
     ├── Fan 2
     ├── Fan 3
     └── Light
```

Architecture Diagram:

```text
Docs/Architecture_Diagram.png
```

---

# Circuit Design

The ESP32-S3 acts as the central controller and interfaces with sensors, actuators, and the Firebase cloud.

Circuit Diagram:

```text
Docs/Circuit Design/circuit_design_diagram.png
```

---

# Firebase Structure

The project uses Firebase Realtime Database for storing device states and sensor data.

```json
{
  "devices": {
    "fan1": {
      "name": "Living Room Fan",
      "status": "off",
      "timestamp": 1764582623467,
      "type": "fan"
    },
    "fan2": {
      "name": "Bedroom Fan",
      "status": "off",
      "timestamp": 1764582623478,
      "type": "fan"
    },
    "fan3": {
      "name": "Kitchen Fan",
      "status": "on",
      "timestamp": 1781107479876,
      "type": "fan"
    },
    "light1": {
      "name": "Main Light",
      "status": "off",
      "timestamp": 1764582623471,
      "type": "light"
    }
  },

  "sensors": {
    "humidity": 84.1,
    "motion": true,
    "temperature": 27.2,
    "timestamp": 1761287759650
  }
}
```

Firebase Screenshot:

```text
Docs/Screenshots/Firebase_Database.png
```

---

# Mobile Application

The Android application provides a user-friendly interface for monitoring sensors and controlling devices.

### Dashboard Features

* Temperature Display
* Humidity Display
* Motion Detection Status
* Fan Control Panel
* Light Control Panel
* Quick Actions

Application Screenshots:

```text
Docs/Screenshots/
├── Dashboard.png
├── Device_Controls.png
└── Quick_Actions.png
```

---

# Installation Guide

## 1. Clone Repository

```bash
git clone https://github.com/your-username/your-repository-name.git
```

## 2. Firebase Setup

Create a Firebase project and enable:

* Firebase Authentication
* Realtime Database

Update the following values inside the ESP32 firmware:

```cpp
#define WIFI_SSID ""
#define WIFI_PASSWORD ""

#define API_KEY ""
#define DATABASE_URL ""

#define USER_EMAIL ""
#define USER_PASSWORD ""
```

## 3. Upload ESP32 Firmware

Open:

```text
Firmware/SmartHomeAutomation_ESP32S3/SmartHomeAutomation_ESP32S3.ino
```

Compile and upload the code using Arduino IDE.

## 4. Build an Android Application

Open:

```text
Mobile App/
```

in Android Studio and run the application on an Android device.

---

# Repository Structure

```text
Home-Automation-System/
│
├── Firmware/
│   └── SmartHomeAutomation_ESP32S3/
│       └── SmartHomeAutomation_ESP32S3.ino
│
├── Mobile App/
│
├── Firebase/
│   └── database_structure.json
│
├── Docs/
│   ├── Architecture_Diagram.png
│   ├── circuit_design_diagram.png
│   ├── Components/
│   └── Screenshots/
│
├── README.md
└── LICENSE
```

---

# Future Improvements

* Device Scheduling
* Energy Consumption Monitoring
* Push Notifications
* Voice Assistant Integration
* User Authentication & Roles
* Device Usage Analytics
* Additional Smart Home Devices
* OTA Firmware Updates
* Multi-Room Management
* Dark Mode for Mobile Application

---

# Authors

### Abu Baker

Department of Computer Science and Engineering
East Delta University
Chattogram, Bangladesh

### Project Type

IoT-Based Smart Home Automation System

### Technologies

ESP32-S3 • Firebase • Android Studio • Kotlin • Arduino IDE • IoT
