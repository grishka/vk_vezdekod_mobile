package me.grishka.vezdekod.donate;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.grishka.appkit.FragmentStackActivity;
import me.grishka.vezdekod.donate.fragments.StartFragment;

public class MainActivity extends FragmentStackActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		if(savedInstanceState==null){
			showFragmentClearingBackStack(new StartFragment());
		}
	}
}
