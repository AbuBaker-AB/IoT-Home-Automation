#include <WiFi.h>
#include <FirebaseESP32.h>
#include "DHT.h"

// WiFi & Firebase configuration
#define WIFI_SSID "ESP32"
#define WIFI_PASSWORD "12345678"
#define API_KEY "AIzaSyD9hxb4UJ6mog5bmcmqEJKqJX3LneVNJdA"
#define DATABASE_URL "https://home-automation-8e058-default-rtdb.firebaseio.com/"
#define USER_EMAIL "esp12345@gmail.com"
#define USER_PASSWORD "esp12345"

// Firebase objects
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// Sensor pins
#define DHTPIN 15
#define DHTTYPE DHT22
DHT dht(DHTPIN, DHTTYPE);
#define MOTION_PIN 23

// Device pins
const int fanPins[] = {5, 18, 19};
const int lightPin = 27;
String fanStates[] = {"off", "off", "off"};  
String lightState = "off";                    

// Timing
unsigned long lastSensorUpdate = 0;
unsigned long lastFirebaseCheck = 0;
const unsigned long sensorInterval = 5000;
const unsigned long firebaseInterval = 500;

// Motion detection
volatile bool motionChanged = false;
volatile bool motionState = false;
bool lastSentMotionState = false;

// Sensor data
float currentTemp = 0.0;
float currentHum = 0.0;

// Motion ISR
void IRAM_ATTR handleMotionChange() {
  motionState = digitalRead(MOTION_PIN);
  motionChanged = true;
}

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("\n╔════════════════════════════════════╗");
  Serial.println("║   Smart Home System Starting      ║");
  Serial.println("╚════════════════════════════════════╝\n");

  // Setup Fans
  Serial.println("→ Configuring Fans...");
  for (int i = 0; i < 3; i++) {
    pinMode(fanPins[i], OUTPUT);
    digitalWrite(fanPins[i], HIGH);  // Relay OFF
    Serial.println("  Fan " + String(i + 1) + " ready (GPIO " + String(fanPins[i]) + ")");
  }

  // Setup Light
  Serial.println("→ Configuring Light...");
  pinMode(lightPin, OUTPUT);
  digitalWrite(lightPin, HIGH);  // Light OFF
  Serial.println("  Light ready (GPIO 16)");

  // Test relays
  Serial.println("\n→ Testing Relays...");
  for (int i = 0; i < 3; i++) {
    digitalWrite(fanPins[i], LOW);
    delay(300);
    digitalWrite(fanPins[i], HIGH);
    delay(200);
  }
  digitalWrite(lightPin, LOW);
  delay(300);
  digitalWrite(lightPin, HIGH);
  Serial.println("  ✓ Relay test complete");

  // Motion sensor
  Serial.println("\n→ Configuring Motion Sensor...");
  pinMode(MOTION_PIN, INPUT);
  attachInterrupt(digitalPinToInterrupt(MOTION_PIN), handleMotionChange, CHANGE);
  Serial.println("  ✓ Motion sensor ready (GPIO 23)");

  // DHT sensor
  Serial.println("→ Configuring DHT22...");
  pinMode(DHTPIN, INPUT_PULLUP);
  dht.begin();
  Serial.println("  ✓ DHT22 ready (GPIO 15)");
  delay(2000);

  // WiFi
  Serial.println("\n→ Connecting to WiFi...");
  Serial.println("  SSID: " + String(WIFI_SSID));
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  
  int wifiAttempts = 0;
  while (WiFi.status() != WL_CONNECTED && wifiAttempts < 40) {
    Serial.print(".");
    delay(500);
    wifiAttempts++;
    yield();
  }
  
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("\n  ✗ WiFi connection FAILED!");
    while(true) delay(1000);
  }
  
  Serial.println("\n  ✓ WiFi Connected!");
  Serial.println("  IP Address: " + WiFi.localIP().toString());

  // Time sync
  Serial.println("\n→ Syncing time...");
  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
  
  int timeAttempts = 0;
  time_t now = time(nullptr);
  while (now < 8 * 3600 * 2 && timeAttempts < 30) {
    delay(500);
    Serial.print(".");
    now = time(nullptr);
    timeAttempts++;
    yield();
  }
  Serial.println("\n  ✓ Time synchronized");

  // Firebase
  Serial.println("\n→ Initializing Firebase...");
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  
  Serial.println("  ✓ Firebase initialized");

  // Wait for Firebase ready
  Serial.println("\n→ Connecting to Firebase...");
  int fbAttempts = 0;
  while (!Firebase.ready() && fbAttempts < 20) {
    Serial.print(".");
    delay(500);
    fbAttempts++;
    yield();
  }
  
  if (Firebase.ready()) {
    Serial.println("\n  ✓ Firebase connected and ready!");
  } else {
    Serial.println("\n  ⚠ Firebase connection slow...");
  }

  Serial.println("\n╔════════════════════════════════════╗");
  Serial.println("║   System Ready - Monitoring...     ║");
  Serial.println("╚════════════════════════════════════╝\n");
}

void loop() {
  // Check Firebase for device states
  if (millis() - lastFirebaseCheck > firebaseInterval) {
    lastFirebaseCheck = millis();
    
    if (Firebase.ready()) {
      
      // Fan 1 - Reading /devices/fan1/status
      if (Firebase.getString(fbdo, "/devices/fan1/status")) {
        String state = fbdo.stringData();
        state.toLowerCase();  // Convert to lowercase
        
        if (state != fanStates[0]) {
          fanStates[0] = state;
          
          if (state == "on") {
            digitalWrite(fanPins[0], LOW);   // Relay ON
            Serial.println("🔵 Fan 1: ON");
          } else {
            digitalWrite(fanPins[0], HIGH);  // Relay OFF
            Serial.println("🔵 Fan 1: OFF");
          }
        }
      } else {
        Serial.println("❌ Failed to read fan1: " + fbdo.errorReason());
      }
      
      // Fan 2 - Reading /devices/fan2/status
      if (Firebase.getString(fbdo, "/devices/fan2/status")) {
        String state = fbdo.stringData();
        state.toLowerCase();
        
        if (state != fanStates[1]) {
          fanStates[1] = state;
          
          if (state == "on") {
            digitalWrite(fanPins[1], LOW);
            Serial.println("🔵 Fan 2: ON");
          } else {
            digitalWrite(fanPins[1], HIGH);
            Serial.println("🔵 Fan 2: OFF");
          }
        }
      } else {
        Serial.println("❌ Failed to read fan2: " + fbdo.errorReason());
      }
      
      // Fan 3 - Reading /devices/fan3/status
      if (Firebase.getString(fbdo, "/devices/fan3/status")) {
        String state = fbdo.stringData();
        state.toLowerCase();
        
        if (state != fanStates[2]) {
          fanStates[2] = state;
          
          if (state == "on") {
            digitalWrite(fanPins[2], LOW);
            Serial.println("🔵 Fan 3: ON");
          } else {
            digitalWrite(fanPins[2], HIGH);
            Serial.println("🔵 Fan 3: OFF");
          }
        }
      } else {
        Serial.println("❌ Failed to read fan3: " + fbdo.errorReason());
      }

      // Light - Reading /devices/light1/status
      if (Firebase.getString(fbdo, "/devices/light1/status")) {
        String state = fbdo.stringData();
        state.toLowerCase();
        
        if (state != lightState) {
          lightState = state;
          
          if (state == "on") {
            digitalWrite(lightPin, LOW);
            Serial.println("💡 Light: ON");
          } else {
            digitalWrite(lightPin, HIGH);
            Serial.println("💡 Light: OFF");
          }
        }
      } else {
        Serial.println("❌ Failed to read light1: " + fbdo.errorReason());
      }
    }
  }

  // Motion sensor
  if (motionChanged) {
    motionChanged = false;
    if (motionState != lastSentMotionState) {
      lastSentMotionState = motionState;
      if (Firebase.ready()) {
        if (Firebase.setBool(fbdo, "/sensors/motion", motionState)) {
          Serial.println("🚶 Motion: " + String(motionState ? "DETECTED" : "Clear"));
        }
      }
    }
  }

  // DHT sensor
  if (millis() - lastSensorUpdate > sensorInterval) {
    lastSensorUpdate = millis();
    currentTemp = dht.readTemperature();
    currentHum = dht.readHumidity();
    
    if (!isnan(currentTemp) && !isnan(currentHum)) {
      if (Firebase.ready()) {
        Firebase.setFloat(fbdo, "/sensors/temperature", currentTemp);
        Firebase.setFloat(fbdo, "/sensors/humidity", currentHum);
        Serial.println("🌡️  Temp: " + String(currentTemp, 1) + "°C | 💧 Humidity: " + String(currentHum, 1) + "%");
      }
    }
  }

  yield();
}