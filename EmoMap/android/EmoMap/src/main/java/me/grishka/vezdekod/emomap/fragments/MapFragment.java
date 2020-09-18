package me.grishka.vezdekod.emomap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.Nav;
import me.grishka.appkit.fragments.AppKitFragment;
import me.grishka.appkit.utils.BindableViewHolder;
import me.grishka.appkit.utils.V;
import me.grishka.appkit.views.UsableRecyclerView;
import me.grishka.vezdekod.emomap.R;

public class MapFragment extends AppKitFragment{

	private MapView mapView;
	private MapboxMap map;
	private MarkerViewManager markerViewManager;
	private RecyclerView topicList;
	private View moodSelector, emojiSides;
	private TextView moodEmoji, moodTitle;

	private static final String HAPPY="\uD83D\uDE03";
	private static final String HIGH_ENERGY="\uD83D\uDE1C";
	private static final String NEGATIVE="\uD83D\uDE41";
	private static final String LOW_ENERGY="\uD83D\uDE34";
	private static final String CALM="\uD83D\uDE0C";
	private static final String NEUTRAL="\uD83D\uDE10";

	private Topic[] topics={
			new Topic("Музыка", "\uD83C\uDFA7", HAPPY),
			new Topic("Фильмы", "\uD83C\uDF7F", HAPPY),
			new Topic("Осень", "\uD83C\uDF42", CALM),
			new Topic("Работа", "\uD83D\uDC54", HIGH_ENERGY),
			new Topic("Карантин", "\uD83D\uDE37", NEUTRAL),
			new Topic("IT", "\uD83D\uDCBB", NEUTRAL),
			new Topic("Авто", "\uD83D\uDE97", HAPPY),
			new Topic("Игры", "\uD83D\uDD79️", HIGH_ENERGY),
			new Topic("Искусство", "\uD83C\uDFA8", CALM),
			new Topic("Юмор", "\uD83C\uDFAD", HIGH_ENERGY),
			new Topic("Фотографии", "\uD83D\uDCF7", CALM)
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
		Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
		View v=inflater.inflate(R.layout.map, container, false);

		v.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				Nav.finish(MapFragment.this);
			}
		});

		mapView=v.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		mapView.getMapAsync(new OnMapReadyCallback(){
			@Override
			public void onMapReady(@NonNull MapboxMap mapboxMap){
				map=mapboxMap;
				mapboxMap.setStyle(Style.LIGHT);
				mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(59.93781089172377, 30.32849546210079)).zoom(12.998235032227575).padding(0, 0, 0, V.dp(12)).build()));

				mapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener(){
					@Override
					public void onMoveBegin(@NonNull MoveGestureDetector detector){
						V.setVisibilityAnimated(emojiSides, View.VISIBLE);
						V.setVisibilityAnimated(moodSelector, View.INVISIBLE);
					}

					@Override
					public void onMove(@NonNull MoveGestureDetector detector){

					}

					@Override
					public void onMoveEnd(@NonNull MoveGestureDetector detector){
//						Log.i("11", mapboxMap.getCameraPosition()+"");
						double lng=mapboxMap.getCameraPosition().target.getLongitude();
						if(lng>30.399538){
							selectMood(0);
						}else if(lng<30.0425){
							selectMood(2);
						}else{
							selectMood(1);
						}

						V.setVisibilityAnimated(emojiSides, View.INVISIBLE);
						V.setVisibilityAnimated(moodSelector, View.VISIBLE);
					}
				});

				markerViewManager=new MarkerViewManager(mapView, mapboxMap);

				markerViewManager.addMarker(makeMarkerView(topics[0], 59.891529, 30.503851, 2));
				markerViewManager.addMarker(makeMarkerView(topics[1], 59.912042, 30.487727, 1));
				markerViewManager.addMarker(makeMarkerView(topics[6], 59.887293, 30.484393, 0));

				markerViewManager.addMarker(makeMarkerView(topics[2], 59.945230, 30.334920, 1));
				markerViewManager.addMarker(makeMarkerView(topics[8], 59.9386354,30.3319917, 0));
				markerViewManager.addMarker(makeMarkerView(topics[10], 59.939114, 30.316089, 2));

				markerViewManager.addMarker(makeMarkerView(topics[4], 59.927043, 30.361038, 0));
				markerViewManager.addMarker(makeMarkerView(topics[5], 59.935687, 30.325728, 3));

				markerViewManager.addMarker(makeMarkerView(topics[3], 60.073760, 30.326297, 0));
				markerViewManager.addMarker(makeMarkerView(topics[7], 60.069909, 30.358077, 1));
				markerViewManager.addMarker(makeMarkerView(topics[9], 60.057953, 30.334686, 0));
			}
		});

		topicList=v.findViewById(R.id.topic_list);
		topicList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
		topicList.setAdapter(new TopicAdapter());

		emojiSides=v.findViewById(R.id.sides_emojis);
		moodSelector=v.findViewById(R.id.mood_selector);
		moodEmoji=v.findViewById(R.id.mood_emoji);
		moodTitle=v.findViewById(R.id.mood_title);

		moodSelector.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				PopupMenu menu=new PopupMenu(getActivity(), moodSelector, Gravity.BOTTOM);
				menu.getMenu().add(0, 0, 0, "Хорошее настроение");
				menu.getMenu().add(0, 1, 0, "Нейтральное настроение");
				menu.getMenu().add(0, 2, 0, "Плохое настроение");
				menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
					@Override
					public boolean onMenuItemClick(MenuItem menuItem){
						switch(menuItem.getItemId()){
							case 0:
								animateCamera(59.89591723087189, 30.505645668568093, 11.517583910395897);
								break;
							case 1:
								animateCamera(59.93781089172377, 30.32849546210079, 12.998235032227575);
								break;
							case 2:
								animateCamera(60.006590222300964, 29.72812027175567, 10.571378029642611);
								break;
						}
						selectMood(menuItem.getItemId());
						return true;
					}
				});
				menu.show();
			}
		});
		selectMood(1);

		return v;
	}

	@Override
	public void onStart(){
		super.onStart();
		mapView.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mapView.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		markerViewManager.onDestroy();
		mapView.onDestroy();
	}

	private MarkerView makeMarkerView(Topic topic, double lat, double lng, int size){
		int layout;
		switch(size){
			case 0:
				layout=R.layout.marker_small;
				break;
			case 1:
				layout=R.layout.marker_medium_notext;
				break;
			case 2:
				layout=R.layout.marker_medium_text;
				break;
			case 3:
				layout=R.layout.marker_large;
				break;
			default:
				throw new IllegalArgumentException();
		}
		View view=LayoutInflater.from(getActivity()).inflate(layout, mapView, false);
		TextView title=view.findViewById(R.id.title);
		TextView emoji=view.findViewById(R.id.emoji);
		if(title!=null)
			title.setText(topic.title);
		emoji.setText(topic.emoji);
		view.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				openTopic(topic);
			}
		});
		return new MarkerView(new LatLng(lat, lng), view);
	}

	private void selectMood(int index){
		switch(index){
			case 0:
				moodTitle.setText("Хорошее настроение");
				moodEmoji.setText(HAPPY);
				break;
			case 1:
				moodTitle.setText("Нейтральное настроение");
				moodEmoji.setText(NEUTRAL);
				break;
			case 2:
				moodTitle.setText("Плохое настроение");
				moodEmoji.setText(NEGATIVE);
				break;
		}
	}

	private void animateCamera(double lat, double lng, double zoom){
		map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(zoom).padding(0, 0, 0, V.dp(12)).build()));
	}

	private void openTopic(Topic topic){
		Bundle args=new Bundle();
		args.putString("title", topic.title);
		Nav.go(getActivity(), TopicPostsFragment.class, args);
	}

	private class Topic{
		public String title;
		public String emoji;
		public String emotionEmoji;

		public Topic(String title, String emoji, String emotionEmoji){
			this.title=title;
			this.emoji=emoji;
			this.emotionEmoji=emotionEmoji;
		}
	}

	private class TopicAdapter extends RecyclerView.Adapter<TopicViewHolder>{

		@NonNull
		@Override
		public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
			return new TopicViewHolder();
		}

		@Override
		public void onBindViewHolder(@NonNull TopicViewHolder holder, int position){
			holder.bind(topics[position]);
		}

		@Override
		public int getItemCount(){
			return topics.length;
		}
	}

	private class TopicViewHolder extends BindableViewHolder<Topic> implements UsableRecyclerView.Clickable{

		private TextView title, emoji, emotionEmoji;

		public TopicViewHolder(){
			super(getActivity(), R.layout.topic_cell, topicList);

			title=findViewById(R.id.title);
			emoji=findViewById(R.id.emoji);
			emotionEmoji=findViewById(R.id.emotion_emoji);
		}

		@Override
		public void onBind(Topic item){
			title.setText(item.title);
			emoji.setText(item.emoji);
			emotionEmoji.setText(item.emotionEmoji);
		}

		@Override
		public void onClick(){
			openTopic(item);
		}
	}
}
