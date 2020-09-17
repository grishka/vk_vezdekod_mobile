package me.grishka.vezdekod.podcasts.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toolbar;

import me.grishka.appkit.fragments.ToolbarFragment;
import me.grishka.appkit.utils.CubicBezierInterpolator;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.podcasts.R;

public abstract class BaseThemedFragment extends ToolbarFragment{
	private ScrollView scroll;
	private boolean shadowVisible;
	private int shadowThreshold=V.dp(5);
	private Toolbar toolbar;
	private Drawable toolbarSeparator;
	private AnimatorSet currentAnim;

	private Animator.AnimatorListener animListener=new AnimatorListenerAdapter(){
		@Override
		public void onAnimationEnd(Animator animation){
			currentAnim=null;
		}
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);

		updateToolbar();
		scroll=view.findViewById(R.id.scroll);
		if(scroll!=null){
			scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener(){
				@Override
				public void onScrollChanged(){
					int y=scroll.getScrollY();
					boolean showShadow=y>shadowThreshold;
					if(showShadow!=shadowVisible){
						setShadowVisible(showShadow);
					}
				}
			});
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		updateToolbar();
	}

	private void updateToolbar(){
		toolbar=getToolbar();
		Drawable navIcon=toolbar.getNavigationIcon();
		if(navIcon!=null){
			navIcon.setTint(0xFF3F8AE0);
		}
		LayerDrawable toolbarBg=(LayerDrawable) getResources().getDrawable(R.drawable.toolbar_bg).mutate();
		toolbarSeparator=toolbarBg.findDrawableByLayerId(R.id.separator);
		toolbar.setBackground(toolbarBg);
		toolbar.setElevation(shadowVisible ? V.dp(3) : 0);
		toolbarSeparator.setAlpha(shadowVisible ? 0 : 255);
	}

	@Override
	public boolean wantsLightStatusBar(){
		return true;
	}

	@Override
	public boolean wantsLightNavigationBar(){
		return true;
	}

	protected void setShadowVisible(boolean visible){
		shadowVisible=visible;
		if(currentAnim!=null){
			currentAnim.cancel();
		}
		currentAnim=new AnimatorSet();
		currentAnim.playTogether(
			ObjectAnimator.ofFloat(toolbar, "elevation", visible ? V.dp(3) : 0),
			ObjectAnimator.ofInt(toolbarSeparator, "alpha", visible ? 0 : 255)
		);
		currentAnim.setInterpolator(CubicBezierInterpolator.DEFAULT);
		currentAnim.setDuration(200);
		currentAnim.addListener(animListener);
		currentAnim.start();
	}
}
