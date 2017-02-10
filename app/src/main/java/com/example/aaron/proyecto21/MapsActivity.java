package com.example.aaron.proyecto21;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener {

    public static String result="";
    public static String distancia;
    private GoogleMap mMap;
    public static double Latitud, Longitud;
    public static double lat1=42.237949;//lat marca 1
    public static double lng1=-8.717819;//long marca 1
    public static double lat2 =42.238876 ; //lat marca 2
    public static double lng2 =-8.71577; //long marca 2
    public static double lat3 =42.237754 ; //latitud premio
    public static double lng3 =-8.714418; //longitud premio

    public Circle circle;
    GoogleApiClient apiClient;
    public static Marker marcaP;
    int PETICION_PERMISO_LOCALIZACION = 1;
    public final static int cod=1;
    private static final String LOGTAG = "android-localizacion";

    LatLng center = new LatLng(42.238078, -8.718384);
    int radius = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        mMap.setOnMapLongClickListener(this);


        LatLng marca1 = new LatLng(lat1, lng1);
        marcaP = mMap.addMarker(new MarkerOptions().position(marca1).title("PRIMERA MARCA, BUSCA QR"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marca1));
        marcaP.setVisible(false);




        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PETICION_PERMISO_LOCALIZACION);
            }
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);


        CircleOptions circleOptions = new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));

        Circle circle = mMap.addCircle(circleOptions);
    }


    private void updateUI(Location loc) {
        if (loc != null) {
            Latitud = loc.getLatitude();
            Longitud = loc.getLongitude();
        } else {
            Toast.makeText(this, "Latitud y Longitud desconocidas", Toast.LENGTH_LONG).show();
        }
    }

    public void calcularDis(){
        double earthRadius = 6372.795477598;

        double dLat = Math.toRadians(Latitud-lat1);
        double dLng = Math.toRadians(Longitud-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(Latitud)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        double distMet=dist*1000;
        int distCort=(int) distMet;
        distancia=String.valueOf(distMet);



        if(distMet<=20){
            marcaP.setVisible(true);
        }else {
            marcaP.setVisible(false);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        //...

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (permissions.length > 0 &&
                    permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            }
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        updateUI(lastLocation);
        calcularDis();

        if(result.equals("")){
            Toast.makeText(this, distancia + " metros ", Toast.LENGTH_SHORT).show();
        }
        if(result.equals("marca")){
            double latC2= 42.238832;
            double longC2=-8.715266;
            circle.setVisible(false);
            LatLng center = new LatLng(latC2, longC2);
            int radius = 100;
            CircleOptions circleOptions = new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeColor(Color.parseColor("#0D47A1"))
                    .strokeWidth(4)
                    .fillColor(Color.argb(32, 33, 150, 243));
            circle = mMap.addCircle(circleOptions);
            LatLng marca2 = new LatLng(lat2, lng2);
            lat1 = lat2;
            lng1 = lng2;
            marcaP.remove();
            marcaP = mMap.addMarker(new MarkerOptions().position(marca2).title("SEGUNDA MARCA, BUSCA QR"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marca2));
            marcaP.setVisible(false);

            Toast.makeText(this, "PULSA PARA SIGUIENTE DISTANCIA", Toast.LENGTH_SHORT).show();
            result = "";


        }
        if (result.equals("premio")) {
            double latC2=42.23781755753058;
            double lngC2= -8.713343739509583;
            circle.setVisible(false);
            LatLng center = new LatLng(latC2, lngC2);
            int radius = 100;
            CircleOptions circleOptions = new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeColor(Color.parseColor("#0D47A1"))
                    .strokeWidth(4)
                    .fillColor(Color.argb(32, 33, 150, 243));

            circle = mMap.addCircle(circleOptions);
            LatLng premio = new LatLng(lat3, lng3);
            lat1 = lat3;
            lng1 = lng3;
            marcaP.remove();
            marcaP = mMap.addMarker(new MarkerOptions().position(premio).title("ULTIMA MARCA, BUSCA QR"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(premio));
            marcaP.setVisible(false);
            Toast.makeText(this, "PULSA PARA ÚLTIMA DISTANCIA", Toast.LENGTH_SHORT).show();
            result = "";
        }


    }

    public void onMapLongClick(LatLng latLng) {
        Intent intent = new Intent(this, codigoQR.class);
        startActivityForResult(intent,cod);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == cod){
            if(resultCode == RESULT_OK){
                result=data.getStringExtra("marca2");

            }
        }
    }
}
