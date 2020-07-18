package com.devsoc.tictactoe.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devsoc.tictactoe.R;
import com.devsoc.tictactoe.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences playerPreferences;
    SharedPreferences.Editor editor;

    ActivitySplashScreenBinding binding;

    FirebaseFirestore createGame = FirebaseFirestore.getInstance();

    Intent rejoin, startGameActivity;

    //Time out for game in hours
    final int TIME_OUT_DURATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        rejoin = new Intent(SplashScreenActivity.this, MainActivityPlayOnline.class);
        startGameActivity = new Intent(SplashScreenActivity.this, StartGameActivity.class);

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);
        editor = playerPreferences.edit();

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
            case 3:
                binding.splashScreenActivityBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                binding.splashScreenActivityBackground.setImageResource(R.drawable.black_background);
                binding.boardBackground.setImageResource(R.drawable.background_with_text_black);
                binding.copyrightText.setTextColor(getResources().getColor(R.color.white));
                break;
        }

        final Animation fadeInBoard = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.fade_in_1500ms);
        binding.animation.startAnimation(fadeInBoard);

        if(playerPreferences.getBoolean("joinPending",false)) {
            createGame.collection("Active Games").document("G" + playerPreferences.getString("gameID", null))
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        if (documentSnapshot.getData() != null) {
                            Timestamp timestamp = Objects.requireNonNull((Timestamp)documentSnapshot.getData().get("timeStamp"));
                            Calendar timeOutTime = Calendar.getInstance();
                            timeOutTime.setTime(timestamp.toDate());
                            timeOutTime.add(Calendar.HOUR, TIME_OUT_DURATION);


                            if (new Date().before(timeOutTime.getTime())){
                                long temp = (long) documentSnapshot.getData().get("gameIsActive");
                                int gameIsActive = (int) temp;

                                switch (gameIsActive) {
                                    case 0:
                                        createGame.collection("Active Games")
                                                .document("G" + playerPreferences.getString("gameID", null))
                                                .delete();
                                        editor.putBoolean("joinPending", false);
                                        editor.putString("gameID", null);
                                        editor.apply();
                                        startActivity(startGameActivity);
                                        finish();
                                        break;
                                    case 1:
                                        Dialog askRejoin = new Dialog(SplashScreenActivity.this);
                                        askRejoin.setContentView(R.layout.dialogbox_rejoin_game);
                                        askRejoin.setCancelable(false);
                                        askRejoin.show();
                                        Toast.makeText(SplashScreenActivity.this, "You previous game is still active!", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            } else{
                                startActivity(startGameActivity);
                                finish();
                            }
                        } else {
                            Toast.makeText(SplashScreenActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            editor.putBoolean("joinPending", false);
                            editor.putString("gameID", null);
                            editor.apply();
                            startActivity(startGameActivity);
                            finish();
                        }
                    }else{
                        editor.putBoolean("joinPending", false);
                        editor.putString("gameID", null);
                        editor.apply();
                        startActivity(startGameActivity);
                        finish();
                    }
                }
            });
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(startGameActivity);
                    finish();
                }
            }, 2000);
        }
    }

    public void rejoinGameYes(View view) {
        rejoin.putExtra("gameID", playerPreferences.getString("gameID", null));
        rejoin.putExtra("turn", true);
        rejoin.putExtra("playerName", playerPreferences.getString("playerName", "Host"));
        createGame.collection("Active Games").document("G" + playerPreferences.getString("gameID", null))
                .update("gameIsActive", 2);

        editor.putBoolean("joinPending", false);
        editor.apply();
        startActivity(rejoin);
        finish();
    }
    public void rejoinGameNo(View view) {
        createGame.collection("Active Games")
                .document("G" + playerPreferences.getString("gameID", null))
                .delete();
        editor.putBoolean("joinPending", false);
        editor.putString("gameID", null);
        editor.apply();
        startActivity(startGameActivity);
        finish();
    }

}