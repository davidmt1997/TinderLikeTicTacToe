package com.example.tinder_liketictactoe;
//references:
//https://www.youtube.com/watch?v=da1uzaj549A

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MinMaxActivity extends AppCompatActivity {

    private Board boardView;
    private MinMaxEngine gameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_min_max);
        boardView = (Board) findViewById(R.id.board2);
        gameEngine = new MinMaxEngine();
        boardView.setGameEngine(gameEngine);
        boardView.setMainActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_new_game){
            newGame();
        }
        return super.onOptionsItemSelected(item);
    }

    public void gameEnded(int c){
        String msg = (c==-1) ? "Game Ended in Tie" : "Game ended " + c + " win.";

        new AlertDialog.Builder(this).setTitle("Tic Tac Toe").
                setMessage(msg).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                newGame();
            }
        }).show();
    }

    private void newGame(){
        gameEngine.newGame();
        boardView.invalidate();
    }
}