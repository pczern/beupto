package me.speeddeveloper.beupto.activity.settings.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.Color;


/**
 * Created by speedDeveloper on 25.06.2016.
 */
public class ColorListAdapter extends BaseAdapter {


    List<Color> colors;
    Context mContext;


    public ColorListAdapter(Context context, List<Color> list){
        mContext = context;
        colors = list;

    }





    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public CharSequence getItem(int position) {
        return colors.get(position).getName();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(mContext, R.layout.adapter_theme_color, null);

        ((TextView) convertView.findViewById(R.id.color_textview)).setText(colors.get(position).getName());
        ((ImageView) convertView.findViewById(R.id.color_imageview)).setImageDrawable(new ColorDrawable(colors.get(position).getColor()));
        return convertView;
    }






}
