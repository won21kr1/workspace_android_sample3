package uk.co.halfninja.readaloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * The activity launched by the app. The main use of the
 * app is through sharing content to the ReadActivity, but
 * you can use this to manually type in text and to access
 * the system TTS settings.
 */
public class LaunchActivity extends RoboActivity {

    @InjectView(R.id.editText) EditText editText;
    @InjectView(R.id.button)   Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                pressSpeakButton(null);
                return true;
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                updateButtonEnabled();
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean b) {
                boolean hasText = updateButtonEnabled();
            }
        });
    }

    private boolean updateButtonEnabled() {
        boolean hasText = !editText.getText().toString().trim().isEmpty();
        button.setEnabled(hasText);
        return hasText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.launch, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void pressSpeakButton(View view) {
        String string = editText.getEditableText().toString();
        Intent intent = new Intent(this, ReadActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, string);
        startActivity(intent);
    }

    public void openTtsSettings(MenuItem item) {
        Intent intent = new Intent();
        intent.setAction("com.android.settings.TTS_SETTINGS");
        this.startActivity(intent);
    }
}