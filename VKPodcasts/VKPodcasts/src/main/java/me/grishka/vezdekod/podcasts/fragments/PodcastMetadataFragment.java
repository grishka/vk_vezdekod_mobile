package me.grishka.vezdekod.podcasts.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import me.grishka.appkit.Nav;
import me.grishka.appkit.imageloader.ViewImageLoader;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.podcasts.AudioEditor;
import me.grishka.vezdekod.podcasts.R;
import me.grishka.vezdekod.podcasts.Utils;

public class PodcastMetadataFragment extends BaseScrollingFragment implements AudioEditor.OnDurationListener{

	private static final int RESULT_AUDIO=300;
	private static final int RESULT_COVER=301;

	private Button pickFileBtn, editAudoBtn, nextBtn;
	private ImageView coverImage;
	private View chosenFileView;
	private TextView fileName, fileDuration;
	private TextView uploadTitle, uploadText;

	private EditText titleEdit, descriptionEdit;
	private boolean fileChosen;
	private View privacyBtn;
	private TextView privacyValue;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setTitle("Новый подкаст");
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v=inflater.inflate(R.layout.podcast_meta, container, false);

		scroller=v.findViewById(R.id.scroll);
		pickFileBtn=v.findViewById(R.id.pick_file_btn);
		coverImage=v.findViewById(R.id.cover_image);
		chosenFileView=v.findViewById(R.id.chosen_file);
		uploadTitle=v.findViewById(R.id.upload_title_text);
		uploadText=v.findViewById(R.id.upload_sub_text);
		fileName=v.findViewById(R.id.file_name_text);
		fileDuration=v.findViewById(R.id.file_duration_text);
		editAudoBtn=v.findViewById(R.id.edit_audio_btn);
		titleEdit=v.findViewById(R.id.name_field);
		descriptionEdit=v.findViewById(R.id.description_field);
		nextBtn=v.findViewById(R.id.next_btn);
		privacyBtn=v.findViewById(R.id.privacy_btn);
		privacyValue=v.findViewById(R.id.privacy_value);

		nextBtn.setEnabled(false);


		coverImage.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, RESULT_COVER);
			}
		});
		coverImage.setClipToOutline(true);
		coverImage.setOutlineProvider(new ViewOutlineProvider(){
			@Override
			public void getOutline(View view, Outline outline){
				outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), V.dp(10));
			}
		});
		View.OnClickListener pickListener=new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("audio/*");
				startActivityForResult(intent, RESULT_AUDIO);
			}
		};
		pickFileBtn.setOnClickListener(pickListener);
		fileName.setOnClickListener(pickListener);
		editAudoBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Nav.go(getActivity(), AudioEditorFragment.class, null);
			}
		});

		TextWatcher buttonUpdater=new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

			}

			@Override
			public void afterTextChanged(Editable editable){
				updateButtonState();
			}
		};
		titleEdit.addTextChangedListener(buttonUpdater);
		descriptionEdit.addTextChangedListener(buttonUpdater);
		privacyBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				new AlertDialog.Builder(getActivity())
						.setItems(new String[]{"Всем пользователям", "Не всем пользователям"}, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialogInterface, int i){
								String[] opts=new String[]{"Всем пользователям", "Не всем пользователям"};
								privacyValue.setText(opts[i]);
							}
						})
						.show();
			}
		});

		nextBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				new AlertDialog.Builder(getActivity())
						.setTitle("Oops")
						.setMessage("А это я не успел сделать ¯\\_(ツ)_/¯")
						.setPositiveButton("OK", null)
						.show();
			}
		});


		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode!=Activity.RESULT_OK)
			return;
		if(requestCode==RESULT_AUDIO){
			Uri uri=data.getData();
			String name=Utils.getFileNameForContentUri(getActivity(), uri);
			uploadText.setVisibility(View.GONE);
			uploadTitle.setVisibility(View.GONE);
			pickFileBtn.setVisibility(View.GONE);
			chosenFileView.setVisibility(View.VISIBLE);
			fileName.setText(name);
			fileDuration.setText("--:--");

			AudioEditor.instance().loadFile(uri, getActivity().getContentResolver().getType(uri));
			fileChosen=true;
			updateButtonState();
		}else if(requestCode==RESULT_COVER){
			ViewImageLoader.load(coverImage, getResources().getDrawable(R.drawable.cover_btn), data.getDataString());
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		AudioEditor.instance().setDurationListener(this);
	}

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		AudioEditor.instance().setDurationListener(null);
	}

	@Override
	public void onDurationAvailable(long duration){
		long sec=duration/1000L;
		if(duration<3600000)
			fileDuration.setText(String.format(Locale.US, "%d:%02d", sec/60, sec%60));
		else
			fileDuration.setText(String.format(Locale.US, "%d:%02d:%02d", sec/3600, sec%3600/60, sec%60));
	}

	private void updateButtonState(){
		nextBtn.setEnabled(titleEdit.length()>0 && descriptionEdit.length()>0 && fileChosen);
	}
}
