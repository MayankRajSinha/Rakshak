package com.example.android.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.System.exit;

public class tracking_eye extends AppCompatActivity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    String number_global;
    private ListView listView;
    ArrayList<String> arrayList;
    ArrayList<friendsclass> updateloc;
    int ab=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_eye);
        listView= (ListView) findViewById(R.id.tracking_listview);
        arrayList=new ArrayList<>();
        updateloc=new ArrayList<>();
        Bundle extras=getIntent().getExtras();
        number_global=extras.getString("number");
        Query query=databaseReference.child("PUBLIC").child(number_global).child("FRIENDS").orderByChild("YES");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    arrayList.add(postSnapshot.getValue() + "");

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       Query query2=databaseReference.child("PUBLIC").orderByChild("FRIENDS");
              query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e("may be ",dataSnapshot+"");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String status = null;int oops=0;
                   String snapshotnumber=postSnapshot.child("ID").getValue().toString();
                    Log.e("current number",snapshotnumber);
                        for (int i = 0; i < arrayList.size(); i++) {
                            Log.e("ye rahe dost", arrayList.get(i));
                            String currentfriend = arrayList.get(i).trim();
                            if (currentfriend.equals(snapshotnumber.trim())) {
                               status = postSnapshot.child("STATUS").getValue().toString();
                                 Log.e("status", status);
                                   if (status.equals("YES"))
                                       if (ab != 0) {
                                           updateloc.remove(i);
                                       }
                                updateloc.add(new friendsclass(arrayList.get(i), postSnapshot.child("CURRENT STATUS").getValue().toString()));
                                ab++;



                            }


                        }


                }
                Log.e("moooooooo",updateloc.size()+"");
                LocationAdapter adapter=new LocationAdapter(tracking_eye.this,updateloc);
                listView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tracking_eye_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.send_request:

                final EditText text=new EditText(tracking_eye.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(tracking_eye.this);
                builder.setTitle("Send Request");
                builder.setView(text)
                        // Add action buttons
                        .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String number=text.getText().toString();
                                databaseReference.child("PUBLIC").child(number).child("PENDING REQUEST").child("NO").setValue(number_global);

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               exit(1);
                            }
                        }).show();

            return true;


            case R.id.receive_request:
                final ArrayList<String> arrayList=new ArrayList<>();
                final TextView text1=new TextView(tracking_eye.this);
                Query query=databaseReference.child("PUBLIC").child(number_global).child("PENDING REQUEST").orderByChild("NO");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("kya chahte ho",dataSnapshot+"");
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                            arrayList.add(postSnapshot.getValue() + "");
                            Log.e("gusdgfu", dataSnapshot.getValue() + "");
                        }


                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                AlertDialog.Builder builder1 = new AlertDialog.Builder(tracking_eye.this);
                builder1.setTitle("Receive Request")
                        .setAdapter(new ArrayAdapter<String>(tracking_eye.this,android.R.layout.simple_list_item_1,arrayList), new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                final String latest_number= (String) arrayList.get(which);
                                AlertDialog.Builder builder = new AlertDialog.Builder(tracking_eye.this);
                                builder.setTitle("Accept request")
                                        // Add action buttons
                                        .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                databaseReference.child("PUBLIC").child(number_global).child("FRIENDS").child("YES").setValue(latest_number);
                                                databaseReference.child("PUBLIC").child(latest_number).child("FRIENDS").child("YES").setValue(number_global);

                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        }).show();
                            }
                        }).show();



                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
