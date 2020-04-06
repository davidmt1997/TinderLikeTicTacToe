package com.example.tinder_liketictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowLeaderboardActivity extends AppCompatActivity {

    private String result;
    private Handler handler;
    private LinearLayout players;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_leaderboard);

        players = findViewById(R.id.topPlayers);
        handler = new Handler();
        Intent intent = getIntent();
        String message = intent.getStringExtra("result");
        result = message;
        String []res = message.split(",");
        for(int i = 0; i < res.length; i ++){
            showMessage(res[i]);
        }
    }

    public TextView textView(String message) {
        if (null == message || message.trim().isEmpty()) {
            message = "<Empty Message>";
        }
        TextView tv = new TextView(this);

        tv.setText(message);
        tv.setTextSize(20);
        tv.setPadding(0, 5, 0, 0);
        return tv;
    }

    public void showMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                players.addView(textView(message));
            }
        });
    }
}