package com.example.helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    Button add_btn, sos_btn;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    String longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_btn = findViewById(R.id.add_num_btn);
        sos_btn = findViewById(R.id.help_btn);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NumAdder.class);
                startActivity(intent);
            }
        });

        sos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED){
                        updateGPS();
                    }
                    else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requires permission to be granted to work properly.", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    smsGPSValues(location);
                }
            });
        }
        else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void smsGPSValues(Location location) {
        String sms;
        Geocoder geocoder = new Geocoder(MainActivity.this);
        try{
            List<Address> address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String add = address.get(0).getAddressLine(0);

            longitude = String.valueOf(location.getLongitude());
            latitude = String.valueOf(location.getLatitude());

            sms = "Emergency! I need Help immediately.\nLongitude: "+longitude+"\nLatitude: "+latitude+"\nAddress: "+add;
            ArrayList contactNo = ContactHelper.readData(this);

            SmsManager smsManager = SmsManager.getDefault();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                smsManager.sendTextMessage(contactNo.toString(),null,sms,null,null,0);
                Toast.makeText(this, "Message is sent.", Toast.LENGTH_SHORT).show();
            }

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show();
            }

        }
    }