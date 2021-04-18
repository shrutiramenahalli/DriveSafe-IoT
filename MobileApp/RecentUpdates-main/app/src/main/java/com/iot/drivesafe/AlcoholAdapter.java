package com.iot.drivesafe;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//import com.squareup.picasso.Picasso;
import java.util.ArrayList;

class AlcoholAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DataModel2> dataModelArrayList2;

    public AlcoholAdapter(Context context, ArrayList<DataModel2> dataModelArrayList2) {
        this.context = context;
        this.dataModelArrayList2 = dataModelArrayList2;
    }

    @Override
    public int getViewTypeCount() {
        if (getCount() > 0) {
            return getCount();

        } else {
            return super.getViewTypeCount();
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return dataModelArrayList2.size();
    }

    @Override
    public Object getItem(int position) {
        return dataModelArrayList2.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_alcohol, null, true);

            //holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.tvname = (TextView) convertView.findViewById(R.id.name);
            holder.tvcountry = (TextView) convertView.findViewById(R.id.country);
            //holder.tvcity = (TextView) convertView.findViewById(R.id.city);

            convertView.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) convertView.getTag();
            }

        //Picasso.get().load(dataModelArrayList.get(position).getImgURL()).into(holder.iv);
        holder.tvname.setText("Alcohol Level: " + dataModelArrayList2.get(position).getAlcoholValue());
        holder.tvcountry.setText("Timestamp: " + dataModelArrayList2.get(position).getTimestamp());
        //holder.tvcity.setText("Timestamp" + dataModelArrayList.get(position).getTimestamp());

        return convertView;
    }

    private class ViewHolder {

        protected TextView tvname, tvcountry;
        //protected ImageView iv;
    }
}
