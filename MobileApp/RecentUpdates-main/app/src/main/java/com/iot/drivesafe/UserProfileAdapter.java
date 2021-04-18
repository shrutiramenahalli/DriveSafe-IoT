package com.iot.drivesafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class UserProfileAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DataModel1> dataModelArrayList1;

    public UserProfileAdapter(Context context, ArrayList<DataModel1> dataModelArrayList1) {
        this.context = context;
        this.dataModelArrayList1 = dataModelArrayList1;
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
        return dataModelArrayList1.size();
    }

    @Override
    public Object getItem(int position) {
        return dataModelArrayList1.get(position);
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
            convertView = inflater.inflate(R.layout.my_profile, null, true);
            holder.welcomename = (TextView) convertView.findViewById(R.id.Name);
            holder.name = (TextView) convertView.findViewById(R.id.FullName);
//            holder.age = (TextView) convertView.findViewById(R.id.Age);
            holder.email_id = (TextView) convertView.findViewById(R.id.Email);
            holder.vehicle_id = (TextView) convertView.findViewById(R.id.Vehicle);
            holder.tag_id = (TextView) convertView.findViewById(R.id.TagId);
            holder.balance = (TextView) convertView.findViewById(R.id.Balance);

            convertView.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.welcomename.setText("Welcome "+dataModelArrayList1.get(position).getName()+"!");
        holder.name.setText(dataModelArrayList1.get(position).getName());
//        holder.age.setText(dataModelArrayList1.get(position).getAge()+ " years");
        holder.email_id.setText(dataModelArrayList1.get(position).getEmail_id());
        holder.vehicle_id.setText("Vehicle Id: "+ dataModelArrayList1.get(position).getVehicle_id());
        holder.tag_id.setText("Tag Id: "+ dataModelArrayList1.get(position).getTag_id());
        holder.balance.setText("Balance: "+ dataModelArrayList1.get(position).getBalance()+ " Euro");
        return convertView;
    }

    private class ViewHolder {

        protected TextView welcomename, name, age, email_id, vehicle_id, tag_id, balance;
        protected ImageView profilepic;
    }
}
