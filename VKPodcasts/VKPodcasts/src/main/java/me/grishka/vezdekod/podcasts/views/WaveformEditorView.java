package me.grishka.vezdekod.podcasts.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import java.util.Locale;

import androidx.annotation.Nullable;
import me.grishka.appkit.utils.CubicBezierInterpolator;
import me.grishka.appkit.utils.V;
import me.grishka.vezdekod.podcasts.AudioEditor;
import me.grishka.vezdekod.podcasts.R;

public class WaveformEditorView extends View implements AudioEditor.OnWaveformUpdateListener{

	private float[] waveform;
	private int waveformOffset, waveformCount;
	private float[] waveformLines;
	private float[] tickLines=new float[40];
	private float startTime, endTime;
	private Scroller scroller;
	private float[] tmpFloat={0, 0, 0, 0};
	private int timeLabelGapSeconds=1;
	private boolean wasPinching;
	private float linesOffset=0f;
	private VelocityTracker velocityTracker;
	private int prevScrollerX;
	private Drawable cursorTriangle;
	private boolean seeking, restartPlayerAfterSeeking;
	private float seekTime=-1;

	private float cropperStart=0, cropperEnd=1, cropperAlpha;
	private boolean draggingCropperEnd, draggingCropperStart;

	private float pinchPointer1InitialX, pinchPointer2InitialX, pinchBeginStartTime, pinchBeginEndTime;

	private Matrix matrix=new Matrix(), currentPinchMatrix=new Matrix();

	private Paint waveformPaint=new Paint(Paint.ANTI_ALIAS_FLAG),
					waveformInactivePaint=new Paint(Paint.ANTI_ALIAS_FLAG),
					bgPaint=new Paint(Paint.ANTI_ALIAS_FLAG),
					bgStrokePaint=new Paint(Paint.ANTI_ALIAS_FLAG),
					timePaint=new Paint(Paint.ANTI_ALIAS_FLAG),
					timeTicksPaint=new Paint(),
					cursorPaint=new Paint(Paint.ANTI_ALIAS_FLAG),
					cropperRectPaint=new Paint(),
					cropperHandlePaint=new Paint(Paint.ANTI_ALIAS_FLAG);

	private GestureDetector gestureDetector;

	private Runnable onSelectionChangedListener;

	public WaveformEditorView(Context context){
		super(context);
		init();
	}

	public WaveformEditorView(Context context, @Nullable AttributeSet attrs){
		super(context, attrs);
		init();
	}

	public WaveformEditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init(){
		waveformPaint.setColor(0xFF3F8AE0);
		waveformPaint.setStyle(Paint.Style.STROKE);
		waveformPaint.setStrokeCap(Paint.Cap.ROUND);
		waveformPaint.setStrokeWidth(V.dp(2));

		waveformInactivePaint.setColor(0xFFC7DCF5);
		waveformInactivePaint.setStyle(Paint.Style.STROKE);
		waveformInactivePaint.setStrokeCap(Paint.Cap.ROUND);
		waveformInactivePaint.setStrokeWidth(V.dp(2));

		bgPaint.setColor(0xFFF2F3F5);
		bgStrokePaint.setStyle(Paint.Style.STROKE);
		bgStrokePaint.setColor(0xFFD7D8D9);
		bgStrokePaint.setStrokeWidth(V.dp(.5f));

		timePaint.setColor(0xFF99A2AD);
		timePaint.setTextSize(V.dp(9));

		timeTicksPaint.setColor(0xFF99A2AD);
		timeTicksPaint.setStyle(Paint.Style.STROKE);
		timeTicksPaint.setStrokeWidth(V.dp(.5f));

		cursorPaint.setColor(0xFFFF3347);
		cursorPaint.setStrokeWidth(V.dp(1));

		cropperRectPaint.setColor(0xFF3F8AE0);

		cropperHandlePaint.setColor(0xffffffff);

		cursorTriangle=getResources().getDrawable(R.drawable.audio_cursor);

		scroller=new Scroller(getContext());

		gestureDetector=new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
				if(seeking || draggingCropperEnd || draggingCropperStart)
					return true;

				float scale=waveformCount/(endTime-startTime);
				int maxX=Math.round(getWidth()*scale-getWidth());
				int curX=prevScrollerX=Math.round(getWidth()*scale*(startTime/waveformCount));
				scroller.fling(curX, 0, Math.round(velocityX), 0, 0, maxX, 0, 0);

				invalidate();
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
				if(seeking){
					if(AudioEditor.instance().isPlaying()){
						AudioEditor.instance().pause();
						restartPlayerAfterSeeking=true;
					}
					seekTime=startTime+e2.getX()/getWidth()*(endTime-startTime);
					invalidate();
					return true;
				}else if(draggingCropperStart){
					cropperStart+=(-distanceX)/getWidth();
					cropperStart=Math.min(cropperEnd-0.1f, Math.max(0f, cropperStart));
					invalidate();
					if(onSelectionChangedListener!=null)
						onSelectionChangedListener.run();
					return true;
				}else if(draggingCropperEnd){
					cropperEnd+=(-distanceX)/getWidth();
					cropperEnd=Math.min(1f, Math.max(cropperEnd, cropperStart+0.1f));
					invalidate();
					if(onSelectionChangedListener!=null)
						onSelectionChangedListener.run();
					return true;
				}

				float scale=waveformCount/(endTime-startTime);
				float scrollerDeltaPx=distanceX;
				float fraction=scrollerDeltaPx/(getWidth()*scale);
				matrix.postTranslate(waveformCount*fraction, 0f);
				updateScrollOffset(matrix);

				updateLines();
				invalidate();
				return true;
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e){
				if(seeking){
					AudioEditor.instance().seek(startTime+e.getX()/getWidth()*(endTime-startTime));
					invalidate();
				}else{
					ObjectAnimator anim=ObjectAnimator.ofFloat(WaveformEditorView.this, "cropperAlpha", 1f).setDuration(200);
					anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
					anim.start();
					postDelayed(new Runnable(){
						@Override
						public void run(){
							if(cropperEnd==1f && cropperStart==0f){
								ObjectAnimator anim=ObjectAnimator.ofFloat(WaveformEditorView.this, "cropperAlpha", 0f).setDuration(200);
								anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
								anim.start();
							}
						}
					}, 2000);
				}
				return true;
			}

			@Override
			public boolean onDown(MotionEvent e){
				if(!scroller.isFinished()){
					scroller.forceFinished(true);
				}
				wasPinching=false;
				seeking=e.getY()<V.dp(36);
				restartPlayerAfterSeeking=false;
				seekTime=-1;
				if(!seeking && cropperAlpha==1f){
					float startX=Math.round(cropperStart*getWidth());
					float endX=Math.round(cropperEnd*getWidth());
					float x=e.getX();
					if(x>startX-V.dp(6) && x<startX+V.dp(24)){
						draggingCropperStart=true;
					}else if(x>endX-V.dp(24) && x<endX+V.dp(6)){
						draggingCropperEnd=true;
					}else{
						draggingCropperStart=draggingCropperEnd=false;
					}
				}
				return true;
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		if(w!=oldw){
			waveformLines=new float[(w/V.dp(6)+2)*4];
			updateLines();
		}
	}

	@Override
	protected void onDraw(Canvas canvas){
		if(waveformLines==null)
			return;

		if(scroller.computeScrollOffset()){
			float scale=waveformCount/(endTime-startTime);
			int scrollerX=scroller.getCurrX();
			int scrollerDeltaPx=-(scrollerX-prevScrollerX);
			float fraction=scrollerDeltaPx/(getWidth()*scale);
			matrix.postTranslate(waveformCount*fraction, 0f);
			updateScrollOffset(matrix);
			prevScrollerX=scrollerX;

			updateLines();
			invalidate();
		}

		canvas.drawRoundRect(0, V.dp(10), getWidth(), getHeight()+V.dp(10), V.dp(8), V.dp(8), bgPaint);

		canvas.save();
		canvas.translate(linesOffset, V.dp(10));

		if(cropperEnd<1 || cropperStart>0){
			float startX=Math.round(cropperStart*getWidth())-linesOffset;
			float endX=Math.round(cropperEnd*getWidth())-linesOffset;
			canvas.save();
			canvas.clipRect(0, 0, startX, getHeight());
			canvas.drawLines(waveformLines, waveformInactivePaint);
			canvas.restore();
			canvas.save();
			canvas.clipRect(endX, 0, getWidth(), getHeight());
			canvas.drawLines(waveformLines, waveformInactivePaint);
			canvas.restore();
			canvas.save();
			canvas.clipRect(startX, 0, endX, getHeight());
			canvas.drawLines(waveformLines, waveformPaint);
			canvas.restore();
		}else{
			canvas.drawLines(waveformLines, waveformPaint);
		}

		canvas.restore();

		float secondSize=getWidth()/(endTime-startTime);
		int lineGroupCount=(int) Math.ceil(getWidth()/(secondSize*10f))+1;
		canvas.save();
		int offset=(int)(startTime-(startTime%timeLabelGapSeconds));

		canvas.translate(-(startTime-offset)*secondSize+V.dp(3)+linesOffset, V.dp(10));
		for(int i=0;i<lineGroupCount;i++){
			canvas.drawLines(tickLines, timeTicksPaint);
			String time;
			int timeAtLabel=offset+timeLabelGapSeconds*(i+1);
			if(waveformCount>3600){
				time=String.format(Locale.US, "%d:%02d:%02d", timeAtLabel/3600, timeAtLabel%3600/60, timeAtLabel%60);
			}else{
				time=String.format(Locale.US, "%d:%02d", timeAtLabel/60, timeAtLabel%60);
			}
			canvas.drawText(time, secondSize*(timeLabelGapSeconds*0.9f)-timePaint.measureText(time)/2, V.dp(5)-timePaint.ascent(), timePaint);
			canvas.translate(secondSize*timeLabelGapSeconds, 0);
		}
		canvas.restore();

		float strokeOffset=bgStrokePaint.getStrokeWidth()/2;
		canvas.drawRoundRect(strokeOffset, strokeOffset+V.dp(10), getWidth()-strokeOffset, getHeight()+V.dp(10), V.dp(8), V.dp(8), bgStrokePaint);
		float rulerBottom=V.dp(26+10)-bgStrokePaint.getStrokeWidth()/2f;
		canvas.drawLine(0, rulerBottom, getWidth(), rulerBottom, bgStrokePaint);

		float cursorPosition=getCursorTime();
		if(cursorPosition>=startTime && cursorPosition<=endTime){
			int cursorX=Math.round((cursorPosition-startTime)/(endTime-startTime)*getWidth()+linesOffset);
			canvas.drawRect(cursorX, V.dp(2), cursorX+V.dp(1), getHeight(), cursorPaint);
			cursorX-=V.dp(2);
			cursorTriangle.setBounds(cursorX-cursorTriangle.getIntrinsicWidth()/2, 0, cursorX+cursorTriangle.getIntrinsicWidth(), cursorTriangle.getIntrinsicHeight());
			cursorTriangle.draw(canvas);
		}

		if(cropperAlpha>0){
			if(cropperAlpha<1){
				canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), Math.round(cropperAlpha*255));
			}
			float startX=Math.round(cropperStart*getWidth());
			float endX=Math.round(cropperEnd*getWidth());
			canvas.drawRect(startX, V.dp(36), startX+V.dp(6), getHeight(), cropperRectPaint);
			canvas.drawRect(endX-V.dp(6), V.dp(36), endX, getHeight(), cropperRectPaint);
			canvas.drawRect(startX, V.dp(36), endX, V.dp(37), cropperRectPaint);
			canvas.drawRect(startX, getHeight(), endX, getHeight()-V.dp(1), cropperRectPaint);
			canvas.drawRoundRect(startX+V.dp(2), V.dp(36+42), startX+V.dp(4), V.dp(36+42+12), V.dp(1), V.dp(1), cropperHandlePaint);
			canvas.drawRoundRect(endX-V.dp(4), V.dp(36+42), endX-V.dp(2), V.dp(36+42+12), V.dp(1), V.dp(1), cropperHandlePaint);
			if(cropperAlpha<1){
				canvas.restore();
			}
		}

		if(AudioEditor.instance().isPlaying()){
			invalidate();
		}
	}

	@Override
	public void onWaveformUpdated(int processed, int offset, int count){
		waveform=AudioEditor.instance().getWaveform();
		if(waveformCount!=count){
			endTime=count;
			startTime=0;
			matrix.reset();
			cropperEnd=1;
			cropperStart=0;
		}
		waveformOffset=offset;
		waveformCount=count;
		updateLines();
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev){
		if(ev.getPointerCount()==2){
			if(ev.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN){
				pinchPointer1InitialX=ev.getX(0);
				pinchPointer2InitialX=ev.getX(1);
				pinchBeginStartTime=startTime;
				pinchBeginEndTime=endTime;
				return true;
			}else if(ev.getAction()==MotionEvent.ACTION_MOVE){
				float distanceRatio=Math.abs(ev.getX(0)-ev.getX(1))/Math.abs(pinchPointer1InitialX-pinchPointer2InitialX);
				float centerTime=pinchBeginStartTime+(Math.abs(pinchPointer1InitialX-pinchPointer2InitialX)/getWidth()*(pinchBeginEndTime-pinchBeginStartTime));

				currentPinchMatrix.set(matrix);
				currentPinchMatrix.postScale(1f/distanceRatio, 1f, centerTime, 0f);
				tmpFloat[0]=0f;
				tmpFloat[2]=waveformCount-1;
				currentPinchMatrix.mapPoints(tmpFloat);
				if(tmpFloat[0]<0f && tmpFloat[2]>waveformCount)
					currentPinchMatrix.reset();

				startTime=Math.max(tmpFloat[0], 0f);
				endTime=Math.min(tmpFloat[2], waveformCount-1);

				updateLines();
				invalidate();
			}else if(ev.getActionMasked()==MotionEvent.ACTION_POINTER_UP){
				matrix.set(currentPinchMatrix);
				wasPinching=true;
			}
		}else if(ev.getPointerCount()==1){
			if(wasPinching && ev.getAction()!=MotionEvent.ACTION_DOWN){
				return false;
			}
			if(ev.getAction()==MotionEvent.ACTION_UP || ev.getAction()==MotionEvent.ACTION_CANCEL){
				if(seeking && seekTime>0){
					seeking=false;
					AudioEditor.instance().seek(seekTime);
					if(restartPlayerAfterSeeking)
						AudioEditor.instance().play();
					invalidate();
				}
				draggingCropperStart=draggingCropperEnd=false;
			}
			return gestureDetector.onTouchEvent(ev);
		}
		return false;
	}

	private void updateScrollOffset(Matrix matrix){
		tmpFloat[0]=0f;
		tmpFloat[2]=waveformCount-1;
		matrix.mapPoints(tmpFloat);
		if(tmpFloat[0]<0f){
			matrix.postTranslate(-tmpFloat[0], 0);
		}else if(tmpFloat[2]>waveformCount){
			matrix.postTranslate(-(tmpFloat[2]-waveformCount), 0);
		}
		tmpFloat[0]=0f;
		tmpFloat[2]=waveformCount-1;
		matrix.mapPoints(tmpFloat);

		float _startTime=Math.max(tmpFloat[0], 0f);
		float visibleDuration=endTime-startTime;
		float timeIn6dp=visibleDuration/getWidth()*V.dp(6);
		startTime=_startTime-(_startTime%timeIn6dp);
		endTime=startTime+visibleDuration;
		linesOffset=V.dp(-6)*((_startTime%timeIn6dp)/timeIn6dp);
	}

	private void updateLines(){
		if(endTime==0 || waveformLines==null)
			return;
		int vcenter=V.dp(72);
		float maxHeight=V.dp(21);
		float lineOffset=V.dp(6);
		float xOffset=V.dp(3);
		int visibleLineCount=waveformLines.length/4;
		for(int i=0;i<visibleLineCount;i++){
			waveformLines[i*4]=waveformLines[i*4+2]=lineOffset*i+xOffset;
			try{

				float sampleIndex=(float) i/(float) visibleLineCount*(endTime-startTime)+startTime;
				float sampleBefore=waveform[Math.min(Math.max(0, (int) Math.floor(sampleIndex)+waveformOffset), waveform.length)];
				float sampleAfter=waveform[Math.min((int) Math.ceil(sampleIndex)+waveformOffset, waveformOffset+waveformCount-1)];
				float offset=sampleIndex-(float) Math.floor(sampleIndex);
				int height=Math.round((sampleBefore*(1f-offset)+sampleAfter*offset)*maxHeight);
				waveformLines[i*4+1]=vcenter-height;
				waveformLines[i*4+3]=vcenter+height;
			}catch(ArrayIndexOutOfBoundsException fuck){}
		}

		float secondSize=getWidth()/(endTime-startTime);
		float bigTickH=V.dp(9), medTickH=V.dp(4), smallTickH=V.dp(2), tickBottom=V.dp(25);
		if(secondSize>V.dp(3)){ // draw a tick every 1 second
			timeLabelGapSeconds=10;
		}else if(secondSize*10f>V.dp(3)){ // draw a tick every 10 seconds
			secondSize*=10f;
			timeLabelGapSeconds=100;
		}else if(secondSize*60f>V.dp(3)){ // draw a tick every minute
			secondSize*=60f;
			timeLabelGapSeconds=600;
		}else if(secondSize*600f>V.dp(3)){ // draw a tick every 10 minutes
			secondSize*=600f;
			timeLabelGapSeconds=6000;
		}else{ // draw a tick every 30 minutes
			secondSize*=1800f;
			timeLabelGapSeconds=18000;
		}
		for(int i=0;i<10;i++){
			tickLines[i*4]=tickLines[i*4+2]=Math.round(secondSize*i);
			tickLines[i*4+3]=tickBottom;
			if(i==9)
				tickLines[i*4+1]=tickBottom-bigTickH;
			else if(i%2==0)
				tickLines[i*4+1]=tickBottom-medTickH;
			else
				tickLines[i*4+1]=tickBottom-smallTickH;
		}
	}

	private float getCursorTime(){
		return seeking && seekTime>0 ? seekTime : AudioEditor.instance().getPlaybackPosition();
	}

	public float getSelectionStart(){
		return cropperStart*(endTime-startTime)+startTime;
	}

	public float getSelectionEnd(){
		return cropperEnd*(endTime-startTime)+startTime;
	}

	public void setOnSelectionChangedListener(Runnable onSelectionChangedListener){
		this.onSelectionChangedListener=onSelectionChangedListener;
	}

	public boolean hasSelection(){
		return cropperEnd<1f || cropperStart>0f;
	}


	public float getCropperAlpha(){
		return cropperAlpha;
	}

	public void setCropperAlpha(float cropperAlpha){
		this.cropperAlpha=cropperAlpha;
		invalidate();
	}
}
