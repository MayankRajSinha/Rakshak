package com.example.android.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class badass_places extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ArrayList<badassclass> arrayList;
    private ListView listView;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badass_places);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        arrayList=new ArrayList<>();
        listView= (ListView) findViewById(R.id.places_badass);
        Query query=databaseReference.child("LOCALITY").orderByChild("RATE");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();int i=1;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String locality_name=postSnapshot.getKey().toString();
                    arrayList.add(new badassclass(locality_name,i));
                   LocalityAdapter adapter=new LocalityAdapter(badass_places.this,arrayList);
                    listView.setAdapter(adapter);
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
