package me.grishka.vezdekod.podcasts;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

import me.grishka.appkit.utils.WorkerThread;

public class AudioEditor{
	private static final String TAG="AudioEditor";
	private static AudioEditor inst=new AudioEditor();

	private WorkerThread thread;
	private Handler mainHandler=new Handler(Looper.getMainLooper());
	private ProcessFileRunnable currentProcessingRunnable;
	private Uri currentUri;
	private String currentMediaType;
	private long duration=-1;
	private OnDurationListener durationListener;
	private OnWaveformUpdateListener waveformUpdateListener;
	private float[] waveform={};
	private int waveformProcessedCount;
	private MediaPlayer player, musicPlayer;
	private long lastPlayPosition, lastPlayPositionTime;
	private long croppedStart=0;
	private Stack<UndoState> undoStack=new Stack<>();
	private Runnable onPlayerCompletionListener;
	private String musicInfo;
	public ArrayList<Timecode> timecodes=new ArrayList<>();

	private Runnable reportDurationRunnable=new Runnable(){
		@Override
		public void run(){
			if(durationListener!=null && duration!=-1)
				durationListener.onDurationAvailable(duration);
		}
	};
	private Runnable reportWaveformUpdate=new Runnable(){
		@Override
		public void run(){
			if(waveformUpdateListener!=null)
				waveformUpdateListener.onWaveformUpdated(waveformProcessedCount, (int)croppedStart/1000, (int)duration/1000);
		}
	};
	private Runnable checkCroppedDurationRunnable=new Runnable(){
		@Override
		public void run(){
			seek(0);
			player.pause();
			if(onPlayerCompletionListener!=null)
				onPlayerCompletionListener.run();
		}
	};

	private AudioEditor(){}

	public static AudioEditor instance(){
		return inst;
	}

	public void loadFile(Uri uri, String type){
		if(thread==null){
			thread=new WorkerThread("AudioEditor");
			thread.start();
		}
		synchronized(this){
			if(currentProcessingRunnable!=null){
				currentProcessingRunnable.cancel();
			}
			currentUri=uri;
			currentMediaType=type;
			duration=-1;
			croppedStart=0;
			undoStack.clear();
			if(player!=null){
				player.release();
				player=null;
			}
			thread.postRunnable(currentProcessingRunnable=new ProcessFileRunnable(), 0);
		}
	}

	public void setDurationListener(OnDurationListener durationListener){
		this.durationListener=durationListener;
		if(durationListener!=null && duration!=-1){
			durationListener.onDurationAvailable(duration);
		}
	}

	public void setWaveformUpdateListener(OnWaveformUpdateListener waveformUpdateListener){
		this.waveformUpdateListener=waveformUpdateListener;
		if(waveformUpdateListener!=null){
			waveformUpdateListener.onWaveformUpdated(waveformProcessedCount, (int)croppedStart/1000, (int)duration/1000);
		}
	}

	public void setOnPlayerCompletionListener(Runnable onPlayerCompletionListener){
		this.onPlayerCompletionListener=onPlayerCompletionListener;
	}

	public float[] getWaveform(){
		return waveform;
	}

	public float getPlaybackPosition(){
		long t=System.currentTimeMillis();
		if(t-lastPlayPositionTime>1000){
			lastPlayPosition=player.getCurrentPosition();
			lastPlayPositionTime=t;
		}
		if(!isPlaying())
			return (lastPlayPosition-croppedStart)/1000f;
		return (lastPlayPosition+(t-lastPlayPositionTime)-croppedStart)/1000f;
	}

	public void play(){
		if(player.getCurrentPosition()>=duration+croppedStart)
			player.seekTo((int) croppedStart);
		player.start();
		lastPlayPosition=player.getCurrentPosition();
		lastPlayPositionTime=System.currentTimeMillis();
		mainHandler.postDelayed(checkCroppedDurationRunnable, duration-(lastPlayPosition-croppedStart));
		if(musicPlayer!=null)
			musicPlayer.start();
	}

	public void pause(){
		player.pause();
		if(musicPlayer!=null)
			musicPlayer.pause();
		mainHandler.removeCallbacks(checkCroppedDurationRunnable);
	}

	public boolean isPlaying(){
		return player.isPlaying();
	}

	public void seek(float offset){
		player.seekTo((int)(croppedStart+offset*1000));
		lastPlayPosition=player.getCurrentPosition();
		lastPlayPositionTime=System.currentTimeMillis();
		mainHandler.removeCallbacks(checkCroppedDurationRunnable);
		mainHandler.postDelayed(checkCroppedDurationRunnable, duration-(lastPlayPosition-croppedStart));
		if(musicPlayer!=null){
			musicPlayer.seekTo((int) ((lastPlayPosition-croppedStart)%musicPlayer.getDuration()));
		}
	}

	public void crop(float _start, float _end){
		undoStack.push(new UndoState(this.croppedStart, this.duration));
		long start=(long)(_start*1000);
		long duration=(long)((_end-_start)*1000);
		this.croppedStart=start;
		this.duration=duration;
		if(lastPlayPosition<start){
			player.seekTo((int)start);
		}else if(lastPlayPosition>start+duration){
			player.seekTo((int)start);
		}
		waveformUpdateListener.onWaveformUpdated(waveformProcessedCount, (int)croppedStart/1000, (int)duration/1000);
		durationListener.onDurationAvailable(duration);
	}

	public boolean canUndo(){
		return !undoStack.isEmpty();
	}

	public void undo(){
		UndoState state=undoStack.pop();
		croppedStart=state.start;
		duration=state.duration;
		waveformUpdateListener.onWaveformUpdated(waveformProcessedCount, (int)croppedStart/1000, (int)duration/1000);
		durationListener.onDurationAvailable(duration);
	}

	public void setMusic(Uri uri, String info){
		if(musicPlayer!=null){
			musicPlayer.release();
			musicPlayer=null;
		}
		if(uri==null){
			musicInfo=null;
			return;
		}
		musicInfo=info;
		try{
			musicPlayer=new MediaPlayer();
			musicPlayer.setDataSource(App.applicationContext, uri);
			musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
				@Override
				public void onPrepared(MediaPlayer mediaPlayer){
					musicPlayer.setLooping(true);
					musicPlayer.setVolume(.1f, .1f);
					musicPlayer.seekTo((int) ((lastPlayPosition-croppedStart)%musicPlayer.getDuration()));
					if(isPlaying())
						musicPlayer.start();
				}
			});
			musicPlayer.prepareAsync();
		}catch(IOException ignore){}
	}

	public String getMusicInfo(){
		return musicInfo;
	}

	private class ProcessFileRunnable implements Runnable{
		private boolean needCancel;
		private short[] pcmBuffer=new short[0];
		private int sampleRate, channels;
		public float[] waveform;

		@Override
		public void run(){
			MediaExtractor extractor=null;
			MediaCodec codec=null;
			try{
				player=MediaPlayer.create(App.applicationContext, currentUri);
				player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
					@Override
					public void onCompletion(MediaPlayer mediaPlayer){
						seek(0);
						if(onPlayerCompletionListener!=null)
							mainHandler.post(onPlayerCompletionListener);
					}
				});
				duration=player.getDuration();
				mainHandler.post(reportDurationRunnable);
				maybeCancel();

				waveform=new float[(int)((duration+999)/1000)];
				if(currentProcessingRunnable==this)
					AudioEditor.this.waveform=waveform;

				extractor=new MediaExtractor();
				extractor.setDataSource(App.applicationContext, currentUri, null);
				if(extractor.getTrackCount()<1){
					Log.e(TAG, "Track count < 1");
					return;
				}
				extractor.selectTrack(0);
				MediaFormat format=extractor.getTrackFormat(0);
				codec=MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
				codec.configure(format, null, null, 0);
				codec.start();
				MediaCodec.BufferInfo info=new MediaCodec.BufferInfo();
				int count=0;
				long lastCallback=0;

				// Only decode 2 buffers for each second of audio to avoid this taking ages to complete.
				seekLoop:
				for(int i=0;i<waveform.length;i++){
					extractor.seekTo(1000000L*i+250000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
					for(int j=0;j<2;j++){
						int inIdx=codec.dequeueInputBuffer(-1);
						ByteBuffer inBuf=codec.getInputBuffer(inIdx);
						int size=extractor.readSampleData(inBuf, 0);
						if(size==-1)
							break seekLoop;
						codec.queueInputBuffer(inIdx, 0, size, extractor.getSampleTime(), 0);
						int outIdx;
						while((outIdx=codec.dequeueOutputBuffer(info, 0))!=MediaCodec.INFO_TRY_AGAIN_LATER){
							if(outIdx >= 0){
								ByteBuffer outBuf=codec.getOutputBuffer(outIdx);

								ShortBuffer sbuf=outBuf.asShortBuffer();
								int len=outBuf.limit()/2;
								if(pcmBuffer.length<len)
									pcmBuffer=new short[len];
								sbuf.get(pcmBuffer, 0, len);
								float val=processPcmBuffer(pcmBuffer, len);
								int index=Math.min((int)(info.presentationTimeUs/1000000), waveform.length-1);
								waveform[index]=Math.max(waveform[index], val);
								codec.releaseOutputBuffer(outIdx, false);
							}else if(outIdx==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
								MediaFormat outFormat=codec.getOutputFormat();
								Log.d(TAG, "output format="+outFormat);
								sampleRate=outFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
								channels=outFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
							}
						}
						if(count%10==0){
							maybeCancel();
							if(System.currentTimeMillis()-lastCallback>200){
								lastCallback=System.currentTimeMillis();
								mainHandler.post(reportWaveformUpdate);
							}
						}
						count++;
						extractor.advance();
					}
				}

				waveformProcessedCount=waveform.length;
				mainHandler.post(reportWaveformUpdate);
			}catch(CancellationException ignore){

			}catch(Exception x){
				Log.w(TAG, x);
			}finally{
				if(extractor!=null)
					extractor.release();
				if(codec!=null)
					codec.release();
			}
		}

		public void cancel(){
			needCancel=true;
		}

		private void maybeCancel(){
			synchronized(AudioEditor.this){
				if(needCancel){
					throw new CancellationException();
				}
			}
		}

		private float processPcmBuffer(short[] buf, int len){
			int max=0;
			for(int i=0;i<len;i++){
				max=Math.max(Math.abs(buf[i]), max);
			}
			return (float)max/(float)Short.MAX_VALUE;
		}
	}

	public interface OnDurationListener{
		void onDurationAvailable(long duration);
	}

	public interface OnWaveformUpdateListener{
		void onWaveformUpdated(int processed, int offset, int count);
	}

	private static class UndoState{
		public long start;
		public long duration;

		public UndoState(long start, long duration){
			this.start=start;
			this.duration=duration;
		}
	}
}
