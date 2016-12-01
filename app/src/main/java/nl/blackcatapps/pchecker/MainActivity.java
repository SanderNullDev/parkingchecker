package nl.blackcatapps.pchecker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import nl.blackcatapps.pchecker.models.ParkingData;
import nl.blackcatapps.pchecker.network.ParkApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private RecyclerView garageRecycler;
    private SwipeRefreshLayout swipeContainer;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers(), new Crashlytics());
        setContentView(R.layout.activity_main);
        garageRecycler = (RecyclerView) findViewById(R.id.garage_recycler);
        garageRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                loadParkingData();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(true);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


    }

    private void loadParkingData() {
        Call<List<ParkingData>> getInfo = ParkApiManager.getInstance().getParkApi().getParkingData();
        getInfo.enqueue(new Callback<List<ParkingData>>() {
            @Override
            public void onResponse(Call<List<ParkingData>> call, Response<List<ParkingData>> response) {
                if (response != null && response.body() != null) {
                    for (ParkingData parkingData : response.body()) {
                        parkingData.toString();
                    }
                } else {
                    Log.d(TAG, "onResponse: Empty?");
                    Log.d(TAG, "onResponse: response is " + response.code());
                }

                setUpList(response.body());

            }

            @Override
            public void onFailure(Call<List<ParkingData>> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to get data");
                t.printStackTrace();
            }
        });
    }

    private void setUpList(List<ParkingData> data) {
        swipeContainer.setRefreshing(false);

        //create our FastAdapter which will manage everything
        FastItemAdapter fastAdapter = new FastItemAdapter();

        //set our adapters to the RecyclerView
        //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
        garageRecycler.setAdapter(fastAdapter);
        garageRecycler.getAdapter().notifyDataSetChanged();

        //set the items to your ItemAdapter
        fastAdapter.add(data);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: Connected!");

        loadParkingData();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Log.d(TAG, "onConnected: First time asking for permission, trying again immediately");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        } else {
            Log.d(TAG, "onConnected: User's location is " + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: Failed to connect");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: Failed to connect!");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "onConnected: User's location is " + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
