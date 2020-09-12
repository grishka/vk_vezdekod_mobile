package me.grishka.vezdekod.donate.fragments;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.ScrollView;
import android.widget.Toolbar;

import me.grishka.appkit.fragments.ToolbarFragment;
import me.grishka.appkit.utils.CubicBezierInterpolator;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.donate.R;
import me.grishka.vezdekod.donate.TypefaceUtil;

public abstract class CustomTypefaceToolbarFragment extends ToolbarFragment{

	private ScrollView scroll;
	private boolean shadowVisible;
	private int shadowThreshold=V.dp(5);
	private Toolbar toolbar;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		TypefaceUtil.setTypefaceOnToolbarTitle(getToolbar());
		toolbar=getToolbar();
		toolbar.setElevation(0);
		Drawable navIcon=toolbar.getNavigationIcon();
		if(navIcon!=null){
			navIcon.setTint(0xFF3F8AE0);
		}
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
	public boolean wantsLightStatusBar(){
		return true;
	}

	@Override
	public boolean wantsLightNavigationBar(){
		return true;
	}

	protected void setShadowVisible(boolean visible){
		shadowVisible=visible;
		ObjectAnimator anim=ObjectAnimator.ofFloat(toolbar, "elevation", visible ? V.dp(3) : 0);
		anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
		anim.setDuration(200);
		anim.setAutoCancel(true);
		anim.start();
	}
}
