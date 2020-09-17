package me.grishka.vezdekod.podcasts;

import android.app.Activity;
import android.os.Bundle;

import me.grishka.appkit.FragmentStackActivity;
import me.grishka.vezdekod.podcasts.fragments.IntroFragment;

public class MainActivity extends FragmentStackActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		if(savedInstanceState==null){
			showFragmentClearingBackStack(new IntroFragment());
		}
	}
}
