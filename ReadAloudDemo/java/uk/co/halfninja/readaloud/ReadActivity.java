package uk.co.halfninja.readaloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Dialog activity which receives a piece of text to read and
 * instructs the TTS engine to read it out.
 *
 * Speech is ended when the activity is destroyed, so dismissing
 * the dialog stops speech.
 */
public class ReadActivity extends RoboActivity {

    private static final String TAG = "ReadAloud-readactivity";

    @InjectView(R.id.text) TextView text;
    @InjectResource(R.string.fallback_text) String fallbackText;
    @InjectResource(R.string.tts_init_error) String ttsInitError;

    private TextToSpeech tts;

    private TextToSpeech.OnUtteranceCompletedListener progressListener = new TextToSpeech.OnUtteranceCompletedListener () {
        @Override
        public void onUtteranceCompleted(String s) {
            Log.d(TAG, "Read utterance '" + s + "'");
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale locale = tts.getLanguage();
                    Log.i(TAG, String.format("Initted TTS. Language:%s", locale.getDisplayName()));
                    for (TextToSpeech.EngineInfo info : tts.getEngines()) {
                        Log.i(TAG, "Engine: " + info.name + " (" + info.label + ")");
                    }
                    tts.setOnUtteranceCompletedListener(progressListener);
                    startSpeaking();
                } else {
                    Toast.makeText(ReadActivity.this, ttsInitError, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void startSpeaking() {
        final String string = getTextToRead();
        HashMap<String,String> options = new HashMap<String, String>();
        options.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "readId");
        tts.speak(string, TextToSpeech.QUEUE_FLUSH, options);
//        Log.i(TAG, "Speak: " + string);
        text.setText(R.string.reading);
    }

    private String getTextToRead() {
        String providedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (providedText == null) providedText = fallbackText;
        return providedText;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.shutdown();
        }
    }

}
