package com.example.android.myapplication;

/**
 * Created by mayank raj sinha on 01-04-2017.
 */

public class badassclass {
    private String mlocality_name;
    private int mnumber;
    public badassclass(String locality_name,int number){
        mlocality_name=locality_name;
        mnumber=number;
    }
    public String getMlocality_name(){
        return mlocality_name;
    }

    public int getMnumber() {
        return mnumber;
    }
}
