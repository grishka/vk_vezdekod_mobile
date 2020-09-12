package me.grishka.vezdekod.donate.fragments;

import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;

import me.grishka.appkit.Nav;
import me.grishka.appkit.imageloader.ViewImageLoader;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.donate.R;
import me.grishka.vezdekod.donate.Utils;

public class SnippetFragment extends CustomTypefaceToolbarFragment{

	private long collected, target;
	private boolean isRecurring;
	private TextView progressText;
	private ProgressBar progressBar;

	private Random rand=new Random();

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.snippet, container, false);

		View snippet=view.findViewById(R.id.snippet);
		snippet.setOutlineProvider(new ViewOutlineProvider(){
			@Override
			public void getOutline(View view, Outline outline){
				outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), V.dp(8));
			}
		});
		snippet.setClipToOutline(true);
		snippet.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Nav.go(getActivity(), DonationDetailsFragment.class, (Bundle) getArguments().clone());
			}
		});

		TextView title=view.findViewById(R.id.title);
		TextView subtitle=view.findViewById(R.id.subtitle);
		ImageView cover=view.findViewById(R.id.cover_image);
		progressText=view.findViewById(R.id.progress_text);
		progressBar=view.findViewById(R.id.progress_bar);
		Button btn=view.findViewById(R.id.donate_button);

		Bundle args=getArguments();
		title.setText(args.getString("title"));
		isRecurring=args.getBoolean("isRecurring");
		String author=args.getString("author");
		target=args.getLong("amount");
		if(isRecurring){
			subtitle.setText(author+" ⋅ Помощь нужна каждый месяц");
		}else{
			if(args.containsKey("endYear")){
				subtitle.setText(author+" ⋅ Закончится "+Utils.daysToDate(args.getInt("endDay"), args.getInt("endMonth"), args.getInt("endYear")));
			}else{
				subtitle.setText(author);
			}
		}

		Uri coverURI=args.getParcelable("coverURI");
		if(coverURI!=null){
			ViewImageLoader.load(cover, null, coverURI.toString());
		}

		btn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				collected=Math.abs(rand.nextLong())%target;
				updateProgress();
			}
		});
		btn.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View view){
				collected=target;
				updateProgress();
				return true;
			}
		});

		updateProgress();

		return view;
	}

	private void updateProgress(){
		if(isRecurring){
			String[] months={
					"январе", "феврале", "марте", "апреле", "мае", "июне", "июле", "августе", "сентябре", "ноябре", "октябре", "декабре"
			};
			progressText.setText("Собрано в "+months[Calendar.getInstance().get(Calendar.MONTH)]+" "+Utils.formatCurrency(collected));
		}else{
			progressText.setText("Собрано "+Utils.formatCurrency(collected)+" из "+Utils.formatCurrency(target));
		}
		progressBar.setProgress((int)(Math.min(1f, (float)collected/(float)target)*1000));
	}
}
