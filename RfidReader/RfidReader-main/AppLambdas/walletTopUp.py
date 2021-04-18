import json
import boto3
from datetime import datetime

STATUS_CODE_0 = "Transaction completed successfully"
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

def perform_payment(account_id, top_up_amount):
    print("Fetching details from db...")
    accountData = fetch_from_db('AccountDetails', 'account_id', account_id)
    if accountData is not None:
        account_balance = accountData['account_balance']
        account_balance = account_balance + top_up_amount
        update_status = update_account_balance(account_id, account_balance)
        transaction_status = persist_transaction(account_id, top_up_amount)
        if update_status and transaction_status:
            return STATUS_CODE_0
        else:
            return STATUS_CODE_3
    else:
        return STATUS_CODE_2


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

def persist_transaction(account_id, top_up_amount):
    # Instantiating connection objects with DynamoDB using boto3 dependency
    dynamodb = boto3.resource('dynamodb')
    client = boto3.client('dynamodb')
    
    # Getting the table the table AccountTransactions object
    accountTransactions = dynamodb.Table('AccountTransactions')
    
    timeStamp = (datetime.now()).strftime("%Y-%m-%d %H:%M:%S")
    transaction_id = account_id+timeStamp
    
    # Putting a try/catch to log to user when some error occurs
    try:
        
        accountTransactions.put_item(
          Item={
                'account_id' : account_id,
                'transaction_id' : transaction_id,
                'timeStamp': timeStamp,
                'amount': top_up_amount,
                'trans_type': 'CREDIT'
            }
        )
        
        return True
    except Exception as e:
        print(e)
        return False
    

def lambda_handler(event, context):
    account_id = event['account_id']
    top_up_amount = event['top_up_amount']
    
    payment_status = None
    try:
        payment_status = perform_payment(account_id, top_up_amount)
        print(payment_status)
        if payment_status == STATUS_CODE_0:
            return {
                'statusCode': 200,
                'body': payment_status
            }
        else:
            return {
                'statusCode': 400,
                'body': payment_status
            }
    except Exception as e:
        print('Closing lambda function')
        print(e)
        return {
                'statusCode': 400,
                'body': STATUS_CODE_3
        }

    
