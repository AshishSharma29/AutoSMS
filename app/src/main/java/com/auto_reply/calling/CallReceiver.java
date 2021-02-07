package com.auto_reply.calling;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.auto_reply.BuildConfig;
import com.auto_reply.model.CheckLicenceResponseModel;
import com.auto_reply.model.LoginResponseModel;
import com.auto_reply.model.PhoneNumberModel;
import com.auto_reply.model.UpdatedMessageModel;
import com.auto_reply.util.PhonecallReceiver;
import com.auto_reply.util.Prefs;
import com.auto_reply.util.SimUtil;
import com.auto_reply.webservice.ApiCallbacks;
import com.auto_reply.webservice.WebApiUrls;
import com.auto_reply.webservice.WebServiceCaller;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        } else if (!(new SimpleDateFormat("dd/MM/yyyy").format(new Date(phoneNumberModel.getTimeMillis())).equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
                || !phoneNumberModel.isInComingMessage()) {
            phoneNumberModel.setInComingMessage(true);
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sendSMS(number, message, ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                }
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        } else {
            if (phoneNumberModel.getInComingMessage().isEmpty() || !phoneNumberModel.getInComingMessage().equals(loginResponseModel.getResponsePacket().getDefaultSMS())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sendSMS(number, loginResponseModel.getResponsePacket().getDefaultSMS(), ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                }
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
        } else if (!(new SimpleDateFormat("dd/MM/yyyy").format(new Date(phoneNumberModel.getTimeMillis())).equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
                || !phoneNumberModel.isOutGoingMessage()) {
            phoneNumberModel.setOutGoingMessage(true);
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sendSMS(number, message, ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                }
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        } else {
            if (phoneNumberModel.getOutGoingMessage().isEmpty() || !phoneNumberModel.getOutGoingMessage().equals(loginResponseModel.getResponsePacket().getDefaultSMS())) {
                phoneNumberModel.setOutGoingMessage(loginResponseModel.getResponsePacket().getDefaultSMS());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sendSMS(number, loginResponseModel.getResponsePacket().getDefaultSMS(), ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                }
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
        } else if (!(new SimpleDateFormat("dd/MM/yyyy").format(new Date(phoneNumberModel.getTimeMillis())).equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
                || !phoneNumberModel.isMissedMessage()) {
            phoneNumberModel.setMissedMessage(true);
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sendSMS(number, message, ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                }
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        } else {
            if (phoneNumberModel.getMissedMessage().isEmpty() || !phoneNumberModel.getMissedMessage().equals(loginResponseModel.getResponsePacket().getDefaultSMS())) {
                phoneNumberModel.setMissedMessage(loginResponseModel.getResponsePacket().getDefaultSMS());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sendSMS(number, loginResponseModel.getResponsePacket().getDefaultSMS(), ctx, loginResponseModel.getResponsePacket().getSLOT_ID());
                }
                Prefs.putObjectIntoPref(ctx, phoneNumberModel, number);
            }
        }
    }

    void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static void sendSMS(String phoneNo, String msg, Context ctx, int simID) {
        if (msg.trim().isEmpty())
            return;
        final ArrayList<Integer> simCardList = new ArrayList<>();
        SubscriptionManager subscriptionManager = SubscriptionManager.from(ctx);
        @SuppressLint("MissingPermission") final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                .getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            simCardList.add(subscriptionId);
        }
//        SimUtil.sendSMS(ctx, simID, phoneNo, null, msg, null, null);
        try {
            int smsToSendFrom = simCardList.get(simID);
            SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom);
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(ctx, "Message Sent",
                    Toast.LENGTH_LONG).show();
            sendLog(msg);
        } catch (Exception ex) {
            Toast.makeText(ctx, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public static void sendLog(String message) {
        message += " version name : " + BuildConfig.VERSION_NAME + " device name : " + android.os.Build.MODEL + " version name : " + Build.VERSION.SDK_INT;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Message", message);
        ;
        WebServiceCaller.INSTANCE.callWebApi(jsonObject, WebApiUrls.ERROR_LOG, new ApiCallbacks() {
            @Override
            public void onSuccess(@NotNull JsonObject jsonObject, @NotNull String anEnum) {
                Log.i("message uploaded", "new message logged");
            }

            @Override
            public void onError(@NotNull String jsonObject, @NotNull String anEnum) {
                Log.i("message uploaded", "new message error");
            }

            @Override
            public void networkError(@Nullable String message) {
                Log.i("message uploaded", "new message network error");
            }
        });
    }
}
