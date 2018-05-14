package com.example.hacker.smartmeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView textEmail;
    private TextView textName;
    private TextView textMeterSerialNumber;
    private DatabaseReference reco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String []subEmail = user.getEmail().split("@");
        String subPath = subEmail[0];

        reco = FirebaseDatabase.getInstance().getReference("HouseHold/"+subPath);

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, MainActivity.class));
        }

        //initializing views
        textEmail = (TextView) findViewById(R.id.textEmail);
        textName = (TextView) findViewById(R.id.name);
        textMeterSerialNumber = (TextView) findViewById(R.id.meterNumber);

        //displaying logged in user name
        textEmail.setText("Email: "+user.getEmail());

        reco.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    Map<String, String> map = postSnapshot.getValue(genericTypeIndicator);

                    String name = map.get("Name");
                    String meterNumber = map.get("MeterSerialNumber");

                    textName.setText("Name: " + name);
                    textMeterSerialNumber.setText("Meter number: " + meterNumber);
                    break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        if (id == R.id.logout) {

            firebaseAuth.signOut();
            //closing activity
            finish();
            startActivity(new Intent(this,MainActivity.class));
            return true;
        }
        if (id == R.id.details) {
            startActivity(new Intent(this,DetailsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
