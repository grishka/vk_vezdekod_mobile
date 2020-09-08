package com.travels.searchtravels;

import android.content.ComponentName;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.travels.searchtravels.activity.ChipActivity;
import com.travels.searchtravels.activity.DetailsActivity;
import com.travels.searchtravels.activity.MainActivity;
import com.travels.searchtravels.utils.Constants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CategoryTests{
	private ActivityScenario<MainActivity> scenario;
	@Rule
	public IntentsTestRule<MainActivity> intentsTestRule=new IntentsTestRule<>(MainActivity.class);
	@Rule
	public IntentsTestRule<DetailsActivity> detailsActivityIntentsTestRule=new IntentsTestRule<>(DetailsActivity.class, true, false);

	private void assertCityForCategory(String category, String cityEn, String cityRu){
		intentsTestRule.getActivity().loadByCategory(category);
		assertEquals(Constants.PICKED_CITY_EN, cityEn);
		assertEquals(Constants.PICKED_CITY_RU, cityRu);

		intended(hasComponent(new ComponentName(intentsTestRule.getActivity(), DetailsActivity.class)));

		try{Thread.sleep(5000);}catch(Exception ignore){}

		intended(hasComponent(new ComponentName(intentsTestRule.getActivity(), ChipActivity.class)));

		onView(withId(R.id.cityTV)).check(matches(withText(Constants.PICKED_CITY_RU)));
	}

	@Test
	public void testCategorySea(){
		assertCityForCategory("sea", "Rimini", "Римини");
	}

	@Test
	public void testCategoryOcean(){
		assertCityForCategory("ocean", "Rimini", "Римини");
	}

	@Test
	public void testCategoryBeach(){
		assertCityForCategory("beach", "Rimini", "Римини");
	}

	@Test
	public void testCategoryMountain(){
		assertCityForCategory("mountain", "Sochi", "Сочи");
	}

	@Test
	public void testCategorySnow(){
		assertCityForCategory("snow", "Helsinki", "Хельсинки");
	}
}
