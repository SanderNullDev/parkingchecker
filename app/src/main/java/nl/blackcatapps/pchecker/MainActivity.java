package nl.blackcatapps.pchecker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.itemanimators.SlideUpAlphaAnimator;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import nl.blackcatapps.pchecker.models.ParkingData;
import nl.blackcatapps.pchecker.network.ParkApiManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private RecyclerView garageRecycler;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers(), new Crashlytics());
        setContentView(R.layout.activity_main);
        garageRecycler = findViewById(R.id.garage_recycler);
        garageRecycler.setLayoutManager(new LinearLayoutManager(this));
        garageRecycler.setItemAnimator(new SlideUpAlphaAnimator());

        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
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
                R.color.dark_text);

        swipeContainer.setRefreshing(true);


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

        if (garageRecycler.getAdapter() == null) {
            //create our FastAdapter which will manage everything
            FastItemAdapter fastAdapter = new FastItemAdapter();

            //set our adapters to the RecyclerView
            //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
            garageRecycler.setAdapter(fastAdapter);
            //set the items to your ItemAdapter
            fastAdapter.add(data);

        } else {
            ((FastItemAdapter) garageRecycler.getAdapter()).setNewList(data);
        }
        garageRecycler.getAdapter().notifyDataSetChanged();


    }


}
