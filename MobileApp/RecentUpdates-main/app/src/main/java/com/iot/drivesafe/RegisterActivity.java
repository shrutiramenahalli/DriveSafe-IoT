package com.iot.drivesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn, gotoLoginBtn;
    private EditText regName, regPhone, regEmail, regPassword, regLicense_id, regVehicle_id;
    String error_message = "";
    String register_messsage = "";
    String URL = "https://4w9dag6eac.execute-api.eu-west-1.amazonaws.com/Prod/registerUser";
    String api_key="rR8dylZSrx7TYlzyqvfhK8y2Ds7lYBoY6LzAiGMa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = findViewById(R.id.btnRegLogin);
        gotoLoginBtn = findViewById(R.id.btnGotoLogin);
        regName = findViewById(R.id.etRegName);
        regPhone = findViewById(R.id.etRegPhone);
        regEmail = findViewById(R.id.etRegGmail);
        regPassword = findViewById(R.id.etRegPassword);
        regLicense_id = findViewById(R.id.etLicenseId);
        regVehicle_id = findViewById(R.id.etVehicleId);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_user();
            }
        });
        gotoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void register_user(){
        String name = regName.getText().toString().trim();
        String Phone = regPhone.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String Password = regPassword.getText().toString().trim();
        String vehicleId = regVehicle_id.getText().toString().trim();
        String licenseId = regLicense_id.getText().toString().trim();

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("phone_number", Phone);
            json.put("email_id", email);
            json.put("password", Password);
            json.put("license_id", vehicleId);
            json.put("vehicle_id", licenseId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL,json,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String login_message = "";
                try {
                    login_message= response.getString("body");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(RegisterActivity.this,login_message,Toast.LENGTH_LONG).show();
                parseData(response);

            }
        },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){


            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("x-api-key", api_key);
                return headers;

            };

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void parseData(JSONObject response) {
        try {
            //               JSONObject jsonObject = new JSONObject(response);
            int status=response.getInt("statusCode");
            if (response.getInt("statusCode")==200) {
                String dataArray = response.getString("body");
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}