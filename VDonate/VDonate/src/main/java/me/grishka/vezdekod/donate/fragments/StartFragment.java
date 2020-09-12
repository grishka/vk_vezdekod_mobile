package me.grishka.vezdekod.donate.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toolbar;

import me.grishka.appkit.Nav;
import me.grishka.appkit.fragments.ToolbarFragment;
import me.grishka.appkit.fragments.WindowInsetsAwareFragment;
import me.grishka.vezdekod.donate.R;
import me.grishka.vezdekod.donate.TypefaceUtil;

public class StartFragment extends CustomTypefaceToolbarFragment{

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setTitle("Пожертвования");
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.start, container, false);
		view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Nav.go(getActivity(), DonationTypeFragment.class, null);
			}
		});
		return view;
	}
}
