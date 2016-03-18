package com.chasetech.pcount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chasetech.pcount.R;
import com.chasetech.pcount.library.Location;

import java.util.ArrayList;

/**
 * Created by Vinid on 10/25/2015.
 */

public class BranchListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Location> arrayListBranch;

    public BranchListViewAdapter(Context context, ArrayList<Location> arrayListBranch )
    {
        this.mContext = context;
        this.arrayListBranch = arrayListBranch;
    }

    public class ViewHolder {
        TextView textViewBranchName;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.branch_row_layout, parent, false);
            holder.textViewBranchName = (TextView) view.findViewById(R.id.textViewBranchName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Location currentLocation = arrayListBranch.get(position);

        holder.textViewBranchName.setText(currentLocation.locationName);
        holder.textViewBranchName.setTag(currentLocation.locationCode);

        view.setBackground(mContext.getResources().getDrawable(R.drawable.list_selector));
        if (currentLocation.type == 1) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        } else if (currentLocation.type == 2) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));
        }

        return view;
    }

    @Override
    public int getCount() {
        return arrayListBranch.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListBranch.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
