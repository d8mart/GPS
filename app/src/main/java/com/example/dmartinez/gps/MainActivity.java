package com.example.dmartinez.gps;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements LocationListener,OnMapReadyCallback {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 0;
    TextView tvLat;
    TextView tvLong;
    Button btstart,btstop;
    private LocationManager mLocationM;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvLat=(TextView) findViewById(R.id.textViewLat);
        tvLong=(TextView) findViewById(R.id.textViewLong);
        btstart=(Button) findViewById(R.id.button);
        btstop=(Button) findViewById(R.id.button2);

        mLocationM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        verifyGPS();
        getBestProvider();

        btstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGps(v);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

        tvLat.setText(location.getLatitude()+"");
        tvLong.setText(location.getLongitude()+"");


        writeExternalS();


        centerMap(new LatLng(location.getLatitude(),location.getLongitude()));
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
            File file = new File(dir,"sensor_locations.txt");
            String message="Lat: "+tvLat.getText().toString()+" "+"Long: "+tvLong.getText().toString()+"\n";
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void startGps(View v){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
      /*  mLocationM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);*/

        /*perfil 1*/ //caminando
         mLocationM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        /*perfil 2*/ //vehiculo veloz
        /* mLocationM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);*/
        /*perfil 3*/ //mas veloz
         /*mLocationM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);*/
    }

   /* @TargetApi(Build.VERSION_CODES.M)
    public void stopGps(View v){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        mLocationM.removeUpdates(this);
    }*/
    @TargetApi(Build.VERSION_CODES.M)
    public void stopSensor(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        mLocationM.removeUpdates(this);
    }

    public void stopGps(View v){stopSensor();}

    @Override
    protected void onStop() {
        super.onStop();
        /*stopSensor();*/
    }

    private boolean verifyGPS(){
        boolean enable = mLocationM.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!enable){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS no està habilitado");
            builder.setMessage("Habilitar ahora?");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setCancelable(false);

            builder.setNegativeButton("Ahora no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            builder = builder.setPositiveButton("Oki", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    return;
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();

            return false;
        }
        return enable;
    }

    private String getBestProvider(){
        Criteria criteria = new Criteria();
        /*criteria.setAccuracy(Criteria.ACCURACY_LOW);*//*criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);*/criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        /*criteria.setPowerRequirement(Criteria.POWER_LOW);*//*criteria.setPowerRequirement(Criteria.POWER_MEDIUM);*/criteria.setPowerRequirement(Criteria.POWER_LOW);
        String bestProvider = mLocationM.getBestProvider(criteria,true);
        Toast.makeText(this,"Provider "+bestProvider,Toast.LENGTH_SHORT).show();
        return bestProvider;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;

        /*LatLng sydney = new LatLng(-34,151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("marker in sidney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        if(!checkPermission())return;
        Location location = mLocationM.getLastKnownLocation(getBestProvider());
        LatLng lastLocation=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(lastLocation).title("Home?"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
    }

    private boolean checkPermission() {
        return true;
    }

    private void centerMap(LatLng mapCenter){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapCenter));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(18.0f)
                .bearing(0f)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),1000,null);
    }

    public void ApiG(View v){Intent i = new Intent(this,Google_Api.class);startActivity(i);}
}
