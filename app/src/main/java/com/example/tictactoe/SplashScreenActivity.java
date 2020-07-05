package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.databinding.ActivitySplashScreenBinding;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences playerPreferences;

    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);

        switch (playerPreferences.getInt("themePref",0)){
            case 0:
            default:
                binding.splashScreenActivityBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                binding.splashScreenActivityBackground.setImageResource(R.drawable.wooden_background);
                binding.boardBackground.setImageResource(R.drawable.background_with_text_wooden);
                binding.copyrightText.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                binding.splashScreenActivityBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                binding.splashScreenActivityBackground.setImageResource(R.drawable.space_background);
                binding.boardBackground.setImageResource(R.drawable.background_with_text_space);
                binding.copyrightText.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                binding.splashScreenActivityBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                binding.splashScreenActivityBackground.setImageResource(R.drawable.ocean_background);
                binding.boardBackground.setImageResource(R.drawable.background_with_text_ocean);
                binding.copyrightText.setTextColor(getResources().getColor(R.color.black));
                break;
        }

        final Animation fadeInBoard = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.fade_in_1500ms);
        binding.animation.startAnimation(fadeInBoard);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, StartGameActivity.class));
                finish();
            }
        }, 2000);
    }
}