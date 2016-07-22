package com.zaodiandao.sharedtransition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 这里的作用是让状态栏从隐藏到显示的过程中,布局不受影响
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        setContentView(R.layout.activity_main);

        final ImageView iv = (ImageView) findViewById(R.id.iv1);
        final ImageView iv2 = (ImageView) findViewById(R.id.iv2);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open(view);
            }
        });

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open(view);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpening = false;
    }

    boolean isOpening;

    private void open(View view) {
        if (!isOpening) {
            // 防止重复点击
            isOpening = true;
            Intent i = new Intent(getApplicationContext(), DetailActivity.class);
            i.putExtra(Constants.ORIG_X, Util.getXY(view)[0]);
            i.putExtra(Constants.ORIG_Y, Util.getXY(view)[1]);
            i.putExtra(Constants.ORIG_WIDTH, view.getWidth());
            i.putExtra(Constants.ORIG_HEIGHT, view.getHeight());

            //Start activity disable animations
            startActivity(i);
            MainActivity.this.overridePendingTransition(0, 0);
        }
    }
}
