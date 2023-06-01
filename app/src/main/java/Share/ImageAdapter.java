package Share;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instafoodies.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<Uri> imageUris;

    // Constructor
    public ImageAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }


    // ViewHolder class
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;


        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_imageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        Glide.with(holder.imageView).load(imageUri).into(holder.imageView);

        // Set a click listener on the delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the photo at the given position
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    imageUris.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                }
            }
        });
    }

}

