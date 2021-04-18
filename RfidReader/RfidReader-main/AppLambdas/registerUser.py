import json
import boto3
from datetime import datetime
from boto3.dynamodb.conditions import Key

STATUS_CODE_0 = "User registered successfully"
STATUS_CODE_1 = "User already exists !"
STATUS_CODE_2 = 'Unexpected error, could not complete registration !'

def check_user_exists(email):
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    userDetails = dynamodb.Table('UserDetails')
    
    # table = dynamodb.Table('Movies')
    scan_kwargs = {
        'FilterExpression': Key('email_id').eq(email),
        'ProjectionExpression': "#yr, title, info.rating",
        'ExpressionAttributeNames': {"#yr": "year"}
    }
    response = userDetails.scan(**scan_kwargs)
    print(response)
    if 'Items' not in response:
        return None
    else:
        return response['Items']

def persist_user(name, phone_number, email_id, password, license_id, vehicle_id):
    # Instantiating connection objects with DynamoDB using boto3 dependency
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    
    # Getting the table the table AccountTransactions object
    userDetails = dynamodb.Table('UserDetails')
    accountDetails = dynamodb.Table('AccountDetails')
    
    timeStamp = (datetime.now()).strftime("%Y-%m-%d %H:%M:%S")
    account_id = "ACT00" + timeStamp
    user_id = "USR00"+timeStamp
    
    # Putting a try/catch to log to user when some error occurs
    try:
        
        accountDetails.put_item(
          Item={
                'account_id' : account_id,
                'account_balance' : 0,
                'user_id': user_id
            }
        )
        userDetails.put_item(
          Item={
                'account_id' : account_id,
                'user_id': user_id,
                'name': name,
                'phone_number' : phone_number,
                'email_id' : email_id,
                'password' : password,
                'license_id' : license_id,
                'vehicle_id' : vehicle_id
            }
        )
        
        return True
    except Exception as e:
        print(e)
        return False
        
        
def lambda_handler(event, context):
    name = event['name']
    phone_number = event['phone_number']
    email_id = event['email_id']
    password = event['phone_number']
    license_id = event['license_id']
    vehicle_id = event['vehicle_id']
    
    try:
        user_exists = check_user_exists(email_id)
        print(user_exists)
        if user_exists is None or not user_exists:
            new_user_persisted = persist_user(name, phone_number, email_id, password, license_id, vehicle_id)
            if new_user_persisted:
                return {
                    'statusCode': 200,
                    'body': STATUS_CODE_0
                }
            else:
                return {
                    'statusCode': 400,
                    'body': STATUS_CODE_2
                }
        else:
            return {
                'statusCode': 400,
                'body': STATUS_CODE_1
                }
    except Exception as e:
        print('Closing lambda function')
        print(e)
        return {
                'statusCode': 400,
                'body': STATUS_CODE_2
        }
