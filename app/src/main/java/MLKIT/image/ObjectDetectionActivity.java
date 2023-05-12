package MLKIT.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.ArrayList;
import java.util.List;

import MLKIT.helpers.BoxWithText;
import MLKIT.helpers.ImageHelperActivity;

public class ObjectDetectionActivity extends ImageHelperActivity {

    private ObjectDetector objectDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Multiple object detection in static images
        ObjectDetectorOptions options =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()
                        .build();

        objectDetector = ObjectDetection.getClient(options);
    }

    @Override
    protected void runClassification(Bitmap bitmap) {
        super.runClassification(bitmap);
        InputImage inputImage = InputImage.fromBitmap(bitmap,0);
        objectDetector.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<DetectedObject>>() {
                    @Override
                    public void onSuccess(@NonNull List<DetectedObject> detectedObjects) {
                        if (!detectedObjects.isEmpty()) {
                            StringBuilder builder = new StringBuilder();
                            List<BoxWithText> boxes = new ArrayList<>();
                            for (DetectedObject object : detectedObjects) {
                                List<DetectedObject.Label> labels = object.getLabels();
                                if (!labels.isEmpty()) {
                                    for (DetectedObject.Label label : labels) {
//                                        System.out.println("\n\n\n\n\n" + label.getText() + "\n\n\n\n\n");
                                        String labelText = label.getText();
                                        float confidence = label.getConfidence();
                                        builder.append(labelText).append(": ").append(confidence).append("\n");
                                        boxes.add(new BoxWithText(labelText, object.getBoundingBox()));
                                    }
                                }
                                else {
                                    builder.append("Unknown").append("\n");
                                    boxes.add(new BoxWithText("Unknown\n", object.getBoundingBox()));
//                                    System.out.println("\n\n\nEmpty, " + object.toString() + "\n\n\n");
                                }
                            }
//                            System.out.println("\n\n\nEmpty??\n\n\n");
                            getOutputTextView().setText(builder.toString());
                            getInputImageView().setImageBitmap(drawDetectionResult(bitmap, boxes));
                        } else {
//                            System.out.println("\n\n\nIm empty \n\n\n");
                            getOutputTextView().setText("Could not detect");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

}
