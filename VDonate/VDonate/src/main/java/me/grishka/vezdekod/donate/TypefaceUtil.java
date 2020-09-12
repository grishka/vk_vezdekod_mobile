package me.grishka.vezdekod.donate;

import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import androidx.annotation.FontRes;

public class TypefaceUtil{

	private static final String TAG="TypefaceUtil";

	private static SparseArray<Typeface> cache=new SparseArray<>();

	public static void setFontResourceOnTextView(@FontRes int font, TextView textView){
		if(Build.VERSION.SDK_INT>=26)
			return;
		Typeface t=cache.get(font);
		if(t!=null){
			textView.setTypeface(t);
			return;
		}
		File file=null;
		try{
			file=File.createTempFile("font"+font, "ttf");
			try(InputStream in=textView.getContext().getResources().openRawResource(font)){
				try(FileOutputStream out=new FileOutputStream(file)){
					byte[] buf=new byte[1024];
					int read;
					while((read=in.read(buf))>0)
						out.write(buf, 0, read);
				}
			}
			t=Typeface.createFromFile(file);
			cache.put(font, t);
			textView.setTypeface(t);
		}catch(IOException x){
			Log.w(TAG, "setFontResourceOnTextView: ", x);
		}finally{
			if(file!=null)
				file.delete();
		}
	}

	public static void setTypefaceOnToolbarTitle(Toolbar toolbar){
		for(int i=0;i<toolbar.getChildCount();i++){
			View child=toolbar.getChildAt(i);
			if(child instanceof TextView){
				TypefaceUtil.setFontResourceOnTextView(R.font.ttcommons_bold, (TextView)child);
			}
		}
	}
}
