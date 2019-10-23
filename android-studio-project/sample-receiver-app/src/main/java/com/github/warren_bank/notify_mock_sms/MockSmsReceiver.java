package com.github.warren_bank.notify_mock_sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MockSmsReceiver extends BroadcastReceiver {
    private static final String TEXT_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String DATA_SMS_RECEIVED = "android.intent.action.DATA_SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final Bundle extras = intent.getExtras();

        if ((action == null) || (extras == null))
            return;

        SmsMessage[] messages = null;
        String sender         = null;
        String body           = null;
        String title          = null;
        String subject        = null;

        if (action.equals(TEXT_SMS_RECEIVED)) {
            messages = get_SmsMessages(extras);

            for (SmsMessage message : messages) {
                if (message == null)
                    continue;

                try {
                    sender  = message.getOriginatingAddress().trim();
                    body    = message.getMessageBody().trim();

                    title   = "Mock Text SMS Received";
                    subject = "from: " + sender;
                    showNotification(context, title, subject, body);
                }
                catch (Exception e) { continue; }
            }
        }

        if (action.equals(DATA_SMS_RECEIVED)) {
            messages = get_SmsMessages(extras);

            Uri uri = intent.getData();
            int port = uri.getPort();

            byte[] data;
            StringBuilder data_sb;

            for (SmsMessage message : messages) {
                if (message == null)
                    continue;

                try {
                    sender = message.getOriginatingAddress().trim();
                    data   = message.getUserData();

                    if ((data == null) || (data.length == 0)) continue;

                    data_sb = new StringBuilder();
                    for (byte b : data) {
                        data_sb.append(String.format("%02x", b));
                    }
                    body = data_sb.toString().toLowerCase();

                    title   = "Mock Data SMS Received";
                    subject = "from: " + sender + "\nport: " + port;
                    showNotification(context, title, subject, body);
                }
                catch (Exception e) { continue; }
            }
        }
    }

    private final static SmsMessage[] get_SmsMessages(Bundle extras) {
        final Object[] pdus = (Object[])extras.get("pdus");
        final String format = extras.getString("format", "3gpp");
        final SmsMessage[] messages = new SmsMessage[pdus.length];

        for (int i = 0; i < pdus.length; i++) {
            try {
                messages[i] = (Build.VERSION.SDK_INT >= 23)
                    ? SmsMessage.createFromPdu((byte[])pdus[i], format)
                    : SmsMessage.createFromPdu((byte[])pdus[i]);
            }
            catch (Exception e) {}
        }
        return messages;
    }

    private final static void showNotification(Context context, String title, String subject, String body) {
        Notification notification = new Notification.Builder(context)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle(title)
            .setContentText(subject)
            .setStyle(new Notification.BigTextStyle().bigText(subject + "\n\n" + body))
            .setAutoCancel(true)
            .build();

        NotificationManager notification_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notification_manager.notify(0, notification);
    }
}
