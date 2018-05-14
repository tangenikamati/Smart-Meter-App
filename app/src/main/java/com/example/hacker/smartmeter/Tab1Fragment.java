package com.example.hacker.smartmeter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Hacker on 5/9/2018.
 */

public class Tab1Fragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reco;

    double min=0.0;
    double max = 0.0;
    double average= 0.0;
    String dateMin = "";
    String dateMax = "";
    double total=0;
    int count=0;

    private TextView txtMin;
    private TextView txtMax;
    private TextView txtAverage;
    private TextView txtTotal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String []subEmail = user.getEmail().split("@");
        String subPath = subEmail[0];

        reco = FirebaseDatabase.getInstance().getReference("HouseHold/"+subPath);

        View view=inflater.inflate(R.layout.fragment_tab1, container, false);

        txtMin =view.findViewById(R.id.textMin);
        txtMax =view.findViewById(R.id.textMax);
        txtAverage =view.findViewById(R.id.textAverage);
        txtTotal =view.findViewById(R.id.textTotal);

        //onStart();
        return view;
    }
    @Override
    public void onStart() {

        super.onStart();

        reco.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    Map<String, String> map = postSnapshot.getValue(genericTypeIndicator);

                    double volume = Double.parseDouble(map.get("Volume"));

                    if (count == 0) {
                        min = volume;
                        max = volume;
                        dateMin = map.get("Date");
                        dateMax = map.get("Date");
                    } else {
                        if (min > volume) {
                            min = volume;
                            dateMin = map.get("Date");
                        }
                        if (max < volume) {
                            max = volume;
                            dateMax = map.get("Date");
                        }
                    }
                    total = total + volume;
                    count++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        average = total / count;
        txtMin.setText("Min = " + min + " m³");
        txtMax.setText("Max = " + max + " m³");
        txtAverage.setText("Average = " + average + " m³");
        txtTotal.setText("Total = " + total + " m³");

        //Resetting to zero
        count = 0;
        average=0;
        total=0;
    }

}
