package com.iot.drivesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Balance extends AppCompatActivity {
    String URL1 = "https://4w9dag6eac.execute-api.eu-west-1.amazonaws.com/Prod/fetchaccountdetails";
    String URL2 = "https://4w9dag6eac.execute-api.eu-west-1.amazonaws.com/Prod/";
    String api_key = "rR8dylZSrx7TYlzyqvfhK8y2Ds7lYBoY6LzAiGMa";
    private TextView balance;
    private EditText amount;
    private Button btnAddMoney;
    public static String account_id;
    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        amount = findViewById(R.id.topupAmount);
        balance = findViewById(R.id.Balance);
        btnAddMoney = findViewById(R.id.AddMoney);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        show_balance();
        btnAddMoney.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                topUp();
            }
        });
    }

    private void show_balance() {
        Intent intent = getIntent();
        JSONObject json = new JSONObject();
        try {
//            String st3= intent.getStringExtra("passed_email3");
//            json.put("email_id", st3);
            json.put("email_id",((User)this.getApplication()).getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL1, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(LoginActivity.this,login_message,Toast.LENGTH_LONG).show();
                parseBalance(response);


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Balance.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("x-api-key", api_key);
                return headers;

            }

            ;

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void topUp() {
        showSimpleProgressDialog(this, "Loading...","Updating Wallet",false);
        String stringwallet = amount.getText().toString();
        int wallet=Integer.parseInt(stringwallet);
        JSONObject json = new JSONObject();
        try {

            json.put("account_id",account_id);
            json.put("top_up_amount", wallet);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL2, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String login_message = "";
                try {
                    login_message = response.getString("body");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(Balance.this, login_message, Toast.LENGTH_LONG).show();
                show_balance();
                removeSimpleProgressDialog();

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Balance.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("x-api-key", api_key);
                return headers;

            }

            ;

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void parseBalance(JSONObject response) {
        try {
            int main;
            if (response.getInt("statusCode")==200) {
                JSONObject dataArray = response.getJSONObject("body");
                main=dataArray.getInt("account_balance");
                account_id=dataArray.getString("account_id");
                balance.setText("Your current wallet balance is "+Integer.toString(main)+ " Euro. " + "Would you like to topup with more money?");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
