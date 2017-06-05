package com.example.android.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub


//        KeyEvent ke = (KeyEvent) arg1.getExtras().get(Intent.EXTRA_KEY_EVENT);
        if (Intent.ACTION_POWER_CONNECTED.equals(arg1.getAction()))
            Toast.makeText(arg0, "volume up button clicked", Toast.LENGTH_LONG).show();
        else{
                Log.v("#@%@%#", "Power button is pressed.");

                Toast.makeText(arg0, "power button clicked", Toast.LENGTH_LONG).show();
            }
            //perform what you want here
        }
    }

