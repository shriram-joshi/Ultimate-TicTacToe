package com.example.tictactoe;

import android.animation.ArgbEvaluator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.tictactoe.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    SharedPreferences userPrefs;
    SharedPreferences.Editor editor;
    boolean playerNameEdited=false;

    ThemeItemAdapter adapter;
    Integer[] backgroundResource = {
            R.drawable.wooden_background,
            R.drawable.space_background
    };
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userPrefs = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);
        editor = userPrefs.edit();

        adapter = new ThemeItemAdapter(this);

        binding.viewPager.setAdapter(adapter);

        binding.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if ((position < adapter.getCount()-1) && position < (backgroundResource.length-1)){
//                    binding.settingsActivityBackground
//                            .setBackgroundResource((Integer)argbEvaluator.evaluate(positionOffset
//                            , backgroundResource[position]
//                            , backgroundResource[position + 1]));
//                } else {
//                    binding.settingsActivityBackground.setBackgroundResource(backgroundResource[backgroundResource.length - 1]);
//                }
                binding.settingsActivityBackground.setBackgroundResource(backgroundResource[position]);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (userPrefs.getString("playerName", null) != null)
            binding.userName.setText(userPrefs.getString("playerName", null));

        binding.editPlayerNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerNameEdited){

                    if (!(binding.userName.getText().toString() == userPrefs.getString("playerName", null))) {
                        editor.putString("playerName", binding.userName.getText().toString());
                        editor.apply();
                        Toast.makeText(SettingsActivity.this, "Display name saved!", Toast.LENGTH_SHORT).show();
                    }

                    binding.userName.setEnabled(false);
                    binding.editPlayerNameBtn.setImageResource(R.drawable.ic_create);
                    playerNameEdited = false;
                } else {
                    binding.userName.setEnabled(true);
                    binding.editPlayerNameBtn.setImageResource(R.drawable.ic_check);
                    playerNameEdited = true;
                }
            }
        });

        binding.leaveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}