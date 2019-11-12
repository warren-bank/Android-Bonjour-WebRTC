package com.github.warren_bank.bonjour_webrtc.service;

import com.github.warren_bank.bonjour_webrtc.R;
import com.github.warren_bank.bonjour_webrtc.data_model.SharedPrefs;
import com.github.warren_bank.bonjour_webrtc.service.glue.ServerPeerConnectionEvents;
import com.github.warren_bank.bonjour_webrtc.service.glue.ServerSignalingEvents;
import com.github.warren_bank.bonjour_webrtc.ui.MainActivity;
import com.github.warren_bank.bonjour_webrtc.util.OrgAppspotApprtcGlue;
import com.github.warren_bank.bonjour_webrtc.util.Util;

import org.appspot.apprtc.AppRTCClient.RoomConnectionParameters;
import org.appspot.apprtc.DirectRTCClient;
import org.appspot.apprtc.PeerConnectionClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class ServerService extends Service {
    private final static int NOTIFICATION_ID = 1;
    private final static String ACTION_START = "START";
    private final static String ACTION_STOP  = "STOP";

    private static boolean                    running                                  = false;
    private static ServerSignalingEvents      appRTCClientSignalingEvents              = null;
    private static DirectRTCClient            appRtcClient                             = null;
    private static ServerPeerConnectionEvents peerConnectionClientPeerConnectionEvents = null;
    private static PeerConnectionClient       peerConnectionClient                     = null;

    private JmDNS bonjour;

    @Override
    public void onCreate() {
        appRtcClient = null;
        bonjour      = null;

        showNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        processIntent(intent);
    }

    @Override
    public void onDestroy() {
        hideNotification();
    }

    // -------------------------------------------------------------------------
    // foregrounding..

    private void showNotification() {
        Notification notification = getNotification();

        if (Build.VERSION.SDK_INT >= 5) {
            startForeground(NOTIFICATION_ID, notification);
        }
        else {
            NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NM.notify(NOTIFICATION_ID, notification);
        }
    }

    private void hideNotification() {
        if (Build.VERSION.SDK_INT >= 5) {
            stopForeground(true);
        }
        else {
            NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NM.cancel(NOTIFICATION_ID);
        }
    }

    private Notification getNotification() {
        Notification notification  = new Notification();
        notification.when          = System.currentTimeMillis();
        notification.flags         = 0;
        notification.flags        |= Notification.FLAG_ONGOING_EVENT;
        notification.flags        |= Notification.FLAG_NO_CLEAR;
        notification.icon          = R.drawable.launcher;
        notification.tickerText    = getString(R.string.notification_service_ticker);
        notification.contentIntent = getPendingIntent_MainActivity();
     // notification.deleteIntent  = getPendingIntent_StopService();

        if (Build.VERSION.SDK_INT >= 16) {
            notification.priority  = Notification.PRIORITY_HIGH;
        }
        else {
            notification.flags    |= Notification.FLAG_HIGH_PRIORITY;
        }

        RemoteViews contentView    = new RemoteViews(getPackageName(), R.layout.service_notification);
        contentView.setImageViewResource(R.id.notification_icon, R.drawable.launcher);
        contentView.setTextViewText(R.id.notification_text_line1, getString(R.string.notification_service_content_line1));
        contentView.setTextViewText(R.id.notification_text_line2, getString(R.string.notification_service_content_line2));
        notification.contentView   = contentView;

        return notification;
    }

    private PendingIntent getPendingIntent_MainActivity() {
        Intent intent = new Intent(ServerService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(ServerService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPendingIntent_StopService() {
        Intent intent = doStop(ServerService.this, false);

        return PendingIntent.getService(ServerService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // -------------------------------------------------------------------------
    // process inbound intents

    private void processIntent(Intent intent) {
        if (intent == null)
            return;

        String action = intent.getAction();
        if (action == null)
            return;

        switch (action) {
            case ACTION_START: {
                running = true;
                startTCPSocketServer();
                bonjourRegister();
                break;
            }
            case ACTION_STOP: {
                running = false;
                stopTCPSocketServer();
                bonjourUnregister();
                stopSelf();
                break;
            }
        }
    }

    // -------------------------------------------------------------------------
    // manage TCPSocketServer

    private void startTCPSocketServer() {
        appRTCClientSignalingEvents              = new ServerSignalingEvents(ServerService.this);
        appRtcClient                             = new DirectRTCClient(appRTCClientSignalingEvents);

        peerConnectionClientPeerConnectionEvents = new ServerPeerConnectionEvents(appRtcClient);
        peerConnectionClient                     = OrgAppspotApprtcGlue.getPeerConnectionClient(ServerService.this, null, peerConnectionClientPeerConnectionEvents, null);

        RoomConnectionParameters connectionParameters = new RoomConnectionParameters(null, "0.0.0.0:8888", false, null);
        appRtcClient.connectToRoom(connectionParameters);
    }

    private void stopTCPSocketServer() {
        appRtcClient.disconnectFromRoom();
        peerConnectionClient.close();

        appRTCClientSignalingEvents.disconnect();
        peerConnectionClientPeerConnectionEvents.disconnect();

        appRTCClientSignalingEvents              = null;
        appRtcClient                             = null;
        peerConnectionClientPeerConnectionEvents = null;
        peerConnectionClient                     = null;
    }

    // -------------------------------------------------------------------------
    // manage Bonjour registration

    private void bonjourRegister() {
        try {
            bonjour = JmDNS.create(Util.getWlanIpAddress_InetAddress(ServerService.this));

            String serverAlias = SharedPrefs.getServerAlias(ServerService.this);
            ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", serverAlias, 8887, "com.github.warren_bank.bonjour_webrtc.service");
            bonjour.registerService(serviceInfo);
        }
        catch(Exception e) {
            bonjour = null;
        }
    }

    private void bonjourUnregister() {
        try {
            bonjour.unregisterAllServices();
        }
        catch(Exception e) {
        }
        finally {
            bonjour = null;
        }
    }

    // -------------------------------------------------------------------------
    // static API for Activities that need to send intents to Service

    public static Intent doToggle(Context context) {
        return doToggle(context, true);
    }

    public static Intent doToggle(Context context, boolean broadcast) {
        return (running)
          ? doStop(context, broadcast)
          : doStart(context, broadcast)
        ;
    }

    public static Intent doStart(Context context) {
        return doStart(context, true);
    }

    public static Intent doStart(Context context, boolean broadcast) {
        Intent intent = new Intent(context, ServerService.class);
        return doAction(context, intent, ACTION_START, (broadcast && !running));
    }

    public static Intent doStop(Context context) {
        return doStop(context, true);
    }

    public static Intent doStop(Context context, boolean broadcast) {
        Intent intent = new Intent(context, ServerService.class);
        return doAction(context, intent, ACTION_STOP, (broadcast && running));
    }

    private static Intent doAction(Context context, Intent intent, String action, boolean broadcast) {
        intent.setAction(action);

        if (broadcast)
            context.startService(intent);

        return intent;
    }

    public static boolean isStarted() {
        return running;
    }

    public static ServerSignalingEvents getServerSignalingEvents() {
        return appRTCClientSignalingEvents;
    }

    public static DirectRTCClient getDirectRTCClient() {
        return appRtcClient;
    }

    public static ServerPeerConnectionEvents getServerPeerConnectionEvents() {
        return peerConnectionClientPeerConnectionEvents;
    }

    public static PeerConnectionClient getPeerConnectionClient() {
        return peerConnectionClient;
    }
}
