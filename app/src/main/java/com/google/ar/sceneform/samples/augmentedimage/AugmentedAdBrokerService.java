package com.google.ar.sceneform.samples.augmentedimage;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


public class AugmentedAdBrokerService {

    private static int Total=2;

    public static void updateAD(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("AdNum", MODE_PRIVATE);
        int currAd = prefs.getInt("num",0);
        SharedPreferences.Editor editor = prefs.edit();
        currAd++;
        currAd%=Total;
        editor.putInt("num", currAd);
        editor.apply();

    }
    public static String getAd(Context context)
    {

        SharedPreferences prefs = context.getSharedPreferences("AdNum", MODE_PRIVATE);
        int currAd = prefs.getInt("num",0);


//        Toast.makeText(context,Integer.toString(currAd),Toast.LENGTH_SHORT).show();
        if(currAd==0)
        {
            return "furniture";
        }
        else if(currAd==1)
        {
            return "fifa";
        }

        return "furniture";
    }




}
