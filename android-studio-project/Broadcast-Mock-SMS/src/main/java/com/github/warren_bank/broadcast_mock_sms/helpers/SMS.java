package com.github.warren_bank.broadcast_mock_sms.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SMS {

    public static final void sendText(Context context, String sender, String body) throws Exception {
        // sanity check
        if ((sender == null) || sender.isEmpty() || (body == null) || body.isEmpty())
            throw new Exception("bad input");

        byte[] pdu = getPduText(sender, body);

        // broadcast: "android.provider.Telephony.SMS_RECEIVED"
        broadcastSmsReceived(context, pdu);
    }

    public static final void sendData(Context context, String sender, String hex, int port) throws Exception {
        // sanity check
        if ((sender == null) || sender.isEmpty() || (hex == null) || hex.isEmpty() || (port < 0))
            throw new Exception("bad input");

        byte[] pdu = getPduData(sender, hex);

        // broadcast: "android.intent.action.DATA_SMS_RECEIVED"
        broadcastDataSmsReceived(context, pdu, port);
    }

    private static final byte[] getPduText(String sender, String body) throws Exception {
        ByteArrayOutputStream bo = getPduHeader(sender);

        String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
        Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
        Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod("stringToGsm7BitPacked", new Class[] { String.class });
        stringToGsm7BitPacked.setAccessible(true);
        byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null, body);
        bo.write(bodybytes);
        byte[] pdu = bo.toByteArray();

        return pdu;
    }

    private static final byte[] getPduData(String sender, String hex) throws Exception {
        ByteArrayOutputStream bo = getPduHeader(sender);

        byte[] bodybytes = hexToByteArray(hex);
        bo.write(bodybytes);
        byte[] pdu = bo.toByteArray();

        return pdu;
    }

    private static final ByteArrayOutputStream getPduHeader(String sender) throws Exception {
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

        return bo;
    }

    private static final byte reverseByte(byte b) {
        return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
    }

    private static final byte[] hexToByteArray(String hex) throws Exception {
        if (hex.length() % 2 != 0) {
            throw new Exception("Hex code contains an incomplete byte.");
        }

        int len = hex.length() / 2;
        byte[] converted = new byte[len];
        for (int i = 0; i < len; i++) {
            converted[i] = (byte) ((Character.digit(hex.charAt(i * 2), 16) << 4)
                    + Character.digit(hex.charAt((i * 2) + 1), 16));
        }
        return converted;
    }

    private static final void broadcastSmsReceived(Context context, byte[] pdu) {
        Intent intent = new Intent();
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra("pdus", new Object[] { pdu });
        intent.putExtra("format", "3gpp");
        context.sendBroadcast(intent);
    }

    private static final void broadcastDataSmsReceived(Context context, byte[] pdu, int port) {
        Uri dataUri = Uri.parse("sms://*:" + port);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DATA_SMS_RECEIVED");
        intent.putExtra("pdus", new Object[] { pdu });
        intent.putExtra("format", "3gpp");
        intent.setDataAndNormalize(dataUri);
        context.sendBroadcast(intent);
    }

    /*
    private static final void startSmsReceiverService(Context context, byte[] pdu) throws Exception {
        Intent intent = new Intent();
        intent.setClassName("com.android.mms", "com.android.mms.transaction.SmsReceiverService");
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra("pdus", new Object[] { pdu });
        intent.putExtra("format", "3gpp");
        context.startService(intent);
    }
    */
}
