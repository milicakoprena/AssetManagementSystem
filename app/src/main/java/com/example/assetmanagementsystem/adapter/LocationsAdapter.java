package com.example.assetmanagementsystem.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.model.Location;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.BeanHolder> {
    private List<Location> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnLocationItemClick onLocationItemClick;

    public LocationsAdapter(List<Location> list, Context context, OnLocationItemClick onLocationItemClick){
        this.list = list;
        this.context = context;
        this.onLocationItemClick = onLocationItemClick;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BeanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.location_list_item, parent, false);
        return new BeanHolder(view, onLocationItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull BeanHolder holder, int position) {
        Log.e("bind", "onBindViewHolder: " + list.get(position));
        holder.textViewName.setText(list.get(position).getName());
        holder.textViewCoordinates.setText("("+list.get(position).getLatitude()+","+list.get(position).getLongitude()+")");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BeanHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textViewName;
        TextView textViewCoordinates;
        ImageButton buttonDelete;
        ImageButton buttonUpdate;
        private OnLocationItemClick onLocationItemClick;
        public BeanHolder(View itemView, LocationsAdapter.OnLocationItemClick listener) {
            super(itemView);
            this.onLocationItemClick = listener;
            itemView.setOnClickListener(this);
            textViewName = itemView.findViewById(R.id.item_name);
            textViewCoordinates = itemView.findViewById(R.id.item_coordinates);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonUpdate = itemView.findViewById(R.id.button_update);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onLocationItemClick.deleteLocation(getAdapterPosition());
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onLocationItemClick.updateLocation(getAdapterPosition());
                }
            });

        }
        @Override
        public void onClick(View view) {
            if (onLocationItemClick != null) {
                onLocationItemClick.onLocationClick(getAdapterPosition());
            }
        }
    }

    public interface OnLocationItemClick{
        void onLocationClick(int pos);
        void deleteLocation(int pos);
        void updateLocation(int pos);
    }
}
