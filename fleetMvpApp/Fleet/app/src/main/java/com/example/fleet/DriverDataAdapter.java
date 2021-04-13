package com.example.fleet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
//import android.support.v
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class DriverDataAdapter extends ArrayAdapter<DriverData> {

    private String TAG = "fleetDriverDataAdapterTag";
    private Context parentContext;

    public DriverDataAdapter(Context context, ArrayList<DriverData> driverDatas)
    {
        super(context,0, driverDatas);
        this.parentContext = context;
    }


    //    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.driver_data_list_item,parent,false);
        }

        final DriverData currentDriverData = getItem(position);

        TextView driverContact = listItemView.findViewById(R.id.driver_data_item_contact);
        driverContact.setText(currentDriverData.getContact());

        TextView driverName = listItemView.findViewById(R.id.driver_data_item_name);
        driverName.setText(currentDriverData.getName());

        String access = currentDriverData.getAccess();
        TextView driverLocation = listItemView.findViewById(R.id.driver_data_item_location);
        TextView driverTimestamp = listItemView.findViewById(R.id.driver_data_item_timestamp);
        final Button driverApproveButton = (Button) listItemView.findViewById(R.id.driver_data_approve_btn);
        final Button driverRevokeButton = (Button) listItemView.findViewById(R.id.driver_data_revoke_btn);

        String location_text = currentDriverData.getLocationText();
        if(location_text.equals("NA")){
            driverLocation.setVisibility(View.GONE);
        }
        else {
            driverLocation.setText(location_text);
            driverLocation.setVisibility(View.VISIBLE);
        }

        String timestamp = currentDriverData.getTimeStamp();
        if(timestamp.equals("NA")){
            driverLocation.setVisibility(View.GONE);
        }
        else{
            driverTimestamp.setText(timestamp);
            driverTimestamp.setVisibility(View.VISIBLE);
        }

        driverApproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String calling_text = driverApproveButton.getText().toString();
                new AlertDialog.Builder(getContext())
                        .setIcon(R.mipmap.logo)
                        .setTitle(calling_text + " Driver")
                        .setMessage("Are you sure you want to " + calling_text + " Driver")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String access = "yes";
                                getMaxClientLimit(access, currentDriverData, driverApproveButton);
//                                updateDriverAccess(access, currentDriverData, driverApproveButton);
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        });

        driverRevokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.mipmap.logo)
                        .setTitle("Revoke Driver")
                        .setMessage("Are you sure you want to Revoke Driver")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String access = "no";
                                getMaxClientLimit(access, currentDriverData, driverRevokeButton);
//                                updateDriverAccess(access, currentDriverData, driverApproveButton);
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        });


        LinearLayout nodelistParentLayout = listItemView.findViewById(R.id.driver_data_item_parent);

        if(access.equals("pending")){
            driverApproveButton.setVisibility(View.VISIBLE);
            driverApproveButton.setText("Approve");
            driverRevokeButton.setVisibility(View.GONE);
            driverLocation.setVisibility(View.GONE);
            driverTimestamp.setVisibility(View.GONE);
            driverApproveButton.setTextColor(getContext().getResources().getColor(R.color.colorWhite));
            setRoundedDrawable(driverApproveButton, getContext().getResources().getColor(R.color.colorPrimary));
            setRoundedDrawable(nodelistParentLayout, getContext().getResources().getColor(R.color.colorWhite));
            int grayColor = getContext().getResources().getColor(R.color.colorPrimary);
            driverName.setTextColor(grayColor);
            driverContact.setTextColor(grayColor);
        }
        if(access.equals("no")){
            driverApproveButton.setVisibility(View.VISIBLE);
            driverApproveButton.setText("Reissue");
            driverRevokeButton.setVisibility(View.GONE);
            driverLocation.setVisibility(View.GONE);
            driverTimestamp.setVisibility(View.GONE);
            driverApproveButton.setTextColor(getContext().getResources().getColor(R.color.colorWhite));
            setRoundedDrawable(driverApproveButton, getContext().getResources().getColor(R.color.colorPrimary));
            setRoundedDrawable(nodelistParentLayout, getContext().getResources().getColor(R.color.colorWhite));
            int grayColor = getContext().getResources().getColor(R.color.colorPrimary);
            driverName.setTextColor(grayColor);
            driverContact.setTextColor(grayColor);
        }
        if(access.equals("yes")){
//            driverLocation.setVisibility(View.VISIBLE);
//            driverTimestamp.setVisibility(View.VISIBLE);
            driverApproveButton.setVisibility(View.GONE);
            driverRevokeButton.setVisibility(View.VISIBLE);
            setRoundedDrawable(driverRevokeButton, getContext().getResources().getColor(R.color.colorWhite));
            setRoundedDrawable(nodelistParentLayout, getContext().getResources().getColor(R.color.colorPrimary));
        }
        if(access.equals("revoked")){
            driverApproveButton.setVisibility(View.VISIBLE);
            driverRevokeButton.setVisibility(View.GONE);
            driverLocation.setVisibility(View.GONE);
            driverTimestamp.setVisibility(View.GONE);
            driverApproveButton.setText("ACCESS REVOKED BY ADMIN");
            driverApproveButton.setBackgroundColor(getContext().getResources().getColor(R.color.colorWhite));
            driverApproveButton.setEnabled(false);
            driverApproveButton.setTextColor(getContext().getResources().getColor(R.color.colorGray));
            setRoundedDrawable(nodelistParentLayout, getContext().getResources().getColor(R.color.colorWhite));
            int grayColor = getContext().getResources().getColor(R.color.colorPrimary);
            driverName.setTextColor(grayColor);
            driverContact.setTextColor(grayColor);
        }

        return listItemView;
    }

    public void getMaxClientLimit(final String access, final DriverData driverData, final Button actionBtn){
        actionBtn.setEnabled(false);
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        final String owner_contact = credentials.get(0).getOwnerContact();
        realm.close();
        final FleetApplication loggerApp = ((FleetApplication) getContext().getApplicationContext());
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
                            getActiveClients(access, driverData, actionBtn, maxClientLimit);
                        }
                        catch (JSONException exception){
                            Toast.makeText(loggerApp, exception.toString(), Toast.LENGTH_SHORT).show();
                            actionBtn.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext().getApplicationContext(), "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                        actionBtn.setEnabled(true);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contact", owner_contact);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getActiveClients(final String access, final DriverData driverData, final Button actionBtn, final String maxClientLimit){
        actionBtn.setEnabled(false);
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        final String owner_contact = credentials.get(0).getOwnerContact();
        realm.close();
        final FleetApplication loggerApp = ((FleetApplication) getContext().getApplicationContext());
        String server_ip = loggerApp.get_Server_IP();
        final String url = "http://" + server_ip + "/api/driver/active_clients";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            int activeClient = jsonArray.length();
                            int maxClients = Integer.parseInt(maxClientLimit);
                            if(activeClient>=maxClients && access.equals("yes")){
                                Toast.makeText(loggerApp, "Maximum active client limit " + maxClientLimit + " exceeded", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                updateDriverAccess(access, driverData, actionBtn);
                            }

                        }
                        catch (JSONException exception){
                            Toast.makeText(loggerApp, exception.toString(), Toast.LENGTH_SHORT).show();
                            actionBtn.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext().getApplicationContext(), "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                        actionBtn.setEnabled(true);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("owner_contact", owner_contact);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void updateDriverAccess(final String access, final DriverData driverData, final Button actionBtn){
        actionBtn.setEnabled(false);
        final String id = driverData.getId();
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        final String owner_contact = credentials.get(0).getOwnerContact();
        realm.close();
        final JSONObject driverJson = new JSONObject();
        try {
            driverJson.put("id", id);
            driverJson.put("owner_contact", owner_contact);
            driverJson.put("name", driverData.getName());
            driverJson.put("contact", driverData.getContact());
            driverJson.put("access", access);
        }
        catch(JSONException exception){
            Toast.makeText(getContext().getApplicationContext(), exception.toString(), Toast.LENGTH_SHORT).show();
        }
//        Log.v(TAG, driverJson.toString());
        final FleetApplication loggerApp = ((FleetApplication) getContext().getApplicationContext());
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
                            if(status.equals("revoked")){
                                Toast.makeText(loggerApp, "Access Revoked by admin", Toast.LENGTH_SHORT).show();
                            }
                            else if(status.equals("success")) {
//                                Toast.makeText(loggerApp, "OK", Toast.LENGTH_SHORT).show();
                                final Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                DriverData driverData1 = realm.where(DriverData.class).equalTo("contact", driverData.getContact()).findFirst();
                                driverData1.setAccess(access);
                                realm.commitTransaction();
                                realm.close();
                                Intent intent = new Intent("custom-message");
                                intent.putExtra("refresh", "yes");
                                LocalBroadcastManager.getInstance(parentContext).sendBroadcast(intent);
//                                Log.v(TAG, "broadcast done");
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
                        Toast.makeText(getContext().getApplicationContext(), "Server issue" + error.toString(), Toast.LENGTH_LONG)
                                .show();
                        actionBtn.setEnabled(true);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("driver", driverJson.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    //Function to create rounded rectangles
    public static void setRoundedDrawable(View view, int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(20f);
        shape.setStroke(5, 0xFF121212);
        shape.setColor(backgroundColor);
        view.setBackgroundDrawable(shape);
    }
}

