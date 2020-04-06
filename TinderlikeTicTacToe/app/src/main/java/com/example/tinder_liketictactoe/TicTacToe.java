package com.example.tinder_liketictactoe;
//references:
//https://medium.com/@ssaurel/learn-to-create-a-tic-tac-toe-game-for-android-82c7bf2369de

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TicTacToe extends AppCompatActivity {

    private BoardView boardView;
    private GameEngine gameEngine;
    private String username;

    public TicTacToe(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        boardView = (BoardView) findViewById(R.id.board);
        gameEngine = new GameEngine();
        boardView.setGameEngine(gameEngine);
        boardView.setMainActivity(this);

        Intent intent = getIntent();
        String message = intent.getStringExtra("username");
        username = message;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_new_game){
            newGame();
        }
        return super.onOptionsItemSelected(item);
    }

    public void gameEnded(char c) {
        String msg = (c == 'T') ? "Game Ended. Tie" : "GameEnded. " + c + " win";

        new AlertDialog.Builder(this).setTitle("Tic Tac Toe").
                setMessage(msg).
                setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        newGame();
                    }
                }).show();

        if(c=='X'){
            updateDB("win");
        }
        else if(c=='O')
            updateDB("loss");
    }

    private void newGame(){
        gameEngine.newGame();
        boardView.invalidate();
    }

    public void updateDB(String ending){
        if(ending== "win"){
            PlayScreenActivity.updateDB("win " + username);
        }
        else if(ending == "loss"){
            PlayScreenActivity.updateDB("loss " + username);
        }
    }
}

