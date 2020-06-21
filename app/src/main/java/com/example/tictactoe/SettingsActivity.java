package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);
        editor = playerPreferences.edit();
        Toast.makeText(this, "Swipe for more themes", Toast.LENGTH_LONG).show();

        adapter = new ThemeItemAdapter();

        binding.viewPager2.setAdapter(adapter);

        binding.viewPager2.setCurrentItem(playerPreferences.getInt("themePref", 0));
//        binding.viewPager2.setPadding(40,0,40,0);
//        binding.viewPager2.setClipToPadding(false);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (binding.viewPager2.getCurrentItem() == playerPreferences.getInt("themePref", 0))
                    binding.setThemeBtn.setVisibility(View.GONE);
                else
                    binding.setThemeBtn.setVisibility(View.VISIBLE);

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

                switch (binding.viewPager2.getCurrentItem()){
                    case 0:
                    case 1:
                        binding.userName.setTextColor(getResources().getColor(R.color.white));
                        binding.leaveSettingsBtn.setImageResource(R.drawable.ic_arrow_back);
                        binding.setThemeBtn.setImageResource(R.drawable.ic_check);
                        if (playerNameEdited){
                            binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check);
                        }else{
                            binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create);
                        }
                        binding.displayNameTil.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.hint_light)));
                        break;
                    case 2:
                        binding.userName.setTextColor(getResources().getColor(R.color.black));
                        binding.leaveSettingsBtn.setImageResource(R.drawable.ic_arrow_back_black);
                        binding.setThemeBtn.setImageResource(R.drawable.ic_check_black);
                        if (playerNameEdited){
                            binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check_black);
                        }else{
                            binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create_black);
                        }
                        binding.displayNameTil.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.hint_dark)));
                        break;
                }

                binding.settingsActivityBackground.setImageResource(backgroundResource[binding.viewPager2.getCurrentItem()]);
            }
        });




        if (playerPreferences.getString("playerName", "Friend") != "Friend")
            binding.userName.setText(playerPreferences.getString("playerName", "Friend"));

        binding.editPlayerNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerNameEdited){
                    if (!(binding.userName.getText().toString().equals(playerPreferences.getString("playerName", null)))) {
                        editor.putString("playerName", binding.userName.getText().toString());
                        editor.apply();
                        Toast.makeText(SettingsActivity.this, "Display name saved!", Toast.LENGTH_SHORT).show();
                    }

                    binding.userName.setEnabled(false);
                    if (binding.viewPager2.getCurrentItem() == 0 || binding.viewPager2.getCurrentItem() == 1)
                    {
                        binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create);
                    }else if (binding.viewPager2.getCurrentItem() == 2){
                        binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create_black);
                    }
                    playerNameEdited = false;
                } else {
                    binding.userName.setEnabled(true);
                    if (binding.viewPager2.getCurrentItem() == 0 || binding.viewPager2.getCurrentItem() == 1)
                    {
                        binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check);
                    }else if (binding.viewPager2.getCurrentItem() == 2){
                        binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check_black);
                    }
                    playerNameEdited = true;
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