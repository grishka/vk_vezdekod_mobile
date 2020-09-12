package me.grishka.vezdekod.donate.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import me.grishka.appkit.Nav;
import me.grishka.appkit.fragments.ToolbarFragment;
import me.grishka.vezdekod.donate.R;

public class DonationTypeFragment extends CustomTypefaceToolbarFragment{

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setTitle("Тип сбора");
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.donation_type, container, false);

		view.findViewById(R.id.type_target).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				openForm(false);
			}
		});

		view.findViewById(R.id.type_recurring).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				openForm(true);
			}
		});

		return view;
	}

	private void openForm(boolean recurring){
		Bundle args=new Bundle();
		args.putBoolean("isRecurring", recurring);
		Nav.go(getActivity(), CreateDonationFragment.class, args);
	}
}
