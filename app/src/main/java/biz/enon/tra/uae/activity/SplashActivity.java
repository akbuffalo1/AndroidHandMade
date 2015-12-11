package biz.enon.tra.uae.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.enon.tra.uae.R;

import java.util.Random;

import biz.enon.tra.uae.activity.base.BaseActivity;

public class SplashActivity extends BaseActivity {

//    private static final int SPLASH_DELAY = 100000;

    public static final String PROGRESS_PROPERTY = "progress";
    private static final int MIN_DURATION = 2000; //ms
    private static final int MAX_DURATION = 3500; //ms

    private ProgressBar pbLoadingProgress;

    private boolean mIsPaused, mIsFinishLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pbLoadingProgress = findView(R.id.pbLoadingProgress_AS);

        ObjectAnimator animator = ObjectAnimator.ofInt(pbLoadingProgress, PROGRESS_PROPERTY, 0, 100);
        animator.setDuration(new Random().nextInt(MAX_DURATION - MIN_DURATION) + MIN_DURATION);
        animator.setInterpolator(getRandomInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsFinishLoading = true;
                invalidateLoadingState();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        animator.start();

//        // Run post delayed activity start
//        new Handler().postDelayed(new Runnable() {
//
//            /*
//             * Showing splash screen with a timer. This will be useful when you
//             */
//            @Override
//            public void run() {
//
//                // We are starting the Main activity
//                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
//                startActivity(i);
//
//                // close this activity
//                finish();
//            }
//        }, /*BuildConfig.DEBUG ? 0 :*/ SPLASH_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPaused = false;
        invalidateLoadingState();
    }

    private void invalidateLoadingState() {
        if (!mIsPaused && mIsFinishLoading) {
            // We are starting the Main activity
            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(i);

            // close this activity
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
    }

    @NonNull
    private TimeInterpolator getRandomInterpolator() {
        int randomNumber = new Random().nextInt(4);
        switch (randomNumber) {
            case 0:
                return new AccelerateDecelerateInterpolator();
            case 1:
                return new DecelerateInterpolator();
            case 2:
                return new FastOutSlowInInterpolator();
            case 3:
                return new LinearInterpolator();
            default:
                return new LinearOutSlowInInterpolator();
        }
    }
}