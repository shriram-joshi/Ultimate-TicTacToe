package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.databinding.ActivityMainPassAndPlayBinding;

public class MainActivityPassAndPlay extends AppCompatActivity {

    private ActivityMainPassAndPlayBinding binding;

    SharedPreferences playerPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPassAndPlayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);

        switch (playerPreferences.getInt("themePref",0)){
            case 0:
                binding.mainActivityPnpBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                setTheme(R.drawable.wooden_background,R.drawable.board_background_template_wooden,R.color.white, false);
                break;
            case 1:
                binding.mainActivityPnpBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setTheme(R.drawable.space_background,R.drawable.board_background_template_space, R.color.white, false);
                break;
            case 2:
                binding.mainActivityPnpBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setTheme(R.drawable.ocean_background,R.drawable.board_background_template_ocean,  R.color.black, true);
                break;
        }

        binding.leaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityPassAndPlay.this,StartGameActivity.class));
                finish();
            }
        });

    }

    private void setTheme(int background, int boardBackground, int textColour, boolean changeOtherColour) {
        GradientDrawable backgroundStroke;

        binding.mainActivityPnpBackground.setImageResource(background);
        binding.boardBackground.setBackgroundResource(boardBackground);
        binding.b11.setTextColor(getResources().getColor(textColour));
        binding.b12.setTextColor(getResources().getColor(textColour));
        binding.b13.setTextColor(getResources().getColor(textColour));
        binding.b21.setTextColor(getResources().getColor(textColour));
        binding.b22.setTextColor(getResources().getColor(textColour));
        binding.b23.setTextColor(getResources().getColor(textColour));
        binding.b31.setTextColor(getResources().getColor(textColour));
        binding.b32.setTextColor(getResources().getColor(textColour));
        binding.b33.setTextColor(getResources().getColor(textColour));
        backgroundStroke = (GradientDrawable)binding.turnRoundBackground.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));
        backgroundStroke = (GradientDrawable)binding.scoresBackground.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));
        backgroundStroke = (GradientDrawable)binding.youText.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));
        backgroundStroke = (GradientDrawable)binding.drawsText.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));
        backgroundStroke = (GradientDrawable)binding.opponentsNameTv.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));
        backgroundStroke = (GradientDrawable)binding.msg.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));
        backgroundStroke = (GradientDrawable)binding.nextRoundBtn.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));

        if (changeOtherColour){
            VectorDrawable drawable;

            binding.nextRoundBtn.setTextColor(getResources().getColor(textColour));
            binding.turnText.setTextColor(getResources().getColor(textColour));
            binding.turnTv.setTextColor(getResources().getColor(textColour));
            binding.roundText.setTextColor(getResources().getColor(textColour));
            binding.roundTv.setTextColor(getResources().getColor(textColour));
            binding.scoreText.setTextColor(getResources().getColor(textColour));
            binding.youText.setTextColor(getResources().getColor(textColour));
            binding.opponentsNameTv.setTextColor(getResources().getColor(textColour));
            binding.myScoreTv.setTextColor(getResources().getColor(textColour));
            binding.opponentsScoreTv.setTextColor(getResources().getColor(textColour));
            binding.drawsText.setTextColor(getResources().getColor(textColour));
            binding.drawScoreTv.setTextColor(getResources().getColor(textColour));
            binding.msg.setTextColor(getResources().getColor(textColour));
            drawable = (VectorDrawable) binding.leaveGame.getDrawable();
            drawable.setTint(getResources().getColor(textColour));
        }
    }
}