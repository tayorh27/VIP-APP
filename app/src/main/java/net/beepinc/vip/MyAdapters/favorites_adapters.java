package net.beepinc.vip.MyAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import net.beepinc.vip.ImagePreview;
import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.R;
import net.beepinc.vip.network.VolleySingleton;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayo on 9/19/2015.
 */
public class favorites_adapters extends RecyclerView.Adapter<favorites_adapters.myViewHolder> {

    private final LayoutInflater inflater;
    private List<favorites_information> informations = new ArrayList<>();
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private Clicklistener clicklistener;
    Context context;
    String display;

    public favorites_adapters(Context context,String display){
        this.context = context;
        inflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        this.display = display;
    }

    public void setList(ArrayList<favorites_information> infos){
        this.informations = infos;
        //notifyItemRangeChanged(0, infos.size());
        notifyDataSetChanged();
    }
    public void setClicklistener(Clicklistener clicklistener){
        this.clicklistener = clicklistener;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.custom_favorites_adapter, viewGroup, false);
        myViewHolder viewholder = new myViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(final myViewHolder viewHolder, int i) {
        favorites_information current = informations.get(i);
        viewHolder.textView.setText(current.title);
        if(display.contentEquals("show")) {
            viewHolder.textView1.setText(current.subtitle);
        }else if(display.contentEquals("hide")){
            viewHolder.textView1.setText("");
            viewHolder.textView.setPadding(0,36,0,0);
        }
        viewHolder.textView2.setText(current.category);

        final String imageUrl = current.image_name;
        try {
            if (imageUrl != null) {
                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        viewHolder.imageView.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewHolder.imageView.setImageResource(R.drawable.avatar_default);
                        //Toast.makeText(context,imageUrl+"-"+error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                viewHolder.imageView.setImageResource(R.drawable.avatar_default);
            }
        }catch (Exception e){
            Log.d("tayo_error", e.toString());
        }

    }

    @Override
    public int getItemCount() {
        return informations.size();
    }


    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView, textView1,textView2;
        Context context_;

        public myViewHolder(View itemView) {
            super(itemView);
            context_ = itemView.getContext();

            imageView = (ImageView)itemView.findViewById(R.id.custom_favorite_dp);
            textView = (TextView)itemView.findViewById(R.id.custom_favorite_text);
            textView1 = (TextView)itemView.findViewById(R.id.custom_favorite_subtext);
            textView2 = (TextView)itemView.findViewById(R.id.custom_favorite_category);
            imageView.setClickable(true);

            imageView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clicklistener != null){
                        clicklistener.ItemClick(v, getPosition());
                    }
                }
            });


        }

        @Override
        public void onClick(View v) {

            try {
                favorites_information getList = informations.get(getPosition());
                BitmapDrawable bmd = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bmd.getBitmap();
                String username = getList.title;
                Bundle bundle = new Bundle();
                bundle.putString("image", getList.image_name);
                bundle.putString("username", username);
                Intent intent = new Intent(context_, ImagePreview.class);
                intent.putExtras(bundle);
                if (bitmap != null) {
                    context_.startActivity(intent);
                } else {
                    Toast.makeText(context_, "No Image yet", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context_, "No Image yet", Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface Clicklistener{
        public void ItemClick(View view, int Position);
    }
}
