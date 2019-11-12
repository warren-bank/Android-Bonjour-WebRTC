package com.github.warren_bank.bonjour_webrtc.security_model;

import com.github.warren_bank.bonjour_webrtc.R;
import com.github.warren_bank.bonjour_webrtc.ui.RuntimePermissionsActivity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import java.util.ArrayList;

public final class RuntimePermissions {
    private static final int REQUEST_CODE = 0;

    public static String[] getMissingPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT < 23)
            return new String[0];

        PackageInfo info;
        try {
            info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
        }
        catch (PackageManager.NameNotFoundException e) {
            return new String[0];
        }

        if (info.requestedPermissions == null) {
            return new String[0];
        }

        ArrayList<String> missingPermissions = new ArrayList<>();
        for (int i = 0; i < info.requestedPermissions.length; i++) {
            if ((info.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) {
                missingPermissions.add(info.requestedPermissions[i]);
            }
        }

        return missingPermissions.toArray(new String[missingPermissions.size()]);
    }

    public static boolean isEnabled(Activity activity) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        final String[] missingPermissions = getMissingPermissions(activity);

        if (missingPermissions.length == 0)
            return true;

        activity.requestPermissions(missingPermissions, REQUEST_CODE);
        return false;
    }

    public static void onRequestPermissionsResult (RuntimePermissionsActivity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQUEST_CODE)
            return;

        if (grantResults.length == 0)
            return;

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return;
        }

        activity.onPermissionsGranted();
    }
}
