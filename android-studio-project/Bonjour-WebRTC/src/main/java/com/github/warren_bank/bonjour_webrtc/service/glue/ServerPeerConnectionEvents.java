package com.github.warren_bank.bonjour_webrtc.service.glue;

import org.appspot.apprtc.DirectRTCClient;
import org.appspot.apprtc.PeerConnectionClient;
import org.webrtc.IceCandidate;
import org.webrtc.RTCStatsReport;
import org.webrtc.SessionDescription;

public class ServerPeerConnectionEvents implements PeerConnectionClient.PeerConnectionEvents {

    private DirectRTCClient appRtcClient;
    private PeerConnectionClient.PeerConnectionEvents callActivity;

    public ServerPeerConnectionEvents(DirectRTCClient client) {
        appRtcClient = client;
        callActivity = null;
    }

    public void setCallActivity(PeerConnectionClient.PeerConnectionEvents events) {
        callActivity = events;
    }

    public void onHangup() {
        if (appRtcClient != null)
            appRtcClient.restartServer();

        callActivity = null;
    }

    public void onStop() {
        if (appRtcClient != null)
            appRtcClient.disconnect();

        appRtcClient = null;
        callActivity = null;
    }

    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        if (callActivity != null) {
            callActivity.onLocalDescription(sdp);
        }
        else {
            // when:
            //   - not already in a video call
            //   - remote client has connected to local server
            // what:
            //   - server sends sdp as message to client through socket in json format
            appRtcClient.sendOfferSdp(sdp);
        }
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        if (callActivity != null)
            callActivity.onIceCandidate(candidate);
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        if (callActivity != null)
            callActivity.onIceCandidatesRemoved(candidates);
    }

    @Override
    public void onIceConnected() {
        if (callActivity != null)
            callActivity.onIceConnected();
    }

    @Override
    public void onIceDisconnected() {
        if (callActivity != null)
            callActivity.onIceDisconnected();
    }

    @Override
    public void onConnected() {
        if (callActivity != null)
            callActivity.onConnected();
    }

    @Override
    public void onDisconnected() {
        if (callActivity != null)
            callActivity.onDisconnected();
    }

    @Override
    public void onPeerConnectionClosed() {
        if (callActivity != null)
            callActivity.onPeerConnectionClosed();
    }

    @Override
    public void onPeerConnectionStatsReady(final RTCStatsReport report) {
        if (callActivity != null)
            callActivity.onPeerConnectionStatsReady(report);
    }

    @Override
    public void onPeerConnectionError(final String description) {
        if (callActivity != null)
            callActivity.onPeerConnectionError(description);
    }
}
