This contains two lambda functions - 
TollBoothSubscriber - This is for performing payments. It is triggered by aws-iot which has been configured already. 
walletTopUp - This for adding money to wallet. It is triggered by a REST endpoint. 

Create the following tables in dynamoDB - 

Table_Name                Primary_Key

AccountDetails            account_id
AccountTransactions       transaction_id
TagData                   tag_id
TollBoothDetails          booth_id
TollBoothTransactions     timeStamp
UserDetails               user_id

Make sure that the table names and primary key names are same as above. 
