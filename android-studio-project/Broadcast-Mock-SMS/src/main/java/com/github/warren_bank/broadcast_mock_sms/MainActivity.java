package com.github.warren_bank.broadcast_mock_sms;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabWidget;

public class MainActivity extends ActivityGroup {
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(getLocalActivityManager());

        tabHost.addTab(tabHost.newTabSpec("tab_sms_text").setIndicator(getResources().getString(R.string.tab_sms_text)).setContent(new Intent(this, TextSmsActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab_sms_data").setIndicator(getResources().getString(R.string.tab_sms_data)).setContent(new Intent(this, DataSmsActivity.class)));
    }
}
