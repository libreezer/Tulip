package breeze.app.tulip.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Random;

import breeze.app.tulip.APP;
import brz.breeze.tool_utils.Blog;
import brz.breeze.web_utils.BWebUtils;

public class AppUtils {

    private static SharedPreferences sharedPreferences;

    static {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(APP.getContext());
    }

    public static void logLong(long obj){
        Blog.ii("JNI日志输出="+obj);
    }

    public static void logStr(String  obj){
        Blog.ii("JNI日志输出="+obj);
    }

    public static String getToken() {
        return sharedPreferences.getString("Token", "");
    }

    public static void setToken(String t) {
        sharedPreferences.edit().putString("Token", t).apply();
    }

    public static void setLong(String tag,long data){
        sharedPreferences.edit().putLong(tag,data/3872).apply();
    }

    public static String createToken(){
        int anInt = new Random().nextInt(202291);
        long l = System.currentTimeMillis();
        String s = String.valueOf(l + anInt);
        return Base64.encodeToString(s.getBytes(), Base64.NO_WRAP);
    }

    public static String getPM(){
        try {
            return BWebUtils.getWebContent("http://www.libreeze.top/tulip/pm.php");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean cmpstr(String str1,String str2){
        Blog.ii("比较文本="+str1+"/"+str2+"/"+str1.equals(str2));
        return str1.equals(str2);
    }

    public static long getLong(String tag){
        return sharedPreferences.getLong(tag,0)*3872;
    }

    @SuppressLint("PackageManagerGetSignatures")
    public static int signature(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signature = packageInfo.signatures[0];
            return signature.hashCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getTime(long time){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(time);
    }

    public static void exit() {
        android.os.Process.killProcess(Process.myPid());
    }

    public static String mkpswd(String str, int key) {
        char c;
        StringBuilder str2 = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            c += key;
            str2.append(c);
        }
        return str2.toString();
    }

    public static String depswd(String str, int key) {
        char c;
        StringBuilder str2 = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            c -= key;
            str2.append(c);
        }
        return str2.toString();
    }

    public static boolean isContainStr(String str1, String str2) {
        return str1.contains(str2);
    }
}
