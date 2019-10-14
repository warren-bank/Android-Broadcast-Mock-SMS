package com.github.warren_bank.broadcast_mock_sms.helpers;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SMS {

    public static final void send(Context context, String sender, String body) throws Exception {
        byte[] pdu = null;
        byte[] scBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD("0000000000");
        byte[] senderBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD(sender);
        int lsmcs = scBytes.length;
        byte[] dateBytes = new byte[7];
        Calendar calendar = new GregorianCalendar();
        dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
        dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
        dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
        dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
        dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
        dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
        dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) +
                calendar.get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
 
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        bo.write(lsmcs);
        bo.write(scBytes);
        bo.write(0x04);
        bo.write((byte) sender.length());
        bo.write(senderBytes);
        bo.write(0x00);
        bo.write(0x00);
        bo.write(dateBytes);
 
        String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
        Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
        Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod("stringToGsm7BitPacked", new Class[] { String.class });
        stringToGsm7BitPacked.setAccessible(true);
        byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null, body);
        bo.write(bodybytes);
        pdu = bo.toByteArray();
 
        // broadcast the SMS_RECEIVED to registered receivers
        broadcastSmsReceived(context, pdu);
 
        // or, directly send the message into the inbox and let the usual SMS handling happen - SMS appearing in Inbox, a notification with sound, etc.
        startSmsReceiverService(context, pdu);
    }

    private static final byte reverseByte(byte b) {
        return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
    }
 
    private static final void broadcastSmsReceived(Context context, byte[] pdu) {
        Intent intent = new Intent();
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra("pdus", new Object[] { pdu });
        context.sendBroadcast(intent);
    }
 
    private static final void startSmsReceiverService(Context context, byte[] pdu) {
        Intent intent = new Intent();
        intent.setClassName("com.android.mms", "com.android.mms.transaction.SmsReceiverService");
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra("pdus", new Object[] { pdu });
        intent.putExtra("format", "3gpp");
        context.startService(intent);
    }
}
