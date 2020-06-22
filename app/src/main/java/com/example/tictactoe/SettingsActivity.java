package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tictactoe.databinding.ActivitySettingsBinding;
import com.example.tictactoe.models.ThemeItemAdapter;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    SharedPreferences playerPreferences;
    SharedPreferences.Editor editor;
    boolean playerNameEdited=false;

    ThemeItemAdapter adapter;
    Integer[] backgroundResource = {
            R.drawable.wooden_background
            ,R.drawable.space_background
            ,R.drawable.ocean_background
    };
    VectorDrawable drawable;
    GradientDrawable backgroundStroke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);
        editor = playerPreferences.edit();
        Toast.makeText(this, "Swipe for more themes", Toast.LENGTH_LONG).show();

        binding.playerName.setText(playerPreferences.getString("playerName", "Friend"));

        adapter = new ThemeItemAdapter();

        int padding = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        binding.viewPager2.setOffscreenPageLimit(1);
        Log.i("Child Count", String.valueOf(binding.viewPager2.getChildCount()));
        if (binding.viewPager2.getChildAt(0) instanceof RecyclerView){
            RecyclerView recyclerView = (RecyclerView)binding.viewPager2.getChildAt(0);
            recyclerView.setPadding(padding, 0, padding, 0);
            recyclerView.setClipToPadding(false);
        }
        else Log.e("Error", "Child view not recycler view");

        binding.viewPager2.setAdapter(adapter);

        binding.viewPager2.setCurrentItem(playerPreferences.getInt("themePref", 0));

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (binding.viewPager2.getCurrentItem() == playerPreferences.getInt("themePref", 0))
                    binding.setThemeBtn.setVisibility(View.GONE);
                else
                    binding.setThemeBtn.setVisibility(View.VISIBLE);

                //for background scale type
                switch (binding.viewPager2.getCurrentItem()){
                    case 0:
                    case 2:
                        binding.settingsActivityBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                        break;
                    case 1:
                    default:
                        binding.settingsActivityBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        break;
                }

                //for button and textView UI changes
                switch (binding.viewPager2.getCurrentItem()){
                    case 0:
                    case 1:
                        binding.displayNameText.setTextColor(getResources().getColor(R.color.hint_light));
                        binding.playerName.setTextColor(getResources().getColor(R.color.white));
                        binding.settingsText.setTextColor(getResources().getColor(R.color.white));
                        binding.themeText.setTextColor(getResources().getColor(R.color.white));

                        drawable = (VectorDrawable)binding.leaveSettingsBtn.getDrawable();
                        drawable.setTint(getResources().getColor(R.color.white));
                        binding.setThemeBtn.setImageResource(R.drawable.ic_check);
                        drawable = (VectorDrawable)binding.setThemeBtn.getDrawable();
                        drawable.setTint(getResources().getColor(R.color.white));

                        if (playerNameEdited){
                            binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check);
                        }else{
                            binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create);
                        }
                        drawable = (VectorDrawable)binding.editPlayerNameBtn.getDrawable();
                        drawable.setTint(getResources().getColor(R.color.white));

                        backgroundStroke = (GradientDrawable)binding.leaveSettingsBtn.getBackground();
                        backgroundStroke.setStroke(2, getResources().getColor(R.color.white));
                        backgroundStroke = (GradientDrawable)binding.editPlayerNameBtn.getBackground();
                        backgroundStroke.setStroke(2, getResources().getColor(R.color.white));
                        backgroundStroke = (GradientDrawable)binding.setThemeBtn.getBackground();
                        backgroundStroke.setStroke(2, getResources().getColor(R.color.white));
                        backgroundStroke = (GradientDrawable)binding.displayNameLinearLayout.getBackground();
                        backgroundStroke.setStroke(2, getResources().getColor(R.color.white));

                        break;
                    case 2:
                        binding.displayNameText.setTextColor(getResources().getColor(R.color.hint_dark));
                        binding.playerName.setTextColor(getResources().getColor(R.color.black));
                        binding.settingsText.setTextColor(getResources().getColor(R.color.black));
                        binding.themeText.setTextColor(getResources().getColor(R.color.black));

                        drawable = (VectorDrawable)binding.leaveSettingsBtn.getDrawable();
                        drawable.setTint(getResources().getColor(R.color.black));
                        binding.setThemeBtn.setImageResource(R.drawable.ic_check);
                        drawable = (VectorDrawable)binding.setThemeBtn.getDrawable();
                        drawable.setTint(getResources().getColor(R.color.black));
                        if (playerNameEdited){
                            binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check);
                        }else{
                            binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create);
                        }
                        drawable = (VectorDrawable)binding.editPlayerNameBtn.getDrawable();
                        drawable.setTint(getResources().getColor(R.color.black));

                        backgroundStroke = (GradientDrawable)binding.leaveSettingsBtn.getBackground();
                        backgroundStroke.setStroke(2, getResources().getColor(R.color.black));
                        backgroundStroke = (GradientDrawable)binding.editPlayerNameBtn.getBackground();
                        backgroundStroke.setStroke(2, getResources().getColor(R.color.black));
                        backgroundStroke = (GradientDrawable)binding.setThemeBtn.getBackground();
                        backgroundStroke.setStroke(2, getResources().getColor(R.color.black));
                        backgroundStroke = (GradientDrawable)binding.displayNameLinearLayout.getBackground();
                        backgroundStroke.setStroke(2, getResources().getColor(R.color.black));

                        break;
                }

                binding.settingsActivityBackground.setImageResource(backgroundResource[binding.viewPager2.getCurrentItem()]);
            }
        });

        binding.editPlayerNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerNameEdited){
                    if (!(binding.playerName.getText().toString().equals(playerPreferences.getString("playerName", "Friend")))) {
                        editor.putString("playerName", binding.playerName.getText().toString());
                        editor.apply();
                        Toast.makeText(SettingsActivity.this, "Display name saved!", Toast.LENGTH_SHORT).show();
                    }

                    if (binding.playerName.getText().toString().isEmpty()){
                        binding.playerName.setText("Friend");
                        editor.putString("playerName", "Friend");
                        editor.apply();
                    }

                    binding.playerName.setEnabled(false);
                    binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create);
                    drawable = (VectorDrawable)binding.editPlayerNameBtn.getDrawable();

                    playerNameEdited = false;
                } else {
                    binding.playerName.setEnabled(true);
                    binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check);
                    drawable = (VectorDrawable)binding.editPlayerNameBtn.getDrawable();
                    playerNameEdited = true;
                }
                if (binding.viewPager2.getCurrentItem() == 0 || binding.viewPager2.getCurrentItem() == 1)
                {
                    drawable.setTint(getResources().getColor(R.color.white));
                }else if (binding.viewPager2.getCurrentItem() == 2){
                    drawable.setTint(getResources().getColor(R.color.black));
                }
            }
        });

        binding.setThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("themePref", binding.viewPager2.getCurrentItem());
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Theme has been set!", Toast.LENGTH_LONG).show();
                binding.setThemeBtn.setVisibility(View.GONE);
            }
        });

        binding.leaveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, StartGameActivity.class));
                finish();
            }
        });
    }
}