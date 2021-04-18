This folder contains the code for rfid reader which is to be uploaded on esp32. 

secrets.h file contains the necessary certificates to connect with AWS IoT. 
Modify the WIFI_SSID & WIFI_PASSWORD for the esp32 to establish connection with internet. 

To run the code - Open the Connect-AWS.ino in arduino IDE. In a new tab open secrets.h file,
make the required changes mentioned above and upload the code. In the serial monitor you will
see AWS IoT connection is completed. Now you can scan the rfid tags.
