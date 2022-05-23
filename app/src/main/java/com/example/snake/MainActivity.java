package com.example.snake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        SharedPreferences sp = getSharedPreferences("nums", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        Button newGame = (Button) findViewById(R.id.new_game);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, game.class);
                startActivity(intent);
            }
        });
        ImageButton setting = (ImageButton) findViewById(R.id.setting_button);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, settingActivity.class);
                startActivity(intent);
            }
        });


        TextView diff_level = findViewById(R.id.diff_level);
        SeekBar level_bar = findViewById(R.id.level_bar);
        int diff_progress = ((sp.getInt("delay", 200) - 400) / -30);
        level_bar.setProgress(diff_progress);
        diff_level.setText("difficulty : " + diff_progress);
        level_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int delay = 400 - (progress * 30);
                diff_level.setText("difficulty : " + progress);
                editor.putInt("delay", delay);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button bounds = (Button) findViewById(R.id.bounds);
        Button continous = (Button) findViewById(R.id.continous);
        Button no_die = (Button) findViewById(R.id.no_die);
        bounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) v).setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.teal_700));
                continous.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.transparent));
                no_die.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.transparent));
                editor.putInt("state", getResources().getInteger(R.integer.bound_state));
                editor.apply();
            }
        });
        continous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) v).setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.teal_700));
                bounds.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.transparent));
                no_die.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.transparent));
                editor.putInt("state", getResources().getInteger(R.integer.continous_state));
                editor.apply();
            }
        });
        no_die.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) v).setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.teal_700));
                bounds.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.transparent));
                continous.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.transparent));
                editor.putInt("state", getResources().getInteger(R.integer.no_die_state));
                editor.apply();
            }
        });
        switch ((sp.getInt("state", getResources().getInteger(R.integer.continous_state)))) {
            case 0:
                bounds.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.teal_700));
                break;
            case 1:
                continous.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.teal_700));
                break;
            case 2:
                no_die.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.teal_700));
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("nums", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        ((TextView) findViewById(R.id.main_best_score)).setText(String.valueOf(sp.getInt("best score", 0)));
    }
}