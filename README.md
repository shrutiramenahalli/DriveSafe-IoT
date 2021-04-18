
**Overview**

The aim of this project is to provide a system that enables safe driving throughout a driver’s journey. There are three core touchpoints: alcohol detection, drowsiness detection, and RFID toll payments. Each component is accessible through a mobile application; named Drive Safe. To cross-verify that the system is being honestly utilized by the drivers, it is also integrated with RFID based toll booth payments which will only be actuated post the checking of alcohol levels. As we believe that people usually tend to act better when they feel rewarded or penalized for their own deeds, we have planned to collaborate with insurance companies, government, police, driver(s) and their family members, all of whom count as actors for our system. 



![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image10.png "image_tooltip")


**IoT Hardware Components**



1. General Overview
    1. Alcohol-GPS Component
        1. This component actively checks the current alcohol content and GPS location of the vehicle. If the alcohol content is over 1000, the current alcohol level and GPS location is published to an MQTT topic which is then pushed to DynamoDB.
    2. RFID Reader-Stepper Motor Component
        2. In this component the RFID reader reads the RFID tags located on the windshield of the vehicles, it then publishes this information to AWS IoT via MQTT to perform the payment transaction in cloud.This event triggers a AWS Lambda function which first checks if the driver was drunk in DynamoDB and then proceeds for the payment. If the payment is successful/failed (depending on the wallet balance) then Lambda publishes a message via AWS IoT to the stepper motor to open/close the toll barricade using MQTT, after receiving the message the toll barricade is open/closed.
2. Parts
    3. ESP-32 
    4. Stepper Motor
    5. MQ3 Alcohol Sensor
    6. RFID tags
    7. RFID reader
    8. GPS sensor
    9. LED
    10. Breadboard
3. Diagram/ScreenShots




![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image2.png "image_tooltip")





![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image14.png "image_tooltip")


**Cloud Architecture**



1. <span style="text-decoration:underline;">General Overview</span>

    In our proposed system, both the GPS sensor, MQ3 alcohol sensor of the car and the RFID reader of the toll booth are connected to AWS IoT through ESP32 which is using MQTT protocol for wireless data transmission. Once the sensor data is received by AWS IoT, calculations (such as user wallet balance deduction on the basis of toll amount) and actuations (such as checking alcohol level value above/below threshold) on the same data are made using AWS Lambda and the data is then stored in DynamoDB. Consequently, the visual data records (such as transaction history, wallet balance, alcohol level, GPS coordinates) are provided to the users (drivers in this case) or 3<sup>rd</sup> party users (insurance companies, police etc.) through the API Gateway endpoints, which fetch the DynamoDB data using Lambda function triggers. The Lambda function also provides actuation for opening and closing the toll gate on the basis of alcohol level consumption and wallet amount information for particular users, both of which are obtained from the Android app and persistently stored in dynamo DB through Lambda. 

2. <span style="text-decoration:underline;">Parts</span>

    AWS Educate Account

*   AWS IoT: connection between hardware and cloud component
*   AWS Lambda: calculations and actuation on sensor data, user data
*   AWS DynamoDB: storage of sensor data, user data
*   AWS API Gateway: enabling data storage and access from users, and providing selective data to 3rd party collaborators.
3. <span style="text-decoration:underline;">Diagram/ScreenShots</span>




![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image8.png "image_tooltip")



**ML Component**



1. General Overview

    The motive behind our project is to make the driving experience as safe as possible hence we have added Drowsiness detection to our SafeDrive project to help reduce the accidents due to driver’s drowsiness.

1. The ESP EYE cam module captures live video streaming and the model accesses the streaming through local IP address, here we are using  Transfer learning model to detect drowsiness of the driver, The model identifies the facial feature using Dlib deep learning library and then processes the video streaming using OpenCV.
2. With the Facial feature detection the model then calculates eye aspect ratio using euclidean distance for upper eyelid and lower eyelid, similarly yawn ratio will be calculated. If the ratio is over threshold then it triggers the buzzer. 
2. Parts
    1. **Raspberry Pi**: The transfer learning model is deployed locally on the Raspberry Pi for security and privacy concerns and to reduce the latency.
    2. **ESP EYE**: For continuous video streaming we have using ESP EYE camera module, The model can access the video streaming through local IP address created by ESP EYE.
    3. **Buzzer**: The buzzer will be triggered when the drowsiness or yawn is detected to alert the Driver
3. Diagram/ScreenShots



![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image12.png "image_tooltip")




![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image1.png "image_tooltip")


![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image13.png "image_tooltip")


**Mobile Application**



1. General Overview

    The mobile application aimed to provide the users (drivers in this case) with multiple features of our proposed system and ease of access to their own data. The drivers who enter the registration details such as Name, Phone Number, Email Id, License Id, Vehicle Id get allocated as a unique user in our database after which they can easily login. Once logged in, they can see their own profile information, toll booth transaction records and alcohol consumption records. Also, they can dynamically add money to their wallets in order to have smooth transactions at toll both whenever they’re not found to be drunk.

2. Parts

Android UI, API Gateway Connection


        The POST requests used in the application to retrieve the relevant information is listed below.


<table>
  <tr>
   <td>Purpose
   </td>
   <td>Request
   </td>
   <td>Response
   </td>
  </tr>
  <tr>
   <td>Login
   </td>
   <td>{
<p>
"email_id": "mail@tcd.ie",
<p>
"password": "password"
<p>
}
   </td>
   <td>{
<p>
    "statusCode": 200,
<p>
    "body": "User login successful"
<p>
}
   </td>
  </tr>
  <tr>
   <td>RegisterUser
   </td>
   <td>{  "name": "Priyanka", "phone_number":"121212121"
<p>
  "email_id": "mail@tcd.ie",
<p>
  "password": "password",
<p>
  "license_id": "license_id",
<p>
  "vehicle_id": "vehicle_id"
   </td>
   <td>{
<p>
 "statusCode": 200,
<p>
 "body": "User registered successfully"
<p>
}
   </td>
  </tr>
  <tr>
   <td>AccountDetails
   </td>
   <td>{  "email_id": "mail@tcd.ie"}
   </td>
   <td>{ "statusCode": 200,
<p>
"body": {
<p>
 "phone_number": "121212121",
<p>
 "account_id": "ACT002021-04-15 20:32:57",
<p>
"name": "Simmu",
<p>
"vehicle_id": "L2611",
<p>
 "user_id": "USR002021-04-15 20:32:57",
<p>
 "password": "********",
<p>
 "tag_id": "DS005",
<p>
 "account_balance": 0,
<p>
 "email_id": "mail@tcd.ie",
<p>
 "license_id": "V30" }}
   </td>
  </tr>
  <tr>
   <td>Transaction
<p>
Details
   </td>
   <td>{  "email_id":”mail@tcd.ie"}
   </td>
   <td>{"statusCode": 200,
<p>
 "body": []}
   </td>
  </tr>
  <tr>
   <td>Alcohol Details
   </td>
   <td>{  "email_id": "mail@tcd.ie"}
   </td>
   <td>{ "statusCode": 200,
<p>
  "body": [
<p>
 { "alcohol_value": "1571",
<p>
    "timeStamp": "2021-04-14 15:42:14", "user_id": "USR002021-04-12 13:53:03",
<p>
 "latitude": "0.00",
<p>
 "longitude": "-0.00"  }]}
   </td>
  </tr>
  <tr>
   <td>Wallet Topup
   </td>
   <td>{"account_id": "ACT01",
<p>
"top_up_amount": 10}
   </td>
   <td>{ "statusCode": 200,   "body": "Transaction completed successfully"}
   </td>
  </tr>
</table>




3. Diagram/ScreenShots




![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image15.jpg "image_tooltip")


![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image3.jpg "image_tooltip")


![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image4.jpg "image_tooltip")


![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image9.jpg "image_tooltip")


![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image5.jpg "image_tooltip")


![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image7.jpg "image_tooltip")


![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image6.jpg "image_tooltip")


![alt_text](https://github.com/DriveSafe-IoT/DriveSafeIoT/blob/main/Images/image11.jpg "image_tooltip")

