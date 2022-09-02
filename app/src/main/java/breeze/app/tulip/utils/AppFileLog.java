package breeze.app.tulip.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import brz.breeze.file_utils.BFileUtils;

public class AppFileLog {

    public static void log(String pkgName, String content) {
        try {
            boolean newFileCreate = true;
            File fileStreamPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/brz_tulip_log/");
            if (!fileStreamPath.exists()) {
                newFileCreate = fileStreamPath.mkdir();
            }
            if (newFileCreate) {
                String mkpswd = AppUtils.mkpswd(AppUtils.getTime(System.currentTimeMillis()) + ":" + content, 3872);
                BFileUtils.addContentToFile(fileStreamPath.getAbsolutePath()+"/"+pkgName, mkpswd+"\n");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static File getLog(String pkgName){
        File fileStreamPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/brz_tulip_log/");
        return new File(fileStreamPath.getAbsolutePath()+"/"+pkgName);
    }
}
