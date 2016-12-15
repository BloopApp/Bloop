package website.bloop.app.fragments;

import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import website.bloop.app.R;

/**
 * Fragment which animates bootprints on the map to show changing distance.
 * Visual eye candy that looks really cool and shows a user where they previously were.
 * Bootprints eventually disappear to not clutter the screen.
 */
public class BootprintMapFragment extends Fragment implements OnMapReadyCallback {
    private static final float DEFAULT_ZOOM_LEVEL = 18f;
    // TODO: replace this with actual sphere distance calculations
    private static final double BOOTPRINT_MIN_MERCATOR_DISTANCE = 0.0000895f;
    private static final float BOOTPRINT_SIZE_METERS = 10;
    private static final int MAX_BOOTPRINTS = 50;
    private static final String TAG = "BootprintMapFragment";

    @BindView(R.id.google_map)
    MapView mMapView;

    private List<GroundOverlay> mBootprintLocations;
    private boolean mHasSetInitialCameraPosition;

    private BitmapDescriptor mLeftBootprint;
    private BitmapDescriptor mRightBootprint;
    private GoogleMap mMap;
    private int mTotalSteps;

    public BootprintMapFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bootprint_map, container, false);

        ButterKnife.bind(this, view);

        MapsInitializer.initialize(getActivity());
        mMapView.onCreate(savedInstanceState);

        // init bootprints
        //TOmaybeDO better data structure for this
        mBootprintLocations = new ArrayList<>(MAX_BOOTPRINTS);

        mLeftBootprint = BitmapDescriptorFactory.fromResource(R.drawable.bootprint_left);
        mRightBootprint = BitmapDescriptorFactory.fromResource(R.drawable.bootprint_right);

        // init map
        mHasSetInitialCameraPosition = false;

        mMapView.getMapAsync(this);

        return view;
    }

    public void updatePlayerLocation(Location location) {
        if (mBootprintLocations == null || location == null) {
            return;
        }

        LatLng latLng = new LatLng(
                location.getLatitude(),
                location.getLongitude()
        );

        if (mBootprintLocations.size() == 0) {
            // first time, place left and right prints
            placeBootprint(latLng, 0f);
            placeBootprint(latLng, 0f);

            return;
        }

        if (mBootprintLocations.size() >= 1) {
            // we have a bootprint, only place another if we're far enough away from it
            final LatLng lastLatLng = mBootprintLocations.get(
                    mBootprintLocations.size() - 1
            ).getPosition();

            double deltaLat = latLng.latitude - lastLatLng.latitude;
            double deltaLong = latLng.longitude - lastLatLng.longitude;

            double mercatorDistance = Math.sqrt(
                    Math.pow(deltaLat, 2) + Math.pow(deltaLong, 2)
            );

            if (mercatorDistance > BOOTPRINT_MIN_MERCATOR_DISTANCE) {
                placeBootprint(latLng, (float) Math.atan2(deltaLong, deltaLat));
            }
        }
    }

    private void placeBootprint(LatLng latLng, float direction) {
        if (mMap != null) {
            final BitmapDescriptor bootprint;
            // pick left/right print
            if (mTotalSteps++ % 2 == 0) {
                bootprint = mLeftBootprint;
            } else {
                bootprint = mRightBootprint;
            }


            final GroundOverlay overlay = mMap.addGroundOverlay(
                    new GroundOverlayOptions().position(
                            latLng,
                            BOOTPRINT_SIZE_METERS
                    ).bearing(
                            (float) (direction / Math.PI * 180d)
                    ).image(bootprint)
            );

            mBootprintLocations.add(overlay);
            removeAndUpdateBootprints();
        }
    }

    private void removeAndUpdateBootprints() {
        if (mBootprintLocations.size() >= MAX_BOOTPRINTS) {
            final GroundOverlay removedOverlay = mBootprintLocations.remove(0);
            removedOverlay.remove();
        }

        for (int i = 0; i < mBootprintLocations.size(); i++) {
            GroundOverlay bootprint = mBootprintLocations.get(i);

            int footprintsLeft = MAX_BOOTPRINTS - mBootprintLocations.size();

            float transparency = 1f - ((i + footprintsLeft) / (float) MAX_BOOTPRINTS);

            bootprint.setTransparency((2f * transparency / 3f) + .33f);
        }
    }

    public void updateMapCenter(Location location) {
        if (mMap != null && location != null) {
            LatLng myLocation = new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
            );

            if (mHasSetInitialCameraPosition) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_ZOOM_LEVEL));
            } else {
                // jump right to the first position.
                mHasSetInitialCameraPosition = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_ZOOM_LEVEL));

                // TODO: reveal map after this?
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mMapView.onLowMemory();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json)
            );

            if (!success) {
                Log.e(TAG, "Set map style failed!");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style file.", e);
        }

        mMap.getUiSettings().setAllGesturesEnabled(false);
    }
}
