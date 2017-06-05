package com.example.android.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login_activity extends AppCompatActivity {
    private EditText login,password_id;
    private Button submit;
    private DatabaseReference databaseReference;
    private SharedPreferences myPref;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        login= (EditText) findViewById(R.id.email_id);
        password_id= (EditText) findViewById(R.id.password_login);
        submit= (Button) findViewById(R.id.submit_button);
        myPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        boolean loggedin = myPref.getBoolean("loggedin", false);
        String number=myPref.getString("number","");
        Log.e("here i am",loggedin+"");
        if(loggedin){
            Intent intent=new Intent(Login_activity.this,MainActivity.class);
            intent.putExtra("number",number);
            startActivity(intent);
            finish();

        }
     //   final long number=Long.parseLong(String.valueOf(login.getText()));
      //  final String password_id=password_.getText().toString();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("bhai dikat kya hai",login.getText()+"");
                Query query=databaseReference.child("PUBLIC").orderByChild("ID").equalTo(Long.parseLong(login.getText().toString().trim()));
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("dhsdhfghsdj",dataSnapshot+"");
                        if(dataSnapshot!=null) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                String password = postSnapshot.child("PASSWORD").getValue() + "";
                            //    Log.e("jdhfajjfhbhjsdf",);
                                if (password.equals(password_id.getText().toString()))
                                {
                                    SharedPreferences.Editor editor = myPref.edit();
                                    editor.putBoolean("loggedin", true);
                                    editor.putString("number",postSnapshot.child("ID").getValue()+"");
                                    editor.apply();
                                    editor.commit();
                                    Intent intent = new Intent(Login_activity.this, MainActivity.class);
                                    intent.putExtra("number",""+postSnapshot.child("ID").getValue());
                                    startActivity(intent);
                                    finish();
                                }else
                                Toast.makeText(Login_activity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                            Toast.makeText(Login_activity.this, "Wrong id", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }
}
