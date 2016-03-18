package com.chasetech.pcount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chasetech.pcount.R;
import com.chasetech.pcount.library.Location;
import com.chasetech.pcount.library.ReportClass;

import java.util.ArrayList;

/**
 * Created by Vinid on 10/25/2015.
 */

public class ReportListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ReportClass> mArrayListReport;

    public ReportListViewAdapter(Context context, ArrayList<ReportClass> arrayListReport )
    {
        this.context = context;
        this.mArrayListReport = arrayListReport;
    }

    public class ViewHolder {
        TextView textViewName;
        TextView textViewIG;
        TextView textViewSO;
        TextView textViewEndinv;
        TextView textViewFinalSO;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.report_row_layout, parent, false);
            holder.textViewName = (TextView) view.findViewById(R.id.se_row_desc);
            holder.textViewIG = (TextView) view.findViewById(R.id.se_row_ig_goal);
            holder.textViewEndinv = (TextView) view.findViewById(R.id.se_row_endinv);
            holder.textViewSO = (TextView) view.findViewById(R.id.se_row_so_qty);
            holder.textViewFinalSO = (TextView) view.findViewById(R.id.se_row_finalso);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ReportClass reportClass = mArrayListReport.get(position);

        holder.textViewName.setText(reportClass.name);
        holder.textViewIG.setText(String.valueOf(reportClass.ig));
        holder.textViewEndinv.setText(String.valueOf(reportClass.endinv));
        holder.textViewSO.setText(String.valueOf(reportClass.so));
        holder.textViewFinalSO.setText(String.valueOf(reportClass.finalso));

        return view;
    }

    @Override
    public int getCount() {
        return mArrayListReport.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayListReport.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
