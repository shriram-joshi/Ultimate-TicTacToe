package com.devsoc.tictactoe.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.devsoc.tictactoe.R;
import com.devsoc.tictactoe.databinding.ActivityMainPlayOnlineBinding;
import com.devsoc.tictactoe.models.ActiveGame;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivityPlayOnline extends AppCompatActivity {

    private ActivityMainPlayOnlineBinding binding;
    private ActiveGame localGame = new ActiveGame();
    private boolean isLeftGame = false, isStopGame = false;
    private long isWaiting = 0;

    SharedPreferences playerPreferences;
    SharedPreferences.Editor editor;

    MediaPlayer youClickedButtonSound, opponentClickedButtonSound, waitingSound;

    FirebaseFirestore gameSync = FirebaseFirestore.getInstance();
    HashMap<String, Object> updateGame = new HashMap<>();

    String YOU_PLAY_AS_TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPlayOnlineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        playerPreferences = getApplicationContext().getSharedPreferences("userPreferences", MODE_PRIVATE);

        youClickedButtonSound = MediaPlayer.create(MainActivityPlayOnline.this, R.raw.you_button_click);
        opponentClickedButtonSound = MediaPlayer.create(MainActivityPlayOnline.this, R.raw.opponent_button_click);
        waitingSound = MediaPlayer.create(MainActivityPlayOnline.this, R.raw.waiting_for_opponent);

        switch (playerPreferences.getInt("themePref",0)){
            case 0:
                binding.mainActivityPoBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                setTheme(R.drawable.wooden_background,R.drawable.board_background_template_wooden,R.color.black, R.color.white);
                break;
            case 1:
                binding.mainActivityPoBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setTheme(R.drawable.space_background,R.drawable.board_background_template_space, R.color.white, R.color.white);
                break;
            case 2:
                binding.mainActivityPoBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setTheme(R.drawable.ocean_background,R.drawable.board_background_template_ocean,  R.color.black, R.color.black);
                break;
            case 3:
                binding.mainActivityPoBackground.setScaleType(ImageView.ScaleType.FIT_XY);
                setTheme(R.drawable.black_background,R.drawable.board_background_template_black,  R.color.white, R.color.white);
                break;
        }

        //Initialization after game start
        {
            localGame.setGameID(getIntent().getStringExtra("gameID"));
            localGame.setPlayerName(playerPreferences.getString("playerName", "Friend"));
            localGame.setTurn(getIntent().getBooleanExtra("turn", false));
            localGame.setGameState(0);
            localGame.setLastButtonPressed((long)0);
            localGame.setFriendScore(0);
            localGame.setMyScore(0);
            localGame.setDraws(0);
            localGame.setRound(1);

            updateGame.put("gameState", localGame.getGameState());
            updateGame.put("lastButtonPressed", localGame.getLastButtonPressed());
            updateGame.put("isWaiting", 0);

            gameSync.collection("Active Games").document("G" + localGame.getGameID()).update(updateGame);

            if (localGame.isTurn()){
                binding.turnTv.setText(R.string.you_text);
                localGame.setGameState(1);

                binding.msg.setText(R.string.you_play_as_x_text);
                YOU_PLAY_AS_TEXT = getString(R.string.you_play_as_x_text);

                updateGame.put("turn", localGame.getPlayerName());
                gameSync.collection("Active Games").document("G" + localGame.getGameID()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        localGame.setOpponentName("" + Objects.requireNonNull(documentSnapshot.getData()).get("playerFriend"));
                        binding.opponentsNameTv.setText(localGame.getOpponentName());
                    }
                });
            } else {
                gameSync.collection("Active Games").document("G" + localGame.getGameID()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        localGame.setOpponentName("" + Objects.requireNonNull(documentSnapshot.getData()).get("playerHost"));
                        binding.turnTv.setText(localGame.getOpponentName());
                        binding.opponentsNameTv.setText(localGame.getOpponentName());

                        binding.msg.setText(R.string.you_play_as_o_text);
                        YOU_PLAY_AS_TEXT = getString(R.string.you_play_as_o_text);
                    }
                });
            }

            gameSync.collection("Active Games").document("G" + localGame.getGameID()).update(updateGame);
        }

        gameSync.collection("Active Games").document("G" + localGame.getGameID())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    if ((long)documentSnapshot.getData().get("gameIsActive") == (long)2){
                        HashMap<String, Object> gameInstance = new HashMap<>(documentSnapshot.getData());
                        
                        if (gameInstance.get("isWaiting") != null && (long)gameInstance.get("isWaiting") == 2){
                            //reset game for next round
                            {
                                isWaiting = 0;
                                localGame.setRound(localGame.getRound()+1);
                                binding.roundTv.setText(String.valueOf(localGame.getRound()));

                                updateGame.put("isWaiting", 0);
                                updateGame.put("round", localGame.getRound());

                                binding.b11.setText("");
                                binding.b12.setText("");
                                binding.b13.setText("");
                                binding.b21.setText("");
                                binding.b22.setText("");
                                binding.b23.setText("");
                                binding.b31.setText("");
                                binding.b32.setText("");
                                binding.b33.setText("");
                                binding.b11.setEnabled(true);
                                binding.b12.setEnabled(true);
                                binding.b13.setEnabled(true);
                                binding.b21.setEnabled(true);
                                binding.b22.setEnabled(true);
                                binding.b23.setEnabled(true);
                                binding.b31.setEnabled(true);
                                binding.b32.setEnabled(true);
                                binding.b33.setEnabled(true);
                                binding.nextRoundBtn.setVisibility(View.GONE);

                                localGame.setGameState(0);
                                localGame.setLastButtonPressed((long)0);

                                updateGame.put("gameState", localGame.getGameState());
                                updateGame.put("lastButtonPressed", localGame.getLastButtonPressed());
                                updateGame.put("isWaiting", 0);

                                gameSync.collection("Active Games").document("G" + localGame.getGameID()).update(updateGame);

                                if (localGame.isTurn()){
                                    binding.turnTv.setText(R.string.you_text);
                                    localGame.setGameState(1);
                                    binding.msg.setText(R.string.you_play_as_x_text);
                                    YOU_PLAY_AS_TEXT = getString(R.string.you_play_as_x_text);
                                    updateGame.put("turn", localGame.getPlayerName());
                                } else {
                                    binding.turnTv.setText(localGame.getOpponentName());
                                    binding.msg.setText(R.string.you_play_as_o_text);
                                    YOU_PLAY_AS_TEXT = getString(R.string.you_play_as_o_text);
                                }

                                gameSync.collection("Active Games").document("G" + localGame.getGameID()).update(updateGame);
                                isStopGame = false;
                            }
                        }else if (gameInstance.get("gameState") != null && (long)gameInstance.get("gameState") > localGame.getGameState() && !isStopGame){
                            localGame.setLastButtonPressed((long)gameInstance.get("lastButtonPressed"));
                            localGame.setGameState((long)gameInstance.get("gameState")-1);
                            updateUI();
                        }
                    }
                } else {
                    if (!isLeftGame) {
                        Toast.makeText(MainActivityPlayOnline.this,  localGame.getOpponentName() + " left the game", Toast.LENGTH_LONG).show();
                    }
                    localGame = new ActiveGame();
                    startActivity(new Intent(MainActivityPlayOnline.this,StartGameActivity.class));
                    finish();
                    Log.d("OnEvent", "Current data: null");
                }
            }
        });

        //9x9 Button OnClickListeners
        {
            binding.b11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b11, 1, false);
                }
            });
            binding.b12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b12, 2, false);
                }
            });
            binding.b13.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b13, 3, false);
                }
            });
            binding.b21.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b21, 4, false);
                }
            });
            binding.b22.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b22, 5, false);
                }
            });
            binding.b23.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b23, 6, false);
                }
            });
            binding.b31.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b31, 7, false);
                }
            });
            binding.b32.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b32, 8, false);
                }
            });
            binding.b33.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedButton(binding.b33, 9, false);
                }
            });
        }

        binding.nextRoundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                    if (playerPreferences.getBoolean("playSounds",true))
                        waitingSound.start();

                    binding.msg.append("\nWaiting for " + localGame.getOpponentName());

                    gameSync.collection("Active Games").document("G" + localGame.getGameID()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            HashMap<String, Object> gameInstance = new HashMap<>(Objects.requireNonNull(documentSnapshot.getData()));
                            if ((long)gameInstance.get("isWaiting") == 0){
                                isWaiting = 1;
                                
                                updateGame.put("isWaiting", 1);
                                gameSync.collection("Active Games").document("G" + localGame.getGameID()).update(updateGame);
                                binding.nextRoundBtn.setEnabled(false);
                            }
                            else if ((long)gameInstance.get("isWaiting") == 1){
                                isWaiting = 2;
                                updateGame.put("isWaiting", 2);
                                gameSync.collection("Active Games").document("G" + localGame.getGameID()).update(updateGame);
                            }else{
                                Toast.makeText(MainActivityPlayOnline.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (isWaiting>0){

                                updateGame.put("isWaiting", 2);
                                gameSync.collection("Active Games").document("G" + localGame.getGameID()).update(updateGame);
                            }
                        }
                    },10000);
            }
        });

        binding.leaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        editor = playerPreferences.edit();
        final Dialog leaveGameDialog = new Dialog(MainActivityPlayOnline.this);
        leaveGameDialog.setContentView(R.layout.dialogbox_leave_game);
        leaveGameDialog.setCancelable(true);
        Button yes = leaveGameDialog.findViewById(R.id.yes_leave), no = leaveGameDialog.findViewById(R.id.dont_leave);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLeftGame = true;
                gameSync.collection("Active Games").document("G" + localGame.getGameID()).delete();
                editor.putString("gameID", null);
                editor.apply();
                leaveGameDialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGameDialog.dismiss();
            }
        });

        leaveGameDialog.show();
    }

    private void setTheme(int background, int boardBackground, int boardTextColour, int otherTextColour) {
        GradientDrawable backgroundStroke;
        Drawable drawable;

        binding.mainActivityPoBackground.setImageResource(background);
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
        backgroundStroke = (GradientDrawable)binding.msg.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(otherTextColour));
        backgroundStroke = (GradientDrawable)binding.nextRoundBtn.getBackground();
        backgroundStroke.setStroke(2, getResources().getColor(otherTextColour));

        drawable = binding.leaveGame.getDrawable();
        drawable.setTint(getResources().getColor(otherTextColour));
    }

    private void updateUI() {
        switch (localGame.getLastButtonPressed().intValue()){
            case 1:
                clickedButton(binding.b11, localGame.getLastButtonPressed().intValue(), true);
                break;
            case 2:
                clickedButton(binding.b12, localGame.getLastButtonPressed().intValue(), true);
                break;
            case 3:
                clickedButton(binding.b13, localGame.getLastButtonPressed().intValue(), true);
                break;
            case 4:
                clickedButton(binding.b21, localGame.getLastButtonPressed().intValue(), true);
                break;
            case 5:
                clickedButton(binding.b22, localGame.getLastButtonPressed().intValue(), true);
                break;
            case 6:
                clickedButton(binding.b23, localGame.getLastButtonPressed().intValue(), true);
                break;
            case 7:
                clickedButton(binding.b31, localGame.getLastButtonPressed().intValue(), true);
                break;
            case 8:
                clickedButton(binding.b32, localGame.getLastButtonPressed().intValue(), true);
                break;
            case 9:
                clickedButton(binding.b33, localGame.getLastButtonPressed().intValue(), true);
                break;
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

    void clickedButton(Button button, int id, boolean isUiUpdate){

        if (localGame.isTurn()){
            button.setEnabled(false);

            if (localGame.getGameState()%2 == 0){button.setText("O");}
            else {button.setText("X");}

            if (playerPreferences.getBoolean("playSounds", true))
                youClickedButtonSound.start();

            binding.msg.setText(R.string.wait_for_play_text);

            if (win()){
                stopGame();
                isStopGame = true;
                binding.nextRoundBtn.setVisibility(View.VISIBLE);
                binding.nextRoundBtn.setEnabled(true);
                binding.msg.setText(R.string.you_win_string);
                localGame.setMyScore(localGame.getMyScore()+1);
                binding.myScoreTv.setText(String.valueOf(localGame.getMyScore()));

                updateGame.put("lastButtonPressed", id);
                updateGame.put("gameState", localGame.getGameState()+1);
                gameSync.collection("Active Games").document("G" + localGame.getGameID())
                        .update(updateGame);

                if (localGame.getGameState()%2 == 0){
                    localGame.setTurn(true);
                } else {
                    localGame.setTurn(false);
                }
            }else if (localGame.getGameState() == 9){
                stopGame();
                binding.nextRoundBtn.setVisibility(View.VISIBLE);
                binding.nextRoundBtn.setEnabled(true);
                binding.msg.setText(R.string.tie_text);
                localGame.setDraws(localGame.getDraws()+1);
                binding.drawScoreTv.setText(String.valueOf(localGame.getDraws()));
                localGame.setGameState(localGame.getGameState()+1);

                updateGame.put("lastButtonPressed", id);
                updateGame.put("gameState", localGame.getGameState());
                gameSync.collection("Active Games").document("G" + localGame.getGameID())
                        .update(updateGame);

                localGame.setTurn(false);
            } else {
                localGame.setTurn(false);
                binding.turnTv.setText(localGame.getOpponentName());
                localGame.setGameState(localGame.getGameState()+1);

                updateGame.put("lastButtonPressed", id);
                updateGame.put("gameState", localGame.getGameState());
                updateGame.put("turn", localGame.getOpponentName());
                gameSync.collection("Active Games").document("G" + localGame.getGameID())
                        .update(updateGame);
            }
        }else if (isUiUpdate) {
            button.setEnabled(false);

            if (localGame.getGameState()%2 == 0){button.setText("O");}
            else {button.setText("X");}

            if (playerPreferences.getBoolean("playSounds", true))
                opponentClickedButtonSound.start();

            binding.msg.setText(YOU_PLAY_AS_TEXT);

            if (win()){
                stopGame();
                isStopGame = true;
                binding.nextRoundBtn.setVisibility(View.VISIBLE);
                binding.nextRoundBtn.setEnabled(true);
                binding.msg.setText(localGame.getOpponentName());
                binding.msg.append(getString(R.string.wins_the_round_text));
                localGame.setFriendScore(localGame.getFriendScore()+1);
                binding.opponentsScoreTv.setText(String.valueOf(localGame.getFriendScore()));

                if (localGame.getGameState()%2 == 0){
                    localGame.setTurn(false);
                } else {
                    localGame.setTurn(true);
                }
            }else if (localGame.getGameState() == 9){
                stopGame();
                isStopGame = true;
                binding.nextRoundBtn.setVisibility(View.VISIBLE);
                binding.nextRoundBtn.setEnabled(true);
                localGame.setDraws(localGame.getDraws()+1);
                binding.drawScoreTv.setText(String.valueOf(localGame.getDraws()));
                binding.msg.setText(R.string.tie_text);

                localGame.setTurn(true);
            } else{
                localGame.setGameState(localGame.getGameState()+1);
                localGame.setTurn(true);
                binding.turnTv.setText(R.string.you_text);
            }
        }
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
