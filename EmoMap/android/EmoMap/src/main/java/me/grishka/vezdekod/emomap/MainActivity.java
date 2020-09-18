package me.grishka.vezdekod.emomap;

import android.app.Activity;
import android.os.Bundle;

import me.grishka.appkit.FragmentStackActivity;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.emomap.fragments.FeedFragment;

public class MainActivity extends FragmentStackActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		V.setApplicationContext(getApplicationContext());
		if(savedInstanceState==null)
			showFragmentClearingBackStack(new FeedFragment());
	}
}
