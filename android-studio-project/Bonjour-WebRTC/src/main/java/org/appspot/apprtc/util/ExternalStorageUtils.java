package org.appspot.apprtc.util;

import android.os.Environment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ExternalStorageUtils {

  // Checks if external storage is available for read and write.
  public static boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }

  public static File getOutputBaseDirectory() {
    File dir = new File(
      Environment.getExternalStorageDirectory(),
      "Bonjour-WebRTC"
    );

    return initDirectory(dir);
  }

  public static File initDirectory(File dir) {
    if (dir.exists() && !dir.isDirectory())
      dir.delete();

    if (!dir.exists())
      dir.mkdir();

    return dir;
  }

  public static String getOutputFilename() {
    return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
  }

}
