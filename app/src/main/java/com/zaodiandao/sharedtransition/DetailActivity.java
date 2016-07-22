package com.zaodiandao.sharedtransition;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

    //Anim duration
    private final int DURATION = 200;

    //Original x and y positions and target x,y position
    private int origX, origY;
    private float targetX, targetY;

    //Original width and height and target width and height
    private int targetWidth, targetHeight;
    private int origWidth, origHeight;

    private View dummyView;
    private ImageView mImageView;

    private ResizeTranslateAnimation resizeTranslateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 沉浸模式, 也需要在onWindowFocusChanged中重写
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        // 透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  // 5.0以上版本
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   // 4.4 ~ 5.0之间版本
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_detail);

        //Set parent view background transparent
        dummyView = findViewById(R.id.dummy);
        Util.setAlpha(dummyView, 0f);
        dummyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setVisibility(View.INVISIBLE);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        //Retrieve original values for x,y,width and height
        origX = getIntent().getIntExtra(Constants.ORIG_X, 0);
        origY = getIntent().getIntExtra(Constants.ORIG_Y, 0);
        origWidth = getIntent().getIntExtra(Constants.ORIG_WIDTH, 0);
        origHeight = getIntent().getIntExtra(Constants.ORIG_HEIGHT, 0);

        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean hasBeenMeasured = false;

            @Override
            public void onGlobalLayout() {
                if (mImageView.getViewTreeObserver().isAlive()) {
                    if (!hasBeenMeasured) {
                        //Save target x and y
                        targetX = Util.getXY(mImageView)[0];
                        targetY = Util.getXY(mImageView)[1];

                        //Save target width and height
                        targetWidth = mImageView.getWidth();
                        targetHeight = mImageView.getHeight();

                        //Set original width and height
                        mImageView.getLayoutParams().height = origHeight;
                        mImageView.getLayoutParams().width = origWidth;

                        //Set original pos x and y
                        Util.setX(mImageView, origX);
                        Util.setY(mImageView, origY);

                        mImageView.requestLayout();
                        hasBeenMeasured = true;
                    } else {
                        //Set resize animation
                        resizeTranslateAnimation = new ResizeTranslateAnimation(
                                mImageView, targetWidth, targetHeight, targetX, targetY);
                        resizeTranslateAnimation.setDuration(DURATION);
                        resizeTranslateAnimation.setAnimationListener(new SimpleAnimatorListener() {
                            public void onAnimationStart(Animation animation) {
                                mImageView.setVisibility(View.VISIBLE);
                                //Animate background alpha
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                                    dummyView.animate().alpha(1).setDuration(DURATION);
                                } else {
                                    dummyView.startAnimation(Util.alphaAnim(DURATION, 0.01f, 1f));
                                }
                            }
                        });
                        mImageView.setAnimation(resizeTranslateAnimation);

                        //Remove global layout listener
                        if (Build.VERSION.SDK_INT >= 16)
                            mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else
                            mImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    boolean isExiting = false;

    private void exit() {
        if (!isExiting) {
            // 防止重复点击
            isExiting = true;
            resizeTranslateAnimation.setInterpolator(new ReverseInterpolator());
            resizeTranslateAnimation.setAnimationListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //Animate background alpha
                    dummyView.startAnimation(Util.alphaAnim(DURATION, 1f, 0f));
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
            mImageView.startAnimation(resizeTranslateAnimation);
        }
    }

    private static class SimpleAnimatorListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}