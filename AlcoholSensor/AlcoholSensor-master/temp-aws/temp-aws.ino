#include "SPIFFS.h"
#include <WiFiClientSecure.h>
#include <Wire.h>

#include <PubSubClient.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#define LED 2
#define SENSOR 36
#define DELAY 500

const char* ssid = ""; //Provide your SSID
const char* password = "";          // Provide Password
const char* mqtt_server = ""; // Relace with your MQTT END point
const int mqtt_port = 8883;

String Read_rootca;
String Read_cert;
String Read_privatekey;
//=============================================================================================================================
#define BUFFER_LEN  256
int sensorValue = 0;
long lastMsg = 40;
char msg[BUFFER_LEN];
byte mac[6];
char mac_Id[18];
int count = 1;
//=============================================================================================================================

#define COUNT_LOW 0
#define COUNT_HIGH 8888

#define TIMER_WIDTH 16

#include "esp32-hal-ledc.h"

WiFiClientSecure espClient;
PubSubClient client(espClient);

#include <Arduino.h>
#include <NMEAGPS.h>

//-------------------------------------------------------------------------
// Check that the config files are set up properly
#if !defined(NMEAGPS_PARSE_RMC) & \
    !defined(NMEAGPS_PARSE_GGA) & \
    !defined(NMEAGPS_PARSE_GLL)
#error You must uncomment at least one of NMEAGPS_PARSE_RMC, NMEAGPS_PARSE_GGA or NMEAGPS_PARSE_GLL in NMEAGPS_cfg.h!
#endif

#if !defined(GPS_FIX_LOCATION)
#error You must uncomment GPS_FIX_LOCATION in GPSfix_cfg.h!
#endif

#define gpsPort Serial2 //ESP32 pins 16RX, 17TX  UART02
#define GPS_PORT_NAME "ESP32_Serial2"
#define DEBUG_PORT Serial
#define BAUD_GPS 9600
#define BAUD_USB_SERIAL 115200
#define fixLEDPin 19

using namespace NeoGPS;
NMEAGPS gps;
//WiFiUDP ntpUDP;
//NTPClient timeClient(ntpUDP);
//String formattedDate;
//String dayStamp;
//String timeStamp;


void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}
void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP32-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      client.publish("ei_out", "hello world");
      // ... and resubscribe
      client.subscribe("ei_in");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(BAUD_USB_SERIAL);
  gpsPort.begin(BAUD_GPS);

  pinMode(fixLEDPin, OUTPUT);

  // Wait for USB Serial
  while (!Serial)
  {
    yield();
  }
  // By default, "16E TTL" and other U-blox GPS modules output RMC, VTG, CGA, GSA, GSV, GLL messages once a second (these are standard NMEA messages)
  // Configure the GPS to only output what is needed, like just RMC as is shown below by disabling all other default sentences.

  gpsPort.println(F("$PUBX,40,RMC,0,0,0,0*47")); //RMC ON (Comented out to leave RMC on...uncomment this line to disable RMC)
  delay(100);
  gpsPort.println(F("$PUBX,40,VTG,0,0,0,0*5E")); //VTG OFF
  delay(100);
  gpsPort.println(F("$PUBX,40,GGA,0,0,0,0*5A")); //GGA OFF
  delay(100);
  gpsPort.println(F("$PUBX,40,GSA,0,0,0,0*4E")); //GSA OFF
  delay(100);
  gpsPort.println(F("$PUBX,40,GSV,0,0,0,0*59")); //GSV OFF
  delay(100);
  gpsPort.println(F("$PUBX,40,GLL,0,0,0,0*5C")); //GLL OFF
  delay(100);

  //  Serial.begin(115200);
  pinMode(LED, OUTPUT);
  Serial.println("Sensor start");
  setup_wifi();
  delay(1000);
  //  ledcSetup(1, 50, TIMER_WIDTH); // channel 1, 50 Hz, 16-bit width
  //  ledcAttachPin(2, 1);

  //=============================================================
  if (!SPIFFS.begin(true)) {
    Serial.println("An Error has occurred while mounting SPIFFS");
    return;
  }
  //=======================================
  //Root CA File Reading.
  File file2 = SPIFFS.open("/AmazonRootCA1.pem", "r");
  if (!file2) {
    Serial.println("Failed to open file for reading");
    return;
  }
  Serial.println("Root CA File Content:");
  while (file2.available()) {
    Read_rootca = file2.readString();
    Serial.println(Read_rootca);
  }
  //=============================================
  // Cert file reading
  File file4 = SPIFFS.open("/75541911d4-certificate.pem.crt", "r");
  if (!file4) {
    Serial.println("Failed to open file for reading");
    return;
  }
  Serial.println("Cert File Content:");
  while (file4.available()) {
    Read_cert = file4.readString();
    Serial.println(Read_cert);
  }
  //=================================================
  //Privatekey file reading
  File file6 = SPIFFS.open("/75541911d4-private.pem.key", "r");
  if (!file6) {
    Serial.println("Failed to open file for reading");
    return;
  }
  Serial.println("privateKey File Content:");
  while (file6.available()) {
    Read_privatekey = file6.readString();
    Serial.println(Read_privatekey);
  }
  //=====================================================

  char* pRead_rootca;
  pRead_rootca = (char *)malloc(sizeof(char) * (Read_rootca.length() + 1));
  strcpy(pRead_rootca, Read_rootca.c_str());

  char* pRead_cert;
  pRead_cert = (char *)malloc(sizeof(char) * (Read_cert.length() + 1));
  strcpy(pRead_cert, Read_cert.c_str());

  char* pRead_privatekey;
  pRead_privatekey = (char *)malloc(sizeof(char) * (Read_privatekey.length() + 1));
  strcpy(pRead_privatekey, Read_privatekey.c_str());

  Serial.println("================================================================================================");
  Serial.println("Certificates that passing to espClient Method");
  Serial.println();
  Serial.println("Root CA:");
  Serial.write(pRead_rootca);
  Serial.println("================================================================================================");
  Serial.println();
  Serial.println("Cert:");
  Serial.write(pRead_cert);
  Serial.println("================================================================================================");
  Serial.println();
  Serial.println("privateKey:");
  Serial.write(pRead_privatekey);
  Serial.println("================================================================================================");

  espClient.setCACert(pRead_rootca);
  espClient.setCertificate(pRead_cert);
  espClient.setPrivateKey(pRead_privatekey);

  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);

  //====================================================================================================================
  WiFi.macAddress(mac);
  snprintf(mac_Id, sizeof(mac_Id), "%02x:%02x:%02x:%02x:%02x:%02x",
           mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
  Serial.print(mac_Id);
  //=====================================================================================================================


  delay(2000);

}
//void loop() {
//  digitalWrite(LED, HIGH);   // turn the LED on (HIGH is the voltage level)
//  delay(DELAY);              // wait for a second
//
//  // read the value from the sensor:
//  sensorValue = analogRead(SENSOR);
//  Serial.print("Value: "); Serial.println(sensorValue);
//
//  digitalWrite(LED, LOW);    // turn the LED off by making the voltage LOW
//  delay(DELAY);
//}

void loop() {

  float lati;
  float lon;
  while (gps.available(gpsPort)) // basically true every second if 1Hz from GPS, only true when the GPS returns a sentence
  {
    gps_fix fix = gps.read(); // save the latest
    // Set the "fix" LED to on or off
    bool gpsWasFixed = fix.valid.status && (fix.status >= gps_fix::STATUS_STD);
    lati = fix.latitude();

    lon = fix.longitude();
  }
  Serial.println(lon);
  Serial.println(lati);
  digitalWrite(LED, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(DELAY);
  //    while (!timeClient.update()) {
  //    timeClient.forceUpdate();
  //  }
  //  // The formattedDate comes with the following format:
  //  // 2018-05-28T16:00:13Z
  //  // We need to extract date and time
  //  formattedDate = timeClient.getFormattedDate();
  //  Serial.println(formattedDate);
  int sensorValue = analogRead(SENSOR);
  Serial.print("Value: "); Serial.println(sensorValue);
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  long now = millis();
  if (sensorValue > 1000) {
    if (now - lastMsg > 20000) {
      lastMsg = now;
      //=============================================================================================
      String macIdStr = mac_Id;
      String sensor = String(sensorValue);
      String timenow = String(now);
      String uuid = "user_911";
      String latitude = String(lati);
      String longitude = String(lon);
      snprintf (msg, BUFFER_LEN, "{\"mac_Id\" : \"%s\",  \"SensorAnalog\" : \"%s\", \"Time\" : \"%s\", \"UserId\" : \"%s\", \"Latitude\" : \"%s\", \"Longitude\" : \"%s\"}", macIdStr.c_str(), sensor.c_str(), timenow.c_str(), uuid.c_str(), latitude.c_str(), longitude.c_str());
      Serial.print("Publish message: ");
      Serial.print(count);
      Serial.println(msg);
      client.publish("ei_out", msg);
      count = count + 1;
      //================================================================================================
    }
  }
  digitalWrite(LED, LOW);    // turn the LED off by making the voltage LOW
  delay(DELAY);
  delay(DELAY);
  
}
