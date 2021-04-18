package com.iot.drivesafe;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iot.drivesafe.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginDetails {
    private Context context;
    String Base_url = "https://4w9dag6eac.execute-api.eu-west-1.amazonaws.com/Prod/";
    String login_info;
    String register_info;
    String api_key = "rR8dylZSrx7TYlzyqvfhK8y2Ds7lYBoY6LzAiGMa";

    public LoginDetails(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String login_info);
    }

    public void getRegisterInfo(String name, String phone_number, String email, String Password, String vehicleid, String licenseid, VolleyResponseListener volleyResponseListener) {

        String URL = Base_url + "registerUser";

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("phone_number", phone_number);
            json.put("email_id", email);
            json.put("password", Password);
            json.put("license_id", licenseid);
            json.put("vehicle_id", vehicleid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                register_info = "";
                try {
                    JSONObject register_body = response.getJSONObject("");
                    register_info = register_body.getString("body");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, register_info, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(register_info);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError Error) {
                Toast.makeText(context, "Register Unsuccessful", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something wrong");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("x-api-key", api_key);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}