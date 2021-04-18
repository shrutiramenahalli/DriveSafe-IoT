package com.iot.drivesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private String URLstring = "https://4w9dag6eac.execute-api.eu-west-1.amazonaws.com/Prod/fetchaccountdetails";
    String api_key = "rR8dylZSrx7TYlzyqvfhK8y2Ds7lYBoY6LzAiGMa";
    private static ProgressDialog mProgressDialog;
    private ListView listView;
    ArrayList<DataModel1> dataModelArrayList1;
    private UserProfileAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.profile);
        retrieveJSON();
    }

    private void retrieveJSON() {
        //Intent intent = getIntent();
        showSimpleProgressDialog(this, "Loading...", "Fetching User Details", false);
        JSONObject json = new JSONObject();
        try {
//            String st1= intent.getStringExtra("passed_email1");
//            json.put("email_id", st1);
            json.put("email_id", ((User)this.getApplication()).getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URLstring, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("strrrrr", ">>" + response);

                        try {

                            JSONObject obj1 = response;
                            if (obj1.getInt("statusCode") == 200) {

                                dataModelArrayList1 = new ArrayList<>();
                                JSONObject dataArray1 = obj1.getJSONObject("body");

                                DataModel1 playerModel = new DataModel1();

                                playerModel.setName(dataArray1.getString("name"));
                                //playerModel.setAge(dataArray1.getInt("age"));
                                playerModel.setEmail_id(dataArray1.getString("email_id"));
                                playerModel.setVehicle_id(dataArray1.getString("vehicle_id"));
                                playerModel.setTag_id(dataArray1.getString("tag_id"));
                                playerModel.setBalance(dataArray1.getInt("account_balance"));

                                dataModelArrayList1.add(playerModel);

                                setupListview();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setupListview(){
        removeSimpleProgressDialog();  //will remove progress dialog
        listAdapter = new UserProfileAdapter(this, dataModelArrayList1);
        listView.setAdapter(listAdapter);
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
