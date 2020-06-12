package com.example.tictactoe;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.tictactoe.models.ThemeItem;

import java.util.ArrayList;
import java.util.List;

public class ThemeItemAdapter extends PagerAdapter {

    private List<ThemeItem> item;
    private LayoutInflater layoutInflater;
    private Context context;

    public ThemeItemAdapter(Context context) {
        item = new ArrayList<>();
        item.add(new ThemeItem(R.drawable.board_background_template_wooden));
        item.add(new ThemeItem(R.drawable.board_background_template_space));
        this.context = context;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.theme_item, container, false);

        ImageView themeBoardImage;

        themeBoardImage = view.findViewById(R.id.theme_board_image);
        themeBoardImage.setImageResource(item.get(position).getTheme_image());

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}

