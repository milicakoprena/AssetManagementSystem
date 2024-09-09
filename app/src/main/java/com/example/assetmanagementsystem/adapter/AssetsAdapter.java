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
import com.example.assetmanagementsystem.assetdb.model.Asset;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import com.example.assetmanagementsystem.glide.GlideApp;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.BeanHolder> {
    private List<Asset> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnAssetItemClick onAssetItemClick;

    public AssetsAdapter(List<Asset> list, Context context, OnAssetItemClick onAssetItemClick) {
        this.list = list;
        this.context = context;
        this.onAssetItemClick = onAssetItemClick;
        layoutInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Asset> newAssetList) {
        this.list = newAssetList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BeanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.asset_list_item, parent, false);
        return new AssetsAdapter.BeanHolder(view, onAssetItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull BeanHolder holder, int position) {
        Log.e("bind", "onBindViewHolder: " + list.get(position));
        holder.textViewName.setText(list.get(position).getName());
        holder.textViewPrice.setText(list.get(position).getPrice() + " KM");
        holder.textViewCategory.setText(list.get(position).getCategory().getDisplayName());

        if (list.get(position).getImageUrl() != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(list.get(position).getImageUrl());

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop();

            GlideApp.with(holder.itemView.getContext())
                    .load(storageReference)
                    .apply(requestOptions)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BeanHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewCategory;
        ImageButton buttonDelete;
        private OnAssetItemClick onAssetItemClick;

        public BeanHolder(View itemView, OnAssetItemClick listener) {
            super(itemView);
            this.onAssetItemClick = listener;
            itemView.setOnClickListener(this);
            textViewName = itemView.findViewById(R.id.item_name);
            textViewPrice = itemView.findViewById(R.id.item_price);
            textViewCategory = itemView.findViewById(R.id.item_category);
            imageView = itemView.findViewById(R.id.item_image);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAssetItemClick.deleteAsset(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (onAssetItemClick != null) {
                onAssetItemClick.onAssetClick(getAdapterPosition());
            }
        }


    }

    public interface OnAssetItemClick {
        void onAssetClick(int pos);

        void deleteAsset(int pos);
    }
}
