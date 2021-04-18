import json
import boto3
from datetime import datetime
from boto3.dynamodb.conditions import Key

STATUS_CODE_0 = "User login successful"
STATUS_CODE_1 = "Email_id does not exist !"
STATUS_CODE_2 = "Incorrect password !"
STATUS_CODE_3 = 'Unexpected error, could not complete login !'

def fetch_from_db(table_name, index_key, index_value):
    
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    
    table = dynamodb.Table(table_name)

    try:
        response = table.get_item(Key={index_key: index_value})
    except Exception as e:
        print(e.response['Error']['Message'])
    else:
        if 'Item' not in response:
            return None
        else:
            return response['Item']
            
def check_user_exists(email_id):
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    userDetails = dynamodb.Table('UserDetails')
    
    
    scan_kwargs = {
        'FilterExpression': Key('email_id').eq(email_id),
        'ProjectionExpression': "email_id, user_id,#ts",
        'ExpressionAttributeNames': {"#ts": "timeStamp"}
    }
    response = userDetails.scan(**scan_kwargs)
    print(response)
    if 'Items' not in response:
        return None
    else:
        Items = response['Items']
        if not Items:
            return None
        else:
            return Items[0]['user_id']
            

def lambda_handler(event, context):
    email_id = event['email_id']
    password = event['password']
    
    try:
        user_id = check_user_exists(email_id)
        if user_id is not None:
            user_details = fetch_from_db('UserDetails', 'user_id', user_id)
            print(user_details)
            if user_details is not None :
                db_password = user_details['password']
                if password == db_password:
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
        else:
            return {
                    'statusCode': 400,
                    'body': STATUS_CODE_1
                }
    except Exception as e:
        print(e)
        print('Closing lambda function')
        print(e)
        return {
                'statusCode': 400,
                'body': STATUS_CODE_3
        }
