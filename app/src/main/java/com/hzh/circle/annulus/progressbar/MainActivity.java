package com.hzh.circle.annulus.progressbar;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.hzh.circle.annulus.progressbar.widget.CircleAnnulusProgressBar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //饼图控件
        final CircleAnnulusProgressBar progressBar = findViewById(R.id.progressBar);
        //提示问题
        final TextView tipText = findViewById(R.id.tipText);
        //设置进度最大值
        progressBar.setMax(100);
        //设置进度更新监听，每次更新时重新设置提示文字
        progressBar.addOnProgressUpdateListener(new CircleAnnulusProgressBar.OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(int progress) {
                tipText.setText("当前进度: ".concat(String.valueOf(progress)));
            }
        });
        //用值动画不断改变进度，测试进度
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