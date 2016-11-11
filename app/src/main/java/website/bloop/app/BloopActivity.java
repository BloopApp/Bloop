package website.bloop.app;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BloopActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "BloopActivity";
    private RxLocation mRxLocation;

    private GoogleMap mMap;

    @BindView(R.id.overlay_text_view)
    TextView mOverlayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloop);

        ButterKnife.bind(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mRxLocation = new RxLocation(this);

        initLocationWithPermission();


    }

    private void initLocationWithPermission() {
        RxPermissions.getInstance(this)
                .request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        Log.d(TAG, "Location permission granted");

                        // Request location now that we know we have permission to do so
                        LocationRequest locationRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(5000);

                        //noinspection MissingPermission
                        mRxLocation.location().updates(locationRequest)
                                .subscribe(location -> {
                                    if (mOverlayTextView != null) {
                                        mOverlayTextView.setText(
                                                String.format("%s %s", location.getLatitude(), location.getLongitude())
                                        );
                                    }

                                    if (mMap != null) {
                                        LatLng myLocation = new LatLng(
                                                location.getLatitude(),
                                                location.getLongitude()
                                        );

                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
                                    }
                                });
                    } else {
                        Log.e(TAG, "Location permission not granted!");
                    }
                });
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
