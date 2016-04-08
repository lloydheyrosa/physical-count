package com.chasetech.pcount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chasetech.pcount.R;
import com.chasetech.pcount.MKL.PCount;
import com.chasetech.pcount.viewholder.PCountViewHolder;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Vinid on 10/25/2015.
 */
public class PCountListViewAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<PCount> mArrayListPCount;
    private ArrayList<PCount> mArrayListPCountResultList;

    public PCountListViewAdapter(Context context, ArrayList<PCount> arrayListPCount)
    {
        this.mContext = context;
        this.mArrayListPCountResultList = arrayListPCount;
        this.mArrayListPCount = new ArrayList<PCount>();
        this.mArrayListPCount.addAll(arrayListPCount);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final PCountViewHolder holder;

        if (view == null) {
            holder = new PCountViewHolder();
            view = inflater.inflate(R.layout.se_style_grid_row, parent, false);
            holder.linearLayoutPCount = (LinearLayout) view.findViewById(R.id.linearLayoutPCount);
            holder.tvSku = (TextView) view.findViewById(R.id.se_row_sku);
            holder.tvDesc = (TextView) view.findViewById(R.id.se_row_desc);
            holder.tvIgGoal = (TextView) view.findViewById(R.id.se_row_ig_goal);
            holder.tvSapc = (TextView) view.findViewById(R.id.se_row_sa_qty);
            holder.tvWhpc = (TextView) view.findViewById(R.id.se_row_whpc_qty);
            holder.tvWhcs = (TextView) view.findViewById(R.id.se_row_whcs_qty);
            holder.tvSo = (TextView) view.findViewById(R.id.se_row_so_qty);
            holder.tvFso = (TextView) view.findViewById(R.id.se_row_finso_qty);
            view.setTag(holder);
        } else {
            holder = (PCountViewHolder) view.getTag();
        }

        PCount pCount =  mArrayListPCountResultList.get(position);

        holder.pCount = pCount;
        holder.tvSku.setText(pCount.barcode);
        holder.tvDesc.setText(pCount.desc);
        holder.tvIgGoal.setText(String.valueOf(pCount.ig));
        holder.tvSapc.setText(String.valueOf(pCount.sapc));
        holder.tvWhpc.setText(String.valueOf(pCount.whpc));
        holder.tvWhcs.setText(String.valueOf(pCount.whcs));
        holder.tvSo.setText(String.valueOf(pCount.so));
        holder.tvFso.setText(String.valueOf(pCount.fso));

        holder.linearLayoutPCount.setBackground(mContext.getResources().getDrawable(R.drawable.list_selector));
        if (pCount.sapc != 0 || pCount.whpc != 0 || pCount.whcs != 0 || pCount.fso != 0 || pCount.updated) {
            holder.linearLayoutPCount.setBackgroundColor(mContext.getResources().getColor(R.color.blue_gray));
        }

        return view;
    }


    public void filter(final int filterCode, String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mArrayListPCountResultList.clear();

        if (charText.length() == 0) {
            mArrayListPCountResultList.addAll(mArrayListPCount);
        }
        else
        {
            for (PCount pCount : mArrayListPCount)
            {
                Boolean lvalid;

                switch (filterCode) {
                    case 0:
                        lvalid = pCount.category.toLowerCase(Locale.getDefault()).contains(charText);
                        break;
                    case 1:
                        lvalid = pCount.subcate.toLowerCase(Locale.getDefault()).contains(charText);
                        break;
                    case 2:
                        lvalid = pCount.brand.toLowerCase(Locale.getDefault()).contains(charText);
                        break;
                    case 3:
                        lvalid = pCount.division.toLowerCase(Locale.getDefault()).contains(charText);
                        break;
                    case 4:
                        lvalid = pCount.sapc != 0 || pCount.whpc != 0 || pCount.whcs != 0;
                        break;
                    case 5:
                        lvalid = pCount.sapc == 0 && pCount.whpc == 0 && pCount.whcs == 0;
                        break;
                    default:
                        lvalid = pCount.category.toLowerCase(Locale.getDefault()).contains(charText) ||
                                pCount.brand.toLowerCase(Locale.getDefault()).contains(charText) ||
                                pCount.subcate.toLowerCase(Locale.getDefault()).contains(charText) ||
                                pCount.division.toLowerCase(Locale.getDefault()).contains(charText) ||
                                pCount.desc.toLowerCase(Locale.getDefault()).contains(charText) ||
                                pCount.barcode.toLowerCase(Locale.getDefault()).contains(charText);
                }

                if (lvalid) mArrayListPCountResultList.add(pCount);

            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArrayListPCountResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayListPCountResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
