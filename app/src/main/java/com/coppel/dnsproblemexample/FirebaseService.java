package com.coppel.dnsproblemexample;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FirebaseService {

    @GET("/wifi.json")
    Call<FirebaseResponse> getWifi();

}
