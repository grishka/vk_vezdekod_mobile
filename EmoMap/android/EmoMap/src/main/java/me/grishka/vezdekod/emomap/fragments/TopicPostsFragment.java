package me.grishka.vezdekod.emomap.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toolbar;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.Nav;
import me.grishka.appkit.fragments.BaseRecyclerFragment;
import me.grishka.appkit.utils.CubicBezierInterpolator;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.emomap.R;

public class TopicPostsFragment extends BaseRecyclerFragment<Object>{
	private RecyclerAdapter adapter;
	private boolean shadowVisible;
	private int shadowThreshold=V.dp(5);
	private Toolbar toolbar;
	private Drawable toolbarSeparator;
	private AnimatorSet currentAnim;
	private Animator.AnimatorListener animListener=new AnimatorListenerAdapter(){
		@Override
		public void onAnimationEnd(Animator animation){
			currentAnim=null;
		}
	};


	public TopicPostsFragment(){
		super(R.layout.post_list_layout, 20);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		onDataLoaded(Collections.nCopies(20, new Object()), false);
		setRefreshEnabled(false);
		setTitle(getArguments().getString("title"));
	}

	@Override
	protected void doLoadData(int offset, int count){

	}

	@Override
	protected RecyclerView.Adapter<?> getAdapter(){
		if(adapter==null)
			adapter=new RecyclerAdapter();
		return adapter;
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
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);

		updateToolbar();
		list.addOnScrollListener(new RecyclerView.OnScrollListener(){
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
				boolean showShadow=recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0))>0 || -recyclerView.getChildAt(0).getY()>shadowThreshold;
				if(showShadow!=shadowVisible){
					setShadowVisible(showShadow);
				}
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		updateToolbar();
	}

	protected void setShadowVisible(boolean visible){
		shadowVisible=visible;
		if(currentAnim!=null){
			currentAnim.cancel();
		}
		currentAnim=new AnimatorSet();
		currentAnim.playTogether(
				ObjectAnimator.ofFloat(toolbar, "elevation", visible ? V.dp(3) : 0),
				ObjectAnimator.ofInt(toolbarSeparator, "alpha", visible ? 0 : 255)
		);
		currentAnim.setInterpolator(CubicBezierInterpolator.DEFAULT);
		currentAnim.setDuration(200);
		currentAnim.addListener(animListener);
		currentAnim.start();
	}

	private void updateToolbar(){
		toolbar=getToolbar();
		Drawable navIcon=toolbar.getNavigationIcon();
		if(navIcon!=null){
			navIcon.setTint(0xFF3F8AE0);
		}
		LayerDrawable toolbarBg=(LayerDrawable) getResources().getDrawable(R.drawable.toolbar_bg).mutate();
		toolbarSeparator=toolbarBg.findDrawableByLayerId(R.id.separator);
		toolbar.setBackground(toolbarBg);
		toolbar.setElevation(shadowVisible ? V.dp(3) : 0);
		toolbarSeparator.setAlpha(shadowVisible ? 0 : 255);
	}

	private class RecyclerAdapter extends RecyclerView.Adapter<PostViewHolder>{

		@NonNull
		@Override
		public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
			return new PostViewHolder();
		}

		@Override
		public void onBindViewHolder(@NonNull PostViewHolder holder, int position){

		}

		@Override
		public int getItemCount(){
			return data.size();
		}
	}

	private class PostViewHolder extends RecyclerView.ViewHolder{
		public PostViewHolder(){
			super(LayoutInflater.from(getActivity()).inflate(R.layout.post2, list, false));
		}
	}
}
