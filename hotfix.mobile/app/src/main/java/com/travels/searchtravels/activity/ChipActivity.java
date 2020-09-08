package com.travels.searchtravels.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dynamitechetan.flowinggradient.FlowingGradientClass;
import com.travels.searchtravels.R;
import com.travels.searchtravels.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChipActivity extends AppCompatActivity {

    private ImageView photoIV;
    private TextView titleTV;
    private TextView airticketTV;
    private RelativeLayout closeRL;
    private RelativeLayout nextBTN;
    private View hotels;
    private View shop;
    private TextView cityTV;
    private TextView countryTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheap);

        airticketTV = findViewById(R.id.airticketTV);
        hotels = findViewById(R.id.hotels);
        shop = findViewById(R.id.shop);
        closeRL = findViewById(R.id.closeRL);
        cityTV = findViewById(R.id.cityTV);
        titleTV = findViewById(R.id.titleTV);
        photoIV = findViewById(R.id.photoIV);
        nextBTN = findViewById(R.id.nextBTN);
        countryTV = findViewById(R.id.countryTV);

        hotels.setVisibility(View.GONE);
        shop.setVisibility(View.GONE);
        titleTV.setVisibility(View.GONE);

        closeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String url = "https://www.aviasales.ru/search/LED0610HKT1410141";
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.aviasales.ru")
                );
                startActivity(intent);
            }
        });
        FlowingGradientClass grad = new FlowingGradientClass();
        grad.setBackgroundResource(R.drawable.horizontal_loader_button)
                .onRelativeLayout(nextBTN)
                .setTransitionDuration(3000)
                .start();
        cityTV.setText(Constants.PICKED_CITY_RU);

        getInfoNomad(Constants.PICKED_CITY_EN);
        if (Constants.PICKED_BITMAP != null) {
            Glide.with(getApplicationContext()).load(Constants.PICKED_BITMAP).centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(photoIV);
        } else {
            Glide.with(getApplicationContext()).load("https://art-on.ru/wp-content/uploads/2013/06/Magic_Paris_11.jpg").centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(photoIV);
        }
    }

    public void getInfoNomad(final String city) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
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

                    }

//
                    Handler handler = new Handler(getMainLooper());

                    final Integer finalTicketPrice = ticketPrice;
                    final Integer finalTicketPrice1 = ticketPrice;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            airticketTV.setText("от " + finalTicketPrice1 + "\u20BD");
                            countryTV.setText(countryName);

                            nextBTN.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = "https://www.aviasales.ru/search/LED0610" + code +"1410";
                                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url)
                                    );
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
