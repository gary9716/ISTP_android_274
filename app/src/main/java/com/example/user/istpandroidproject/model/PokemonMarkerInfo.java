package com.example.user.istpandroidproject.model;

import android.graphics.Bitmap;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2016/10/3.
 */
public class PokemonMarkerInfo implements ImageLoadingListener {


    public enum PokemonMarkerType{
        GYM, POKEMON, STOP
    }

    PokemonMarkerType type;
    Marker marker;
    String imageURL;
    LatLng location;
    long expirationTime = 0;
    String id;

    public static PokemonMarkerInfo newInstanceWithJSONObject(JSONObject jsonObject, PokemonMarkerType type) throws JSONException {
        PokemonMarkerInfo pokemonMarkerInfo = new PokemonMarkerInfo();
        pokemonMarkerInfo.type = type;
        pokemonMarkerInfo.location = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
        if (type == PokemonMarkerType.GYM)
        {
            pokemonMarkerInfo.id = jsonObject.getString("gym_id");
            pokemonMarkerInfo.imageURL = "http://www.csie.ntu.edu.tw/~r03944003/forts/Gym.png";
        }
        else if (type == PokemonMarkerType.POKEMON)
        {
            pokemonMarkerInfo.id = jsonObject.getString("encounter_id");
            pokemonMarkerInfo.imageURL = "http://www.csie.ntu.edu.tw/~r03944003/listImg/" + jsonObject.getString("pokemon_id") +".png";
            if(jsonObject.getLong("disappear_time") != JSONObject.NULL)
            {
                pokemonMarkerInfo.expirationTime = jsonObject.getLong("disappear_time");
            }
        }
        else
        {
            pokemonMarkerInfo.id = jsonObject.getString("pokestop_id");
            if(jsonObject.get("lure_expiration") == JSONObject.NULL)
            {
                pokemonMarkerInfo.imageURL = "http://www.csie.ntu.edu.tw/~r03944003/forts/Pstop.png";
            }
            else
            {
                pokemonMarkerInfo.expirationTime = jsonObject.getLong("lure_expiration");
                pokemonMarkerInfo.imageURL = "http://www.csie.ntu.edu.tw/~r03944003/forts/PstopLured.png";
            }
        }
        return pokemonMarkerInfo;
    }

    public boolean update()
    {

        if(type == PokemonMarkerType.POKEMON)
        {
            Long time = System.currentTimeMillis();
            if(time > expirationTime)
            {
                marker.remove();
                return true;
            }
            DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
            Date date = new Date(expirationTime - time);

            marker.setSnippet("Remain Time: " + dateFormat.format(date));
            if(marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
            return false;
        }
        else if (type == PokemonMarkerType.STOP)
        {
            Long time = System.currentTimeMillis();
            if(time > expirationTime && expirationTime > 0)
            {
                imageURL = "http://www.csie.ntu.edu.tw/~r03944003/forts/Pstop.png";
                ImageLoader.getInstance().loadImage(imageURL, this);
            }
            if(expirationTime > 0) {
                DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                Date date = new Date(expirationTime - time);

                marker.setSnippet("Remain Time: " + dateFormat.format(date));
                if(marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                    marker.showInfoWindow();
                }
            }
            return false;
        }


        return false;
    }

    public void addMarkerToGoogleMap(GoogleMap googleMap)
    {
        MarkerOptions options = new MarkerOptions().position(location).title(id);
        marker = googleMap.addMarker(options);
        ImageLoader.getInstance().loadImage(imageURL, this);
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(loadedImage);
        marker.setIcon(icon);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
