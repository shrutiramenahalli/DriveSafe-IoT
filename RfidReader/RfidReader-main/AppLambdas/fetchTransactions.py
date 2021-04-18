import json
import boto3
from decimal import Decimal
from boto3.dynamodb.conditions import Key

STATUS_CODE_1 = "User/Account not found"
STATUS_CODE_2 = 'Unexpected error, could not fetch transactions !'

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


def fetch_transactions(account_id):
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    transaction_details = dynamodb.Table('AccountTransactions')
    
    scan_kwargs = {
        'FilterExpression': Key('account_id').eq(account_id),
        'ProjectionExpression': "account_id, transaction_id, amount, booth_id, #ts, vehicle_id, trans_type",
        'ExpressionAttributeNames': {"#ts": "timeStamp"}
    }
    response = transaction_details.scan(**scan_kwargs)
    print(response)
    if 'Items' not in response:
        return None
    else:
        return response['Items']
        
        
def handle_decimal_type(obj):
  if isinstance(obj, Decimal):
      if float(obj).is_integer():
         return int(obj)
      else:
         return float(obj)
  raise TypeError
  
  
def lambda_handler(event, context):
    email_id = event['email_id']
    try:
        user_id = check_user_exists(email_id)
        if user_id is not None:
            user_details = fetch_from_db('UserDetails', 'user_id', user_id)
            if user_details is not None :
                account_id = user_details['account_id']
                account_details = fetch_from_db('AccountDetails', 'account_id', account_id)
                if account_details is not None :
                    transaction_details = fetch_transactions(account_id)
                    if transaction_details is not None :
                        return {
                            'statusCode': 200,
                            'body': transaction_details
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
                'body': STATUS_CODE_2
        }
    
