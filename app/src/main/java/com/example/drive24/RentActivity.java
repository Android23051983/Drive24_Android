package com.example.drive24;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class RentActivity extends AppCompatActivity {
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rent);
        // Установка русской локали
        Locale locale = new Locale("ru", "RU");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        TextView selectedDate = findViewById(R.id.selectedDate);
        CalendarView calendarView = findViewById(R.id.calendarView);

        Intent incoming = getIntent();
        role = incoming.getStringExtra("role");

        long currentTime = System.currentTimeMillis();
        String currentDate = "Текущая дата: " + getDate(currentTime);
        selectedDate.setText(currentDate);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = getDate(year, month + 1, dayOfMonth); // Месяц начинается с 0
                Intent intent = new Intent(RentActivity.this,RentDataActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Button prevMenu = findViewById(R.id.prevMenu);
        prevMenu.setOnClickListener(v->{
            finish();
        });
    }

    private String getDate(long millis) {
        return android.text.format.DateFormat.format("dd-MM-yyyy", new java.util.Date(millis)).toString();
    }

    private String getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day); // Месяц начинается с 0
        return android.text.format.DateFormat.format("dd-MM-yyyy", cal).toString();
    }
}