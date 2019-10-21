package com.github.warren_bank.broadcast_mock_sms;

import com.github.warren_bank.broadcast_mock_sms.helpers.SMS;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TextSmsActivity extends Activity {
    private static String TAG = "TextSmsActivity";

    private TextView input_sender;
    private TextView input_message;
    private Button button_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_sms);

        input_sender  = (TextView) findViewById(R.id.input_sender);
        input_message = (TextView) findViewById(R.id.input_message);
        button_send   = (Button)   findViewById(R.id.button_send);

        reset();

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity context = TextSmsActivity.this;
                final String   sender  = input_sender.getText().toString();
                final String   message = input_message.getText().toString();

                try {
                    SMS.sendText(context, sender, message);

                    Toast.makeText(context, "Mock SMS broadcast sent", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Sent the test sms");
                }
                catch (Exception e) {
                    Toast.makeText(context, "Error: Mock SMS broadcast not sent", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Exception : " + e.getClass() + " : " + e.getMessage());
                }
                reset();
            }
        });
    }

    private void reset() {
        reset("", "");
    }

    private void reset(String sender, String message) {
        input_sender.setText(sender);
        input_message.setText(message);
    }
}
