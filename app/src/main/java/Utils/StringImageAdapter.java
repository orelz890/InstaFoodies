package Utils;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instafoodies.R;

import java.util.List;

public class StringImageAdapter extends RecyclerView.Adapter<StringImageAdapter.ImageViewHolder> {
    private List<String> images;

    // Constructor
    public StringImageAdapter(List<String> images) {
        this.images = images;
    }


    // ViewHolder class
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_imageView);

        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        String imageUri = images.get(position);
        Glide.with(holder.imageView).load(imageUri).fitCenter().into(holder.imageView);
    }

}

