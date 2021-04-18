package com.iot.drivesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.iot.drivesafe.User;

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
import static com.android.volley.Request.Method.POST;

public class LoginActivity extends AppCompatActivity {
    private String URLline="https://4w9dag6eac.execute-api.eu-west-1.amazonaws.com/Prod/login";
    String api_key="rR8dylZSrx7TYlzyqvfhK8y2Ds7lYBoY6LzAiGMa";

    private EditText etUname, etPass;
    private Button btn,btnRegister;

    User email=new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUname = findViewById(R.id.etLogGmail);
        etPass = findViewById(R.id.etLoginPassword);
        btn = findViewById(R.id.btnLogin);
        btnRegister=findViewById(R.id.tvRegister);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    private void loginUser(){

        final String username = etUname.getText().toString().trim();
        final String password = etPass.getText().toString().trim();


        JSONObject json = new JSONObject();
        try {
            json.put("email_id", username);
            json.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URLline,json,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String login_message = "";
                try {
                    login_message= response.getString("body");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this,login_message,Toast.LENGTH_SHORT).show();
                parseData(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){


            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("x-api-key", api_key);
                return headers;

            };

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseData(JSONObject response) {

        try {
            //JSONObject jsonObject = new JSONObject(response);
            //int status=response.getInt("statusCode");
            if (response.getInt("statusCode")==200) {
                String dataArray = response.getString("body");
                Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                ((User)this.getApplication()).setEmail(etUname.getText().toString());
                //String st = etUname.getText().toString();
                //intent.putExtra("passed_email", st);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}