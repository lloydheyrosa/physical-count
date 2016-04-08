package com.chasetech.pcount.Assortment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chasetech.pcount.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ULTRABOOK on 2/29/2016.
 */
public class AssortmentAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<Assortment> arrAssortmentList;
    private ArrayList<Assortment> arrAssortmentResultList;

    public AssortmentAdapter(Context context, ArrayList<Assortment> arrayListPCount)
    {
        this.mContext = context;
        this.arrAssortmentResultList = arrayListPCount;
        this.arrAssortmentList = new ArrayList<Assortment>();
        this.arrAssortmentList.addAll(arrayListPCount);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final AssortmentViewHolder holder;

        if (view == null) {
            holder = new AssortmentViewHolder();
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
            holder = (AssortmentViewHolder) view.getTag();
        }

        Assortment assortment =  arrAssortmentResultList.get(position);

        holder.assortment = assortment;
        holder.tvSku.setText(assortment.barcode);
        holder.tvDesc.setText(assortment.desc);
        holder.tvIgGoal.setText(String.valueOf(assortment.ig));
        holder.tvSapc.setText(String.valueOf(assortment.sapc));
        holder.tvWhpc.setText(String.valueOf(assortment.whpc));
        holder.tvWhcs.setText(String.valueOf(assortment.whcs));
        holder.tvSo.setText(String.valueOf(assortment.so));
        holder.tvFso.setText(String.valueOf(assortment.fso));

        holder.linearLayoutPCount.setBackground(mContext.getResources().getDrawable(R.drawable.list_selector));
        if (assortment.sapc != 0 || assortment.whpc != 0 || assortment.whcs != 0 || assortment.fso != 0 || assortment.updated) {
            holder.linearLayoutPCount.setBackgroundColor(mContext.getResources().getColor(R.color.blue_gray));
        }

        return view;
    }


    public void filter(final int filterCode, String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrAssortmentResultList.clear();

        if (charText.length() == 0) {
            arrAssortmentResultList.addAll(arrAssortmentList);
        }
        else
        {
            for (Assortment assortment : arrAssortmentList)
            {
                Boolean lvalid;

                switch (filterCode) {
                    case 0:
                        lvalid = assortment.category.toLowerCase(Locale.getDefault()).contains(charText);
                        break;
                    case 1:
                        lvalid = assortment.subcate.toLowerCase(Locale.getDefault()).contains(charText);
                        break;
                    case 2:
                        lvalid = assortment.brand.toLowerCase(Locale.getDefault()).contains(charText);
                        break;
                    case 3:
                        lvalid = assortment.division.toLowerCase(Locale.getDefault()).contains(charText);
                        break;
                    case 4:
                        lvalid = assortment.sapc != 0 || assortment.whpc != 0 || assortment.whcs != 0;
                        break;
                    case 5:
                        lvalid = assortment.sapc == 0 && assortment.whpc == 0 && assortment.whcs == 0;
                        break;
                    default:
                        lvalid = assortment.category.toLowerCase(Locale.getDefault()).contains(charText) ||
                                assortment.brand.toLowerCase(Locale.getDefault()).contains(charText) ||
                                assortment.subcate.toLowerCase(Locale.getDefault()).contains(charText) ||
                                assortment.division.toLowerCase(Locale.getDefault()).contains(charText) ||
                                assortment.desc.toLowerCase(Locale.getDefault()).contains(charText) ||
                                assortment.barcode.toLowerCase(Locale.getDefault()).contains(charText);
                }

                if (lvalid) arrAssortmentResultList.add(assortment);

            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arrAssortmentResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrAssortmentResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
