package com.example.fooddiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodItems;
    private OnFoodItemClickListener listener;

    public interface OnFoodItemClickListener {
        void onDeleteClick(int position);
    }

    public FoodAdapter(OnFoodItemClickListener listener) {
        this.foodItems = new ArrayList<>();
        this.listener = listener;
    }



    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems != null ? foodItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);
        holder.tvFoodName.setText(item.getName());
        holder.tvFoodDetails.setText(String.format("%dg • %d ккал • Б: %dг, Ж: %dг, У: %dг",
                item.getWeight(), item.getCalories(), item.getProtein(), item.getFat(), item.getCarbs()));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodDetails;
        ImageButton btnDelete;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDetails = itemView.findViewById(R.id.tvFoodDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}