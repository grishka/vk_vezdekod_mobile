package me.grishka.vezdekod.podcasts.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.grishka.appkit.Nav;
import me.grishka.vezdekod.podcasts.R;
import me.grishka.vezdekod.podcasts.Utils;

public class MusicFragment extends BaseThemedFragment{

	private static final int RESULT_AUDIO=203;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setTitle("Выбрать музыку");
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v=inflater.inflate(R.layout.music, container, false);

		v.findViewById(R.id.music1).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Bundle b=new Bundle();
				b.putString("artist", "Rick Astley");
				b.putString("title", "Never Gonna Give You Up");
				b.putParcelable("url", Uri.parse("https://grishka.me/apps/vezdekod/music1.mp3"));
				setResult(true, b);
				Nav.finish(MusicFragment.this);
			}
		});
		v.findViewById(R.id.music2).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Bundle b=new Bundle();
				b.putString("artist", "Smash Mouth");
				b.putString("title", "All Star");
				b.putParcelable("url", Uri.parse("https://grishka.me/apps/vezdekod/music2.mp3"));
				setResult(true, b);
				Nav.finish(MusicFragment.this);
			}
		});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.music, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId()==R.id.add){
			Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("audio/*");
			startActivityForResult(intent, RESULT_AUDIO);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==RESULT_AUDIO && resultCode==Activity.RESULT_OK){
			Bundle b=new Bundle();
			b.putString("artist", "Неизвестен");
			b.putString("title", Utils.getFileNameForContentUri(getActivity(), data.getData()));
			b.putParcelable("url", data.getData());
			setResult(true, b);
			Nav.finish(MusicFragment.this);
		}
	}
}
