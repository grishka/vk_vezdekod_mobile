package me.grishka.vezdekod.podcasts.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.grishka.appkit.Nav;
import me.grishka.appkit.fragments.ToolbarFragment;
import me.grishka.vezdekod.podcasts.R;

public class IntroFragment extends ToolbarFragment{

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v=inflater.inflate(R.layout.intro, container, false);
		v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Nav.go(getActivity(), PodcastMetadataFragment.class, null);
			}
		});
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		getToolbar().setElevation(0);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		getToolbar().setElevation(0);
	}

	@Override
	public boolean wantsLightStatusBar(){
		return true;
	}

	@Override
	public boolean wantsLightNavigationBar(){
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.close, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId()==R.id.close){
			Nav.finish(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
