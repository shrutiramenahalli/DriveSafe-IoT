package com.iot.drivesafe;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import android.app.ProgressDialog;
import android.content.Context;
//import android.support.v7.app.AppCompatActivity;

import android.util.Log;
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
import com.iot.drivesafe.User;

public class Alcohol extends AppCompatActivity {

    private String URLstring = "https://4w9dag6eac.execute-api.eu-west-1.amazonaws.com/Prod/fetchalcoholdetails";
    String api_key="rR8dylZSrx7TYlzyqvfhK8y2Ds7lYBoY6LzAiGMa";
    private static ProgressDialog mProgressDialog;
    private ListView listView;
    ArrayList<DataModel2> dataModelArrayList2;
    private AlcoholAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.lv2);
        retrieveJSON();

    }

    private void retrieveJSON() {
        Intent intent = getIntent();
        showSimpleProgressDialog(this, "Loading...","Fetching Alcohol Level Info",false);
        JSONObject json = new JSONObject();
        try {
//            String st4= intent.getStringExtra("passed_email4");
//            json.put("email_id", st4);
            json.put("email_id",((User)this.getApplication()).getEmail());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URLstring,json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("strrrrr", ">>" + response);

                        try {

                            JSONObject obj2 = response;
                            if(obj2.getInt("statusCode")==200){

                                dataModelArrayList2 = new ArrayList<>();
                                JSONArray dataArray2  = obj2.getJSONArray("body");

                                for (int i = 0; i < dataArray2.length(); i++) {

                                    DataModel2 playerModel = new DataModel2();
                                    JSONObject dataobj1 = dataArray2.getJSONObject(i);

                                    playerModel.setAlcoholValue(dataobj1.getString("alcohol_value"));
                                    //playerModel.setAmount(dataobj1.getInt("amount"));
                                    playerModel.setTimestamp(dataobj1.getString("timeStamp"));
                                    //playerModel.setImgURL(dataobj1.getString("imgURL"));

                                    dataModelArrayList2.add(playerModel);

                                }

                                setupListview();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "No alcohol records found!", Toast.LENGTH_LONG).show();
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
        listAdapter = new AlcoholAdapter(this, dataModelArrayList2);
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
