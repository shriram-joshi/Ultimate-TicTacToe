package com.example.tictactoe;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.databinding.ActivityMainPassAndPlayBinding;

public class MainActivityPassAndPlay extends AppCompatActivity {

    private ActivityMainPassAndPlayBinding binding;

    Integer gameState=1;
    String firstPlayer;

    SharedPreferences playerPreferences;

    MediaPlayer buttonClickedO, buttonClickedX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPassAndPlayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);

        buttonClickedO = MediaPlayer.create(MainActivityPassAndPlay.this, R.raw.board_button_o_click);
        buttonClickedX = MediaPlayer.create(MainActivityPassAndPlay.this, R.raw.board_button_x_click);

        firstPlayer = playerPreferences.getString("playerName", "Host");

        binding.youText.setText(playerPreferences.getString("playerName", "Host"));
        binding.opponentsNameTv.setText(playerPreferences.getString("pnp:opponentName", "Friend"));
        binding.turnTv.setText(playerPreferences.getString("playerName", "Host"));

        binding.msg.setText(playerPreferences.getString("playerName", "Host") + " plays first as X");

        switch (playerPreferences.getInt("themePref",0)){
            case 0:
                binding.mainActivityPnpBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                setTheme(R.drawable.wooden_background,R.drawable.board_background_template_wooden,R.color.black, R.color.white);
                break;
            case 1:
                binding.mainActivityPnpBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setTheme(R.drawable.space_background,R.drawable.board_background_template_space, R.color.white, R.color.white);
                break;
            case 2:
                binding.mainActivityPnpBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setTheme(R.drawable.ocean_background,R.drawable.board_background_template_ocean,  R.color.black, R.color.black);
                break;
        }

        //9 board buttons on click listeners
        {
            binding.b11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b11, 1);
                }
            });
            binding.b12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b12, 2);
                }
            });
            binding.b13.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b13, 3);
                }
            });
            binding.b21.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b21, 4);
                }
            });
            binding.b22.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b22, 5);
                }
            });
            binding.b23.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b23, 6);
                }
            });
            binding.b31.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b31, 7);
                }
            });
            binding.b32.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b32, 8);
                }
            });
            binding.b33.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b33, 9);
                }
            });
        }

        binding.leaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog leaveGameDialog = new Dialog(MainActivityPassAndPlay.this);
                leaveGameDialog.setContentView(R.layout.dialogbox_leave_game);
                leaveGameDialog.setCancelable(true);
                Button yes = leaveGameDialog.findViewById(R.id.yes_leave), no = leaveGameDialog.findViewById(R.id.dont_leave);

                leaveGameDialog.show();

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivityPassAndPlay.this,StartGameActivity.class));
                        finish();
                        leaveGameDialog.dismiss();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveGameDialog.dismiss();
                    }
                });
            }
        });

        binding.nextRoundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.nextRoundBtn.setVisibility(View.GONE);
                binding.roundTv.setText(String.valueOf(Integer.parseInt(binding.roundTv.getText().toString())+1));
                gameState = 1;

                if (firstPlayer.equals(playerPreferences.getString("playerName", "Host"))){
                    binding.msg.setText(playerPreferences.getString("pnpGame:opponentName", "Friend") + " plays first as X");
                    binding.turnTv.setText(playerPreferences.getString("pnpGame:opponentName", "Friend"));
                    firstPlayer = playerPreferences.getString("pnpGame:opponentName", "Friend");
                } else {
                    binding.turnTv.setText(playerPreferences.getString("playerName", "Host"));
                    binding.msg.setText(playerPreferences.getString("playerName", "Host") + " plays first as X");
                    firstPlayer = playerPreferences.getString("playerName", "Host");
                }

                binding.b11.setText("");
                binding.b12.setText("");
                binding.b13.setText("");
                binding.b21.setText("");
                binding.b22.setText("");
                binding.b23.setText("");
                binding.b31.setText("");
                binding.b32.setText("");
                binding.b33.setText("");
                binding.msg.setVisibility(View.VISIBLE);
                binding.b11.setEnabled(true);
                binding.b12.setEnabled(true);
                binding.b13.setEnabled(true);
                binding.b21.setEnabled(true);
                binding.b22.setEnabled(true);
                binding.b23.setEnabled(true);
                binding.b31.setEnabled(true);
                binding.b32.setEnabled(true);
                binding.b33.setEnabled(true);
            }
        });

    }



    private void setTheme(int background, int boardBackground, int boardTextColour, int otherTextColour) {
        GradientDrawable backgroundStroke;
        Drawable drawable;

        binding.mainActivityPnpBackground.setImageResource(background);
        binding.boardBackground.setBackgroundResource(boardBackground);

        binding.b11.setTextColor(getResources().getColor(boardTextColour));
        binding.b12.setTextColor(getResources().getColor(boardTextColour));
        binding.b13.setTextColor(getResources().getColor(boardTextColour));
        binding.b21.setTextColor(getResources().getColor(boardTextColour));
        binding.b22.setTextColor(getResources().getColor(boardTextColour));
        binding.b23.setTextColor(getResources().getColor(boardTextColour));
        binding.b31.setTextColor(getResources().getColor(boardTextColour));
        binding.b32.setTextColor(getResources().getColor(boardTextColour));
        binding.b33.setTextColor(getResources().getColor(boardTextColour));

        binding.nextRoundBtn.setTextColor(getResources().getColor(otherTextColour));
        binding.turnText.setTextColor(getResources().getColor(otherTextColour));
        binding.turnTv.setTextColor(getResources().getColor(otherTextColour));
        binding.roundText.setTextColor(getResources().getColor(otherTextColour));
        binding.roundTv.setTextColor(getResources().getColor(otherTextColour));
        binding.scoreText.setTextColor(getResources().getColor(otherTextColour));
        binding.youText.setTextColor(getResources().getColor(otherTextColour));
        binding.opponentsNameTv.setTextColor(getResources().getColor(otherTextColour));
        binding.myScoreTv.setTextColor(getResources().getColor(otherTextColour));
        binding.opponentsScoreTv.setTextColor(getResources().getColor(otherTextColour));
        binding.drawsText.setTextColor(getResources().getColor(otherTextColour));
        binding.drawScoreTv.setTextColor(getResources().getColor(otherTextColour));
        binding.msg.setTextColor(getResources().getColor(otherTextColour));

        backgroundStroke = (GradientDrawable)binding.turnRoundBackground.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(otherTextColour));
        backgroundStroke = (GradientDrawable)binding.scoresBackground.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(otherTextColour));
        backgroundStroke.setStroke(2, getResources().getColor(otherTextColour));
        backgroundStroke = (GradientDrawable)binding.msg.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(otherTextColour));
        backgroundStroke = (GradientDrawable)binding.nextRoundBtn.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(otherTextColour));

        drawable = (Drawable) binding.leaveGame.getDrawable();
        drawable.setTint(getResources().getColor(otherTextColour));
    }

    private void clickedButton(Button button, int id) {
        if (gameState == 1)
            binding.msg.setVisibility(View.GONE);

        button.setEnabled(false);

        if (gameState%2 == 0){
            button.setText("O");
            if (playerPreferences.getBoolean("playSounds", true))
                buttonClickedO.start();
        } else {
            button.setText("X");
            if (playerPreferences.getBoolean("playSounds", true))
                buttonClickedX.start();
        }
        if (win()){
            stopGame();
            binding.nextRoundBtn.setVisibility(View.VISIBLE);
            binding.msg.setVisibility(View.VISIBLE);
            binding.msg.setText(binding.turnTv.getText().toString() + " wins the round!");
            if (binding.turnTv.getText().toString().equals(binding.youText.getText().toString()))
                binding.myScoreTv.setText(String.valueOf(Integer.parseInt(binding.myScoreTv.getText().toString())+1));
            else
                binding.opponentsScoreTv.setText(String.valueOf(Integer.parseInt(binding.opponentsScoreTv.getText().toString())+1));

        } else if (gameState == 9) {
            stopGame();
            binding.nextRoundBtn.setVisibility(View.VISIBLE);
            binding.msg.setVisibility(View.VISIBLE);
            binding.msg.setText(R.string.tie_text);
            binding.drawScoreTv.setText(String.valueOf(Integer.parseInt(binding.drawScoreTv.getText().toString())+1));
        } else {
            gameState++;
            if (binding.turnTv.getText().toString().equals(binding.youText.getText().toString()))
                binding.turnTv.setText(binding.opponentsNameTv.getText().toString());
            else
                binding.turnTv.setText(binding.youText.getText().toString());
        }
    }

    public boolean win(){
        return(
                ((!binding.b11.isEnabled() && !binding.b12.isEnabled() && !binding.b13.isEnabled())
                        && (binding.b11.getText() == binding.b12.getText() && binding.b12.getText() == binding.b13.getText()))
                        || ((!binding.b21.isEnabled() && !binding.b22.isEnabled() && !binding.b23.isEnabled())
                        && (binding.b21.getText() == binding.b22.getText() && binding.b22.getText() == binding.b23.getText()))
                        || ((!binding.b31.isEnabled() && !binding.b32.isEnabled() && !binding.b33.isEnabled())
                        && (binding.b31.getText() == binding.b32.getText() && binding.b32.getText() == binding.b33.getText()))
                        || ((!binding.b11.isEnabled() && !binding.b21.isEnabled() && !binding.b31.isEnabled())
                        && (binding.b11.getText() == binding.b21.getText() && binding.b21.getText() == binding.b31.getText()))
                        || ((!binding.b12.isEnabled() && !binding.b22.isEnabled() && !binding.b32.isEnabled())
                        && (binding.b12.getText() == binding.b22.getText() && binding.b22.getText() == binding.b32.getText()))
                        || ((!binding.b13.isEnabled() && !binding.b23.isEnabled() && !binding.b33.isEnabled())
                        && (binding.b13.getText() == binding.b23.getText() && binding.b23.getText() == binding.b33.getText()))
                        || ((!binding.b11.isEnabled() && !binding.b22.isEnabled() && !binding.b33.isEnabled())
                        && (binding.b11.getText() == binding.b22.getText() && binding.b22.getText() == binding.b33.getText()))
                        || ((!binding.b13.isEnabled() && !binding.b22.isEnabled() && !binding.b31.isEnabled())
                        && (binding.b13.getText() == binding.b22.getText() && binding.b22.getText() == binding.b31.getText()))
        );
    }

    private void stopGame() {
        binding.b11.setEnabled(false);
        binding.b12.setEnabled(false);
        binding.b13.setEnabled(false);
        binding.b21.setEnabled(false);
        binding.b22.setEnabled(false);
        binding.b23.setEnabled(false);
        binding.b31.setEnabled(false);
        binding.b32.setEnabled(false);
        binding.b33.setEnabled(false);
    }
}