package MLKIT.helpers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.drjacky.imagepicker.ImagePicker;
import com.example.instafoodies.R;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;
import java.io.ByteArrayOutputStream;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class ImageHelperActivity extends AppCompatActivity {

    private ImageView inputImageView;
    private FloatingActionButton fab;
    private TextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_halper);

        inputImageView = findViewById(R.id.input_image_view);
        outputTextView = findViewById(R.id.text_View_Output);


        setImageButtons();
    }

    public void setLongClickListeners() {
        inputImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (inputImageView.getDrawable() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ImageHelperActivity.this,  R.style.Theme_Instafoodies_PopupOverlay);
                    final View customLayout = getLayoutInflater().inflate(R.layout.yes_no_dialog_layout, null);
                    builder.setView(customLayout);
                    builder.setCancelable(false);
                    builder.setTitle("do you want to delete?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            inputImageView.setImageDrawable(null);
                        }
                    });

                    builder.setNegativeButton("no", (dialogInterface, which) -> {
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            }
        });
    }


    private void setImageButtons() {
        setLongClickListeners();
        ActivityResultLauncher<Intent> launcher=
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                    if(result.getResultCode()==RESULT_OK){
//                        List<Uri> uris = Utils.ImagePicker.Companion.getAllFile(result.getData());
//                        for (Uri uri : uris) {
//                            if (uri != null){
//                                inputImageView.setImageURI(uri);
//                            }
//                        }
//                        Toast.makeText(ImageHelperActivity.this, "Num of images: " + uris.size(), Toast.LENGTH_LONG).show();

                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                inputImageView.setImageURI(uri);
                                String path = uri.getPath();
                                // Convert image to base64-encoded string
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                Bitmap bitmap = BitmapFactory.decodeFile(path);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                runClassification(bitmap);
                            }
                        }
                        // Use the uri to load the image
                    }else if(result.getResultCode()==ImagePicker.RESULT_ERROR){
                        // Use Utils.ImagePicker.Companion.getError(result.getData()) to show an error
                    }
                });
        fab = findViewById(R.id.floatingActionButtonImageHelper);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ImagePicker.Companion.with(ImageHelperActivity.this)
                            .crop()                    //Crop image(Optional), Check Customization for more option
                            .cropFreeStyle()
                            .cropSquare()
                            .setMultipleAllowed(false)
                            .maxResultSize(1080, 1080, true)    //Final image resolution will be less than 1080 x 1080(Optional)
                            .provider(ImageProvider.BOTH)
                            .createIntentFromDialog((Function1)(new Function1(){
                                public Object invoke(Object var1){
                                    this.invoke((Intent)var1);
                                    return Unit.INSTANCE;
                                }

                                public final void invoke(@NotNull Intent it){
                                    Intrinsics.checkNotNullParameter(it,"it");
                                    launcher.launch(it);
                                }
                            }));
                } catch (Exception ignored) {

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
//            Toast.makeText(getApplicationContext(), "image", Toast.LENGTH_LONG).show();
//            System.out.println("\n\nData = " + data + "\n\n");
            super.onActivityResult(requestCode, resultCode, data);
            if (data != null) {
                Uri imgUri = data.getData();
                if (imgUri != null) {
//                    inputImageView.setImageURI(imgUri);

                    String path = imgUri.getPath();
                    // Convert image to base64-encoded string
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                    byte[] imageData = outputStream.toByteArray();
                    String imageDataBase64 = Base64.encodeToString(imageData, Base64.DEFAULT);
//                    Toast.makeText(getApplicationContext(), "image: " + imageDataBase64, Toast.LENGTH_LONG).show();

//                    runClassification(bitmap);
//                                uploadedImages.add(imageDataBase64);
                }

            } else {
                Toast.makeText(getApplicationContext(), "You exceeded limit of images", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ignored) {

        }
    }

    protected void runClassification(Bitmap bitmap) {

    }

    protected TextView getOutputTextView() {
        return this.outputTextView;
    }

    protected ImageView getInputImageView() {
        return this.inputImageView;
    }

    protected Bitmap drawDetectionResult(
            Bitmap bitmap,
            List<BoxWithText> detectionResults
    ) {
        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint pen = new Paint();
        pen.setTextAlign(Paint.Align.LEFT);

        for (BoxWithText box : detectionResults) {
            // draw bounding box
            pen.setColor(Color.RED);
            pen.setStrokeWidth(8F);
            pen.setStyle(Paint.Style.STROKE);
            canvas.drawRect(box.rect, pen);

            Rect tagSize = new Rect(0, 0, 0, 0);

            // calculate the right font size
            pen.setStyle(Paint.Style.FILL_AND_STROKE);
            pen.setColor(Color.YELLOW);
            pen.setStrokeWidth(2F);

            pen.setTextSize(96F);
            pen.getTextBounds(box.text, 0, box.text.length(), tagSize);
            float fontSize = pen.getTextSize() * box.rect.width() / tagSize.width();

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.getTextSize()) {
                pen.setTextSize(fontSize);
            }

            float margin = (box.rect.width() - tagSize.width()) / 2.0F;
            if (margin < 0F) margin = 0F;
            canvas.drawText(
                    box.text, box.rect.left + margin,
                    box.rect.top + tagSize.height(), pen
            );
        }
        return outputBitmap;
    }
}

