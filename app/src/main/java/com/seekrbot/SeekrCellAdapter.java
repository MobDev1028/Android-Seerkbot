package com.seekrbot;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by silver on 11/11/16.
 */
public class SeekrCellAdapter extends BaseAdapter {
    private ArrayList<String> m_items = new ArrayList<>();
    private  LayoutInflater inflater=null;
    private Context m_context;

    public ArrayList<ViewHolder> _sacredCellArray = new ArrayList<>();

    public SeekrCellAdapter(ArrayList<String> items, Context context)
    {
        super();
        m_items = items;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        return m_items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        String url = null;

        if(convertView==null){
            holder = new ViewHolder();


            convertView = inflater.inflate(R.layout.seekr_cell, null);
            holder.txtSelection = (TextView)convertView.findViewById(R.id.tv_text);
            holder.txtSelection.setText(m_items.get(position));

            Typeface font = Typeface.createFromAsset(m_context.getAssets(), "fonts/Existence-Light.ttf");
            holder.txtSelection.setTypeface(font);


            holder.checkImage = (ImageView) convertView.findViewById(R.id.check_image);
            if (position == 0) {
                holder.checkImage.setImageResource(R.drawable.checked);
//                Global.sacredName = m_items.get(0);
//                Global.sacredName =Global.sacredName.replace(Global.sacredName.substring(1), Global.sacredName.substring(1).toLowerCase());
//
//                Global.attributeName = Global.attributeNames[0];
            }

            _sacredCellArray.add(holder);

        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }
    public static class ViewHolder {
        public TextView txtSelection;
        public ImageView checkImage;


    }


}



