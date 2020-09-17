package me.grishka.vezdekod.podcasts.fragments;

import android.app.Activity;
import android.os.Build;
import android.view.WindowInsets;
import android.widget.ScrollView;

public abstract class BaseScrollingFragment extends BaseThemedFragment{

	protected ScrollView scroller;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		if(Build.VERSION.SDK_INT>=27){
			setNavigationBarColor(0x80FFFFFF);
		}
	}

	@Override
	public void onApplyWindowInsets(WindowInsets insets){
		if(Build.VERSION.SDK_INT>=27){
			scroller.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
			super.onApplyWindowInsets(insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), 0));
		}else{
			super.onApplyWindowInsets(insets);
		}
	}
}
