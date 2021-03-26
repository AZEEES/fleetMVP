package com.example.fleetclient;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    private String TAG="FleetClientLoginTag";
    private String owner_contact = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final TextView loginOwnerTextView = (TextView) findViewById(R.id.login_owner_text);
        final EditText loginOwnerContactView = (EditText) findViewById(R.id.login_owner_contact);

        loginOwnerTextView.setVisibility(View.VISIBLE);
        loginOwnerContactView.setVisibility(View.VISIBLE);

        loginOwnerContactView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==10){
                    loginOwnerContactView.setEnabled(false);
                    final ImageView imageProgress = (ImageView) findViewById(R.id.loginImageProgress);
                    imageProgress.setVisibility(View.VISIBLE);
                    final Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide);
                    imageProgress.startAnimation(animSlide);
                    get_User_Details(s.toString());
                }
            }
        });

        Button loginButtonView = (Button) findViewById(R.id.login_button);
        loginButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText driverNameView = (EditText) findViewById(R.id.login_driver_name);
                EditText driverContactView = (EditText) findViewById(R.id.login_driver_contact);
                String driver_name = driverNameView.getText().toString();
                String driver_contact = driverContactView.getText().toString();
                if(driver_name.length()==0){
                    Toast.makeText(LoginActivity.this, "Driver Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(driver_contact.length()<10){
                    Toast.makeText(LoginActivity.this, "Driver phone number cannot be less than 10 digits", Toast.LENGTH_SHORT).show();
                }
                else{
                    login(driver_name, driver_contact, owner_contact);
                }
            }
        });

    }

    public void get_User_Details(final String contact){
        final FleetClientApplication loggerApp = ((FleetClientApplication) getApplicationContext());
        String server_ip = loggerApp.get_Server_IP();
        final String url = "http://" + server_ip + "/api/owner/get_details";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        TextView loginOwnerDetailsView = (TextView) findViewById(R.id.login_owner_details);
                        loginOwnerDetailsView.setText(response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                            String contact = jsonObject.getString("contact");
                            owner_contact = contact;
                            String name = jsonObject.getString("name");
                            String business_name = jsonObject.getString("business_name");
                            String address3 = jsonObject.getString("address3");
                            String address = jsonObject.getString("address1") + "\n" + jsonObject.getString("address2") + "\n" + jsonObject.getString("address3");
                            loginOwnerDetailsView.setText(name + "\n" + business_name + "\n" + address3 + "\n" + contact);
                            final Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            Credentials credential = realm.where(Credentials.class).equalTo("credentialId", 1).findFirst();
                            credential.setOwnerName(name);
                            credential.setOwnerBusiness(business_name);
                            credential.setOwnerContact(contact);
                            credential.setOwnerAddress(address);
                            realm.copyToRealmOrUpdate(credential);
                            realm.commitTransaction();
                            realm.close();
                            final TextView loginOwnerTextView = (TextView) findViewById(R.id.login_owner_text);
                            final EditText loginOwnerContactView = (EditText) findViewById(R.id.login_owner_contact);
                            final ImageView loginProgressView = (ImageView) findViewById(R.id.loginImageProgress);
                            loginOwnerTextView.setVisibility(View.GONE);
                            loginOwnerContactView.setVisibility(View.GONE);
                            loginProgressView.setVisibility(View.GONE);
                            showDriverEditables();
                        }
                        catch (JSONException exception){
                            Toast.makeText(LoginActivity.this, "Owner does not exist", Toast.LENGTH_SHORT).show();
                            makeOwnerEditable();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                        makeOwnerEditable();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contact", contact);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void login(final String driver_name, final String driver_contact, final String owner_contact ){
        final Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setEnabled(false);
        final ImageView imageProgress = (ImageView) findViewById(R.id.loginImageProgress);
        imageProgress.setVisibility(View.VISIBLE);
        final Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide);
        imageProgress.startAnimation(animSlide);
        final FleetClientApplication loggerApp = ((FleetClientApplication) getApplicationContext());
        String server_ip = loggerApp.get_Server_IP();
        final String url = "http://" + server_ip + "/api/driver";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String contact = jsonObject.getString("contact");
                            Toast.makeText(LoginActivity.this, "Logged in ", Toast.LENGTH_SHORT).show();
                            final Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            Credentials credential = realm.where(Credentials.class).equalTo("credentialId", 1).findFirst();
                            credential.setDriverName(driver_name);
                            credential.setDriverContact(driver_contact);
                            realm.copyToRealmOrUpdate(credential);
                            realm.commitTransaction();
                            realm.close();
                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                        }
                        catch(JSONException exception){
                            Toast.makeText(LoginActivity.this, "Driver phone number already registered : ", Toast.LENGTH_SHORT).show();
                            loginButton.setEnabled(true);
                            imageProgress.setVisibility(View.GONE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                        loginButton.setEnabled(true);
                        imageProgress.setVisibility(View.GONE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", driver_name);
                params.put("contact", driver_contact);
                params.put("owner_contact", owner_contact);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void makeOwnerEditable(){
        ImageView imageProgress = (ImageView) findViewById(R.id.loginImageProgress);
        imageProgress.setVisibility(View.GONE);
        EditText ownerContactEditView = (EditText) findViewById(R.id.login_owner_contact);
        ownerContactEditView.setEnabled(true);
        String texthint = ownerContactEditView.getText().toString();
        ownerContactEditView.setHint(texthint);
        ownerContactEditView.setText("");
    }

    private void showDriverEditables(){
        TextView loginOwnerLabelView = (TextView) findViewById(R.id.login_owner_label);
        TextView loginOwnerDetailsView = (TextView) findViewById(R.id.login_owner_details);
        TextView loginDriverNameLabel = (TextView) findViewById(R.id.login_driver_name_label);
        EditText loginDriverName = (EditText) findViewById(R.id.login_driver_name);
        TextView loginDriverContactLabel = (TextView) findViewById(R.id.login_driver_contact_label);
        EditText loginDriverContact = (EditText) findViewById(R.id.login_driver_contact);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginOwnerDetailsView.setVisibility(View.VISIBLE);
        loginOwnerLabelView.setVisibility(View.VISIBLE);
        loginDriverNameLabel.setVisibility(View.VISIBLE);
        loginDriverName.setVisibility(View.VISIBLE);
        loginDriverContactLabel.setVisibility(View.VISIBLE);
        loginDriverContact.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);

    }


}
