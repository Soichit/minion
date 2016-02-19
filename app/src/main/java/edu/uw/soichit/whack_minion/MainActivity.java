package edu.uw.soichit.whack_minion;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;
import android.os.Handler;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ViewGroup soichisLayout;
    View moleButton;
    ImageView explosionView;
    MediaPlayer punchSfx;
    MediaPlayer bombSfx;
    int count = 0;
    int currentPoints = 0;
    boolean bombOn = false;

    private Handler mHandler = new Handler();
    volatile boolean runningThread = false;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soichisLayout = (ViewGroup) findViewById(R.id.soichisLayout);
        //ImageButton moleImageButton = (ImageButton) findViewById(R.id.moleButton);
        moleButton = findViewById(R.id.moleButton);
        explosionView = (ImageView) findViewById(R.id.explosionView);
        punchSfx = MediaPlayer.create(this, R.raw.punch_sfx);
        bombSfx = MediaPlayer.create(this, R.raw.bomb_sfx);

        /*
        moleImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickedMole();
                    }
                }
        );
        */
    }

    public void onMinionClicked(View view) {
        if (count == 0) {
            clickedMole();
        } else {
            synchronized (thread) {
                thread.notifyAll();
            }
            clickedMole();
        }
    }

    public void countPoints(){
        TextView pointsNumber = (TextView) findViewById(R.id.pointsNumber);
        pointsNumber.setText("" + count);
    }

    public void clickedBomb() {
        bombOn = false;
        count = count - 5;
        countPoints();
        bombSfx.start();
        //explosionView.setVisibility(View.VISIBLE);
        //endExplosion();
    }
    /*
    public void endExplosion() {
        eHandler.postDelayed(new Runnable() {
            public void run() {
                moleButton.setVisibility(View.INVISIBLE);
            }
        }, 500);
    }
    */

    public void clickedMole() {
        runningThread = false;
        if (bombOn) {
            clickedBomb();
        } else {
            count++;
            countPoints();
            punchSfx.start();
        }
        moveMole();
    }

    public void moveMole() {
        currentPoints = count;
        runningThread = true;

        moleButton.setVisibility(View.VISIBLE);
        moleButton.setEnabled(true);
        moleButton.setBackgroundResource(R.drawable.minion_small);
        //TransitionManager.beginDelayedTransition(soichisLayout);

        Random rand = new Random();
        int bombChance = rand.nextInt(6);
        if (bombChance == 0) {
            moleButton.setBackgroundResource(R.drawable.bomb);
            bombOn = true;
        }
        int spot = rand.nextInt(6) + 1; //1, 2, 3, 4, 5, 6


        RelativeLayout.LayoutParams positionRules = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (spot == 1) {
            positionRules.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            positionRules.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        } else if (spot == 2) {
            positionRules.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            positionRules.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        } else if (spot == 3) {
            positionRules.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            positionRules.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        } else if (spot == 4) {
            positionRules.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            positionRules.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            positionRules.bottomMargin = 50;
        } else if (spot == 5) {
            positionRules.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            positionRules.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            positionRules.bottomMargin = 50;
        } else if (spot == 6) {
            positionRules.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            positionRules.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            positionRules.bottomMargin = 50;
        }

        moleButton.setLayoutParams(positionRules);
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_in);
        moleButton.startAnimation(animation);


        thread = new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(3000);
                    }
                }
                catch(InterruptedException ex){
                }
                moleDisappears();
            }
        };
        thread.start();


        /*
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (currentPoints == count) {
                    moleDisappears();
                }
            }
        }, 2000);
        */
    }


    public void moleDisappears() {
        moleButton.setVisibility(View.GONE);
        moleButton.setEnabled(false);
        moveMole();
        //moleAppears();
    }

    /*
    public void moleDisappears() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!runningThread) {
                    return;
                }
                moleButton.setVisibility(View.GONE);
                moleButton.setEnabled(false);
                moleAppears();
            }
        }, 2000);
    }
    */

    public void moleAppears() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                moleButton.setVisibility(View.VISIBLE);
                moleButton.setEnabled(true);
                moveMole();
            }
        }, 1000);
    }
}
