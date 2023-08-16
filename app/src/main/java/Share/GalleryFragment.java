package Share;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instafoodies.R;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
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
    private ImageView addImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private ArrayList<Uri> selectedImages;
    private GridImageAdapter adapter;
    private ActivityResultLauncher<Intent> launcher;

    private ImageLabeler imageLabeler;

    private List<Bitmap> imageUris;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage = view.findViewById(R.id.galleryImageView);
        gridView = view.findViewById(R.id.gridView);
        mProgressBar = view.findViewById(R.id.progressBar);

        imageUris = new ArrayList<>();

        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        selectedImages = new ArrayList<>();
        Log.d(TAG, "onCreateView: started.");

        imageLabeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build());

        ImageView shareClose = view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                getActivity().finish();
            }
        });

        ImageView nextScreen = view.findViewById(R.id.tvNext);
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


                if (selectedImages.size() >= 1) {
                    if (!imageUris.isEmpty()){
                        for (int i = 0; i < imageUris.size() -1 ; i++){
                            runClassification(imageUris.get(i));

                        }
                    }
                    // Proceed to the next screen
                    if (isRootTask()) {
                        Intent intent = new Intent(getActivity(), NextActivity.class);
                        // Pass the selected images to the NextActivity
                        intent.putParcelableArrayListExtra(getString(R.string.selected_images), new ArrayList<>(selectedImages));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), NextRecipeActivity.class);
                        // Pass the selected images to the NextRecipeActivity
                        intent.putParcelableArrayListExtra(getString(R.string.selected_images), new ArrayList<>(selectedImages));
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(getActivity(), "Your Post must to Include at Least One Photo " , Toast.LENGTH_SHORT).show();
                }

            }
        });

        setupGridView();
        ImagePicker();


        // Register the long click listener on the GridView
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the image URI at the clicked position
                Uri imageUri = selectedImages.get(position);

                // Show a confirmation dialog before deleting the image
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Delete Image");
                builder.setMessage("Are you sure you want to delete this image?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the image from the selectedImages list
                        selectedImages.remove(imageUri);

                        // Update the GridView with the updated list
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

                // Return true to consume the long click event
                return true;
            }
        });


        return view;
    }

    protected void runClassification(Bitmap bitmap) {
        System.out.println("im in runClassification");
        InputImage inputImage = InputImage.fromBitmap(bitmap,0);
        imageLabeler.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
            @Override
            public void onSuccess(List<ImageLabel> imageLabels) {
                if (imageLabels.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (ImageLabel label : imageLabels) {
                        assert true;
                        assert false;
                        if (!label.getText().equals("Food")){
                            System.out.println("\n\n\nProblem - " + label.getText() +" not food\\n\\n\\n");
                        }
                        else {
                            System.out.println("\n\n\nAmazing its a " + label.getText() + "\\n\\n\\n");
                        }

                        builder.append(label.getText())
                                .append(" : ")
                                .append(label.getConfidence())
                                .append("\n");
                    }
//                    getOutputTextView().setText(builder.toString());
                    System.out.println("\n\n\nFinished classification\\n\\n\\n");

                } else {
                    System.out.println("\n\n\nProblem - could not classify\\n\\n\\n");

//                    getOutputTextView().setText("Could not classify");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        System.out.println("im out of runClassification");

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
                        imageUris.clear();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();

                            // Run image classification
                            String path = imageUri.getPath();
                            // Convert image to base64-encoded string
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                            System.out.println("\n\n\nim here see me!!!\\n\\n\\n");
                            imageUris.add(bitmap);

                            runClassification(bitmap);

                            System.out.println("\n\n\nim here see me22!!!\\n\\n\\n");

                            // Set image in the grid
                            setImage(imageUri, galleryImage, mAppend);
                            selectedImages.add(imageUri);  // Add the imageUri to the list
                        }
                        // Update the GridView with the selected images
                        adapter.notifyDataSetChanged();
                    } else if (data.getData() != null) {
                        Uri imageUri = data.getData();
                        selectedImages.add(imageUri);  // Add the imageUri to the list
                        // Update the GridView with the selected images
                        setImage(imageUri, galleryImage, mAppend);
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
                        .setMultipleAllowed(false)
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
        Glide.with(getActivity()).load(imageURL).into(image);
        mProgressBar.setVisibility(View.GONE);

    }
}
