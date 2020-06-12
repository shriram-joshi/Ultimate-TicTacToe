package com.example.tictactoe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class StartGameActivity extends AppCompatActivity {

    ActivityStartGameBinding binding;

    Dialog playWithFriendDialog;
    Button hostGameButton, joinGameButton;

    Dialog startGameDialog;
    Button createHosting, cancelHosting;
    TextView helpText;
    EditText startGameCodeEt;

    Dialog joinGameDialog;
    Button joinGame;
    EditText joinGameCodeEt;
    TextView joiningTv;

    SharedPreferences playerPreferences;
    SharedPreferences.Editor saveGameID;

    FirebaseFirestore createGame = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartGameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);
        saveGameID = playerPreferences.edit();

        binding.playOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playWithFriendDialog = new Dialog(StartGameActivity.this);
                playWithFriendDialog.setContentView(R.layout.dialogbox_play_with_friend);

                hostGameButton = playWithFriendDialog.findViewById(R.id.host_game_btn);
                joinGameButton = playWithFriendDialog.findViewById(R.id.join_game_btn);

                hostGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        {
                            playWithFriendDialog.dismiss();
                            startGameDialog = new Dialog(StartGameActivity.this);
                            startGameDialog.setContentView(R.layout.dialogbox_start_game);
                            startGameDialog.setCancelable(false);
                            createHosting = startGameDialog.findViewById(R.id.create_hosting);
                            cancelHosting = startGameDialog.findViewById(R.id.cancel_hosting);
                            helpText = startGameDialog.findViewById(R.id.show_to_friends_help);
                            startGameCodeEt = startGameDialog.findViewById(R.id.start_game_code_et);

                            createHosting.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    {
                                        startGameCodeEt.setEnabled(false);
                                        createHosting.setEnabled(false);
                                        helpText.setText("Creating...");

                                        if (startGameCodeEt.getText().toString().length() == 4) {
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

                                                                                    Intent start = new Intent(StartGameActivity.this, MainActivity.class);
                                                                                    start.putExtra("gameID", startGameCodeEt.getText().toString());
                                                                                    start.putExtra("turn", true);
                                                                                    start.putExtra("playerName", playerPreferences.getString("playerName", "Host"));
                                                                                    createGame.collection("Active Games").document("G" + startGameCodeEt.getText().toString())
                                                                                            .update("gameIsActive", 2);

                                                                                    startGameDialog.dismiss();
                                                                                    finish();
                                                                                    startActivity(start);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                        helpText.setText(R.string.share_this_code_prompt_1);
                                                        saveGameID.putString("gameID", startGameCodeEt.getText().toString());
                                                        saveGameID.apply();
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
                                            helpText.setText(R.string.share_this_code_prompt_1);
                                            Toast.makeText(getApplicationContext(), "Enter a 4 digit code", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                            cancelHosting.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    startGameDialog.dismiss();
                                    if (!(startGameCodeEt.isEnabled())){
                                        createGame.collection("Active Games")
                                                .document("G" + startGameCodeEt.getText().toString()).delete();
                                        saveGameID.putString("gameID", null);
                                    }
                                }
                            });

                            startGameDialog.show();
                        }
                    }
                });

                joinGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        {
                            playWithFriendDialog.dismiss();
                            joinGameDialog = new Dialog(StartGameActivity.this);
                            joinGameDialog.setContentView(R.layout.dialogbox_join_game);
                            joinGame = joinGameDialog.findViewById(R.id.join_game);
                            joinGameCodeEt = joinGameDialog.findViewById(R.id.join_game_code_et);
                            joiningTv = joinGameDialog.findViewById(R.id.joining_tv);

                            joinGame.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
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

                                                        Intent start = new Intent(StartGameActivity.this, MainActivity.class);
                                                        start.putExtra("gameID", joinGameCodeEt.getText().toString());
                                                        start.putExtra("turn", false);
                                                        start.putExtra("playerName", playerPreferences.getString("playerName", "Friend"));

                                                        joinGameDialog.cancel();
                                                        finish();
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
                                }
                            });
                            joinGameDialog.show();
                        }
                    }
                });

                playWithFriendDialog.show();

            }
        });

        binding.settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartGameActivity.this, SettingsActivity.class));
            }
        });
        
        binding.passAndPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StartGameActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
