package com.example.fooddiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

    private List<String> emojis;
    private String selectedEmoji;
    private OnEmojiClickListener listener;

    public interface OnEmojiClickListener {
        void onEmojiClick(String emoji);
    }

    public EmojiAdapter(List<String> emojis, String selectedEmoji, OnEmojiClickListener listener) {
        this.emojis = emojis;
        this.selectedEmoji = selectedEmoji;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emoji, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String emoji = emojis.get(position);
        holder.tvEmoji.setText(emoji);

        if (emoji.equals(selectedEmoji)) {
            holder.tvEmoji.setBackgroundResource(R.drawable.emoji_selected);
        } else {
            holder.tvEmoji.setBackgroundResource(R.drawable.emoji_unselected);
        }

        holder.tvEmoji.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmojiClick(emoji);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return emojis.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEmoji;

        public ViewHolder(View view) {
            super(view);
            tvEmoji = view.findViewById(R.id.tvEmoji);
        }
    }
}