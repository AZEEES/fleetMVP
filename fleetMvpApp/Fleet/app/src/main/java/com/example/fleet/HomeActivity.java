package com.example.fleet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity {

    private String TAG = "fleetHomeTag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.home_refresh);
        createViews(pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMaxClientLimit(pullToRefresh);
                pullToRefresh.setRefreshing(false);
            }
        });
//        fetchDrivers(pullToRefresh);
        getMaxClientLimit(pullToRefresh);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.v(TAG, "Broadcast received");
            // Get extra data included in the Intent
            String refresh = intent.getStringExtra("refresh");
            final SwipeRefreshLayout pullToRefresh = findViewById(R.id.home_refresh);
            createViews(pullToRefresh);
        }
    };

    private void fetchDrivers(final SwipeRefreshLayout pulltoRefresh){
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        final String owner_contact = credentials.get(0).getOwnerContact();
        realm.close();
        final FleetApplication fleetApp = ((FleetApplication) getApplicationContext());
        String server_ip = fleetApp.get_Server_IP();
        final String url = "http://" + server_ip + "/api/driver/owned_by";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            final Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<String> fetched_ids = new ArrayList<>();
                            ArrayList<String> stored_ids = new ArrayList<>();
                            for (int i=0;i<jsonArray.length(); i++){
                                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                                String _id = jsonObject.getString("_id");
                                fetched_ids.add(_id);
                            }
                            final RealmResults<DriverData> driverDataSet = realm.where(DriverData.class).findAll();
                            for(int i=0;i<driverDataSet.size();i++){
                                String _id = driverDataSet.get(i).getId();
                                if(fetched_ids.contains(_id)){
                                    stored_ids.add(_id);
                                }
                                else{
                                    driverDataSet.deleteFromRealm(i);
                                }
                            }
                            for (int i=0;i<jsonArray.length(); i++){
                                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                                String _id = jsonObject.getString("_id");
                                String contact = jsonObject.getString("contact");
                                String name = jsonObject.getString("name");
                                String access = jsonObject.getString("access");
                                if(stored_ids.contains(_id)){
                                    DriverData driverData = realm.where(DriverData.class).equalTo("_id", _id).findFirst();
                                    driverData.setName(name);
                                    driverData.setContact(contact);
                                    driverData.setAccess(access);
                                }
                                else{
                                    DriverData driverData = realm.createObject(DriverData.class, _id);
                                    driverData.setOwner_contact(owner_contact);
                                    driverData.setContact(contact);
                                    driverData.setName(name);
                                    driverData.setAccess(access);
                                    driverData.setLatitude("NA");
                                    driverData.setLongitude("NA");
                                    driverData.setLocationText("NA");
                                    driverData.setTimeStamp("NA");
                                    Log.v(TAG, "Creating " + name);
                                }
                            }
                            realm.commitTransaction();
                            realm.close();
                            fetchData(pulltoRefresh);
                            createViews(pulltoRefresh);
                        }
                        catch (JSONException exception){
                            Toast.makeText(fleetApp, "No drivers exist for the owner", Toast.LENGTH_SHORT).show();
                            pulltoRefresh.setRefreshing(false);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pulltoRefresh.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_contact", owner_contact);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void fetchData(final SwipeRefreshLayout pulltoRefresh){
        final FleetApplication fleetApp = ((FleetApplication) getApplicationContext());
        String server_ip = fleetApp.get_Server_IP();
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<DriverData> driverDataSet = realm.where(DriverData.class).findAll();
        final ArrayList<String> driverData = new ArrayList<>();
        for(int i=0;i<driverDataSet.size();i++){
            driverData.add(driverDataSet.get(i).getContact());
        }
        realm.close();
        final String url = "http://" + server_ip + "/api/location/get_last_location";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray jsonArray = new JSONArray(response.toString());
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                                String driver_id = jsonObject.getString("driver_id");
                                String latitude = jsonObject.getString("latitude");
                                String longitude = jsonObject.getString("longitude");
                                String timestamp = jsonObject.getString("timestamp");
                                String city_name = jsonObject.getString("city_name");
                                final Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                DriverData driverData1 = realm.where(DriverData.class).equalTo("contact", driver_id ).findFirst();
                                driverData1.setLocationText(city_name);
                                driverData1.setTimeStamp(timestamp);
                                realm.commitTransaction();
                                realm.close();
                                createViews(pulltoRefresh);
                            }
                        }
                        catch (JSONException exception){
                            Toast.makeText(fleetApp, "No drivers exist for the owner", Toast.LENGTH_SHORT).show();
                            pulltoRefresh.setRefreshing(false);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pulltoRefresh.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_ids", driverData.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void createViews(final SwipeRefreshLayout pulltoRefresh){
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<DriverData> driverDataSet = realm.where(DriverData.class).findAll();
        ArrayList<DriverData> driverDataset = new ArrayList<>();
        ArrayList<DriverData> driverDataSorted = new ArrayList<>();
        driverDataset.addAll(realm.copyFromRealm(driverDataSet));
        final RealmResults<DriverData> driverDataSet1 = realm.where(DriverData.class).equalTo("access", "yes").findAll();
        driverDataSorted.addAll(realm.copyFromRealm(driverDataSet1));
        final RealmResults<DriverData> driverDataSet2 = realm.where(DriverData.class).equalTo("access", "no").findAll();
        driverDataSorted.addAll(realm.copyFromRealm(driverDataSet2));
        final RealmResults<DriverData> driverDataSet3 = realm.where(DriverData.class).equalTo("access", "pending").findAll();
        driverDataSorted.addAll(realm.copyFromRealm(driverDataSet3));
        final RealmResults<DriverData> driverDataSet4 = realm.where(DriverData.class).equalTo("access", "revoked").findAll();
        driverDataSorted.addAll(realm.copyFromRealm(driverDataSet4));
        ListView driverDataListView = findViewById(R.id.home_list_view);
        DriverDataAdapter driverDataAdapter = new DriverDataAdapter(HomeActivity.this, driverDataSorted);
        driverDataListView.setAdapter(driverDataAdapter);
        pulltoRefresh.setRefreshing(false);
        realm.close();
    }

    public void getMaxClientLimit(final SwipeRefreshLayout pullToRefresh){
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        final String owner_contact = credentials.get(0).getOwnerContact();
        realm.close();
        final FleetApplication loggerApp = ((FleetApplication) getApplicationContext());
        String server_ip = loggerApp.get_Server_IP();
        final String url = "http://" + server_ip + "/api/owner/get_details";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(loggerApp, response, Toast.LENGTH_SHORT).show();
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                            String maxClientLimit = jsonObject.getString("max_client");
                            Log.v(TAG, "MAX_CLIENT : " + maxClientLimit);
                            final Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            Credentials credential = realm.where(Credentials.class).equalTo("credentialId", 1).findFirst();
                            credential.setOwnerMaxLimit(maxClientLimit);
                            realm.copyToRealmOrUpdate(credential);
                            realm.commitTransaction();
                            realm.close();
                            getActiveClients(maxClientLimit, pullToRefresh);
                        }
                        catch (JSONException exception){
                            Toast.makeText(loggerApp, exception.toString(), Toast.LENGTH_SHORT).show();
                            pullToRefresh.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                        pullToRefresh.setRefreshing(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contact", owner_contact);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);
    }

    public void getActiveClients(final String maxClientLimit, final SwipeRefreshLayout pullToRefresh){
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        final String owner_contact = credentials.get(0).getOwnerContact();
        realm.close();
        final FleetApplication loggerApp = ((FleetApplication) getApplicationContext());
        String server_ip = loggerApp.get_Server_IP();
        final String url = "http://" + server_ip + "/api/driver/active_clients";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            int activeClientCount = jsonArray.length();
                            int maxClients = Integer.parseInt(maxClientLimit);
                            if(activeClientCount>maxClients){
                                Toast.makeText(loggerApp, "Max client limit " + maxClientLimit + " exceeded", Toast.LENGTH_SHORT).show();
                                int extra_Clients = activeClientCount - maxClients;
                                for (int i=0;i<extra_Clients;i++){
                                    JSONObject activeClient = new JSONObject(jsonArray.get(i).toString());
                                    updateDriverAccess("no", activeClient);
                                }
                            }
                            fetchDrivers(pullToRefresh);

                        }
                        catch (JSONException exception){
                            Toast.makeText(loggerApp, exception.toString(), Toast.LENGTH_SHORT).show();
                            pullToRefresh.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(loggerApp, "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                        pullToRefresh.setRefreshing(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_contact", owner_contact);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);
    }

    public void updateDriverAccess(final String access, final JSONObject activeClient){
//        Log.v(TAG, activeClient.toString());
        String idFetched = "";
        try {
            idFetched = activeClient.getString("_id");
            activeClient.put("access", "no");
        }
        catch (JSONException exception){
            Log.v(TAG, exception.toString());
        }
        final String id = idFetched;
        final FleetApplication loggerApp = ((FleetApplication) getApplicationContext());
        String server_ip = loggerApp.get_Server_IP();
        final String url = "http://" + server_ip + "/api/driver/update";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(loggerApp, response, Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String status = jsonObject.getString("status");
                            if(status.equals("success")) {
                                final SwipeRefreshLayout pullToRefresh = findViewById(R.id.home_refresh);
                                fetchDrivers(pullToRefresh);
                            }
                        }
                        catch (JSONException exception){
                            Toast.makeText(loggerApp, exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("driver", activeClient.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);
    }

}
