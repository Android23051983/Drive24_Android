package com.example.drive24;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;


public class RentDataActivity extends AppCompatActivity {

    SQLiteDatabase db;
    List<ClientCars> clientCars = new ArrayList<ClientCars>();
    List<ClientCars> filteredClientCars = new ArrayList<ClientCars>();
    List<LandlordCars> filteredLandlordCars = new ArrayList<LandlordCars>();
    List<LandlordCars> landlordCars = new ArrayList<LandlordCars>();
    Date targetDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rent_data);
        TextView thedate = findViewById(R.id.date);
        RecyclerView recyclerView = findViewById(R.id.data);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(3, ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary_dark));
        recyclerView.addItemDecoration(itemDecoration);

        Button btngocalendar = findViewById(R.id.btngocalendar);
        Intent incoming = getIntent();
        String date = incoming.getStringExtra("date");
        String role = incoming.getStringExtra("role");

        thedate.setText(date);
        DatabaseHelper helper = new DatabaseHelper(this);
        if(Objects.equals(role, "Клиент")) {
            clientCars = helper.getClientCars();

            if (clientCars != null && !clientCars.isEmpty()) {
                for (ClientCars car : clientCars) {
                    Log.d("RentDataActivity", "Start: " + String.valueOf(car.getDate_start()) + " End: " + String.valueOf(car.getDate_end()));
                    Date startDate = car.getDate_start();
                    Date endDate = car.getDate_end();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    try {
                        targetDate = sdf.parse(date);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    if ((targetDate.equals(startDate) || targetDate.after(startDate)) && (targetDate.equals(endDate) || targetDate.before(endDate))) {
                        filteredClientCars.add(car);
                    }
                }
                CarClientAdapter adapter = new CarClientAdapter(this, filteredClientCars);
                recyclerView.setAdapter(adapter);
            }
        } else if (Objects.equals(role, "Владелец")) {
            landlordCars = helper.getLandlordCars();
            if(landlordCars != null && !landlordCars.isEmpty()){
                for(LandlordCars car : landlordCars) {
                    Log.d("RentDataActivity", "Start: " + String.valueOf(car.getDate_start()) + " End: " + String.valueOf(car.getDate_end()));
                    Date startDate = car.getDate_start();
                    Date endDate = car.getDate_end();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    try{
                        targetDate = sdf.parse(date);
                    } catch(ParseException e) {
                        throw new RuntimeException(e);
                    }

                    if ((targetDate.equals(startDate) || targetDate.after(startDate)) && (targetDate.equals(endDate) || targetDate.before(endDate))) {
                        filteredLandlordCars.add(car);
                    }
                }
                CarLandlordAdapter adapter = new CarLandlordAdapter(this, filteredLandlordCars);
                recyclerView.setAdapter(adapter);
            }
        }

        btngocalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }



}