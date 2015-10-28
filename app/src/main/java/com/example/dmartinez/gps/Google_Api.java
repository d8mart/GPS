package com.example.dmartinez.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Google_Api extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    GoogleApiClient mGoogleApiC;
    LocationRequest mLocationR;
    TextView tvLt;
    TextView tvLg;
    /*public static final long UPDATE_INTERVAL_IN_MILLISECONDS=10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;*/

    /*p1*/
       public static final long UPDATE_INTERVAL_IN_MILLISECONDS=2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /*p2*/
      /* public static final long UPDATE_INTERVAL_IN_MILLISECONDS=10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;*/
    /*p3*/
      /* public static final long UPDATE_INTERVAL_IN_MILLISECONDS=20000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google__api);

        tvLt=(TextView) findViewById(R.id.textViewLt);
        tvLg=(TextView) findViewById(R.id.textViewLg);

        mGoogleApiC=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationR=new LocationRequest();
        mLocationR.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationR.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationR.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);/*mLocationR.setPriority(LocationRequest.PRIORITY_NO_POWER);*/


    }

    public void onStart(){
        super.onStart();
        mGoogleApiC.connect();
        Toast.makeText(this,"Connecting",Toast.LENGTH_SHORT).show();
    }

    public void stopGps(View v){
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mGoogleApiC.disconnect();
    }

    public void startGps(View v){
        if(!mGoogleApiC.isConnected()){mGoogleApiC.connect();}
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiC, mLocationR, this);
    }

   /* public void onStart(View v){
        super.onStart();
        mGoogleApiC.connect();
        Toast.makeText(this,"Connecting",Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onConnected(Bundle bundle) {

        Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        tvLt.setText(location.getLatitude()+"");
        tvLg.setText(location.getLongitude()+"");

        writeExternalS();
    }


    public void writeExternalS(){
        String state;
        state= Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath()+"/MyAppFile");
            if(!dir.exists()){
                dir.mkdir();
            }
            File file = new File(dir,"api_locations.txt");
            String message="ApiLat: "+tvLt.getText().toString()+" "+"ApiLong: "+tvLg.getText().toString()+"";
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                fileOutputStream.write('\n');
                fileOutputStream.write("\n".getBytes());
                fileOutputStream.write(message.getBytes());

                fileOutputStream.close();
                Toast.makeText(getApplicationContext(),"saved",Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{Toast.makeText(getApplicationContext(),"sd not found",Toast.LENGTH_SHORT).show();}
    }

}
