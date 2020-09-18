package me.grishka.vezdekod.emomap.fragments;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.Nav;
import me.grishka.appkit.fragments.BaseRecyclerFragment;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.emomap.R;

public class FeedFragment extends BaseRecyclerFragment<Object>{
	private RecyclerAdapter adapter;

	public FeedFragment(){
		super(R.layout.feed_layout, 20);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		onDataLoaded(Collections.nCopies(20, new Object()), false);
		setRefreshEnabled(false);
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
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		list.setBackgroundColor(0xFFECEDF1);
		list.addItemDecoration(new RecyclerView.ItemDecoration(){
			@Override
			public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state){
				outRect.left=outRect.right=V.dp(8);
				outRect.bottom=outRect.top=V.dp(5);
			}
		});
		list.setPadding(0, V.dp(5), 0, V.dp(5));
		list.setClipToPadding(false);

		view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Nav.go(getActivity(), MapFragment.class, null);
			}
		});
	}

	@Override
	public boolean wantsLightStatusBar(){
		return true;
	}

	@Override
	public boolean wantsLightNavigationBar(){
		return true;
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
			super(LayoutInflater.from(getActivity()).inflate(R.layout.post1, list, false));
		}
	}
}
