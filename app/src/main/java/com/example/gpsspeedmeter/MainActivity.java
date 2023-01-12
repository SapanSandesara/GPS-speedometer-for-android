package com.example.gpsspeedmeter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Calendar;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity implements OnMapsSdkInitializedCallback {
    //This is the map marker that will be updated with each location update in the map.
    private Marker currentlocationmarker;
    public float distance = 0.0F;
    private String time;
    private PendingIntent pi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST,this);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        createNotificationChannel();

        setAlarm();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // below is auto generated by IDE to request location updates. Apparently need to check
        // if permission granted! Got some help from
        //https://developer.android.com/reference/androidx/core/app/ActivityCompat and
        //some online resources.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



       // Display google map

        MapView mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        Location currentlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


       if(currentlocation == null){
           Toast.makeText(this,"Cannot retrieve location. Please restart",
                   Toast.LENGTH_LONG).show();
       }
        else {
           LatLng currentlatlng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());

           mapView.getMapAsync(new OnMapReadyCallback() {
               @Override
               public void onMapReady(@NonNull GoogleMap googleMap) {

                   googleMap.moveCamera(CameraUpdateFactory.newLatLng
                           (currentlatlng));
                   googleMap.setMinZoomPreference(15);
                  currentlocationmarker= googleMap.addMarker(new MarkerOptions().position(currentlatlng));
                  currentlocationmarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker1));

               }
           });
           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 30,
                   new LocationListener() {


                       @Override
                       public void onLocationChanged(@NonNull Location location) {

                           LatLng currentlatlng2 = new LatLng(location.getLatitude(), location.getLongitude());
                           mapView.getMapAsync(new OnMapReadyCallback() {
                               @Override
                               public void onMapReady(@NonNull GoogleMap googleMap) {
                                   googleMap.moveCamera(CameraUpdateFactory.newLatLng
                                           (currentlatlng2));
                                   googleMap.setMinZoomPreference(15);
                                   currentlocationmarker.setPosition(currentlatlng2);

                               }

                           });
                       }
                       @Override
                       public void onProviderEnabled(String provider) {
                           // This method is called when the provider is enabled by the user.
                       }

                       @Override
                       public void onProviderDisabled(String provider) {
                           // This method is called when the provider is disabled by the user.
                       }
                   });
       }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                new myLocationListener(this));

        Button endtripbutton = (Button) this.findViewById(R.id.endtrip);
        endtripbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Trip ended", Toast.LENGTH_LONG).show();
                time = Calendar.getInstance().getTime().toString();
                DecimalFormat df = new DecimalFormat("#.####");
                String dist = df.format(distance);
                String content = "Date: "+time+"\n Distance travelled: "+dist;

                writetofile("temp5.txt",content);
                distance = 0.0F;
            }
        });
        Button viewtripsbutton = (Button) this.findViewById((R.id.viewtrips));
        viewtripsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ViewTripsActivity.class);
                startActivity(intent);
//                setContentView(R.layout.view_trips);
//
//




            }
        });


    }

    private void setAlarm() {

        Calendar cal = Calendar.getInstance();
      //  cal.setTimeInMillis(System.currentTimeMillis());
//        if(cal.get(Calendar.HOUR_OF_DAY) >15){
////            cal.add(Calendar.DATE,1);
//            return;
//        }
        cal.set(Calendar.HOUR_OF_DAY, 9); // Alarm for 4 pm
        cal.set(Calendar.MINUTE,45);
        cal.set(Calendar.SECOND, 0);


        AlarmManager am = getSystemService(AlarmManager.class);
        Intent i = new Intent(this, AlarmReceiver.class);
        pi = PendingIntent.getBroadcast(this,0,i,
                0);
        Calendar currenttime = Calendar.getInstance();
        long currenttimemillis = currenttime.getTimeInMillis();
        long intendedtime = cal.getTimeInMillis();
        if(intendedtime>=currenttimemillis){
            am.setRepeating(AlarmManager.RTC_WAKEUP, intendedtime,
                    AlarmManager.INTERVAL_DAY, pi);
        }
        else{
            cal.add(Calendar.DAY_OF_MONTH,1);
            intendedtime = cal.getTimeInMillis();
            am.setRepeating(AlarmManager.RTC_WAKEUP, intendedtime,
                    AlarmManager.INTERVAL_DAY, pi);
        }
//
//


    }

    private void createNotificationChannel() {

        NotificationChannel nchannel = new NotificationChannel("abc", "Reminder",
                NotificationManager.IMPORTANCE_HIGH);
        nchannel.setDescription("Channel for reminder");

        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(nchannel);
    }


    public void writetofile(String filename, String content){
        File file = new File(this.getExternalFilesDir(null), "temp5.txt");
        try {

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
            pw.println(content);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //This method does a lot more than calculate speed at this point lol.
    public void calculatespeed(Location location){

        float speed = location.getSpeed();
        float speedinkmh = speed * 3600/1000;
        double roundedspeed = this.roundoff(speedinkmh);
        String speed2 = Double.toString(roundedspeed);
//        TextView speedmeter = this.findViewById(R.id.speedmeter);
//        speedmeter.setText(speed2 + "km/h");
        Gauge gauge = this.findViewById(R.id.gauge);
        gauge.moveToValue(speedinkmh);
        distance = (float) (distance + (roundedspeed/3600));
        TextView texvi = findViewById(R.id.odometer);
        DecimalFormat dfo = new DecimalFormat("#.##");
        String dist = dfo.format(distance);
        texvi.setText("ODOMETER: "+dist + " KM");
        //Sending the speed to widget with intent
//        Intent intent = new Intent(this, SpeedmeterWidget.class);
//        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        int[] ids = AppWidgetManager.getInstance(getApplication())
//                .getAppWidgetIds(new ComponentName(getApplication(), SpeedmeterWidget.class));
//
//        intent.putExtra("speed", roundedspeed);
//        sendBroadcast(intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.speedmeter_widget);
        ComponentName thisWidget = new ComponentName(this, SpeedmeterWidget.class);

        remoteViews.setTextViewText(R.id.appwidget_text, dist+ " km");
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);


    }
    public double roundoff(float speed){

        BigDecimal bd = new BigDecimal(speed);
        BigDecimal rounded = bd.setScale(3,BigDecimal.ROUND_HALF_UP);
        return rounded.doubleValue();

    }

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {

    }

}