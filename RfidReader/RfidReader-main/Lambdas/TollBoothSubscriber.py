import json
import boto3
from datetime import datetime

STATUS_CODE_0 = "Transaction completed successfully"
STATUS_CODE_1 = "Insufficient balance !"
STATUS_CODE_2 = "Details not found !"
STATUS_CODE_3 = 'Unexpected error, could not complete payment !'

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
    
def perform_payment(tag_id, booth_id):
    print("Fetching details from db...")
    tagData = fetch_from_db('TagData', 'tag_id', tag_id)
    boothData = fetch_from_db('TollBoothDetails', 'booth_id', booth_id)
    
    if tagData and boothData is not None:
        toll_amount = boothData['toll_amount']
        vehicle_id = tagData['vehicle_id']
        
        user_id = tagData['user_id']
        userData = fetch_from_db('UserDetails', 'user_id', user_id)
        if userData is not None:
            account_id = userData['account_id']
            accountData = fetch_from_db('AccountDetails', 'account_id', account_id)
            if accountData is not None:
                account_balance = accountData['account_balance']
                if account_balance >= toll_amount:
                    account_balance = account_balance - toll_amount
                    update_status = update_account_balance(account_id, account_balance)
                    transaction_status = persist_transaction(account_id, toll_amount, vehicle_id, tag_id, user_id, booth_id)
                    if update_status and transaction_status:
                        return STATUS_CODE_0
                    else:
                        return STATUS_CODE_3
                else:
                    return STATUS_CODE_1
            else:
                return STATUS_CODE_2
        else:
            return STATUS_CODE_2
    else:
        return STATUS_CODE_2

def persist_transaction(account_id, toll_amount, vehicle_id, tag_id, user_id, booth_id):
    # Instantiating connection objects with DynamoDB using boto3 dependency
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    
    # Getting the table the table AccountTransactions object
    accountTransactions = dynamodb.Table('AccountTransactions')
    
    timeStamp = (datetime.now()).strftime("%Y-%m-%d %H:%M:%S")
    transaction_id = vehicle_id+tag_id+user_id+timeStamp
    
    # Putting a try/catch to log to user when some error occurs
    try:
        
        accountTransactions.put_item(
          Item={
                'account_id' : account_id,
                'transaction_id' : transaction_id,
                'booth_id' : booth_id,
                'timeStamp': timeStamp,
                'amount': toll_amount,
                'vehicle_id': vehicle_id,
                'trans_type': 'DEBIT'
            }
        )
        
        return True
    except Exception as e:
        print(e)
        return False
    
    
def update_account_balance(account_id, account_balance):
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    
    account_details = dynamodb.Table('AccountDetails')
    response = account_details.update_item(
        Key={
            'account_id': account_id,
        },
        UpdateExpression="set account_balance = :b",
        ExpressionAttributeValues={
            ':b': account_balance,
        },
        ReturnValues="UPDATED_NEW"
    )
    updated_balance = response['Attributes']['account_balance']
    if updated_balance == account_balance:
        return True
    else:
        return False
        
def insert_booth_trans_to_db(event):
    # Instantiating connection objects with DynamoDB using boto3 dependency
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    
    # Getting the table the table TollBoothTransactions object
    tollBoothTransactions = dynamodb.Table('TollBoothTransactions')
    
    # Getting the current datetime and transforming it to string in the format bellow
    timeStamp = (datetime.now()).strftime("%Y-%m-%d %H:%M:%S")
    booth_id = event['booth_id']
    tag_id = event['tag_id']
    
    # Putting a try/catch to log to user when some error occurs
    try:
        
        tollBoothTransactions.put_item(
          Item={
                'booth_id' : booth_id,
                'timeStamp': timeStamp,
                'tag_id': tag_id
            }
        )
        
        return True
    except Exception as e:
        print(e)
        return False
    
def handle_toll_barricade(action):
    client = boto3.client('', region_name='') #add IoT device name and AWS region
    response = client.publish(
                topic='esp32/sub',
                qos=1,
                payload=json.dumps({"toll_barricade":action}))
                    
#That's the lambda handler, you can not modify this method
def lambda_handler(event, context):
    
    booth_trans = insert_booth_trans_to_db(event)
    print(booth_trans)
    
    booth_id = event['booth_id']
    tag_id = event['tag_id']
    
    payment_status = None
    try:
        payment_status = perform_payment(tag_id, booth_id)
        print(payment_status)
        if payment_status == STATUS_CODE_0:
            handle_toll_barricade("OPEN")
            return {
                'statusCode': 200,
                'body': json.dumps(payment_status)
            }
        else:
            handle_toll_barricade("CLOSE")
            return {
                'statusCode': 400,
                'body': json.dumps(payment_status)
            }
    except Exception as e:
        print('Closing lambda function')
        print(e)
        handle_toll_barricade("CLOSE")
        return {
                'statusCode': 400,
                'body': json.dumps(STATUS_CODE_3)
        }
