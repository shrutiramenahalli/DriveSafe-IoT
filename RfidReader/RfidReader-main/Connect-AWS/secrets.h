#include <pgmspace.h>

#define SECRET
#define THINGNAME "RFID-Reader-Toll-Booth-1"

const char WIFI_SSID[] = "--";
const char WIFI_PASSWORD[] = "--";
const char AWS_IOT_ENDPOINT[] = ""; //Insert Endpoint values here

// Amazon Root CA 1
static const char AWS_CERT_CA[] PROGMEM = R"EOF(
-----BEGIN CERTIFICATE-----

-----END CERTIFICATE-----
)EOF"; // Insert Certificate values


// Device Certificate
static const char AWS_CERT_CRT[] PROGMEM = R"KEY(
-----BEGIN CERTIFICATE-----

-----END CERTIFICATE-----
)KEY"; // Insert Certificate values


// Device Private Key
static const char AWS_CERT_PRIVATE[] PROGMEM = R"KEY(
-----BEGIN RSA PRIVATE KEY-----

-----END RSA PRIVATE KEY-----
)KEY"; // Insert Certificate values
