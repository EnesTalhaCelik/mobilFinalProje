package com.example.finalproje;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList <WeatherRVModal> weatherRVModals;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModal> weatherRVModals) {
        this.context = context;
        this.weatherRVModals = weatherRVModals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
     }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherRVModal modal = weatherRVModals.get(position);
        holder.temperatureTV.setText(modal.getTemperature() + "°c");
        Picasso.get().load("http:".concat(modal.getIcon())).into (holder.conditionIV);
        holder.windTV.setText(modal.getWindSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat( "yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat( "hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());

            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModals.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windTV, temperatureTV,timeTV;
        private ImageView conditionIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.idTVWindSped);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature) ;
            timeTV = itemView.findViewById(R.id.idTVTime) ;
            conditionIV = itemView.findViewById(R.id.idIVCondition) ;

        }
    }

}