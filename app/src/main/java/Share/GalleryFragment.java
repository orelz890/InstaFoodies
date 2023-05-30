package Share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instafoodies.R;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import Profile.AccountSettingsActivity;
import Utils.FilePaths;
import Utils.FileSearch;
import Utils.GridImageAdapter;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int IMAGE_PICKER_REQUEST_CODE = 1;


    private GridView gridView;
    private ImageView galleryImage;
    private TextView addImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private List<Uri> selectedImages;
    private GridImageAdapter adapter;
    private ActivityResultLauncher<Intent> launcher;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage = view.findViewById(R.id.galleryImageView);
        gridView = view.findViewById(R.id.gridView);
        mProgressBar = view.findViewById(R.id.progressBar);

        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        selectedImages = new ArrayList<>();
        Log.d(TAG, "onCreateView: started.");

        ImageView shareClose = view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                getActivity().finish();
            }
        });

        TextView nextScreen = view.findViewById(R.id.tvNext);
        addImage = view.findViewById(R.id.tvAdd);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

                // Handle the selected images
                for (Uri imageUri : selectedImages) {
                    // Do something with the image URI
                    Log.d(TAG, "Selected Image URI: " + imageUri.toString());
                }

                // Proceed to the next screen
                if (isRootTask()) {
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    // Pass the selected images to the next activity
                    intent.putParcelableArrayListExtra(getString(R.string.selected_images), new ArrayList<>(selectedImages));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    // Pass the selected images to the account settings activity
                    intent.putParcelableArrayListExtra(getString(R.string.selected_images), new ArrayList<>(selectedImages));
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        setupGridView();
        ImagePicker();

        return view;
    }

    private boolean isRootTask() {
        return ((ShareActivity) requireActivity()).getTask() == 0;
    }


    private void setupGridView() {
        // Set up the grid view with the images from the selected directory
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        // Use the GridImageAdapter to populate the grid view with images
        adapter = new GridImageAdapter(requireContext(), R.layout.layout_grid_image_view, mAppend, selectedImages);
        gridView.setAdapter(adapter);
    }

    private void ImagePicker() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            selectedImages.add(imageUri);  // Add the imageUri to the list
                        }
                        // Update the GridView with the selected images
                        adapter.notifyDataSetChanged();
                    } else if (data.getData() != null) {
                        Uri imageUri = data.getData();
                        selectedImages.add(imageUri);  // Add the imageUri to the list
                        // Update the GridView with the selected images
                        adapter.notifyDataSetChanged();
                    }
                }
            } else if (result.getResultCode() == com.github.drjacky.imagepicker.ImagePicker.RESULT_ERROR) {
                // Use Utils.ImagePicker.Companion.getError(result.getData()) to show an error
            }
        });

        addImage.setOnClickListener(view -> {
            try {

                com.github.drjacky.imagepicker.ImagePicker.Companion.with(requireActivity())
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .cropFreeStyle()
                        .cropSquare()
                        .setMultipleAllowed(true)
                        .bothCameraGallery()
                        .maxResultSize(1080, 1080, true)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .provider(ImageProvider.BOTH)
                        .createIntentFromDialog((Function1<Intent, Unit>) intent -> {
                            launcher.launch(intent);
                            return Unit.INSTANCE;
                        });
            } catch (Exception ignored) {

            }
        });


        // Add the selected image URIs to the list and update the GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri imageURL = selectedImages.get(position);
                setImage(imageURL, galleryImage, mAppend);
            }
        });
    }


    private void setImage(Uri imageURL, ImageView image, String append) {
        mProgressBar.setVisibility(View.VISIBLE);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imageURL.getPath(), image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
