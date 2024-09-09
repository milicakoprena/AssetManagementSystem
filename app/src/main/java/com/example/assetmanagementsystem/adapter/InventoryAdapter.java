package com.example.assetmanagementsystem.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.assetmanagementsystem.R;
import com.example.assetmanagementsystem.assetdb.helpers.InventoryDetails;
import com.example.assetmanagementsystem.assetdb.model.Employee;
import com.example.assetmanagementsystem.glide.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.BeanHolder>{
    private List<InventoryDetails> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnInventoryItemClick onInventoryItemClick;

    public InventoryAdapter(List<InventoryDetails> list, Context context, OnInventoryItemClick onInventoryItemClick) {
        this.list = list;
        this.context = context;
        this.onInventoryItemClick = onInventoryItemClick;
        layoutInflater = LayoutInflater.from(context);
    }

    public void updateData(List<InventoryDetails> newInventoryList) {
        this.list = newInventoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BeanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.inventory_list_item, parent, false);
        return new BeanHolder(view, onInventoryItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull BeanHolder holder, int position) {
        Log.e("bind", "onBindViewHolder: " + list.get(position));
        holder.textViewBarcode.setText(String.valueOf(list.get(position).getAsset().getBarcode()));
        holder.textViewName.setText(list.get(position).getAsset().getName());
        holder.textViewEmployee.setText(list.get(position).getOldEmployee() + " > " + list.get(position).getNewEmployee());
        holder.textViewLocation.setText(list.get(position).getOldLocation() + " > " + list.get(position).getNewLocation());
        if (list.get(position).getAsset().getImageUrl() != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(list.get(position).getAsset().getImageUrl());

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop();

            GlideApp.with(holder.itemView.getContext())
                    .load(storageReference)
                    .apply(requestOptions)
                    .into(holder.assetImage);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BeanHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewBarcode;
        TextView textViewName;
        TextView textViewEmployee;
        TextView textViewLocation;
        ImageButton buttonDelete;
        ImageView assetImage;
        private OnInventoryItemClick onInventoryItemClick;

        public BeanHolder(View itemView, OnInventoryItemClick listener) {
            super(itemView);
            this.onInventoryItemClick = listener;
            itemView.setOnClickListener(this);
            textViewBarcode = itemView.findViewById(R.id.item_barcode);
            textViewName = itemView.findViewById(R.id.item_name);
            textViewEmployee = itemView.findViewById(R.id.item_employee);
            textViewLocation = itemView.findViewById(R.id.item_location);
            assetImage = itemView.findViewById(R.id.assetImage);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onInventoryItemClick.deleteInventory(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (onInventoryItemClick != null) {
                onInventoryItemClick.onInventoryClick(getAdapterPosition());
            }
        }
    }

    public interface OnInventoryItemClick {
        void onInventoryClick(int pos);
        void deleteInventory(int pos);
    }
}
