package me.grishka.vezdekod.donate.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.grishka.appkit.Nav;
import me.grishka.vezdekod.donate.R;

public class NonRecurringDonationDetailsFragment extends CustomTypefaceToolbarFragment{

	private Spinner authorSpinner;
	private RadioGroup endConditionRadio;
	private Button datePicker, next;
	private TextView dateLabel;
	private int endDay, endMonth, endYear;

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setTitle("Дополнительно");
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.non_recurring_details, container, false);

		authorSpinner=view.findViewById(R.id.author_spinner);
		endConditionRadio=view.findViewById(R.id.end_cond_radiogroup);
		datePicker=view.findViewById(R.id.date_picker);
		dateLabel=view.findViewById(R.id.date_label);
		next=view.findViewById(R.id.next);

		ArrayAdapter<String> authorAdapter=new ArrayAdapter<>(getActivity(), R.layout.spinner_item);
		authorAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		authorAdapter.add("Григорий Клюшников");
		authorAdapter.add("Группа любителей старого дизайна");
		authorAdapter.add("ВКонтакте для Android 3.0");
		authorSpinner.setAdapter(authorAdapter);

		datePicker.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				int year, month, day;
				if(endYear==0){
					Calendar calendar=Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					year=calendar.get(Calendar.YEAR);
					month=calendar.get(Calendar.MONTH);
					day=calendar.get(Calendar.DAY_OF_MONTH);
				}else{
					year=endYear;
					month=endMonth;
					day=endDay;
				}
				DatePickerDialog dpd=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker picker, int year, int month, int day){
						endYear=year;
						endMonth=month;
						endDay=day;
						Date date=new Date(year-1900, month, day);
						datePicker.setText(DateFormat.getDateInstance(DateFormat.LONG, Locale.forLanguageTag("ru-RU")).format(date));
						updateButton();
					}
				}, year, month, day);
				dpd.getDatePicker().setMinDate(System.currentTimeMillis()+3600000*24);
				dpd.show();
			}
		});

		endConditionRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int selected){
				datePicker.setVisibility(selected==R.id.end_date ? View.VISIBLE : View.GONE);
				dateLabel.setVisibility(selected==R.id.end_date ? View.VISIBLE : View.GONE);
				updateButton();
			}
		});

		next.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Bundle args=(Bundle)getArguments().clone();
				args.putString("author", (String) authorSpinner.getSelectedItem());
				if(endConditionRadio.getCheckedRadioButtonId()==R.id.end_date){
					args.putInt("endDay", endDay);
					args.putInt("endMonth", endMonth);
					args.putInt("endYear", endYear);
				}
				Nav.go(getActivity(), SnippetFragment.class, args);
			}
		});

		return view;
	}

	private void updateButton(){
		int option=endConditionRadio.getCheckedRadioButtonId();
		next.setEnabled((option==R.id.end_date && endYear!=0) || option==R.id.end_entire_amount);
	}
}
