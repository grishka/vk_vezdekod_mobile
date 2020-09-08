package com.travels.searchtravels.adapters;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.travels.searchtravels.R;

import java.util.LinkedHashMap;
import java.util.List;

public class ListAdapter extends ArrayAdapter<ClipData.Item> {

    private int resourceLayout;
    private Context mContext;
    private LinkedHashMap<String, String> data;

    public ListAdapter(Context context, int resource, LinkedHashMap<String, String> data) {
        super(context, resource);
        this.resourceLayout = resource;
        this.mContext = context;
        this.data = new LinkedHashMap<>(data);
        this.data.remove("Билеты на самолет");
        this.data.remove("\uD83C\uDFE8 Hotel");
        this.data.remove("\uD83D\uDCB5 Nomad Cost™");
        this.data.remove("Город вылета");
    }

    public void setData(LinkedHashMap<String, String> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Object[] keys = data.keySet().toArray();

        String key = keys[position].toString();
        String value = data.get(key);

        TextView keyTV = (TextView) v.findViewById(R.id.keyTV);
        TextView valueTV = (TextView) v.findViewById(R.id.valueTV);

        if(key.contains("Nomad Cost")){
            key = "Кроткосрочное проживание";
        } else if(key.contains("Cost of living for family")){
            key = "На семью";
        } else if(key.contains("Cost of living for local")){
            key = "Бюджетное проживание";
        } else if(key.contains("studio rent in center")){
            key = "Студия в центре";
        } else if(key.contains("Hotel") ){
            key = "Отель";
        } else if(key.contains("Airbnb") && key.contains("listings")){
            key = "Airbnb длительный срок";
        } else if(key.contains("Cost of living for expat")){
            key = "Долгосрочное проживание";
        } else if(key.contains("Coworking")){
            key = "Коворкинг";
        } else if(key.contains("Airbnb")){
            key = "Airbnb";
        } else if(key.contains("Dinner")){
            key = "Обед";
        } else if(key.contains("Beer")){
            key = "Пиво (0.5)";
        } else if(key.contains("Coffee")){
            key = "Кофе";
        } else if(key.contains("Coca-Cola")){
            key = "Банка колы (0.3)";
        }

        keyTV.setText(key);
        valueTV.setText(value);

        return v;
    }

}
