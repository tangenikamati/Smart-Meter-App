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

public class Tab2Fragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reco;

    TextView txtCost;
    double total=0.0;
    double costRate = 0.95;
    double totalCost;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tab2, container, false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String []subEmail = user.getEmail().split("@");
        String subPath = subEmail[0];

        reco = FirebaseDatabase.getInstance().getReference("HouseHold/"+subPath);
        txtCost =view.findViewById(R.id.cost);


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
                    total = total + volume;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        totalCost = total*costRate;
        txtCost.setText("Cost = "+totalCost);
        total=0;
    }
}

