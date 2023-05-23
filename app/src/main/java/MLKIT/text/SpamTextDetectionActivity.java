package MLKIT.text;


import android.os.Bundle;
import android.util.Log;


import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier;

import java.io.IOException;
import java.util.List;

import MLKIT.helpers.TextHelperActivity;

public class SpamTextDetectionActivity extends TextHelperActivity {

    private static final String MODEL_PATH = "model_spam.tflite";
    private NLClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            classifier = NLClassifier.createFromFile(this, MODEL_PATH);
        } catch (IOException e) {
            Log.e(SpamTextDetectionActivity.class.getSimpleName(), e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        classifier.close();
        classifier = null;
    }

    @Override
    protected void runDetection(String text) {
        List<Category> apiResults = classifier.classify(text);
        float score = apiResults.get(1).getScore();
        if (score > 0.8f) {
            getOutputTextView().setText("Detected as spam.\nSpam score: " + score);
        } else {
            getOutputTextView().setText("Not detected as spam.\nSpam score: " + score);
        }
    }
}
