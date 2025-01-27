package com.example.drive24;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CarClientAdapter extends RecyclerView.Adapter<CarClientAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<ClientCars> cars;

    public CarClientAdapter(Context context, List<ClientCars> cars) {
        this.cars = cars;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CarClientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int vievType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new CarClientAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarClientAdapter.ViewHolder holder, int position) {
        ClientCars car = cars.get(position);
        holder.modelView.setText(car.getCar_model());
        holder.numberView.setText(car.getCar_number());
        String landlord = car.getFirst_name() + " " + car.getLast_name();
        holder.landlordView.setText(landlord);
        holder.phoneView.setText(car.getPhone());
        holder.emailView.setText(car.getEmail());
        holder.countryView.setText(car.getCountry());
        holder.cityView.setText(car.getCity());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        holder.startDateView.setText(sdf.format(car.getDate_start()));
        holder.endDateView.setText(sdf.format(car.getDate_end()));

    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView modelView, numberView, landlordView, phoneView, emailView, countryView, cityView, startDateView, endDateView;
        ViewHolder(View view) {
            super(view);
            modelView       = view.findViewById(R.id.model);
            numberView      = view.findViewById(R.id.number);
            landlordView    = view.findViewById(R.id.landlord);
            phoneView       = view.findViewById(R.id.phone);
            emailView       = view.findViewById(R.id.email);
            countryView     = view.findViewById(R.id.country);
            cityView        = view.findViewById(R.id.city);
            startDateView   = view.findViewById(R.id.date_start);
            endDateView     = view.findViewById(R.id.date_end);
        }
    }
}
