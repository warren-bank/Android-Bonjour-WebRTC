package com.github.warren_bank.bonjour_webrtc.service.glue;

import com.github.warren_bank.bonjour_webrtc.util.OrgAppspotApprtcGlue;

import org.appspot.apprtc.AppRTCClient;
import org.appspot.apprtc.AppRTCClient.SignalingParameters;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import android.content.Context;

public class ServerSignalingEvents implements AppRTCClient.SignalingEvents {

    private Context context;
    private AppRTCClient.SignalingEvents callActivity;
    private SignalingParameters signalingParams;

    public ServerSignalingEvents(Context context) {
        this.context    = context;
        callActivity    = null;
        signalingParams = null;
    }

    public void setCallActivity(AppRTCClient.SignalingEvents events) {
        callActivity = events;

        if (signalingParams != null) {
            callActivity.onConnectedToRoom(signalingParams);
            signalingParams = null;
        }
    }

    public void disconnect() {
        context         = null;
        callActivity    = null;
        signalingParams = null;
    }

    public void disconnectFromRoom() {
        callActivity    = null;
        signalingParams = null;
    }

    @Override
    public void onConnectedToRoom(final SignalingParameters params) {
        if (callActivity != null) {
            callActivity.onConnectedToRoom(params);
        }
        else if (params.initiator) {
            // when:
            //   - not already in a video call
            //   - remote client had previously connected to local server
            //   - server had previously sent an sdp offer to client
            //   - client has accepted
            // what:
            //   - start CallActivity
            //   - add extras to intent that will signal that it is an inbound call
            signalingParams = params;
            OrgAppspotApprtcGlue.startInboundCallActivity(context);
        }
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        if (callActivity != null)
            callActivity.onRemoteDescription(sdp);
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        if (callActivity != null)
            callActivity.onRemoteIceCandidate(candidate);
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        if (callActivity != null)
            callActivity.onRemoteIceCandidatesRemoved(candidates);
    }

    @Override
    public void onChannelClose() {
        if (callActivity != null)
            callActivity.onChannelClose();
    }

    @Override
    public void onChannelError(final String description) {
        if (callActivity != null)
            callActivity.onChannelError(description);
    }
}
