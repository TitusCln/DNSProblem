package com.coppel.dnsproblemexample;

import android.util.Log;

import androidx.annotation.NonNull;


import org.xbill.DNS.Address;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import okhttp3.Dns;

public class EasyDns implements Dns {

    private static final String LIVE_API_HOST = "easytaxi.com.br";
    private static final String LIVE_API_IP = "1.2.3.4";

    private boolean mInitialized;
    private InetAddress mLiveApiStaticIpAddress;

    @NonNull
    @Override
    public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException, IllegalStateException {
        // I'm initializing the DNS resolvers here to take advantage of this method being called in a background-thread managed by OkHttp
        init();
        try {
            return Collections.singletonList(Address.getByName(hostname));
        } catch (UnknownHostException e) {
            // fallback to the API's static IP
            if (LIVE_API_HOST.equals(hostname) && mLiveApiStaticIpAddress != null) {
                return Collections.singletonList(mLiveApiStaticIpAddress);
            } else {
                throw e;
            }
        } catch (IllegalStateException ie) {
            Log.w("DNS", "Couldn't initialize custom resolvers");
            throw ie;
        }
    }

    private void init() {
        if (mInitialized) return;
        else mInitialized = true;

        try {
            mLiveApiStaticIpAddress = InetAddress.getByName(LIVE_API_IP);
            // configure the resolvers, starting with the default ones (based on the current network connection)
            Resolver defaultResolver = Lookup.getDefaultResolver();
            // use Google's public DNS services
            Resolver googleFirstResolver = new SimpleResolver("8.8.8.8");
            Resolver googleSecondResolver = new SimpleResolver("8.8.4.4");
            // also try using Amazon
            Resolver amazonResolver = new SimpleResolver("205.251.198.30");
            Lookup.setDefaultResolver(new ExtendedResolver(new Resolver[]{
                    googleFirstResolver, googleSecondResolver, amazonResolver, defaultResolver}));
        } catch (UnknownHostException | IllegalStateException e) {
            Log.w("DNS", "Couldn't initialize custom resolvers");
        }
    }

}