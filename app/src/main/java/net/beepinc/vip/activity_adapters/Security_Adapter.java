package net.beepinc.vip.activity_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.beepinc.vip.R;

import java.security.Security;

/**
 * Created by tayo on 11/4/2015.
 */
public class Security_Adapter extends BaseAdapter {

    Context context;
    String[] entries;
    public Security_Adapter(Context context){
        this.context = context;
        entries = context.getResources().getStringArray(R.array.questions);
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
        img.setImageResource(R.drawable.faq_icon);
        return row;
    }
}
