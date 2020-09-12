package me.grishka.vezdekod.donate.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.URI;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import me.grishka.appkit.Nav;
import me.grishka.appkit.imageloader.ViewImageLoader;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.donate.R;

public class CreateDonationFragment extends CustomTypefaceToolbarFragment{

	private static final int RESULT_IMAGE=304;

	private boolean isRecurring;
	private ScrollView scroller;
	private View coverWrap, coverRemoveBtn;
	private ImageView coverImage;
	private Uri coverURI;
	private EditText titleField, amountField, targetField, descrField;
	private Spinner destinationSpinner, authorSpinner;
	private Button nextButton;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		isRecurring=getArguments().getBoolean("isRecurring");
		setTitle(isRecurring ? "Регулярный сбор" : "Целевой сбор");
		if(Build.VERSION.SDK_INT>=27){
			setNavigationBarColor(0x80FFFFFF);
		}
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.create_donation, container, false);

		scroller=view.findViewById(R.id.scroll);
		coverWrap=view.findViewById(R.id.cover_wrap);
		coverRemoveBtn=view.findViewById(R.id.cover_remove);
		coverImage=view.findViewById(R.id.cover_image);
		titleField=view.findViewById(R.id.title_field);
		amountField=view.findViewById(R.id.amount_field);
		targetField=view.findViewById(R.id.target_field);
		descrField=view.findViewById(R.id.descr_field);
		destinationSpinner=view.findViewById(R.id.destination_spinner);
		authorSpinner=view.findViewById(R.id.author_spinner);
		nextButton=view.findViewById(R.id.next);

		coverImage.setClipToOutline(true);
		coverImage.setOutlineProvider(new ViewOutlineProvider(){
			@Override
			public void getOutline(View view, Outline outline){
				outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), V.dp(10));
			}
		});
		coverWrap.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, RESULT_IMAGE);
			}
		});
		coverRemoveBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				coverRemoveBtn.setVisibility(View.GONE);
				coverImage.setImageDrawable(null);
				coverURI=null;
			}
		});

		amountField.addTextChangedListener(new TextWatcher(){
			private boolean ignoreCallback=false;
			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after){
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count){

			}

			@Override
			public void afterTextChanged(Editable editable){
				if(ignoreCallback)
					return;
				ignoreCallback=true;
				try{
					String s=editable.toString();
					if(s.trim().equals("₽")){
						editable.clear();
						return;
					}
					String n=s.replaceAll("[^\\d]", "");
					if(n.length()>18)
						n=n.substring(0, 18);
					int selection=amountField.getSelectionEnd();
					if(n.length()<3){
						editable.replace(0, editable.length(), n+" ₽");
						amountField.setSelection(selection);
					}else{
						int selectionInDigits=0;
						for(int i=0;i<selection;i++){
							if(Character.isDigit(editable.charAt(i))) selectionInDigits++;
						}

						editable.clear();
						int parts=n.length()/3;
						int firstLen=n.length()%3;
						if(firstLen>0){
							editable.append(n.substring(0, firstLen));
							editable.append(' ');
						}
						for(int i=0;i<parts;i++){
							editable.append(n.substring(firstLen+i*3, firstLen+i*3+3));
							editable.append(' ');
						}
						editable.append('₽');

						amountField.setSelection(Math.max(0, selectionInDigits+(selectionInDigits/3-(selectionInDigits%3==0 ? 1 : 0))));
					}
				}finally{
					ignoreCallback=false;
					updateButtonState();
				}
			}
		});

		TextWatcher stateUpdater=new TextWatcher(){
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
		titleField.addTextChangedListener(stateUpdater);
		targetField.addTextChangedListener(stateUpdater);
		descrField.addTextChangedListener(stateUpdater);

		ArrayAdapter<String> destinationAdapter=new ArrayAdapter<>(getActivity(), R.layout.spinner_item);
		destinationAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		destinationAdapter.add("Счёт VK Pay ・ 1234");
		destinationAdapter.add("Счёт VK Pay ・ 5678");
		destinationAdapter.add("Хоть куда-нибудь");
		destinationSpinner.setAdapter(destinationAdapter);

		if(isRecurring){
			TextView amountLabel=view.findViewById(R.id.amount_label);
			amountLabel.setText("Сумма в месяц, ₽");
			amountField.setHint("Сколько нужно в месяц?");
			targetField.setHint("Например, поддержка приюта");

			ArrayAdapter<String> authorAdapter=new ArrayAdapter<>(getActivity(), R.layout.spinner_item);
			authorAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
			authorAdapter.add("Григорий Клюшников");
			authorAdapter.add("Группа любителей старого дизайна");
			authorAdapter.add("ВКонтакте для Android 3.0");
			authorSpinner.setAdapter(authorAdapter);

			nextButton.setText("Создать сбор");
		}else{
			view.findViewById(R.id.author_label).setVisibility(View.GONE);
			authorSpinner.setVisibility(View.GONE);
		}

		nextButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
				Bundle args=new Bundle();
				args.putParcelable("coverURI", coverURI);
				args.putString("title", titleField.getText().toString());
				long amount=Long.parseLong(amountField.getText().toString().replaceAll("[^\\d]", ""));
				if(amount==0)
					return;
				args.putLong("amount", amount);
				args.putString("target", targetField.getText().toString());
				args.putString("description", descrField.getText().toString());
				args.putBoolean("isRecurring", isRecurring);

				if(isRecurring){
					args.putString("author", (String)authorSpinner.getSelectedItem());
					Nav.go(getActivity(), SnippetFragment.class, args);
				}else{
					Nav.go(getActivity(), NonRecurringDonationDetailsFragment.class, args);
				}
			}
		});

		return view;
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==RESULT_IMAGE && resultCode==Activity.RESULT_OK){
			coverURI=data.getData();
			ViewImageLoader.load(coverImage, null, coverURI.toString());
			coverRemoveBtn.setVisibility(View.VISIBLE);
		}
	}

	private void updateButtonState(){
		nextButton.setEnabled(titleField.length()>0 && amountField.length()>0 && targetField.length()>0 && descrField.length()>0);
	}
}
