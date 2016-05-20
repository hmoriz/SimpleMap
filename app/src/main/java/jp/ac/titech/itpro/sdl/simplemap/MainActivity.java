package jp.ac.titech.itpro.sdl.simplemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "MainActivity";
    private final static LatLng MY_LOCATION = new LatLng(35.604667, 139.682759);
    private GoogleApiClient googleapiclient;
    private boolean googleapiconnected = false;
    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final static int REQCODE_PERMISSIONS = 1111;

    private Button button;
    private Location loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.addMarker(new MarkerOptions().position(MY_LOCATION).title(getString(R.string.my_location)));
                map.moveCamera(CameraUpdateFactory.newLatLng(MY_LOCATION));
            }
        });
        googleapiclient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onButtonClick");
                if(loc != null){
                    Log.d(TAG, "onButtonClick");
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            Log.d(TAG, "MapMoving");
                            LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
                            CameraPosition camerapos =new CameraPosition.Builder()
                                    .target(latlng)
                                    .zoom(17)
                                    .build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camerapos));
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleapiclient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleapiclient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        googleapiconnected = true;
        showLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        googleapiconnected = false;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleapiconnected = false;
    }

    private void showLastLocation() {
        for (String permission : PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(this, permission)  != PackageManager.PERMISSION_GRANTED) {
            if (googleapiconnected)  ActivityCompat.requestPermissions(this, PERMISSIONS, REQCODE_PERMISSIONS);
            else{Toast.makeText(this, "Failed, Access not allowed", Toast.LENGTH_SHORT).show();  return;}
        }  }
        loc = LocationServices.FusedLocationApi .getLastLocation(googleapiclient);
        Log.d(TAG, "loc="+loc.toString());
    }

}
