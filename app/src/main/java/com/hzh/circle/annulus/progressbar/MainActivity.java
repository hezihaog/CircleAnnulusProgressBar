package com.hzh.circle.annulus.progressbar;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.hzh.circle.annulus.progressbar.widget.CircleAnnulusProgressBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CircleAnnulusProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.addOnProgressUpdateListener(new CircleAnnulusProgressBar.OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(int progress) {
                Log.d(TAG, "progress ==>".concat(String.valueOf(progress)));
            }
        });

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer cValue = (Integer) animation.getAnimatedValue();
                progressBar.setProgress(cValue);
            }
        });
        animator.start();
    }
}
