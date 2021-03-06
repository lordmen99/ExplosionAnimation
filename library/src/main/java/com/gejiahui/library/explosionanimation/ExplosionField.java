package com.gejiahui.library.explosionanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.gejiahui.library.explosionanimation.particle.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gejiahui on 2015/12/28.
 */
public class ExplosionField extends View {
    Random random = new Random();
    private List<ExplosionAnimator> mExplosions = new ArrayList<>();
    public ExplosionField(Context context) {
        super(context);
    }

    public ExplosionField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExplosionField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void explode(Context context,final View view,Particle particle){
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        Rect frame = new Rect();
        ((Activity)getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = ((ViewGroup)getParent()).getTop();
        rect.offset(0, -contentTop-statusBarHeight );
        ValueAnimator animator = ValueAnimator.ofFloat(0,1).setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((random.nextFloat() - 0.5f) * view.getWidth() * 0.05f);
                view.setTranslationY((random.nextFloat() - 0.5f) * view.getHeight() * 0.05f);
            }
        });
        animator.start();
        view.animate().alpha(0).scaleY(0).scaleX(0).setStartDelay(100).setDuration(150).start();
        explode(Utils.createBitmapFromView(context,view), rect, Constant.DEFAULT_DELAY_TIME, Constant.DEFAULT_DURATION, particle, Constant.Mode.view);
    }


    private void explode(Bitmap bitmap, Rect bound, long startDelay, long duration,Particle particle,Constant.Mode mode) {
        final ExplosionAnimator explosion = new ExplosionAnimator(this, bitmap, bound,particle,mode);
        explosion.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mExplosions.remove(animation);
            }
        });
        explosion.setStartDelay(startDelay);
        explosion.setDuration(duration);
        mExplosions.add(explosion);
        explosion.start();
    }


    public void explode(Context context,View view,int x,int y,Particle particle){
        Rect rect = new Rect(x-120,y-120,x+120,y+120);
        int contentTop = ((ViewGroup)getParent()).getTop();
        rect.offset(0, -contentTop );
        explode(Utils.createBitmapFromView(context,view), rect, Constant.DEFAULT_DELAY_TIME, Constant.DEFAULT_TOUCH_DURATION, particle,Constant.Mode.touch);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (ExplosionAnimator explosion : mExplosions) {
            explosion.draw(canvas);
        }

    }

    public static ExplosionField attach2Window(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ExplosionField explosionField = new ExplosionField(activity);
        rootView.addView(explosionField, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return explosionField;
    }
}
