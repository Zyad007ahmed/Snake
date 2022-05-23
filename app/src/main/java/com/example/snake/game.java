package com.example.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;

public class game extends AppCompatActivity {

    DisplayMetrics metrics;

    final int UP = 0;
    final int RIGHT = 1;
    final int DOWN = 2;
    final int LEFT = 3;

    RelativeLayout r;
    LinkedList<ImageView> s;
    RelativeLayout.LayoutParams lp;
    int dim;
    float dpi;
    int curDir = RIGHT;
    int nextDir = RIGHT;
    int choosenDir = RIGHT;
    int maxPosX;
    int maxPosY;
    ImageView food;
    int foodX;
    int foodY;
    int headX;
    int headY;
    int score = 0;
    SharedPreferences sp;
    Handler h2 = new Handler(Looper.getMainLooper());
    Runnable r2;
    int delay;
    int normalDelay;
    boolean eatSound;
    MediaPlayer mMediaPlayer;

    private MediaPlayer.OnCompletionListener mCompletionListener = mp -> releaseMediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        metrics = getResources().getDisplayMetrics();
        dpi = metrics.density;
        dim = (int) (16 * dpi);
        r = (RelativeLayout) findViewById(R.id.game);
        lp = new RelativeLayout.LayoutParams(dim, dim);    //for snake node dims
        s = new LinkedList<>();

        //   to set dimensions for game
        setGameDimensions();

        maxPosY = (r.getLayoutParams().height / dim) - 1;
        maxPosX = (r.getLayoutParams().width / dim) - 1;

        sp = getSharedPreferences("nums", Context.MODE_PRIVATE);
        delay = sp.getInt("delay", getResources().getInteger(R.integer.medium_delay));
        normalDelay = delay;
        eatSound = sp.getBoolean("eatSound", true);

        ((TextView) findViewById(R.id.best_score)).setText(String.valueOf(sp.getInt("best score", 0)));

        begin();

        if (sp.getBoolean("control", getResources().getBoolean(R.bool.arrows_control))) {
            RelativeLayout arrowsControl = (RelativeLayout) findViewById(R.id.arrows);
            arrowsControl.setVisibility(View.VISIBLE);

            ImageButton lefBut = (ImageButton) findViewById(R.id.left_button);
            ImageButton upBut = (ImageButton) findViewById(R.id.up_button);
            ImageButton rightBut = (ImageButton) findViewById(R.id.right_button);
            ImageButton downBut = (ImageButton) findViewById(R.id.down_button);

            //  to change direction when touch the button
            View.OnTouchListener touchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (v.getId()) {
                        case R.id.left_button:
                            if (curDir == RIGHT)
                                nextDir = curDir;
                            else
                                nextDir = LEFT;

                            choosenDir = LEFT;
                            break;
                        case R.id.up_button:
                            if (curDir == DOWN)
                                nextDir = curDir;
                            else
                                nextDir = UP;

                            choosenDir = UP;
                            break;
                        case R.id.right_button:
                            if (curDir == LEFT)
                                nextDir = curDir;
                            else
                                nextDir = RIGHT;

                            choosenDir = RIGHT;
                            break;
                        case R.id.down_button:
                            if (curDir == UP)
                                nextDir = curDir;
                            else
                                nextDir = DOWN;

                            choosenDir = DOWN;
                            break;
                    }

                    return false;
                }
            };

            lefBut.setOnTouchListener(touchListener);
            rightBut.setOnTouchListener(touchListener);
            upBut.setOnTouchListener(touchListener);
            downBut.setOnTouchListener(touchListener);

            //  to increase speed
            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (choosenDir == nextDir)
                        delay /= 2;

                    return false;
                }
            };
            lefBut.setOnLongClickListener(longClickListener);
            rightBut.setOnLongClickListener(longClickListener);
            upBut.setOnLongClickListener(longClickListener);
            downBut.setOnLongClickListener(longClickListener);

            //    to make speed normal
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delay = normalDelay;
                }
            };
            lefBut.setOnClickListener(clickListener);
            rightBut.setOnClickListener(clickListener);
            upBut.setOnClickListener(clickListener);
            downBut.setOnClickListener(clickListener);
        } else {
            RelativeLayout swipeControl = (RelativeLayout) findViewById(R.id.large);
            swipeControl.setOnTouchListener(new OnSwipeTouchListener(game.this) {
                @Override
                public void onSwipeTop() {
                    if (curDir == DOWN)
                        nextDir = curDir;
                    else
                        nextDir = UP;

                    choosenDir = UP;
                    swipeDelay();
                }

                public void onSwipeRight() {
                    if (curDir == LEFT)
                        nextDir = curDir;
                    else
                        nextDir = RIGHT;

                    choosenDir = RIGHT;
                    swipeDelay();
                }

                public void onSwipeLeft() {
                    if (curDir == RIGHT)
                        nextDir = curDir;
                    else
                        nextDir = LEFT;

                    choosenDir = LEFT;
                    swipeDelay();
                }

                public void onSwipeBottom() {
                    if (curDir == UP)
                        nextDir = curDir;
                    else
                        nextDir = DOWN;

                    choosenDir = DOWN;
                    swipeDelay();
                }
            });
        }

        if (sp.getInt("state", getResources().getInteger(R.integer.continous_state)) == getResources().getInteger(R.integer.bound_state)) {  //for bound mode
            r2 = new Runnable() {
                @Override
                public void run() {

                    h2.postDelayed(r2, delay);

                    switch (nextDir) {
                        case RIGHT:
                            if (curDir == DOWN)
                                s.getFirst().setImageResource(R.drawable.turn_4);
                            if (curDir == UP)
                                s.getFirst().setImageResource(R.drawable.turn_1);
                            newNode(headX + 1, headY, RIGHT);
                            break;
                        case LEFT:
                            if (curDir == DOWN)
                                s.getFirst().setImageResource(R.drawable.turn_3);
                            if (curDir == UP)
                                s.getFirst().setImageResource(R.drawable.turn_2);
                            newNode(headX - 1, headY, LEFT);
                            break;
                        case UP:
                            if (curDir == RIGHT)
                                s.getFirst().setImageResource(R.drawable.turn_3);
                            if (curDir == LEFT)
                                s.getFirst().setImageResource(R.drawable.turn_4);
                            newNode(headX, headY - 1, UP);
                            break;
                        case DOWN:
                            if (curDir == RIGHT)
                                s.getFirst().setImageResource(R.drawable.turn_2);
                            if (curDir == LEFT)
                                s.getFirst().setImageResource(R.drawable.turn_1);
                            newNode(headX, headY + 1, DOWN);
                            break;
                    }
                    // if he didn't eat delete last node
                    if (!didEat()) {
                        deleteLastNode();
                    } else {
                        if (eatSound)
                            foodSound();

                        score += 5;
                        TextView tv = (TextView) findViewById(R.id.score);
                        tv.setText(String.valueOf(score));
                        if (score > sp.getInt("best score", 0)) {
                            tv = (TextView) findViewById(R.id.best_score);
                            tv.setText(String.valueOf(score));
                        }
                        r.removeView(food);
                        createFood();
                    }
                    if (dead()) {
                        h2.removeCallbacks(r2);

                        TextView tv = (TextView) findViewById(R.id.best_score);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("best score", Integer.valueOf(tv.getText().toString()));
                        editor.apply();

                        ((TextView) findViewById(R.id.game_over)).setVisibility(View.VISIBLE);

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }

                    curDir = nextDir;
                }
            };
        } else if (sp.getInt("state", getResources().getInteger(R.integer.continous_state)) == getResources().getInteger(R.integer.continous_state)) {  //for continous mode
            r2 = new Runnable() {
                @Override
                public void run() {

                    h2.postDelayed(r2, delay);

                    bounds();

                    switch (nextDir) {
                        case RIGHT:
                            if (curDir == DOWN)
                                s.getFirst().setImageResource(R.drawable.turn_4);
                            if (curDir == UP)
                                s.getFirst().setImageResource(R.drawable.turn_1);
                            newNode(headX + 1, headY, RIGHT);
                            break;
                        case LEFT:
                            if (curDir == DOWN)
                                s.getFirst().setImageResource(R.drawable.turn_3);
                            if (curDir == UP)
                                s.getFirst().setImageResource(R.drawable.turn_2);
                            newNode(headX - 1, headY, LEFT);
                            break;
                        case UP:
                            if (curDir == RIGHT)
                                s.getFirst().setImageResource(R.drawable.turn_3);
                            if (curDir == LEFT)
                                s.getFirst().setImageResource(R.drawable.turn_4);
                            newNode(headX, headY - 1, UP);
                            break;
                        case DOWN:
                            if (curDir == RIGHT)
                                s.getFirst().setImageResource(R.drawable.turn_2);
                            if (curDir == LEFT)
                                s.getFirst().setImageResource(R.drawable.turn_1);
                            newNode(headX, headY + 1, DOWN);
                            break;
                    }

                    // if he didn't eat delete last node
                    if (!didEat()) {
                        deleteLastNode();
                    } else {
                        if (eatSound)
                            foodSound();

                        score += 5;
                        TextView tv = (TextView) findViewById(R.id.score);
                        tv.setText(String.valueOf(score));
                        if (score > sp.getInt("best score", 0)) {
                            tv = (TextView) findViewById(R.id.best_score);
                            tv.setText(String.valueOf(score));
                        }
                        r.removeView(food);
                        createFood();
                    }
                    if (isInSnakeBody(headX, headY)) {
                        h2.removeCallbacks(r2);

                        TextView tv = (TextView) findViewById(R.id.best_score);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("best score", Integer.valueOf(tv.getText().toString()));
                        editor.apply();

                        ((TextView) findViewById(R.id.game_over)).setVisibility(View.VISIBLE);

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }

                    curDir = nextDir;
                }
            };
        } else { //for no die mode
            r2 = new Runnable() {
                @Override
                public void run() {

                    h2.postDelayed(r2, delay);

                    bounds();

                    switch (nextDir) {
                        case RIGHT:
                            if (curDir == DOWN)
                                s.getFirst().setImageResource(R.drawable.turn_4);
                            if (curDir == UP)
                                s.getFirst().setImageResource(R.drawable.turn_1);
                            newNode(headX + 1, headY, RIGHT);
                            break;
                        case LEFT:
                            if (curDir == DOWN)
                                s.getFirst().setImageResource(R.drawable.turn_3);
                            if (curDir == UP)
                                s.getFirst().setImageResource(R.drawable.turn_2);
                            newNode(headX - 1, headY, LEFT);
                            break;
                        case UP:
                            if (curDir == RIGHT)
                                s.getFirst().setImageResource(R.drawable.turn_3);
                            if (curDir == LEFT)
                                s.getFirst().setImageResource(R.drawable.turn_4);
                            newNode(headX, headY - 1, UP);
                            break;
                        case DOWN:
                            if (curDir == RIGHT)
                                s.getFirst().setImageResource(R.drawable.turn_2);
                            if (curDir == LEFT)
                                s.getFirst().setImageResource(R.drawable.turn_1);
                            newNode(headX, headY + 1, DOWN);
                            break;
                    }

                    // if he didn't eat delete last node
                    if (!didEat()) {
                        deleteLastNode();
                    } else {
                        if (eatSound)
                            foodSound();

                        score += 5;
                        TextView tv = (TextView) findViewById(R.id.score);
                        tv.setText(String.valueOf(score));
                        if (score > sp.getInt("best score", 0)) {
                            tv = (TextView) findViewById(R.id.best_score);
                            tv.setText(String.valueOf(score));
                        }
                        r.removeView(food);
                        createFood();
                    }
                    curDir = nextDir;
                }
            };

        }
    }

    private void setGameDimensions() {
        int gameWidth = metrics.widthPixels - (int) ((16 * 2) * dpi);
        int gameHeight = metrics.heightPixels - (int) (/*getStatusBarHeight() +  getNavigationBarHeight() +*/getActionBarHeight() + (16 * 2 * dpi) + ((180 + 16 + 16) * dpi));

        gameWidth -= gameWidth % (16 * dpi);
        gameHeight -= gameHeight % (16 * dpi);

        LinearLayout.LayoutParams gameParams = new LinearLayout.LayoutParams(gameWidth, gameHeight);
        r.setLayoutParams(gameParams);

    }

    private int getActionBarHeight() {
        //   to get action bar height
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    private void foodSound() {
        releaseMediaPlayer();

        mMediaPlayer = MediaPlayer.create(game.this, R.raw.mixkit_arcade_retro_changing_tab_206);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
    }

    private void swipeDelay() {
        if (nextDir == choosenDir && nextDir == curDir) {
            delay /= 2;
        } else {
            delay = normalDelay;
        }
    }

    private void bounds() {
        if (headX > maxPosX) {
            s.getFirst().setX(0);
            headX = 0;
            return;
        }
        if (headX < 0) {
            s.getFirst().setX(maxPosX * dim);
            headX = maxPosX;
            return;
        }
        if (headY > maxPosY) {
            s.getFirst().setY(0);
            headY = 0;
            return;
        }
        if (headY < 0) {
            s.getFirst().setY(maxPosY * dim);
            headY = maxPosY;
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (state) {
            h2.postDelayed(r2, delay);
        }
    }

    @Override
    protected void onPause() {
        h2.removeCallbacks(r2);
        releaseMediaPlayer();
        state = true;
        pausePlay(findViewById(R.id.pause));

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        h2.removeCallbacks(r2);

        new AlertDialog.Builder(game.this)
                .setTitle("Exit game")
                .setMessage("Are you sure you want to exit the game?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (sp.getInt("state", getResources().getInteger(R.integer.bound_state)) != getResources().getInteger(R.integer.no_die_state)) {
                            TextView tv = (TextView) findViewById(R.id.best_score);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("best score", Integer.valueOf(tv.getText().toString()));
                            editor.apply();
                        }
                        game.super.onBackPressed();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        h2.postDelayed(r2, delay);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        h2.postDelayed(r2, delay);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void newNode(int x, int y, int dir) {
        s.addFirst(new ImageView(this));
        s.getFirst().setLayoutParams(lp);
        if (dir == RIGHT || dir == LEFT)
            s.getFirst().setImageResource(R.drawable.straight1);
        else
            s.getFirst().setImageResource(R.drawable.straight2);
/*
        //  with animation
        int lastX = headX;
        int lastY = headY;

        if (lastX == maxPosX && x == 0)
            lastX = -1;
        else if (lastX == 0 && x == maxPosX)
            lastX = maxPosX + 1;

        if (lastY == maxPosY && y == 0)
            lastY = -1;
        else if (lastY == 0 && y == maxPosY)
            lastY = maxPosY + 1;

        s.getFirst().setX(lastX * dim);
        s.getFirst().setY(lastY * dim);
        r.addView(s.getFirst());

        s.getFirst().animate().setDuration((long) (delay * 0.8)).translationX(x * dim).translationY(y * dim).start();

        headX = x;
        headY = y;*/

        //  without animation
        s.getFirst().setX(x * dim);
        s.getFirst().setY(y * dim);
        r.addView(s.getFirst());

        headX = x;
        headY = y;

    }

    private void deleteLastNode() {
        /*//  with animation
        int preLastX = (int) (s.get(s.size() - 2).getX() / dim);
        int preLastY = (int) (s.get(s.size() - 2).getY() / dim);

        int lastX = (int) (s.getLast().getX() / dim);
        int lastY = (int) (s.getLast().getY() / dim);

        if (preLastX == maxPosX && lastX == 0)
            lastX = -1;
        else if (preLastX == 0 && lastX == maxPosX)
            lastX = maxPosX + 1;
        else
            lastX = preLastX;

        if (preLastY == maxPosY && lastY == 0)
            lastY = -1;
        else if (preLastY == 0 && lastY == maxPosY)
            lastY = maxPosY + 1;
        else
            lastY = preLastY;

        s.getLast().animate().setDuration((long) (delay * 0.8)).translationX(lastX * dim).translationY(lastY * dim)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        r.removeView(s.getLast());
                        s.removeLast();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();*/

        //  without animation
        r.removeView(s.getLast());
        s.removeLast();

    }

    private boolean dead() {
        int x = ((int) s.getFirst().getX()) / dim;
        int y = ((int) s.getFirst().getY()) / dim;

        if (isInSnakeBody(headX, headY))
            return true;
        // the bounders
        if (headX > maxPosX || headY > maxPosY || headX < 0 || headY < 0)
            return true;

        return false;
    }

    private boolean didEat() {
        return (headX == foodX && headY == foodY);
    }

    private void begin() {
        s.add(new ImageView(this));
        s.get(0).setLayoutParams(lp);
        s.get(0).setImageResource(R.drawable.straight1);
        s.get(0).setX(2 * dim);
        s.get(0).setY(0);
        r.addView(s.get(0));

        s.addLast(new ImageView(this));
        s.get(1).setLayoutParams(lp);
        s.get(1).setImageResource(R.drawable.straight1);
        s.get(1).setX(dim);
        s.get(1).setY(0);
        r.addView(s.get(1));

        s.addLast(new ImageView(this));
        s.get(2).setLayoutParams(lp);
        s.get(2).setImageResource(R.drawable.straight1);
        s.get(2).setX(0);
        s.get(2).setY(0);
        r.addView(s.get(2));

        headX = 2;
        headY = 0;

        createFood();
    }

    private void createFood() {
        food = null;

        Random rn = new Random();
        foodX = rn.nextInt(maxPosX + 1);
        foodY = rn.nextInt(maxPosY + 1);
        while (isInSnake(foodX, foodY)) {
            foodX = rn.nextInt(maxPosX + 1);
            foodY = rn.nextInt(maxPosY + 1);
        }

        food = new ImageView(this);
        food.setLayoutParams(lp);
        food.setImageResource(R.drawable.red);
        food.setX(foodX * dim);
        food.setY(foodY * dim);
        r.addView(food);
    }

    private boolean isInSnake(int x, int y) {
        for (ImageView ex : s) {
            if ((int) ex.getX() / dim == x && (int) ex.getY() / dim == y)
                return true;
        }
        return false;
    }

    private boolean isInSnakeBody(int x, int y) {
        for (int i = 1; i < s.size(); i++) {
            if ((int) s.get(i).getX() / dim == x && (int) s.get(i).getY() / dim == y)
                return true;
        }
        return false;
    }

    boolean state = true;

    public void pausePlay(View view) {
        if (state) {
            state = false;
            ((ImageButton) view).setImageResource(R.drawable.ic_baseline_play_arrow_24);
            h2.removeCallbacks(r2);
        } else {
            state = true;
            ((ImageButton) view).setImageResource(R.drawable.ic_baseline_pause_24);
            onResume();
        }
    }

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

        }
    }
}