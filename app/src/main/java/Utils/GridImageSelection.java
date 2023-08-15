//package Utils;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.drawable.Drawable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
//import com.example.instafoodies.R;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class GridImageSelection extends ArrayAdapter<String> {
//    private Context mContext;
//    private LayoutInflater mInflater;
//    private int layoutResource;
//    private String mAppend;
//    private List<String> imgURLs;
//    private List<Boolean> isSelectedList;
//
//
//    public GridImageSelection(Context context, int layoutResource, String append, List<String> imgURLs, GridView gridView) {
//        super(context, layoutResource, imgURLs);
//        mContext = context;
//        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.layoutResource = layoutResource;
//        mAppend = append;
//        this.imgURLs = imgURLs;
//        isSelectedList = new ArrayList<>(Collections.nCopies(imgURLs.size(), false));
//
//
//    }
//
//
//    private static class ViewHolder {
//        SquareImageView image;
//        ImageView selected;
//        ProgressBar mProgressBar;
//
//    }
//
//    public void toggleSelection(int position) {
//        isSelectedList.set(position, !isSelectedList.get(position));
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        /*
//         * ViewHolder build pattern (similar to recyclerview)
//         */
//        final Utils.GridImageSelection.ViewHolder holder;
//
//
//        if (convertView == null) {
//            convertView = mInflater.inflate(layoutResource, parent, false);
//            holder = new Utils.GridImageSelection.ViewHolder();
//            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressBar);
//            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);
//            holder.selected = (ImageView) convertView.findViewById(R.id.selectedIndicator);
//
//
//            // Storing the view im memory, not putting it on the page so the app will not slow down.
//            convertView.setTag(holder);
//
//        } else {
//            holder = (Utils.GridImageSelection.ViewHolder) convertView.getTag();
//        }
//        String imgURL = getItem(position).toString();
//
//        Glide.with(mContext)
//                .load(imgURL)
//                .placeholder(R.drawable.ic_android)
//                .error(R.drawable.ic_android)
//                .fitCenter()
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        holder.mProgressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        holder.mProgressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .into(holder.image);
//
//        // Update the image border based on selection
//        if (isSelectedList.get(position)) {
//            holder.selected.setVisibility(View.VISIBLE);
//        } else {
//            holder.selected.setVisibility(View.INVISIBLE);
//        }
//
//        return convertView;
//    }
//
//    public void selectionMode(int position){
//            isSelectedList.set(position, !isSelectedList.get(position));
//            notifyDataSetChanged();
//    }
//
//    @Override
//    public int getCount() {
//        return imgURLs.size();
//    }
//
//    @Override
//    public String getItem(int position) {
//        return imgURLs.get(position);
//    }
//}
//
//
//
//
package Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

public class GridImageSelection extends ArrayAdapter<String> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private List<String> imgURLs;
    private List<Integer> selectedIndexes; // Store selected indexes


    public GridImageSelection(Context context, int layoutResource, String append, List<String> imgURLs, GridView gridView) {
        super(context, layoutResource, imgURLs);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
        selectedIndexes = new ArrayList<>();
    }


    private static class ViewHolder {
        SquareImageView image;
        ImageView selected;
        ProgressBar mProgressBar;
    }

    public void toggleSelection(int position) {
        if (selectedIndexes.contains(position)) {
            selectedIndexes.remove(Integer.valueOf(position));
        } else {
            selectedIndexes.add(position);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Utils.GridImageSelection.ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new Utils.GridImageSelection.ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressBar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);
            holder.selected = (ImageView) convertView.findViewById(R.id.selectedIndicator);

            convertView.setTag(holder);
        } else {
            holder = (Utils.GridImageSelection.ViewHolder) convertView.getTag();
        }
        String imgURL = getItem(position).toString();

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
        if (selectedIndexes.contains(position)) {
            holder.selected.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
    public List<Integer> getSelectedIndexes() {
        return selectedIndexes;
    }


    public void selectionMode(int position) {
        if (selectedIndexes.contains(position)) {
            selectedIndexes.remove(Integer.valueOf(position));
        } else {
            selectedIndexes.add(position);
        }
        notifyDataSetChanged();
    }

    public void clearSelectedIndexes() {
        selectedIndexes.clear();
        notifyDataSetChanged();
    }

    public void clearImgURLs() {
        imgURLs.clear();
        notifyDataSetChanged();
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
