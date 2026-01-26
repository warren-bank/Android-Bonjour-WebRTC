package com.github.warren_bank.bonjour_webrtc.security_model;

import com.github.warren_bank.bonjour_webrtc.R;
import com.github.warren_bank.bonjour_webrtc.data_model.SharedPrefs;
import com.github.warren_bank.bonjour_webrtc.ui.RuntimePermissionsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import java.util.ArrayList;

public final class RuntimePermissions {
    private static final int REQUEST_CODE_PERMISSIONS   = 0;
    private static final int REQUEST_CODE_DRAWOVERLAYS  = 1;
    private static final int REQUEST_CODE_MANAGESTORAGE = 2;

    private static final ArrayList<String> MANDATORY_PERMISSIONS;

    static {
      MANDATORY_PERMISSIONS = new ArrayList<String>();

      MANDATORY_PERMISSIONS.add("android.permission.INTERNET");
      MANDATORY_PERMISSIONS.add("android.permission.MODIFY_AUDIO_SETTINGS");
      MANDATORY_PERMISSIONS.add("android.permission.RECORD_AUDIO");
    }

    // =============================================================================================

    public static boolean hasMandatoryPermissions(Context context) {
        for (String permission : MANDATORY_PERMISSIONS) {
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    // =============================================================================================

    public static boolean isEnabled(Activity activity) {
        return isEnabled(activity, /*skipMissingPermissions*/ false);
    }

    public static boolean isEnabled(Activity activity, boolean skipMissingPermissions) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        final String[] missingPermissions = skipMissingPermissions
            ? null
            : getMissingPermissions(activity);

        if ((missingPermissions != null) && (missingPermissions.length > 0)) {
            activity.requestPermissions(missingPermissions, REQUEST_CODE_PERMISSIONS);
            return false;
        }
        else if (SharedPrefs.getCallAlertEnabled(activity) && !canDrawOverlays(activity)) {
            requestPermissionDrawOverlays(activity);
            return false;
        }
        else if (!canManageExternalStorage()) {
            requestPermissionManageExternalStorage(activity);
            return false;
        }
        return true;
    }

    // =============================================================================================

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

    // =============================================================================================

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        return Settings.canDrawOverlays(context);
    }

    public static void requestPermissionDrawOverlays(Activity activity) {
        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(permissionIntent, REQUEST_CODE_DRAWOVERLAYS);
    }

    // =============================================================================================

    public static boolean canManageExternalStorage() {
        if (Build.VERSION.SDK_INT < 30)
            return true;

        return Environment.isExternalStorageManager();
    }

    public static void requestPermissionManageExternalStorage(Activity activity) {
        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(permissionIntent, REQUEST_CODE_MANAGESTORAGE);
    }

    // =============================================================================================

    public static void onRequestPermissionsResult(RuntimePermissionsActivity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQUEST_CODE_PERMISSIONS)
            return;

        boolean skipMissingPermissions;

        if (grantResults.length == 0) {
            if (permissions.length == 0) {
                // no "dangerous" permissions are needed
                skipMissingPermissions = true;
            }
            else {
                // request was cancelled. show the prompts again.
                skipMissingPermissions = false;
            }
        }
        else {
            ArrayList<String> deniedPermissions = new ArrayList<>();

            for (int i=0; i < grantResults.length; i++) {
                if (
                    (grantResults[i] != PackageManager.PERMISSION_GRANTED) &&
                    MANDATORY_PERMISSIONS.contains(permissions[i])
                ) {
                    // a mandatory permission is not granted
                    deniedPermissions.add(permissions[i]);
                }
            }

            if (deniedPermissions.isEmpty()) {
                skipMissingPermissions = true;
            }
            else {
                activity.onPermissionsDenied(
                    deniedPermissions.toArray(new String[deniedPermissions.size()])
                );
                return;
            }
        }

        if (isEnabled(activity, skipMissingPermissions))
            activity.onPermissionsGranted();
    }

    // =============================================================================================

    public static void onActivityResult(RuntimePermissionsActivity activity, int requestCode, int resultCode, Intent data) {
        if ((requestCode != REQUEST_CODE_DRAWOVERLAYS) && (requestCode != REQUEST_CODE_MANAGESTORAGE))
            return;

        if (isEnabled(activity, /*skipMissingPermissions*/ true)) {
            activity.onPermissionsGranted();
            return;
        }

        switch(requestCode) {
            case REQUEST_CODE_DRAWOVERLAYS :
                activity.onPermissionsDenied(new String[]{"android.permission.SYSTEM_ALERT_WINDOW"});
                break;
            case REQUEST_CODE_MANAGESTORAGE :
                activity.onPermissionsDenied(new String[]{"android.permission.MANAGE_EXTERNAL_STORAGE"});
                break;
        }
    }
}
