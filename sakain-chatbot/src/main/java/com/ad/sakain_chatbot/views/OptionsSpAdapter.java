package com.ad.sakain_chatbot.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ad.sakain_chatbot.R;
import com.ad.sakain_chatbot.model.MessageOption;

import java.util.ArrayList;

public class OptionsSpAdapter extends BaseAdapter implements SpinnerAdapter {

    private ArrayList<MessageOption> mOptions;
    private Context mContext;


    public OptionsSpAdapter(Context context, ArrayList<MessageOption> options) {
        this.mOptions = options;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return mOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  View.inflate(mContext, R.layout.item_sp_header, null);
        TextView textView = (TextView) view.findViewById(R.id.txtHeaderOptionItem);
        textView.setText(mOptions.get(position).getTitle());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view =  View.inflate(mContext, R.layout.item_sp, null);
        final TextView textView = view.findViewById(R.id.txtOptionItem);
        textView.setText(mOptions.get(position).getTitle());
        return view;
    }
}