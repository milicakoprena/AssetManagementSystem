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
import com.example.assetmanagementsystem.assetdb.model.Employee;

import java.util.List;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.BeanHolder> {
    private List<Employee> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnEmployeeItemClick onEmployeeItemClick;

    public EmployeesAdapter(List<Employee> list, Context context, OnEmployeeItemClick onEmployeeItemClick) {
        this.list = list;
        this.context = context;
        this.onEmployeeItemClick = onEmployeeItemClick;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BeanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.employee_list_item, parent, false);
        return new BeanHolder(view, onEmployeeItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull BeanHolder holder, int position) {
        Log.e("bind", "onBindViewHolder: " + list.get(position));
        holder.textViewName.setText(list.get(position).getFirstName() + " " + list.get(position).getLastName());
        holder.textViewEmail.setText(list.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BeanHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewName;
        TextView textViewEmail;
        ImageButton buttonDelete;
        private OnEmployeeItemClick onEmployeeItemClick;

        public BeanHolder(View itemView, OnEmployeeItemClick listener) {
            super(itemView);
            this.onEmployeeItemClick = listener;
            itemView.setOnClickListener(this);
            textViewName = itemView.findViewById(R.id.item_name);
            textViewEmail = itemView.findViewById(R.id.item_email);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onEmployeeItemClick.deleteEmployee(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (onEmployeeItemClick != null) {
                onEmployeeItemClick.onEmployeeClick(getAdapterPosition());
            }
        }
    }

    public interface OnEmployeeItemClick {
        void onEmployeeClick(int pos);
        void deleteEmployee(int pos);
    }
}
