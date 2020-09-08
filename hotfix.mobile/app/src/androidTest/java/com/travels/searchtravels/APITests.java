package com.travels.searchtravels;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.travels.searchtravels.activity.DetailsActivity;
import com.travels.searchtravels.activity.MainActivity;
import com.travels.searchtravels.utils.Constants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class APITests {
	public ActivityTestRule<DetailsActivity> detailsActivityIntentsTestRule=new ActivityTestRule<DetailsActivity>(DetailsActivity.class, true, false);

	private String md5(String s){
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] hash=md.digest(s.getBytes(StandardCharsets.UTF_8));
			StringBuilder r= new StringBuilder();
			for(int i=0;i<hash.length;i++){
				r.append(String.format("%02x", (int) hash[i] & 0xFF));
			}
			return r.toString();
		}catch(Exception x){
			throw new RuntimeException(x);
		}
	}

	@Test
	public void testTicketPricesAndNomadlist(){
		Constants.PICKED_CITY_EN="Rimini";
		Constants.PICKED_CITY_RU="Римини";

		DetailsActivity.urlProvider=url->{
			return new URL("https://grishka.me/apps/vk_hotfix_mobile/"+md5(url)+".html");
		};

		detailsActivityIntentsTestRule.launchActivity(new Intent());

		try{Thread.sleep(1000);}catch(Exception ignore){}

		onView(withId(R.id.cityTV)).check(matches(withText("Римини")));
		onView(withId(R.id.countryTV)).check(matches(withText("Italy")));
		onView(withId(R.id.airticketTV)).check(matches(withText("от 6200 ₽")));
		onView(withId(R.id.hotelsTV)).check(matches(withText("3 828₽/ночь")));
	}
}
