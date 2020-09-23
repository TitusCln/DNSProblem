package com.coppel.dnsproblemexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val database = Firebase.database("https://app-coppel-prueba.firebaseio.com/")
        val myRef = database.getReference("wifi")



        view.findViewById<Button>(R.id.button_listener).setOnClickListener { view ->

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value
                    Snackbar.make(
                        view,
                        "DATA FETCHED WITH LISTENER: ${value.toString()}",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Snackbar.make(view, "FAILED WITH LISTENER", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            })


        }

        view.findViewById<Button>(R.id.button_retrofit).setOnClickListener { view ->

            val retrofit = Retrofit.Builder()
                .baseUrl("https://app-coppel-prueba.firebaseio.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: FirebaseService = retrofit.create(FirebaseService::class.java)

            service.getWifi().enqueue(object : Callback<FirebaseResponse> {
                override fun onResponse(
                    call: Call<FirebaseResponse>,
                    response: Response<FirebaseResponse>
                ) {
                    Snackbar.make(
                        view,
                        "DATA FETCHED WITH RETROFIT: ${response.body().toString()}",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null).show()
                }

                override fun onFailure(call: Call<FirebaseResponse>, t: Throwable) {
                    Snackbar.make(view, "FAILED WITH RETROFIT", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            })

        }

        view.findViewById<Button>(R.id.button_retrofit_dns).setOnClickListener { view ->
            val httpClient = OkHttpClient.Builder()
                .dns(EasyDns())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://app-coppel-prueba.firebaseio.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            val service: FirebaseService = retrofit.create(FirebaseService::class.java)

            service.getWifi().enqueue(object : Callback<FirebaseResponse> {
                override fun onResponse(
                    call: Call<FirebaseResponse>,
                    response: Response<FirebaseResponse>
                ) {
                    Snackbar.make(
                        view,
                        "DATA FETCHED WITH RETROFIT: ${response.body().toString()}",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null).show()
                }

                override fun onFailure(call: Call<FirebaseResponse>, t: Throwable) {
                    Snackbar.make(view, "FAILED WITH RETROFIT FIX", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            })
        }

    }
}

