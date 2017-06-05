package com.example.android.myapplication;

/**
 * Created by mayank raj sinha on 01-04-2017.
 */

public class friendsclass {
    private String mnumber;
    private String maddress;

    public friendsclass(String number,String address){
        maddress=address;
        mnumber=number;
    }

    public String getMaddress() {
        return maddress;
    }

    public String getMnumber() {
        return mnumber;
    }
}
