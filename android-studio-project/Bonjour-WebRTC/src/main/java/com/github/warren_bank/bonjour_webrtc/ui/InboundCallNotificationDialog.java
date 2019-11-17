package com.github.warren_bank.bonjour_webrtc.ui;

import com.github.warren_bank.bonjour_webrtc.R;
import com.github.warren_bank.bonjour_webrtc.data_model.SharedPrefs;
import com.github.warren_bank.bonjour_webrtc.service.ServerService;
import com.github.warren_bank.bonjour_webrtc.util.OrgAppspotApprtcGlue;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.WindowManager;

public final class InboundCallNotificationDialog {

    public static void show(Context context, String contactName) {
        final MediaPlayer mediaPlayer;
        final AlertDialog alert;
        final Handler handler;
        final Runnable runnable;
        final int milliseconds;
        final int windowType;

        mediaPlayer = getRingtoneMediaPlayer(context);

        alert = new AlertDialog.Builder(context).create();

        handler  = ServerService.getMainThreadHandler();
        runnable = new Runnable() {
            public void run() {
                stopMediaPlayer(mediaPlayer);
                if (alert.isShowing())
                    alert.getButton(DialogInterface.BUTTON_NEGATIVE).callOnClick();
            }
        };

        milliseconds = (int) (SharedPrefs.getCallAlertTimeout(context) * 1000);
        handler.postDelayed(runnable, milliseconds);

        windowType = (Build.VERSION.SDK_INT < 26) ? WindowManager.LayoutParams.TYPE_SYSTEM_ALERT : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        alert.setTitle(context.getString(R.string.dialog_inbound_call_title));
        if ((contactName != null) && !contactName.isEmpty())
            alert.setMessage(contactName);
        alert.setButton(
            DialogInterface.BUTTON_POSITIVE,
            context.getString(R.string.dialog_inbound_call_positive),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    stopMediaPlayer(mediaPlayer);
                    dialog.dismiss();
                    handler.removeCallbacks(runnable);
                    OrgAppspotApprtcGlue.startInboundCallActivity(context);
                }
            }
        );
        alert.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            context.getString(R.string.dialog_inbound_call_negative),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    stopMediaPlayer(mediaPlayer);
                    dialog.cancel();
                    handler.removeCallbacks(runnable);
                    ServerService.doHangup(context);
                }
            }
        );
        alert.getWindow().setType(windowType);
        alert.setCancelable(false);
        alert.show();
    }

    public static MediaPlayer getRingtoneMediaPlayer(Context context) {
        MediaPlayer mediaPlayer = null;
        try {
            Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, ringtone);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e) {
            mediaPlayer = null;
        }
        return mediaPlayer;
    }

    public static void stopMediaPlayer(MediaPlayer mediaPlayer) {
        if ((mediaPlayer != null) && mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }
}
