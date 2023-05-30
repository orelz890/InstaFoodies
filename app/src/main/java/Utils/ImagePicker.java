//package Utils;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Base64;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.instafoodies.R;
//import com.github.drjacky.imagepicker.constant.ImageProvider;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.ByteArrayOutputStream;
//
//import MLKIT.helpers.ImageHelperActivity;
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//import kotlin.jvm.internal.Intrinsics;
//
//public class ImagePicker extends AppCompatActivity {
//
//    private Activity activity;
//    public ImagePicker(){
//
//    }
//
//    private void setImageButtons() {
//        ActivityResultLauncher<Intent> launcher=
//                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
//                    if(result.getResultCode()==RESULT_OK){
////                        List<Uri> uris = Utils.ImagePicker.Companion.getAllFile(result.getData());
////                        for (Uri uri : uris) {
////                            if (uri != null){
////                                inputImageView.setImageURI(uri);
////                            }
////                        }
////                        Toast.makeText(ImageHelperActivity.this, "Num of images: " + uris.size(), Toast.LENGTH_LONG).show();
//
//                        Intent data = result.getData();
//                        if (data != null) {
//                            Uri uri = data.getData();
//                            if (uri != null) {
//                                inputImageView.setImageURI(uri);
//                                String path = uri.getPath();
//                                // Convert image to base64-encoded string
//                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                            }
//                        }
//                        // Use the uri to load the image
//                    }else if(result.getResultCode()== com.github.drjacky.imagepicker.ImagePicker.RESULT_ERROR){
//                        // Use Utils.ImagePicker.Companion.getError(result.getData()) to show an error
//                    }
//                });
//        fab = findViewById(R.id.floatingActionButtonImageHelper);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    com.github.drjacky.imagepicker.ImagePicker.Companion.with(activity)
//                            .crop()                    //Crop image(Optional), Check Customization for more option
//                            .cropFreeStyle()
//                            .cropSquare()
//                            .setMultipleAllowed(false)
//                            .maxResultSize(1080, 1080, true)    //Final image resolution will be less than 1080 x 1080(Optional)
//                            .provider(ImageProvider.BOTH)
//                            .createIntentFromDialog((Function1)(new Function1(){
//                                public Object invoke(Object var1){
//                                    this.invoke((Intent)var1);
//                                    return Unit.INSTANCE;
//                                }
//
//                                public final void invoke(@NotNull Intent it){
//                                    Intrinsics.checkNotNullParameter(it,"it");
//                                    launcher.launch(it);
//                                }
//                            }));
//                } catch (Exception ignored) {
//
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        try {
////            Toast.makeText(getApplicationContext(), "image", Toast.LENGTH_LONG).show();
////            System.out.println("\n\nData = " + data + "\n\n");
//            super.onActivityResult(requestCode, resultCode, data);
//            if (data != null) {
//                Uri imgUri = data.getData();
//                if (imgUri != null) {
////                    inputImageView.setImageURI(imgUri);
//
//                    String path = imgUri.getPath();
//                    // Convert image to base64-encoded string
//                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                    Bitmap bitmap = BitmapFactory.decodeFile(path);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//
//                    byte[] imageData = outputStream.toByteArray();
//                    String imageDataBase64 = Base64.encodeToString(imageData, Base64.DEFAULT);
////                    Toast.makeText(getApplicationContext(), "image: " + imageDataBase64, Toast.LENGTH_LONG).show();
//
////                    runClassification(bitmap);
////                                uploadedImages.add(imageDataBase64);
//                }
//
//            } else {
//                Toast.makeText(getApplicationContext(), "You exceeded limit of images", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (Exception ignored) {
//
//        }
//    }
//}
