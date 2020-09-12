package me.grishka.vezdekod.donate.fragments;

import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import me.grishka.appkit.FragmentStackActivity;
import me.grishka.appkit.fragments.ToolbarFragment;
import me.grishka.appkit.imageloader.ViewImageLoader;
import me.grishka.appkit.utils.CubicBezierInterpolator;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.donate.R;
import me.grishka.vezdekod.donate.TypefaceUtil;
import me.grishka.vezdekod.donate.Utils;

public class DonationDetailsFragment extends ToolbarFragment{

	private long collected, target;
	private boolean isRecurring;
	private TextView progressText, bigProgressTarget, bigProgressCollected, bigProgressFullyCollected;
	private ProgressBar progressBar, bigProgressBar;
	private Toolbar toolbar;
	private ScrollView scroll;
	private View coverFader, statusBarBG;
	private int shadowThreshold=V.dp(140);
	private boolean shadowVisible=false, lightStatusBarUsed=false;

	private Random rand=new Random();

	public DonationDetailsFragment(){
		super(R.layout.donation_details);
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		content=inflater.inflate(layoutID, container, false);
		return content;
	}

	@Override
	public boolean wantsLightStatusBar(){
		return lightStatusBarUsed;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);

		TextView title=view.findViewById(R.id.title);
		TextView subtitle=view.findViewById(R.id.subtitle);
		TextView subsubtitle=view.findViewById(R.id.subsubtitle);
		TextView description=view.findViewById(R.id.description);
		ImageView cover=view.findViewById(R.id.cover_image);
		progressText=view.findViewById(R.id.progress_text);
		progressBar=view.findViewById(R.id.progress_bar);
		Button btn=view.findViewById(R.id.donate_button);
		TextView bigProgressTitle=view.findViewById(R.id.big_progress_title);
		bigProgressBar=view.findViewById(R.id.big_progress_bar);
		bigProgressTarget=view.findViewById(R.id.big_progress_target);
		bigProgressCollected=view.findViewById(R.id.big_progress_collected);
		bigProgressFullyCollected=view.findViewById(R.id.big_progress_fully_collected);
		coverFader=view.findViewById(R.id.cover_fader);
		statusBarBG=view.findViewById(R.id.status_bar_bg);

		Bundle args=getArguments();
		title.setText(args.getString("title"));
		isRecurring=args.getBoolean("isRecurring");
		String author=args.getString("author");
		subtitle.setText("Автор "+author);
		description.setText(args.getString("description"));
		target=args.getLong("amount");
		if(isRecurring){
			subsubtitle.setText("Помощь нужна каждый месяц");
			bigProgressTitle.setText("Нужно собрать в "+getCurrentMonth());
		}else{
			if(args.containsKey("endYear")){
				int year=args.getInt("endYear"), month=args.getInt("endMonth"), day=args.getInt("endDay");
				subsubtitle.setText("Сбор закончится "+Utils.daysToDate(day, month, year));
				Date date=new Date(year-1900, month, day);
				bigProgressTitle.setText("Нужно собрать до "+DateFormat.getDateInstance(DateFormat.LONG, Locale.forLanguageTag("ru-RU")).format(date));
			}else{
				bigProgressTitle.setText("Нужно собрать всю сумму");
				subsubtitle.setVisibility(View.GONE);
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

		toolbar=getToolbar();
		toolbar.setElevation(0);
		toolbar.setBackgroundColor(0xFFFFFFFF);
		toolbar.getBackground().setAlpha(0);
		toolbar.setNavigationIcon(toolbar.getNavigationIcon().mutate());
		setStatusBarColor(0);
		setUseLightStatusBar(false);
		scroll=view.findViewById(R.id.scroll);
		if(scroll!=null){
			scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener(){
				@Override
				public void onScrollChanged(){
					int y=scroll.getScrollY();
					float threshold=shadowThreshold-(toolbar.getHeight()+toolbar.getTranslationY());
					float fraction=y/threshold;
					if(y<=threshold){
						coverFader.setAlpha(fraction);
					}
					boolean showShadow=y>threshold;
					if(showShadow!=shadowVisible){
						setShadowVisible(showShadow);
					}
					boolean useLightStatusBar=fraction>=0.5f;
					if(useLightStatusBar!=lightStatusBarUsed){
						setUseLightStatusBar(useLightStatusBar);
					}
				}
			});
		}
	}

	private String getCurrentMonth(){
		String[] months={
				"январе", "феврале", "марте", "апреле", "мае", "июне", "июле", "августе", "сентябре", "ноябре", "октябре", "декабре"
		};
		return months[Calendar.getInstance().get(Calendar.MONTH)];
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		updateProgress();
	}

	private void updateProgress(){
		if(isRecurring){
			progressText.setText("Собрано в "+getCurrentMonth()+" "+Utils.formatCurrency(collected));
		}else{
			progressText.setText("Собрано "+Utils.formatCurrency(collected)+" из "+Utils.formatCurrency(target));
		}
		float fraction=Math.min(1f, (float)collected/(float)target);
		int progress=(int)(fraction*1000);
		progressBar.setProgress(progress);
		bigProgressBar.setProgress(progress);

		if(collected<target){
			bigProgressFullyCollected.setVisibility(View.GONE);
			bigProgressTarget.setVisibility(View.VISIBLE);
			bigProgressCollected.setVisibility(View.VISIBLE);
			bigProgressTarget.setText(Utils.formatCurrency(target));
			bigProgressCollected.setText(Utils.formatCurrency(collected));
			bigProgressBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
				@Override
				public boolean onPreDraw(){
					bigProgressBar.getViewTreeObserver().removeOnPreDrawListener(this);

					int targetWidth=bigProgressTarget.getWidth()+V.dp(16);
					int collectedWidth=bigProgressCollected.getWidth()+V.dp(16);
					int progressWidth=bigProgressBar.getWidth();
					int progressFilled=Math.round(progressWidth*fraction);
					int progressEmpty=progressWidth-progressFilled;

					if(progressFilled >= collectedWidth){
						bigProgressCollected.setTranslationX(progressFilled-collectedWidth+V.dp(8));
						bigProgressCollected.setTextColor(0xFFFFFFFF);
					}else{
						bigProgressCollected.setTranslationX(progressFilled+V.dp(8));
						bigProgressCollected.setTextColor(0xFF4BB34B);
					}

					if(progressEmpty >= targetWidth){
						bigProgressTarget.setTranslationY(V.dp(28));
						bigProgressTarget.setTranslationX(V.dp(-8));
					}else{
						bigProgressTarget.setTranslationX(0);
						bigProgressTarget.setTranslationY(0);
					}

					return true;
				}
			});
		}else{
			bigProgressFullyCollected.setVisibility(View.VISIBLE);
			bigProgressTarget.setVisibility(View.GONE);
			bigProgressCollected.setVisibility(View.GONE);
			bigProgressFullyCollected.setText(Utils.formatCurrency(target)+" собраны!");
		}
	}

	protected void setShadowVisible(boolean visible){
		shadowVisible=visible;
		ObjectAnimator anim=ObjectAnimator.ofFloat(toolbar, "elevation", visible ? V.dp(3) : 0);
		anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
		anim.setDuration(200);
		anim.setAutoCancel(true);
		anim.start();
		toolbar.getBackground().setAlpha(visible ? 255 : 0);
		statusBarBG.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}

	private void setUseLightStatusBar(boolean use){
		lightStatusBarUsed=use;
		((FragmentStackActivity)getActivity()).invalidateSystemBarColors(this);
		toolbar.getNavigationIcon().setTint(use ? 0xFF3F8AE0 : 0xFFFFFFFF);
	}

	@Override
	public void onApplyWindowInsets(WindowInsets insets){
		getToolbar().setTranslationY(insets.getSystemWindowInsetTop());
		statusBarBG.setScaleY(insets.getSystemWindowInsetTop());
		super.onApplyWindowInsets(insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom()));
	}
}
