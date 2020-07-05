package com.example.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.databinding.ActivityStartGameBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Objects;

public class StartGameActivity extends AppCompatActivity {

    ActivityStartGameBinding binding;

    Dialog playWithFriendDialog;

    Dialog startGameDialog;
    Button createHosting;
    TextView helpText;
    EditText startGameCodeEt;

    Dialog joinGameDialog;
    Button joinGame;
    EditText joinGameCodeEt;
    TextView joiningTv;

    SharedPreferences playerPreferences;
    SharedPreferences.Editor editor;

    FirebaseFirestore createGame = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartGameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.howToPlayLink.setMovementMethod(LinkMovementMethod.getInstance());

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);
        editor = playerPreferences.edit();

        switch (playerPreferences.getInt("themePref",0)){
            case 0:
                binding.startGameActivityBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                setTheme(R.drawable.wooden_background,R.drawable.background_with_text_wooden, R.color.white);
                break;
            case 1:
                binding.startGameActivityBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setTheme(R.drawable.space_background,R.drawable.background_with_text_space, R.color.white);
                break;
            case 2:
                binding.startGameActivityBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setTheme(R.drawable.ocean_background,R.drawable.background_with_text_ocean, R.color.black);
                break;
        }

        binding.playOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playWithFriendDialog = new Dialog(StartGameActivity.this);
                playWithFriendDialog.setContentView(R.layout.dialogbox_play_with_friend);
                playWithFriendDialog.show();
            }
        });
        
        binding.passAndPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartGameActivity.this, MainActivityPassAndPlay.class));
            }
        });
    }

    public void goToSettings(View view) {
        startActivity(new Intent(StartGameActivity.this, SettingsActivity.class));
    }

    public void hostGameClicked(View view) {
            playWithFriendDialog.dismiss();
            startGameDialog = new Dialog(StartGameActivity.this);
            startGameDialog.setContentView(R.layout.dialogbox_start_game);
            startGameDialog.setCancelable(false);
            createHosting = startGameDialog.findViewById(R.id.create_hosting);
            helpText = startGameDialog.findViewById(R.id.show_to_friends_help);
            startGameCodeEt = startGameDialog.findViewById(R.id.start_game_code_et);

        startGameDialog.show();
    }
    public void createHostingClicked(View view) {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected){
            startGameCodeEt.setEnabled(false);
            createHosting.setEnabled(false);

            if (startGameCodeEt.getText().toString().length() == 4) {
                helpText.setText("Creating...");
                createGame.collection("Active Games").document("G" + startGameCodeEt.getText().toString())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() == null) {
                            HashMap<String, Object> createNewGame = new HashMap<>();
                            createNewGame.put("gameID", startGameCodeEt.getText().toString());
                            createNewGame.put("gameIsActive", 0);
                            createNewGame.put("playerHost", playerPreferences.getString("playerName", "Host"));

                            createGame.collection("Active Games")
                                    .document("G" + startGameCodeEt.getText().toString())
                                    .set(createNewGame);

                            createGame.collection("Active Games").document("G" + startGameCodeEt.getText().toString())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                                if (documentSnapshot.getData() != null) {
                                                    if ((long) documentSnapshot.getData().get("gameIsActive") == 1) {

                                                        Intent start = new Intent(StartGameActivity.this, MainActivityPlayOnline.class);
                                                        start.putExtra("gameID", startGameCodeEt.getText().toString());
                                                        start.putExtra("turn", true);
                                                        start.putExtra("playerName", playerPreferences.getString("playerName", "Host"));
                                                        createGame.collection("Active Games").document("G" + startGameCodeEt.getText().toString())
                                                                .update("gameIsActive", 2);

                                                        startGameDialog.dismiss();
                                                        startActivity(start);
                                                    }
                                                }
                                            }
                                        }
                                    });
                            helpText.setText(R.string.share_this_code_prompt_1);
                            editor.putString("gameID", startGameCodeEt.getText().toString());
                            editor.apply();
                            createHosting.setEnabled(false);

                        } else {
                            startGameCodeEt.setEnabled(true);
                            createHosting.setEnabled(true);
                            helpText.setText(R.string.share_this_code_prompt_1);
                            Toast.makeText(getApplicationContext(), "The code is already taken.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                startGameCodeEt.setEnabled(true);
                createHosting.setEnabled(true);
                helpText.setText(R.string.share_this_code_prompt_2);
                Toast.makeText(getApplicationContext(), "Enter a 4 digit code", Toast.LENGTH_SHORT).show();
            }
        } else{
            startGameCodeEt.setEnabled(true);
            createHosting.setEnabled(true);
            helpText.setText(R.string.share_this_code_prompt_2);
            Toast.makeText(StartGameActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
        }
    }
    public void cancelHostingClicked(View view) {
        startGameDialog.dismiss();
        if (!(startGameCodeEt.isEnabled())){
            createGame.collection("Active Games")
                    .document("G" + startGameCodeEt.getText().toString()).delete();
            editor.putString("gameID", null);
        }
    }

    public void joinGameDialogClicked(View view) {
        playWithFriendDialog.dismiss();
        joinGameDialog = new Dialog(StartGameActivity.this);
        joinGameDialog.setContentView(R.layout.dialogbox_join_game);
        joinGame = joinGameDialog.findViewById(R.id.join_game);
        joinGameCodeEt = joinGameDialog.findViewById(R.id.join_game_code_et);
        joiningTv = joinGameDialog.findViewById(R.id.joining_tv);
        joinGameDialog.show();
    }
    public void joinGameClicked(View view) {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            joiningTv.setVisibility(View.VISIBLE);
            joinGameCodeEt.setEnabled(false);
            joinGame.setEnabled(false);
            if (joinGameCodeEt.getText().toString().length() == 4){
                createGame.collection("Active Games").document("G" + joinGameCodeEt.getText().toString())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() == null){
                            joinGameCodeEt.setEnabled(true);
                            joinGame.setEnabled(true);
                            joiningTv.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Invalid code", Toast.LENGTH_LONG).show();
                        } else {
                            if ((long)documentSnapshot.getData().get("gameIsActive") == 2){
                                Toast.makeText(StartGameActivity.this, "The game has already started", Toast.LENGTH_SHORT).show();
                                joinGameCodeEt.setEnabled(true);
                                joiningTv.setVisibility(View.GONE);
                                joinGame.setEnabled(true);
                            } else {
                                HashMap<String, Object> gameStart = new HashMap<>();

                                gameStart.put("gameIsActive", 1);
                                gameStart.put("playerFriend", playerPreferences.getString("playerName", "Friend"));

                                createGame.collection("Active Games").document("G" + joinGameCodeEt.getText().toString())
                                        .update(gameStart);

                                Intent start = new Intent(StartGameActivity.this, MainActivityPlayOnline.class);
                                start.putExtra("gameID", joinGameCodeEt.getText().toString());
                                start.putExtra("turn", false);
                                start.putExtra("playerName", playerPreferences.getString("playerName", "Friend"));

                                joinGameDialog.cancel();
                                startActivity(start);
                            }
                        }
                    }
                });
            } else {
                joinGameCodeEt.setEnabled(true);
                joinGame.setEnabled(true);
                joiningTv.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Enter a 4 digit code", Toast.LENGTH_SHORT).show();
            }
        } else {
            joinGameCodeEt.setEnabled(true);
            joinGame.setEnabled(true);
            joiningTv.setVisibility(View.GONE);
            Toast.makeText(StartGameActivity.this, "No internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    private void setTheme(int background, int boardBackground, int textColour) {
        Drawable drawable;
        GradientDrawable backgroundStroke;

        binding.startGameActivityBackground.setImageResource(background);
        binding.boardBackground.setImageResource(boardBackground);

        binding.playOnlineBtn.setTextColor(getResources().getColor(textColour));
        binding.passAndPlayBtn.setTextColor(getResources().getColor(textColour));
        binding.howToPlayLink.setTextColor(getResources().getColor(textColour));
        binding.settingsBtn.setImageResource(R.drawable.ic_settings);
        drawable = (Drawable) binding.settingsBtn.getDrawable();
        drawable.setTint(getResources().getColor(textColour));

        backgroundStroke = (GradientDrawable)binding.playOnlineBtn.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));
        backgroundStroke = (GradientDrawable)binding.passAndPlayBtn.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(textColour));
    }
}
