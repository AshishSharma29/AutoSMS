package com.auto_reply.calling;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.auto_reply.model.CheckLicenceResponseModel;
import com.auto_reply.model.LoginResponseModel;
import com.auto_reply.model.PhoneNumberModel;
import com.auto_reply.model.UpdatedMessageModel;
import com.auto_reply.util.PhonecallReceiver;
import com.auto_reply.util.Prefs;
import com.auto_reply.util.SimUtil;

import java.util.ArrayList;
import java.util.Date;

public class CallReceiver extends PhonecallReceiver {

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        //
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        //
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        PhoneNumberModel phoneNumberModel = Prefs.getObjectFromPref(ctx, number);
        if (phoneNumberModel == null) {
            phoneNumberModel = new PhoneNumberModel();
            phoneNumberModel.setNumber(number);
            phoneNumberModel.setTimeMillis(start.getTime());
            phoneNumberModel.setInComingMessage(true);
        } else if (!(new Date(phoneNumberModel.getTimeMillis()).equals(new Date()))) {
            phoneNumberModel.setTimeMillis(start.getTime());
        } else return;

        LoginResponseModel loginResponseModel = Prefs.getObjectFromPref(ctx, LoginResponseModel.class.getName());
        if (!loginResponseModel.getResponsePacket().getSMS_SERVICE())
            return;
        UpdatedMessageModel updatedMessageModel = Prefs.getObjectFromPref(ctx, UpdatedMessageModel.class.getSimpleName());
        CheckLicenceResponseModel checkLicenceResponseModel = Prefs.getObjectFromPref(ctx, CheckLicenceResponseModel.class.getSimpleName());
        String message = "";
        if (updatedMessageModel == null) {
            message = loginResponseModel.getResponsePacket().getDefaultSMS();
            message += loginResponseModel.getResponsePacket().getWEB_URL() ? "" : loginResponseModel.getResponsePacket().getUserWebUrl();
        } else {
            message = updatedMessageModel.getIncomingMessage();
            if (message.trim().isEmpty())
                return;
            message += loginResponseModel.getResponsePacket().getWEB_URL() ? " " + updatedMessageModel.getWebSiteUrl() : "";
        }

        if (checkLicenceResponseModel.getResponsePacket().getStatus() == 200 || checkLicenceResponseModel.getResponsePacket().getStatus() == 201) {
            if (phoneNumberModel.getInComingMessage().isEmpty() || !phoneNumberModel.getInComingMessage().equals(message)) {
                sendSMS(number, message, ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        } else {
            if (phoneNumberModel.getInComingMessage().isEmpty() || !phoneNumberModel.getInComingMessage().equals(loginResponseModel.getResponsePacket().getDefaultSMS())) {
                sendSMS(number, loginResponseModel.getResponsePacket().getDefaultSMS(), ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        }
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        if ((end.getTime() - start.getTime()) / 1000 > 10)
            createMessageAndSend(ctx, number, start, end);
    }

    private void createMessageAndSend(Context ctx, String number, Date start, Date end) {
        PhoneNumberModel phoneNumberModel = Prefs.getObjectFromPref(ctx, number);
        if (phoneNumberModel == null) {
            phoneNumberModel = new PhoneNumberModel();
            phoneNumberModel.setNumber(number);
            phoneNumberModel.setTimeMillis(start.getTime());
            phoneNumberModel.setOutGoingMessage(true);
        } else if (!(new Date(phoneNumberModel.getTimeMillis()).equals(new Date()))) {
            phoneNumberModel.setTimeMillis(start.getTime());
        } else return;


        LoginResponseModel loginResponseModel = Prefs.getObjectFromPref(ctx, LoginResponseModel.class.getName());
        if (!loginResponseModel.getResponsePacket().getSMS_SERVICE())
            return;
        UpdatedMessageModel updatedMessageModel = Prefs.getObjectFromPref(ctx, UpdatedMessageModel.class.getSimpleName());
        CheckLicenceResponseModel checkLicenceResponseModel = Prefs.getObjectFromPref(ctx, CheckLicenceResponseModel.class.getSimpleName());
        String message = "";
        if (updatedMessageModel == null) {
            message = loginResponseModel.getResponsePacket().getDefaultSMS();
            message += loginResponseModel.getResponsePacket().getWEB_URL() ? "" : loginResponseModel.getResponsePacket().getUserWebUrl();
        } else {
            message = updatedMessageModel.getOutgoingMessage();
            if (message.trim().isEmpty())
                return;
            message += loginResponseModel.getResponsePacket().getWEB_URL() ? " " + updatedMessageModel.getWebSiteUrl() : "";
        }

        if (checkLicenceResponseModel.getResponsePacket().getStatus() == 200 || checkLicenceResponseModel.getResponsePacket().getStatus() == 201) {
            if (phoneNumberModel.getOutGoingMessage().isEmpty() || !phoneNumberModel.getOutGoingMessage().equals(message)) {
                phoneNumberModel.setOutGoingMessage(message);
                sendSMS(number, message, ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        } else {
            if (phoneNumberModel.getOutGoingMessage().isEmpty() || !phoneNumberModel.getOutGoingMessage().equals(loginResponseModel.getResponsePacket().getDefaultSMS())) {
                phoneNumberModel.setOutGoingMessage(loginResponseModel.getResponsePacket().getDefaultSMS());
                sendSMS(number, loginResponseModel.getResponsePacket().getDefaultSMS(), ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        }
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        PhoneNumberModel phoneNumberModel = Prefs.getObjectFromPref(ctx, number);
        if (phoneNumberModel == null) {
            phoneNumberModel = new PhoneNumberModel();
            phoneNumberModel.setNumber(number);
            phoneNumberModel.setTimeMillis(start.getTime());
            phoneNumberModel.setOutGoingMessage(true);
        } else if (!(new Date(phoneNumberModel.getTimeMillis()).equals(new Date()))) {
            phoneNumberModel.setTimeMillis(start.getTime());
        } else return;

        LoginResponseModel loginResponseModel = Prefs.getObjectFromPref(ctx, LoginResponseModel.class.getName());
        if (!loginResponseModel.getResponsePacket().getSMS_SERVICE())
            return;
        UpdatedMessageModel updatedMessageModel = Prefs.getObjectFromPref(ctx, UpdatedMessageModel.class.getSimpleName());
        CheckLicenceResponseModel checkLicenceResponseModel = Prefs.getObjectFromPref(ctx, CheckLicenceResponseModel.class.getSimpleName());
        String message = "";
        if (updatedMessageModel == null) {
            message = loginResponseModel.getResponsePacket().getDefaultSMS();
            message += loginResponseModel.getResponsePacket().getWEB_URL() ? "" : loginResponseModel.getResponsePacket().getUserWebUrl();
        } else {
            message = updatedMessageModel.getMissedMessage();
            if (message.trim().isEmpty())
                return;
            message += loginResponseModel.getResponsePacket().getWEB_URL() ? " " + updatedMessageModel.getWebSiteUrl() : "";
        }

        if (checkLicenceResponseModel.getResponsePacket().getStatus() == 200 || checkLicenceResponseModel.getResponsePacket().getStatus() == 201) {
            if (phoneNumberModel.getMissedMessage().isEmpty() || !phoneNumberModel.getMissedMessage().equals(message)) {
                phoneNumberModel.setMissedMessage(message);
                sendSMS(number, message, ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        } else {
            if (phoneNumberModel.getMissedMessage().isEmpty() || !phoneNumberModel.getMissedMessage().equals(loginResponseModel.getResponsePacket().getDefaultSMS())) {
                phoneNumberModel.setMissedMessage(loginResponseModel.getResponsePacket().getDefaultSMS());
                sendSMS(number, loginResponseModel.getResponsePacket().getDefaultSMS(), ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        }
    }

    void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void sendSMS(String phoneNo, String msg, Context ctx, int simID) {
        if (msg.trim().isEmpty())
            return;

        SimUtil.sendSMS(ctx, simID, phoneNo, null, msg, null, null);
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(ctx, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(ctx, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}