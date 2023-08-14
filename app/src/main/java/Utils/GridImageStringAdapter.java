package Utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instafoodies.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Post;

public class GridImageStringAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private List<String> imgURLs;
    private List<Integer> selectedPositions = new ArrayList<>(); // To store selected item positions



    public GridImageStringAdapter(Context context, int layoutResource, String append, List<String> imgURLs) {
        super(context, layoutResource, imgURLs);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;


    }

    public void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(Integer.valueOf(position));
        } else {
            selectedPositions.add(position);
        }
        notifyDataSetChanged();
    }
    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList<>();
        for (Integer position : selectedPositions) {
            selectedItems.add(getItem(position));
        }
        return selectedItems;
    }

    public List<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /*
         * ViewHolder build pattern (similar to recyclerview)
         */
        final ViewHolder holder;



        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressBar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);

            // Storing the view im memory, not putting it on the page so the app will not slow down.
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String imgURL = getItem(position).toString();

        System.out.println("GridImageStringAdapter: - " + selectedPositions.toString());
        Glide.with(mContext)
                .load(imgURL)
                .placeholder(R.drawable.ic_android)
                .error(R.drawable.ic_android)
                .fitCenter()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.mProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.image);

        if (selectedPositions.contains(position)) {
            holder.image.setBackgroundResource(R.color.link_blue);
        } else {
            holder.image.setBackgroundResource(android.R.color.transparent);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return imgURLs.size();
    }

    @Override
    public String getItem(int position) {
        return imgURLs.get(position);
    }
}




