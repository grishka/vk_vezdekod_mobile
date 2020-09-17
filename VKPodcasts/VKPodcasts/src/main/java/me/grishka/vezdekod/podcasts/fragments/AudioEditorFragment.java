package me.grishka.vezdekod.podcasts.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import me.grishka.appkit.Nav;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.podcasts.AudioEditor;
import me.grishka.vezdekod.podcasts.R;
import me.grishka.vezdekod.podcasts.Timecode;
import me.grishka.vezdekod.podcasts.views.WaveformEditorView;

public class AudioEditorFragment extends BaseScrollingFragment{

	private WaveformEditorView waveformView;
	private ImageButton playButton, scissorsButton, undoButton;
	private ToggleButton musicButton, fadeInButton, fadeOutButton;
	private TextView musicText, fadeInText, fadeOutText, addTimecode;
	private LinearLayout timecodeContainer;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setTitle("Редактирование");
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v=inflater.inflate(R.layout.audio_editor, container, false);

		scroller=v.findViewById(R.id.scroll);
		waveformView=v.findViewById(R.id.waveform);
		playButton=v.findViewById(R.id.play_btn);
		scissorsButton=v.findViewById(R.id.scissors_btn);
		undoButton=v.findViewById(R.id.undo_btn);
		musicButton=v.findViewById(R.id.music_btn);
		fadeInButton=v.findViewById(R.id.fade_in_btn);
		fadeOutButton=v.findViewById(R.id.fade_out_btn);
		musicText=v.findViewById(R.id.music_text);
		fadeInText=v.findViewById(R.id.fade_in_text);
		fadeOutText=v.findViewById(R.id.fade_out_text);
		addTimecode=v.findViewById(R.id.add_timecode);
		timecodeContainer=v.findViewById(R.id.timecode_container);

		scissorsButton.setEnabled(false);
		undoButton.setEnabled(AudioEditor.instance().canUndo());

		AudioEditor.instance().setWaveformUpdateListener(waveformView);

		playButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(AudioEditor.instance().isPlaying()){
					AudioEditor.instance().pause();
					playButton.setImageResource(R.drawable.ic_play_24);
				}else{
					AudioEditor.instance().play();
					playButton.setImageResource(R.drawable.ic_pause_24);
					waveformView.invalidate();
				}
			}
		});
		waveformView.setOnSelectionChangedListener(new Runnable(){
			@Override
			public void run(){
				boolean enabled=waveformView.hasSelection();
				if(enabled!=scissorsButton.isEnabled())
					scissorsButton.setEnabled(enabled);
			}
		});
		scissorsButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				float start=waveformView.getSelectionStart();
				float end=waveformView.getSelectionEnd();
				if(end-start<10f){
					Toast.makeText(getActivity(), "Минимальная длина фрагмента — 10 секунд", Toast.LENGTH_SHORT).show();
					return;
				}

				AudioEditor.instance().crop(start, end);
				undoButton.setEnabled(true);
				scissorsButton.setEnabled(false);
				waveformView.setCropperAlpha(0f);
			}
		});
		undoButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				AudioEditor.instance().undo();
				undoButton.setEnabled(AudioEditor.instance().canUndo());
			}
		});
		AudioEditor.instance().setOnPlayerCompletionListener(new Runnable(){
			@Override
			public void run(){
				playButton.setImageResource(R.drawable.ic_play_24);
			}
		});
		fadeInButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b){
				V.setVisibilityAnimated(fadeInText, b ? View.VISIBLE : View.GONE);
			}
		});
		fadeOutButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b){
				V.setVisibilityAnimated(fadeOutText, b ? View.VISIBLE : View.GONE);
			}
		});
		musicButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				musicButton.setChecked(!musicButton.isChecked());
				if(AudioEditor.instance().getMusicInfo()!=null){
					new AlertDialog.Builder(getActivity())
							.setItems(new CharSequence[]{"Изменить музыку", Html.fromHtml("<font color=\"#E64646\">Удалить музыку</font>")}, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialogInterface, int i){
									if(i==1){
										AudioEditor.instance().setMusic(null, null);
										musicButton.setChecked(false);
										V.setVisibilityAnimated(musicText, View.GONE);
									}else{
										Nav.goForResult(getActivity(), MusicFragment.class, null, 1, AudioEditorFragment.this);
									}
								}
							})
							.show();
				}else{
					Nav.goForResult(getActivity(), MusicFragment.class, null, 1, AudioEditorFragment.this);
				}
			}
		});

		if(AudioEditor.instance().getMusicInfo()!=null){
			String info=AudioEditor.instance().getMusicInfo();
			musicText.setVisibility(View.VISIBLE);
			musicText.setText(info);
			musicButton.setChecked(true);
		}

		final View.OnClickListener removeTimecodeListener=new View.OnClickListener(){
			@Override
			public void onClick(View view){
				int index=timecodeContainer.indexOfChild((View) view.getParent());
				timecodeContainer.removeViewAt(index);
				AudioEditor.instance().timecodes.remove(index);
			}
		};

		addTimecode.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				View v=View.inflate(getActivity(), R.layout.timecode_row, null);
				v.findViewById(R.id.timecode_remove).setOnClickListener(removeTimecodeListener);
				EditText timeEdit=v.findViewById(R.id.timecode_time);
				int sec=(int) AudioEditor.instance().getPlaybackPosition();
				if(sec<3600)
					timeEdit.setText(String.format(Locale.US, "%d:%02d", sec/60, sec%60));
				else
					timeEdit.setText(String.format(Locale.US, "%d:%02d:%02d", sec/3600, sec%3600/60, sec%60));
				timecodeContainer.addView(v);
				AudioEditor.instance().timecodes.add(new Timecode());
			}
		});

		for(Timecode t:AudioEditor.instance().timecodes){
			View tv=View.inflate(getActivity(), R.layout.timecode_row, null);
			EditText timeEdit=tv.findViewById(R.id.timecode_time);
			int sec=t.time;
			if(sec<3600)
				timeEdit.setText(String.format(Locale.US, "%d:%02d", sec/60, sec%60));
			else
				timeEdit.setText(String.format(Locale.US, "%d:%02d:%02d", sec/3600, sec%3600/60, sec%60));

			EditText titleEdit=tv.findViewById(R.id.timecode_title);
			titleEdit.setText(t.title);
			tv.findViewById(R.id.timecode_remove).setOnClickListener(removeTimecodeListener);


			timecodeContainer.addView(tv);
		}

		return v;
	}

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		AudioEditor.instance().setWaveformUpdateListener(null);
		AudioEditor.instance().setOnPlayerCompletionListener(null);
	}

	@Override
	public void onPause(){
		super.onPause();
		if(AudioEditor.instance().isPlaying()){
			AudioEditor.instance().pause();
			playButton.setImageResource(R.drawable.ic_play_24);
		}

		AudioEditor.instance().timecodes.clear();

		for(int i=0;i<timecodeContainer.getChildCount();i++){
			View v=timecodeContainer.getChildAt(i);
			EditText timeEdit=v.findViewById(R.id.timecode_time);
			EditText titleEdit=v.findViewById(R.id.timecode_title);

			Timecode timecode=new Timecode();
			timecode.title=titleEdit.getText().toString();
			String[] time=timeEdit.getText().toString().split(":");
			if(time.length<2 || time.length>3)
				continue;
			if(time.length==2)
				timecode.time=Integer.parseInt(time[0])*60+Integer.parseInt(time[1]);
			else if(time.length==3)
				timecode.time=Integer.parseInt(time[0])*3600+Integer.parseInt(time[1])*60+Integer.parseInt(time[2]);
			AudioEditor.instance().timecodes.add(timecode);
		}
		Collections.sort(AudioEditor.instance().timecodes, new Comparator<Timecode>(){
			@Override
			public int compare(Timecode a, Timecode b){
				return a.time-b.time;
			}
		});
	}

	@Override
	public void onFragmentResult(int reqCode, boolean success, Bundle result){
		if(success){
			musicButton.setChecked(true);
			V.setVisibilityAnimated(musicText, View.VISIBLE);
			String info=result.getString("artist")+" — "+result.getString("title");
			musicText.setText(info);
			Uri uri=result.getParcelable("url");
			AudioEditor.instance().setMusic(uri, info);
		}
	}
}
