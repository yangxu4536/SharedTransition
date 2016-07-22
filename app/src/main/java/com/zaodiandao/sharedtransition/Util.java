package com.zaodiandao.sharedtransition;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

public class Util {

    public static void setY(View v, int y){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
        params.setMargins(params.leftMargin,y,params.rightMargin,params.bottomMargin);
        v.setLayoutParams(params);
    }

    public static void setX(View v, int x){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
        params.setMargins(x,params.topMargin,params.rightMargin,params.bottomMargin);
        v.setLayoutParams(params);
    }

    public static int getX(View v){
        Rect myViewRect = new Rect();
        v.getGlobalVisibleRect(myViewRect);
        return myViewRect.left;
    }

    public static int[] getXY(View v){
        int location[] = new int[2];
        v.getLocationOnScreen(location);
        return location;
    }

    public static int getY(View v){
        Rect myViewRect = new Rect();
        v.getGlobalVisibleRect(myViewRect);
        return v.getTop();
    }

    public static void setAlpha(View v, float alpha){
        if (Build.VERSION.SDK_INT < 11) {
            final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        } else
            v.setAlpha(alpha);
    }

    public static Animation alphaAnim(int duration, float from, float to){
        final AlphaAnimation animation = new AlphaAnimation(from, to);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        return animation;
    }
}