package net.beepinc.vip.MyAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import net.beepinc.vip.ImagePreview;
import net.beepinc.vip.Information.comment_information;
import net.beepinc.vip.R;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.json.TimeUtils;
import net.beepinc.vip.network.VolleySingleton;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tayo on 10/27/2015.
 */
public class comment_adapters extends RecyclerView.Adapter<comment_adapters.CommentsAdapter>{

    private ArrayList<comment_information> information;
    private LayoutInflater inflater;
    private Context context;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private UserLocalStore userLocalStore;

    public comment_adapters(Context context_){
        this.context = context_;
        inflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        userLocalStore = new UserLocalStore(context);
    }

    public void setList(ArrayList<comment_information> lists){
        this.information = lists;
        notifyDataSetChanged();
    }

    @Override
    public CommentsAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_comment_adapter, parent, false);
        CommentsAdapter commentsAdapter = new CommentsAdapter(view);
        return commentsAdapter;
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter holder, int position) {
        comment_information current = information.get(position);
User user  = userLocalStore.getLoggedUser();

        holder.textView.setText(current.username);
        holder.textView1.setText(current.comment);

        Date getTime = new Date(current.time);
        long milli = getTime.getTime();
        holder.textView2.setText(TimeUtils.setAgo(milli));

        String web_url = "http://www.gisanrinadetayo.comuf.com/images/";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/vip-profile-pictures/");
        File file = new File(path, current.image);
        if (file.exists()){
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(file.toString()));
        }else{
            String getRealImageName = Uri.encode(current.image);
            imageLoader.get(web_url+getRealImageName, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.imageView.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    holder.imageView.setImageResource(R.drawable.avatar_default);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return information.size();
    }

    class CommentsAdapter extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView, textView1, textView2;
        Context context_;
        public CommentsAdapter(View itemView) {
            super(itemView);

            context_ = itemView.getContext();
            imageView = (ImageView)itemView.findViewById(R.id.custom_comment_dp);
            textView = (TextView)itemView.findViewById(R.id.custom_comment_text);
            textView1 = (TextView)itemView.findViewById(R.id.custom_comment);
            textView2 = (TextView)itemView.findViewById(R.id.custom_comment_time);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayDP();
                }
            });
        }

        private void displayDP() {
            comment_information current = information.get(getPosition());
            String web_url = "http://www.gisanrinadetayo.comuf.com/images/";
            BitmapDrawable bmd = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bmd.getBitmap();
            String url = current.image;
            String username = current.username;
            Bundle bundle = new Bundle();
            bundle.putString("image", web_url+Uri.encode(url));
            bundle.putString("username", username);
            Intent intent = new Intent(context, ImagePreview.class);
            intent.putExtras(bundle);
            if (bitmap != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No Image yet", Toast.LENGTH_LONG).show();
            }
        }
    }
}
