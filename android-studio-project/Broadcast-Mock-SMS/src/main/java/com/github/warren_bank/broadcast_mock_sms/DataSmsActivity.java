package com.github.warren_bank.broadcast_mock_sms;

import com.github.warren_bank.broadcast_mock_sms.helpers.SMS;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DataSmsActivity extends Activity {
    private static String TAG = "DataSmsActivity";

    private TextView input_sender;
    private TextView input_hex;
    private TextView input_port;
    private Button button_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_sms);

        input_sender = (TextView) findViewById(R.id.input_sender);
        input_hex    = (TextView) findViewById(R.id.input_hex_data);
        input_port   = (TextView) findViewById(R.id.input_port_number);
        button_send  = (Button)   findViewById(R.id.button_send);

        reset("", "0a0603b0af8203066a0005", "9200");

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity context = DataSmsActivity.this;
                final String   sender  = input_sender.getText().toString().trim();
                final String   hex     = input_hex.getText().toString().trim();
                final String   port    = input_port.getText().toString().trim();

                try {
                    SMS.sendData(context, sender, hex, Integer.parseInt(port, 10));

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
        reset("", "", "");
    }

    private void reset(String sender, String hex, String port) {
        input_sender.setText(sender);
        input_hex.setText(hex);
        input_port.setText(port);
    }
}
