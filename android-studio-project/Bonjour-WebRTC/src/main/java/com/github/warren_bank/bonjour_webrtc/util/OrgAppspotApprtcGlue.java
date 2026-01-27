package com.github.warren_bank.bonjour_webrtc.util;

import com.github.warren_bank.bonjour_webrtc.R;
import com.github.warren_bank.bonjour_webrtc.data_model.SharedPrefs;

import org.appspot.apprtc.CallActivity;
import org.appspot.apprtc.PeerConnectionClient;
import org.appspot.apprtc.PeerConnectionClient.DataChannelParameters;
import org.appspot.apprtc.PeerConnectionClient.PeerConnectionEvents;
import org.appspot.apprtc.PeerConnectionClient.PeerConnectionParameters;

import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Random;

public final class OrgAppspotApprtcGlue {

    // =============================================================================================
    // based on code from:
    //     https://webrtc.googlesource.com/src/+/49fa4ea0e7533cac190e32f3f7f2de3876385e82/examples/androidapp/src/org/appspot/apprtc/ConnectActivity.java
    // =============================================================================================

    public static void setDefaultPreferenceValues(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.org_appspot_apprtc_preferences, false);
    }

    private static String sharedPrefGetString(SharedPreferences sharedPreferences, Context context, int attributeId, String intentName, int defaultId, boolean useFromIntent, Intent intent) {
        String defaultValue = context.getString(defaultId);

        if (useFromIntent && (intent != null)) {
            String value = intent.getStringExtra(intentName);
            return (value != null)
              ? value
              : defaultValue
            ;
        }
        else {
            return SharedPrefs.getString(sharedPreferences, context, attributeId, defaultValue);
        }
    }

    private static boolean sharedPrefGetBoolean(SharedPreferences sharedPreferences, Context context, int attributeId, String intentName, int defaultId, boolean useFromIntent, Intent intent) {
        boolean defaultValue = Boolean.parseBoolean(context.getString(defaultId));

        return (useFromIntent && (intent != null))
          ? intent.getBooleanExtra(intentName, defaultValue)
          : SharedPrefs.getBoolean(sharedPreferences, context, attributeId, defaultValue)
        ;
    }

    private static int sharedPrefGetInteger(SharedPreferences sharedPreferences, Context context, int attributeId, String intentName, int defaultId, boolean useFromIntent, Intent intent) {
        String defaultStr = context.getString(defaultId);
        int    defaultInt = Integer.parseInt(defaultStr);

        if (useFromIntent && (intent != null)) {
            return intent.getIntExtra(intentName, defaultInt);
        }
        else {
            String valueStr = SharedPrefs.getString(sharedPreferences, context, attributeId, defaultStr);

            try {
                int valueInt = Integer.parseInt(valueStr);
                return valueInt;
            }
            catch(Exception e) {
                return defaultInt;
            }
        }
    }

    public static Intent getCallActivityIntent(Context context, String serverIpAddress) {
        return getCallActivityIntent(context, serverIpAddress, false, false, 0, false, null);
    }

    public static Intent getCallActivityIntent(Context context, String roomId, boolean loopback, boolean commandLineRun, int runTimeMs, boolean useValuesFromIntent, Intent intentIn) {
        if (useValuesFromIntent && (intentIn == null)) {
            useValuesFromIntent = false;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        // Video call enabled flag.
        boolean videoCallEnabled = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_videocall_key,
                CallActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent, intentIn);

        // Use screencapture option.
        boolean useScreencapture = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_screencapture_key,
                CallActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent, intentIn);

        // Use Camera2 option.
        boolean useCamera2 = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_camera2_key,
                CallActivity.EXTRA_CAMERA2, R.string.pref_camera2_default, useValuesFromIntent, intentIn);

        // Get default codecs.
        String videoCodec = sharedPrefGetString(sharedPreferences, context, R.string.pref_videocodec_key,
                CallActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent, intentIn);
        String audioCodec = sharedPrefGetString(sharedPreferences, context, R.string.pref_audiocodec_key,
                CallActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent, intentIn);

        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_hwcodec_key,
                CallActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent, intentIn);

        // Check Capture to texture.
        boolean captureToTexture = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_capturetotexture_key,
                CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default, useValuesFromIntent, intentIn);

        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_flexfec_key,
                CallActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent, intentIn);

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_noaudioprocessing_key,
                CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default, useValuesFromIntent, intentIn);

        boolean aecDump = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_aecdump_key,
                CallActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent, intentIn);

        boolean saveInputAudioToFile = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_enable_save_input_audio_to_file_key,
                CallActivity.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, R.string.pref_enable_save_input_audio_to_file_default, useValuesFromIntent, intentIn);

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_opensles_key,
                CallActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent, intentIn);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_disable_built_in_aec_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default, useValuesFromIntent, intentIn);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_disable_built_in_agc_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default, useValuesFromIntent, intentIn);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_disable_built_in_ns_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default, useValuesFromIntent, intentIn);

        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_disable_webrtc_agc_and_hpf_key,
                CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent, intentIn);

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = intentIn.getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = intentIn.getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    SharedPrefs.getString(sharedPreferences, context, R.string.pref_resolution_key, context.getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                }
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = intentIn.getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = SharedPrefs.getString(sharedPreferences, context, R.string.pref_fps_key, context.getString(R.string.pref_fps_default));
            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                }
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_capturequalityslider_key,
                CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, R.string.pref_capturequalityslider_default, useValuesFromIntent, intentIn);

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = intentIn.getIntExtra(CallActivity.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = context.getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = SharedPrefs.getString(sharedPreferences, context, R.string.pref_maxvideobitrate_key, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = SharedPrefs.getString(sharedPreferences, context, R.string.pref_maxvideobitratevalue_key, context.getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = intentIn.getIntExtra(CallActivity.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = context.getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = SharedPrefs.getString(sharedPreferences, context, R.string.pref_startaudiobitrate_key, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = SharedPrefs.getString(sharedPreferences, context, R.string.pref_startaudiobitratevalue_key, context.getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option.
        boolean displayHud = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_displayhud_key,
                CallActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent, intentIn);

        boolean tracing = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_tracing_key,
                CallActivity.EXTRA_TRACING, R.string.pref_tracing_default, useValuesFromIntent, intentIn);

        // Check Enable RtcEventLog.
        boolean rtcEventLogEnabled = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_enable_rtceventlog_key,
                CallActivity.EXTRA_ENABLE_RTCEVENTLOG, R.string.pref_enable_rtceventlog_default, useValuesFromIntent, intentIn);

        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_enable_datachannel_key,
                CallActivity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default, useValuesFromIntent, intentIn);
        boolean ordered = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_ordered_key,
                CallActivity.EXTRA_ORDERED, R.string.pref_ordered_default, useValuesFromIntent, intentIn);
        boolean negotiated = sharedPrefGetBoolean(sharedPreferences, context, R.string.pref_negotiated_key,
                CallActivity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent, intentIn);
        int maxRetrMs = sharedPrefGetInteger(sharedPreferences, context, R.string.pref_max_retransmit_time_ms_key,
                CallActivity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default, useValuesFromIntent, intentIn);
        int maxRetr = sharedPrefGetInteger(sharedPreferences, context, R.string.pref_max_retransmits_key,
                CallActivity.EXTRA_MAX_RETRANSMITS, R.string.pref_max_retransmits_default, useValuesFromIntent, intentIn);
        int id = sharedPrefGetInteger(sharedPreferences, context, R.string.pref_data_id_key,
                CallActivity.EXTRA_ID, R.string.pref_data_id_default, useValuesFromIntent, intentIn);
        String protocol = sharedPrefGetString(sharedPreferences, context, R.string.pref_data_protocol_key,
                CallActivity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent, intentIn);

        Intent intentOut = new Intent(context, CallActivity.class);
        intentOut.putExtra(CallActivity.EXTRA_ROOMID, roomId);
        intentOut.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
        intentOut.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
        intentOut.putExtra(CallActivity.EXTRA_SCREENCAPTURE, useScreencapture);
        intentOut.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2);
        intentOut.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
        intentOut.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
        intentOut.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
        intentOut.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
        intentOut.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
        intentOut.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
        intentOut.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
        intentOut.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
        intentOut.putExtra(CallActivity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
        intentOut.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
        intentOut.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
        intentOut.putExtra(CallActivity.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, saveInputAudioToFile);
        intentOut.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
        intentOut.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
        intentOut.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
        intentOut.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
        intentOut.putExtra(CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
        intentOut.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
        intentOut.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
        intentOut.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
        intentOut.putExtra(CallActivity.EXTRA_TRACING, tracing);
        intentOut.putExtra(CallActivity.EXTRA_ENABLE_RTCEVENTLOG, rtcEventLogEnabled);
        intentOut.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
        intentOut.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);
        intentOut.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

        if (dataChannelEnabled) {
            intentOut.putExtra(CallActivity.EXTRA_ORDERED, ordered);
            intentOut.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
            intentOut.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, maxRetr);
            intentOut.putExtra(CallActivity.EXTRA_PROTOCOL, protocol);
            intentOut.putExtra(CallActivity.EXTRA_NEGOTIATED, negotiated);
            intentOut.putExtra(CallActivity.EXTRA_ID, id);
        }

        if (useValuesFromIntent) {
            if (intentIn.hasExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                String videoFileAsCamera =
                        intentIn.getStringExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA);
                intentOut.putExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
            }

            if (intentIn.hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                String saveRemoteVideoToFile =
                        intentIn.getStringExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                intentOut.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
            }

            if (intentIn.hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                int videoOutWidth =
                        intentIn.getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                intentOut.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
            }

            if (intentIn.hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                int videoOutHeight =
                        intentIn.getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                intentOut.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
            }
        }

        return intentOut;
    }

    // =============================================================================================
    // based on code from:
    //     https://webrtc.googlesource.com/src/+/49fa4ea0e7533cac190e32f3f7f2de3876385e82/examples/androidapp/src/org/appspot/apprtc/CallActivity.java
    // =============================================================================================

    public static PeerConnectionClient getPeerConnectionClient(Context context, EglBase eglBase, PeerConnectionEvents events, Intent intent) {
        if (eglBase == null)
            eglBase = EglBase.create();

        if (intent == null)
            intent = getInboundCallActivityIntent(context);

        boolean loopback = intent.getBooleanExtra(CallActivity.EXTRA_LOOPBACK, false);
        int videoWidth   = intent.getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0);
        int videoHeight  = intent.getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0);

        boolean screencaptureEnabled = intent.getBooleanExtra(CallActivity.EXTRA_SCREENCAPTURE, false);
        if (screencaptureEnabled && (videoWidth == 0) && (videoHeight == 0)) {
          DisplayMetrics displayMetrics = getDisplayMetrics(context);
          videoWidth = displayMetrics.widthPixels;
          videoHeight = displayMetrics.heightPixels;
        }

        DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, false)) {
            dataChannelParameters = new DataChannelParameters(
                intent.getBooleanExtra(CallActivity.EXTRA_ORDERED, true),
                intent.getIntExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, -1),
                intent.getIntExtra(CallActivity.EXTRA_MAX_RETRANSMITS, -1),
                intent.getStringExtra(CallActivity.EXTRA_PROTOCOL),
                intent.getBooleanExtra(CallActivity.EXTRA_NEGOTIATED, false),
                intent.getIntExtra(CallActivity.EXTRA_ID, -1)
            );
        }

        PeerConnectionParameters peerConnectionParameters =
            new PeerConnectionParameters(
                intent.getBooleanExtra(CallActivity.EXTRA_VIDEO_CALL, true),
                loopback,
                intent.getBooleanExtra(CallActivity.EXTRA_TRACING, false),
                videoWidth,
                videoHeight,
                intent.getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0),
                intent.getIntExtra(CallActivity.EXTRA_VIDEO_BITRATE, 0),
                intent.getStringExtra(CallActivity.EXTRA_VIDEOCODEC),
                intent.getBooleanExtra(CallActivity.EXTRA_HWCODEC_ENABLED, true),
                intent.getBooleanExtra(CallActivity.EXTRA_FLEXFEC_ENABLED, false),
                intent.getIntExtra(CallActivity.EXTRA_AUDIO_BITRATE, 0),
                intent.getStringExtra(CallActivity.EXTRA_AUDIOCODEC),
                intent.getBooleanExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                intent.getBooleanExtra(CallActivity.EXTRA_AECDUMP_ENABLED, false),
                intent.getBooleanExtra(CallActivity.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, false),
                intent.getBooleanExtra(CallActivity.EXTRA_OPENSLES_ENABLED, false),
                intent.getBooleanExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, false),
                intent.getBooleanExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, false),
                intent.getBooleanExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, false),
                intent.getBooleanExtra(CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false),
                intent.getBooleanExtra(CallActivity.EXTRA_ENABLE_RTCEVENTLOG, false), dataChannelParameters);

        PeerConnectionClient peerConnectionClient = new PeerConnectionClient(context, eglBase, peerConnectionParameters, events);
        PeerConnectionFactory.Options options     = new PeerConnectionFactory.Options();
        if (loopback) {
            options.networkIgnoreMask = 0;
        }
        peerConnectionClient.createPeerConnectionFactory(options);

        return peerConnectionClient;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    // =============================================================================================
    // misc
    // =============================================================================================

    private static Intent getInboundCallActivityIntent(Context context) {
        return getCallActivityIntent(context, Util.getSocketServerIpAddress(context), false, false, 0, false, null);
    }

    public static void startInboundCallActivity(Context context) {
        Intent intent = getInboundCallActivityIntent(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
