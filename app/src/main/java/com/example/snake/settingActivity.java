package com.example.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;

public class settingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SharedPreferences sp = getSharedPreferences("nums", Context.MODE_PRIVATE);

        RadioButton arrowControl = (RadioButton) findViewById(R.id.radio_arrows);
        RadioButton swipeControl = (RadioButton) findViewById(R.id.radio_swipe);
        if(sp.getBoolean("control",getResources().getBoolean(R.bool.arrows_control))){
            arrowControl.setChecked(true);
        }
        else {
            swipeControl.setChecked(true);
        }

        CheckBox eatSound = (CheckBox) findViewById(R.id.eat_sound_check_box);
        if(sp.getBoolean("eatSound",true)){
            eatSound.setChecked(true);
        }else{
            eatSound.setChecked(false);
        }
    }

    public void controlModeRadio(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        SharedPreferences sp = getSharedPreferences("nums", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        switch(view.getId()) {
            case R.id.radio_arrows:
                if (checked){
                    editor.putBoolean("control",true);
                    editor.apply();
                }
                    break;
            case R.id.radio_swipe:
                if (checked){
                    editor.putBoolean("control",false);
                    editor.apply();
                }
                    break;
        }
    }

    public void playEatSound(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        SharedPreferences sp = getSharedPreferences("nums", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(checked)
            editor.putBoolean("eatSound",true);
        else
            editor.putBoolean("eatSound",false);

        editor.apply();
    }
}