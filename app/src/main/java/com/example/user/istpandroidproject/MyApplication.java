package com.example.user.istpandroidproject;

import android.app.Application;

import com.example.user.istpandroidproject.model.OwnedPokemonInfo;
import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by user on 2016/9/5.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheSize(50 * 1024 * 1024) //50MB
                .diskCacheFileCount(100)
                .build();

        ImageLoader.getInstance().init(config);

        ParseObject.registerSubclass(OwnedPokemonInfo.class);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .enableLocalDataStore()
                .applicationId("6w03PE6va2FNpxmw9gI3lOwE17Y8fJMmmQ2IvjvX")
                .clientKey("AtuIJrkKgw95UKKnW5Y3Z6GjTKv46gvzJDwFSiZj")
                .server("https://parseapi.back4app.com/")
                .build());

        FacebookSdk.sdkInitialize(getApplicationContext());


    }
}
