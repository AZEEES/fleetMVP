package com.example.fleet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private String TAG = "FleetLoginActivityTag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText ownerContactView = (EditText) findViewById(R.id.login_owner_contact);
                final EditText ownerPasswordView = (EditText) findViewById(R.id.login_owner_password);
                final String contact = ownerContactView.getText().toString();
                final String password = ownerPasswordView.getText().toString();
                if(contact.length()<10){
                    Toast.makeText(LoginActivity.this, "Phone number cannot be less than 10 digits", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()==0){
                    Toast.makeText(LoginActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    login();
                }
            }
        });
    }

    public void login(){
        final EditText ownerContactView = (EditText) findViewById(R.id.login_owner_contact);
        final EditText ownerPasswordView = (EditText) findViewById(R.id.login_owner_password);
        final Button loginButton = (Button) findViewById(R.id.login_button);
        ownerContactView.setEnabled(false);
        ownerPasswordView.setEnabled(false);
        final String contact = ownerContactView.getText().toString();
        final String password = ownerPasswordView.getText().toString();
        loginButton.setEnabled(false);
        final ImageView imageProgress = (ImageView) findViewById(R.id.loginImageProgress);
        imageProgress.setVisibility(View.VISIBLE);
        final Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide);
        imageProgress.startAnimation(animSlide);
        final FleetApplication loggerApp = ((FleetApplication) getApplicationContext());
        String server_ip = loggerApp.get_Server_IP();
        final String url = "http://" + server_ip + "/api/owner/check_creds";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.toString());
                            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                            String contact = jsonObject.getString("contact");
                            String name = jsonObject.getString("name");
                            String business_name = jsonObject.getString("business_name");
                            String address3 = jsonObject.getString("address3");
                            String address = jsonObject.getString("address1") + "\n" + jsonObject.getString("address2") + "\n" + jsonObject.getString("address3");
                            Toast.makeText(LoginActivity.this, "Logged in ", Toast.LENGTH_SHORT).show();
                            final Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            Credentials credential = realm.where(Credentials.class).equalTo("credentialId", 1).findFirst();
                            credential.setOwnerContact(contact);
                            credential.setOwnerName(name);
                            credential.setOwnerBusiness(business_name);
                            credential.setOwnerAddress(address);
                            realm.copyToRealmOrUpdate(credential);
                            realm.commitTransaction();
                            realm.close();
                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                        }
                        catch(JSONException exception){
                            Toast.makeText(LoginActivity.this, "User id - password mismatch", Toast.LENGTH_SHORT).show();
                            ownerContactView.setEnabled(true);
                            ownerPasswordView.setEnabled(true);
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
                        ownerContactView.setEnabled(true);
                        ownerPasswordView.setEnabled(true);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contact", contact);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
