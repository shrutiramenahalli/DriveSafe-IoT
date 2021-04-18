package com.iot.drivesafe;

public class DataModel {

    private String trans_type;
    private String vehicle_id;
    private int amount;
    private String timestamp;
    private String transaction_id;
    private String account_id;
    private String booth_id;

    public DataModel() {
        this.trans_type = trans_type;
        this.vehicle_id = vehicle_id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transaction_id = transaction_id;
        this.account_id = account_id;
        this.booth_id = booth_id;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public int getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public void setBooth_id(String booth_id) {
        this.booth_id = booth_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public String getBooth_id() {
        return booth_id;
    }

}
