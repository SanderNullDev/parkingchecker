package nl.blackcatapps.pchecker.network;

import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.blackcatapps.pchecker.models.ParkingData;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by Sander on 26-11-2016.
 */

public class ParkApiManager {

    private static ParkApiManager apiManager;
    private static OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private static ParkApi parkApi;

    public static ParkApiManager getInstance(){
        if(apiManager == null){
            apiManager = new ParkApiManager();
            return apiManager;
        }else{
            return apiManager;
        }
    }

    public ParkApiManager(){

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.blackcatapps.nl/parkingdata/api/")
                .client(getOkHttpClient())
        	                .addConverterFactory(GsonConverterFactory.create())
        	                .build();

        parkApi = retrofit.create(ParkApi.class);

    }

    private OkHttpClient getOkHttpClient(){
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            return okHttpClient;
        }else{
            return okHttpClient;
        }
    }

    public ParkApi getParkApi(){
        return parkApi;
    }

    public interface ParkApi {

        @GET("parkingforapp.php")
        Call<List<ParkingData>> getParkingData();



    }
}
