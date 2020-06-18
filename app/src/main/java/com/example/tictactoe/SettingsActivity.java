package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.tictactoe.databinding.ActivitySettingsBinding;

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

        adapter = new ThemeItemAdapter(this);

        binding.viewPager.setAdapter(adapter);
//        binding.viewPager.setPadding(40, 0, 40,  0);
        binding.viewPager.setPageMargin(20);

        binding.viewPager.setCurrentItem(playerPreferences.getInt("themePref", 0));

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                binding.setThemeBtn.setVisibility(View.VISIBLE);

                if (binding.viewPager.getCurrentItem() == playerPreferences.getInt("themePref", 0)){
                    binding.setThemeBtn.setVisibility(View.GONE);
                }

                switch (binding.viewPager.getCurrentItem()){
                    case 0:
                    case 2:
                        binding.settingsActivityBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                        break;
                    case 1:
                    default:
                        binding.settingsActivityBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        break;
                }

                switch (binding.viewPager.getCurrentItem()){
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

                binding.settingsActivityBackground.setImageResource(backgroundResource[binding.viewPager.getCurrentItem()]);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (playerPreferences.getString("playerName", null) != null)
            binding.userName.setText(playerPreferences.getString("playerName", null));

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
                    if (binding.viewPager.getCurrentItem() == 0 || binding.viewPager.getCurrentItem() == 1)
                    {
                        binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create);
                    }else if (binding.viewPager.getCurrentItem() == 2){
                        binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create_black);
                    }
                    playerNameEdited = false;
                } else {
                    binding.userName.setEnabled(true);
                    if (binding.viewPager.getCurrentItem() == 0 || binding.viewPager.getCurrentItem() == 1)
                    {
                        binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check);
                    }else if (binding.viewPager.getCurrentItem() == 2){
                        binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check_black);
                    }
                    playerNameEdited = true;
                }
            }
        });

        binding.setThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("themePref", binding.viewPager.getCurrentItem());
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