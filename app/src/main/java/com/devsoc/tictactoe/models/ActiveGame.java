package com.devsoc.tictactoe.models;

public class ActiveGame {

    private long gameState, myScore, friendScore, draws, round;
    private String gameID, playerName, opponentName;
    private Long lastButtonPressed;
    private boolean turn;

    public ActiveGame(){
        gameID = null;

        gameState = 0;
        myScore = 0;
        friendScore = 0;
        round = 0;
        lastButtonPressed = null;
        turn = false;

        playerName = null;
        opponentName = null;
    }

    public String getGameID(){ return gameID; }
    public void setGameID(String gameID){ this.gameID = gameID; }

    public long getGameState() {
        return gameState;
    }
    public void setGameState(long gameState) { this.gameState = gameState;}

    public Long getLastButtonPressed() { return lastButtonPressed;}
    public void setLastButtonPressed(Long lastButtonPressed) { this.lastButtonPressed = lastButtonPressed;}

    public boolean isTurn() {
        return turn;
    }
    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }
    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public long getMyScore() {
        return myScore;
    }
    public void setMyScore(long myScore) {
        this.myScore = myScore;
    }

    public long getFriendScore() {
        return friendScore;
    }
    public void setFriendScore(long friendScore) {
        this.friendScore = friendScore;
    }

    public long getRound() {
        return round;
    }

    public void setRound(long round) {
        this.round = round;
    }

    public long getDraws() {
        return draws;
    }

    public void setDraws(long draws) {
        this.draws = draws;
    }
}
