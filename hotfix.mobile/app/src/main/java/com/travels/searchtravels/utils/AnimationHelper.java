package com.travels.searchtravels.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

public class AnimationHelper {
    public static void animateBtn(View view, Context context, AnimationFinished animationFinished){
        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(0, (int) ConverterUtil.convertDpToPixel(200, context))
                .setDuration(700);


// we want to manually handle how each tick is handled so add a
// listener
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                view.getLayoutParams().height = value.intValue();
                view.getLayoutParams().width = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                view.requestLayout();
            }
        });
        slideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animationFinished.finished();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

// create a new animationset
        AnimatorSet set = new AnimatorSet();
// since this is the only animation we are going to run we just use
// play
        set.play(slideAnimator);
// this is how you set the parabola which controls acceleration
        set.setInterpolator(new BounceInterpolator());
// start the animation
        set.start();
    }
    public static void animateBtnLoop(View view, Context context, int startWidth, int width){
        ValueAnimator slideAnimator = ValueAnimator
                .ofInt((int) ConverterUtil.convertDpToPixel(startWidth, context), (int) ConverterUtil.convertDpToPixel(width, context))
                .setDuration(1500);


// we want to manually handle how each tick is handled so add a
// listener
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                view.getLayoutParams().height = value.intValue();
                view.getLayoutParams().width = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                view.requestLayout();
            }
        });
        slideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animateBtnLoop(view, context, width, startWidth);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

// create a new animationset
        AnimatorSet set = new AnimatorSet();
// since this is the only animation we are going to run we just use
// play
        set.play(slideAnimator);
// this is how you set the parabola which controls acceleration
        set.setInterpolator(new LinearInterpolator());
// start the animation
        set.start();
    }
    public static void roundAnimation(View view, float degrees) {
        view.animate().rotationBy(degrees).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(2000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                roundAnimation(view, 360f);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}
