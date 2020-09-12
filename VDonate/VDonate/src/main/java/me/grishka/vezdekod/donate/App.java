package me.grishka.vezdekod.donate;

import android.app.Application;

import me.grishka.appkit.utils.V;

public class App extends Application{
	@Override
	public void onCreate(){
		super.onCreate();
		V.setApplicationContext(getApplicationContext());
	}
}
