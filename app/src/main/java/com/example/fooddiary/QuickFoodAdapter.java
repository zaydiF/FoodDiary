package com.example.fooddiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuickFoodAdapter extends RecyclerView.Adapter<QuickFoodAdapter.ViewHolder> {

    private List<QuickFoodItem> foodList;
    private OnQuickFoodClickListener listener;

    public interface OnQuickFoodClickListener {
        void onAddClick(QuickFoodItem food);
        void onDeleteClick(QuickFoodItem food);
        void onEditClick(QuickFoodItem food);
    }

    public QuickFoodAdapter(List<QuickFoodItem> foodList, OnQuickFoodClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickFoodItem food = foodList.get(position);

        holder.tvEmoji.setText(food.getEmoji());
        holder.tvName.setText(food.getName());
        holder.tvDetails.setText(String.format("%dг • %d ккал", food.getWeight(), food.getCalories()));
        holder.tvNutrition.setText(String.format("Б: %dг, У: %dг, Ж: %dг",
                food.getProtein(), food.getCarbs(), food.getFat()));

        // Кнопка добавления
        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddClick(food);
            }
        });

        // Кнопка редактирования (для всех продуктов)
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(food);
            }
        });

        // Кнопка удаления (для всех продуктов)
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEmoji, tvName, tvDetails, tvNutrition;
        public Button btnAdd, btnEdit, btnDelete;

        public ViewHolder(View view) {
            super(view);
            tvEmoji = view.findViewById(R.id.tvEmoji);
            tvName = view.findViewById(R.id.tvName);
            tvDetails = view.findViewById(R.id.tvDetails);
            tvNutrition = view.findViewById(R.id.tvNutrition);
            btnAdd = view.findViewById(R.id.btnAdd);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}