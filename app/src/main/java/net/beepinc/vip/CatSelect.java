package net.beepinc.vip;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Created by tayo on 8/15/2015.
 */
public class CatSelect extends BaseAdapter {

    Context context;
    String[] entries;
    int[] ims = { R.drawable.avatar_default, R.drawable.avatar_default,
            R.drawable.avatar_default, R.drawable.avatar_default};

    public CatSelect(Context context){
        this.context = context;
        entries = context.getResources().getStringArray(R.array.category);
    }


    @Override
    public int getCount() {
        return entries.length;
    }

    @Override
    public Object getItem(int position) {
        return entries[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.spinnerlist,parent,false);
        }else {
            row = convertView;
        }
        TextView tv = (TextView)row.findViewById(R.id.textView1);
        ImageView img = (ImageView)row.findViewById(R.id.imageView1);
        tv.setText(entries[position]);
        img.setImageResource(ims[position]);
        return row;
    }
}
