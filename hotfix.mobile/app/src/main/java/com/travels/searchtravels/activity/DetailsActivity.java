package com.travels.searchtravels.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.dynamitechetan.flowinggradient.FlowingGradientClass;
import com.travels.searchtravels.R;
import com.travels.searchtravels.adapters.ListAdapter;
import com.travels.searchtravels.utils.AnimationHelper;
import com.travels.searchtravels.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private ListAdapter costAdapter;
    private ListView costListRV;
    private RelativeLayout progressRL;
    private RelativeLayout closeRL;
    private RelativeLayout nextBTN;
    private ImageView loaderIV;
    private EditText cityET;
    private Button searchBTN;
    private LinkedHashMap<String, String> data = new LinkedHashMap<>();
    private ViewGroup header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        costListRV = findViewById(R.id.costListRV);
        cityET = findViewById(R.id.cityET);
        searchBTN = findViewById(R.id.searchBTN);
        progressRL = findViewById(R.id.progressRL);
        closeRL = findViewById(R.id.closeRL);
        loaderIV = findViewById(R.id.loaderIV);


        closeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfoNomad(cityET.getText().toString());
            }
        });
        nextBTN = findViewById(R.id.nextBTN);
        Log.d("myLogs","Constants.PICKED_CITY_EN = " + Constants.PICKED_CITY_EN);
        startLoader();
        getInfoNomad(Constants.PICKED_CITY_EN);
        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aviasales.ru")
                );
                startActivity(intent);
            }
        });
        FlowingGradientClass grad = new FlowingGradientClass();
        grad.setBackgroundResource(R.drawable.horizontal_loader_button)
                .onRelativeLayout(nextBTN)
                .setTransitionDuration(3000)
                .start();
    }
    public void startLoader(){
        progressRL.setVisibility(View.VISIBLE);
        AnimationHelper.roundAnimation(loaderIV, 360f);
    }


    public void getInfoNomad(final String city) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("https://nomadlist.com/cost-of-living/in/" + city.toLowerCase().trim());
                    Log.d("myLogs","url = " + url);
                    Document document = Jsoup.parse(url, 5000);

                    Element cost_of_living = document.body().select("div.tab-cost-of-living").get(0);

                    Log.d("myLogs", "cost_of_living = " + cost_of_living);

//                    Elements prices =  cost_of_living.select("td.value > span.price");
                    Elements prices = cost_of_living.select("span.price");
                    Log.d("myLogs", "prices size = " + prices.size());
                    Log.d("myLogs", "prices size = " + prices);
                    Elements fieldNames = cost_of_living.select(".key");
                    Log.d("myLogs", "prices fieldNames size = " + fieldNames.size());
                    Log.d("myLogs", "prices fieldNames = " + fieldNames);


                    URL obj = new URL("https://autocomplete.travelpayouts.com/places2?locale=en&types[]=city&term="+city);
                    HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                    //add reuqest header
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
                    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection.setRequestProperty("Content-Type", "application/json");

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = bufferedReader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    bufferedReader.close();

                    JSONArray responseJSON = new JSONArray(response.toString());
                    Log.d("myLogs", "responseJSON = " + responseJSON);
                    String code = responseJSON.getJSONObject(0).getString("code");
                    String countryName = responseJSON.getJSONObject(0).getString("country_name");

                    Log.d("myLogs", "code = " + code);



                    URL obj2 = new URL("https://api.travelpayouts.com/v1/prices/cheap?origin=LED&depart_date=2019-12&return_date=2019-12&token=471ae7d420d82eb92428018ec458623b&destination="+code);
                    HttpURLConnection connection2 = (HttpURLConnection) obj2.openConnection();

                    //add reuqest header
                    connection2.setRequestMethod("GET");
                    connection2.setRequestProperty("User-Agent", "Mozilla/5.0" );
                    connection2.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection2.setRequestProperty("Content-Type", "application/json");

                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
                    String inputLine2;
                    StringBuffer response2 = new StringBuffer();

                    while ((inputLine2 = bufferedReader2.readLine()) != null) {
                        response2.append(inputLine2);
                    }
                    bufferedReader2.close();

                    JSONObject responseJSON2 = new JSONObject(response2.toString());
                    Log.d("myLogs", "responseJSON2 = " + responseJSON2);

                    Integer ticketPrice = 6200;

                    try{
                        ticketPrice = Integer.parseInt(responseJSON2.getJSONObject("data").getJSONObject(code).getJSONObject("1").getString("price"));

                    }catch (Exception e){
                        e.printStackTrace();



                    }



                    https://api.travelpayouts.com/v1/prices/cheap?origin=LED&destination=HKT&depart_date=2019-12&return_date=2019-12&token=471ae7d420d82eb92428018ec458623b



                    data = new LinkedHashMap<>();

                    data.put("Город вылета", "Санкт-Пеербург");
                    data.put("Билеты на самолет", "от " + ticketPrice + " \u20BD");

                    Boolean firstHotel0 = true;
                    for (Element element : fieldNames) {
                        if (!element.text().contains(" tax ")) {

//                            if(element.text().contains("Hotel") && !firstHotel0){
//                                add(data,4, element.text(), null);
//                            }else{
//                                add(data,4, element.text(), null);
////                                data.put(element.text(),null);
//                            }
                            data.put(element.text(), null);

                            if (element.text().contains("Hotel") && firstHotel0) {
                                firstHotel0 = false;
                            }
                        }
                    }

                    int pos = 2;
                    Boolean firstHotel = true;
                    for (Element element : prices) {
                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

                        symbols.setGroupingSeparator(' ');
                        formatter.setDecimalFormatSymbols(symbols);

                        String price = formatter.format(Math.round(Float.parseFloat(element.attr("data-usd")) * 64.42f)) + "";

                        Object[] keys = data.keySet().toArray();
                        Log.d("myLogs", "keys[pos] = " + keys[pos]);

                        String keyTarget = keys[pos].toString();



                        if (keyTarget.contains("Nomad Cost")) {
                            price += "\u20BD/мес";
                        } else if (keyTarget.contains("Cost of living for family")) {
                            price += "\u20BD за чел./мес";
                        } else if (keyTarget.contains("Cost of living for local")) {
                            price += "\u20BD/мес";
                        } else if (keyTarget.contains("studio rent in center")) {
                            price += "\u20BD/мес";
                        } else if (keyTarget.contains("Hotel") && firstHotel) {
                            firstHotel = false;
                            price += "\u20BD/мес";
                            continue;
                        } else if (keyTarget.contains("Airbnb") && keyTarget.contains("listings")) {
                            price += "\u20BD/мес";
                        } else if (keyTarget.contains("Cost of living for expat")) {
                            price += "\u20BD/мес";
                        } else if (keyTarget.contains("Coworking")) {
                            price += "\u20BD/мес";
                        } else if (keyTarget.contains("Airbnb")) {
                            price += "\u20BD/ночь";
                        } else if (keyTarget.contains("Hotel")) {
                            price += "\u20BD/ночь";
                        } else {
                            price += " \u20BD";
                        }

                        data.put(keys[pos].toString(), price);

//                        values.add(price);

                        Log.d("myLogs", "element.attr = " + element.attr("data-usd"));
                        pos++;
                    }

//
                    Handler handler = new Handler(getMainLooper());

                    final Integer finalTicketPrice = ticketPrice;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {



                            if(header != null){
                                costListRV.removeHeaderView(header);
                            }
                            LayoutInflater inflater = getLayoutInflater();
                            header = (ViewGroup)inflater.inflate(R.layout.header3, costListRV, false);
                            costListRV.addHeaderView(header, null, false);

                            TextView airticketTV = header.findViewById(R.id.airticketTV);
                            TextView hotelsTV = header.findViewById(R.id.hotelsTV);
                            TextView shopTV = header.findViewById(R.id.shopTV);
                            TextView cityTV = header.findViewById(R.id.cityTV);
                            TextView countryTV  = header.findViewById(R.id.countryTV);
                            cityTV.setText(Constants.PICKED_CITY_RU);
                            countryTV.setText(countryName);
                            ImageView photoIV = header.findViewById(R.id.photoIV);

                            if (Constants.PICKED_BITMAP != null) {
                                Glide.with(getApplicationContext()).load(Constants.PICKED_BITMAP).centerCrop()
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .into(photoIV);
                            } else {
                                Glide.with(getApplicationContext()).load("https://art-on.ru/wp-content/uploads/2013/06/Magic_Paris_11.jpg").centerCrop()
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .into(photoIV);
                            }

                            airticketTV.setText(data.get("Билеты на самолет"));
                            hotelsTV.setText(data.get("\uD83C\uDFE8 Hotel"));
                            shopTV.setText(data.get("\uD83D\uDCB5 Nomad Cost™"));

                            if (costAdapter != null) {
                                costAdapter.setData(data);
                                costAdapter.notifyDataSetChanged();
                                return;
                            }
                            nextBTN.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = "https://www.aviasales.ru/search/LED0610" + code +"1410";
                                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url)
                                    );
                                    startActivity(intent);
                                }
                            });
                            costAdapter = new ListAdapter(getApplicationContext(),R.layout.list_item ,data);
                            costListRV.setAdapter(costAdapter);
                            progressRL.animate().alpha(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    progressRL.setVisibility(View.GONE);

                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });

                            Log.d("myLogs", "Finish");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent = new Intent(DetailsActivity.this, ChipActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        thread.start();
    }

    public static <K, V> void add(LinkedHashMap<K, V> map, int index, K key, V value) {
        assert (map != null);
        assert !map.containsKey(key);
        assert (index >= 0) && (index < map.size());

        int i = 0;
        List<Map.Entry<K, V>> rest = new ArrayList<Map.Entry<K, V>>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (i++ >= index) {
                rest.add(entry);
            }
        }
        map.put(key, value);
        for (int j = 0; j < rest.size(); j++) {
            Map.Entry<K, V> entry = rest.get(j);
            map.remove(entry.getKey());
            map.put(entry.getKey(), entry.getValue());
        }
    }
}
