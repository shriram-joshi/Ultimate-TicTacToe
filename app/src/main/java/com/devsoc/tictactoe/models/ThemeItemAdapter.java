package com.devsoc.tictactoe.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsoc.tictactoe.R;

import java.util.ArrayList;
import java.util.List;

public class ThemeItemAdapter extends RecyclerView.Adapter<ThemeItemAdapter.ThemeItemViewHolder> {

    private List<Integer> item;

    public ThemeItemAdapter() {
        item = new ArrayList<>();
        item.add(R.drawable.board_background_template_wooden);
        item.add(R.drawable.board_background_template_space);
        item.add(R.drawable.board_background_template_ocean);
        item.add(R.drawable.board_background_template_black);
    }

    @NonNull
    @Override
    public ThemeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThemeItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeItemViewHolder holder, int position) {
            holder.populate(item.get(position));
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ThemeItemViewHolder extends RecyclerView.ViewHolder{

        ImageView boardImage;

        public ThemeItemViewHolder(@NonNull View itemView) {
            super(itemView);

            boardImage = itemView.findViewById(R.id.theme_board_image);
        }

        public void populate(Integer themeItem) {
            boardImage.setImageResource(themeItem);
        }
    }
}

