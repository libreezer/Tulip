package breeze.app.tulip.xposed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import breeze.app.tulip.database.AppDataBaseTool;
import breeze.app.tulip.model.AppPathBundle;
import breeze.app.tulip.utils.AppFileLog;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HOOK implements IXposedHookLoadPackage {

    private final List<AppPathBundle> appPathBundles = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //HOOK自身
        //读取Context
        @SuppressLint("PrivateApi") Class<?> aClass1 = loadPackageParam.classLoader.loadClass("android.app.Application");
        XposedHelpers.findAndHookMethod(aClass1, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                mContext = (Context) param.args[0];
                init();
                main(loadPackageParam);
                super.beforeHookedMethod(param);
            }
        });
    }


    /**
     * @param loadPackageParam 参数
     * @throws ClassNotFoundException 错误
     */
    private void main(XC_LoadPackage.LoadPackageParam loadPackageParam) throws ClassNotFoundException {
        if (loadPackageParam.packageName.equals("breeze.app.tulip")){
            Class<?> aClass = loadPackageParam.classLoader.loadClass("breeze.app.tulip.MainActivity");
            XposedHelpers.findAndHookMethod(aClass, "isHooked", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(true);
                    super.afterHookedMethod(param);
                }
            });
        }

        //寻找目标
        for (AppPathBundle appPathBundle : appPathBundles) {
            if (loadPackageParam.packageName.equals(appPathBundle.getPackageName())) {
                //找到目标开始hook
                //recordLOG(loadPackageParam);
                //HOOk方法
                List<AppPathBundle.MethodPath> methodPaths = appPathBundle.getMethodPaths();
                for (AppPathBundle.MethodPath methodPath : methodPaths) {
                    //hookLog(methodPath);
                    if (methodPath.getParentClass().equals("Environment")) {
                        hookEnvironment(loadPackageParam, methodPath);
                    } else if (methodPath.getParentClass().equals("ContextWrapper")) {
                        hookContextWrapper(loadPackageParam, methodPath);
                    }
                }
            }
        }
    }

    private void recordLOG(XC_LoadPackage.LoadPackageParam loadPackageParam) throws ClassNotFoundException {
        Class<?> aClass = loadPackageParam.classLoader.loadClass("java.io.File");
        XposedHelpers.findAndHookMethod(aClass, "createNewFile", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Field path = aClass.getDeclaredField("path");
                path.setAccessible(true);
                AppFileLog.log(loadPackageParam.packageName, "创建文件=" + path);
                super.afterHookedMethod(param);
            }
        });

        XposedHelpers.findAndHookMethod(aClass, "mkdir", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Field path = aClass.getDeclaredField("path");
                path.setAccessible(true);
                AppFileLog.log(loadPackageParam.packageName, "创建文件夹=" + path);
                super.afterHookedMethod(param);
            }
        });

        XposedHelpers.findAndHookMethod(aClass, "delete", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Field path = aClass.getDeclaredField("path");
                path.setAccessible(true);
                AppFileLog.log(loadPackageParam.packageName, "删除文件=" + path);
                super.afterHookedMethod(param);
            }
        });
    }

    private void hookContextWrapper(XC_LoadPackage.LoadPackageParam loadPackageParam, AppPathBundle.MethodPath methodPath) throws ClassNotFoundException {
        Class<?> aClass = loadPackageParam.classLoader.loadClass("android.content.ContextWrapper");
        if (methodPath.getMethodName().equals("getObbDir") && !methodPath.getValue().isEmpty()) {
            hookSingleFile(aClass, "getObbDir", methodPath);
        }
        if (methodPath.getMethodName().equals("getExternalCacheDir") && !methodPath.getValue().isEmpty()) {
            hookSingleFile(aClass, "getExternalCacheDir", methodPath);
        }
        if (methodPath.getMethodName().equals("getDataDir") && !methodPath.getValue().isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            hookSingleFile(aClass, "getDataDir", methodPath);
        }
        if (methodPath.getMethodName().equals("getCacheDir") && !methodPath.getValue().isEmpty()) {
            hookSingleFile(aClass, "getCacheDir", methodPath);
        }
        if (methodPath.getMethodName().equals("getFilesDir") && !methodPath.getValue().isEmpty()) {
            hookSingleFile(aClass, "getFilesDir", methodPath);
        }
        if (methodPath.getMethodName().equals("getCodeCacheDir") && !methodPath.getValue().isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hookSingleFile(aClass, "getCodeCacheDir", methodPath);
        }
    }

    private void hookEnvironment(XC_LoadPackage.LoadPackageParam loadPackageParam, AppPathBundle.MethodPath methodPath) throws ClassNotFoundException {
        //HOOK公共目录
        @SuppressLint("PrivateApi")
        Class<?> aClass = loadPackageParam.classLoader.loadClass("android.os.Environment$UserEnvironment");
        if (methodPath.getMethodName().equals("getExternalDirs") && !methodPath.getValue().isEmpty()) {
            XposedHelpers.findAndHookMethod(aClass, "getExternalDirs", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    File[] files = (File[]) param.getResult();
                    files[0] = new File(methodPath.getValue());
                    param.setResult(files);
                    super.afterHookedMethod(param);
                }
            });
        }
        //HOOK下载缓存目录
        Class<?> aClass1 = loadPackageParam.classLoader.loadClass("android.os.Environment");
        if (methodPath.getMethodName().equals("getDownloadCacheDirectory") && !methodPath.getValue().isEmpty()) {
            hookSingleFile(aClass1, "getDownloadCacheDirectory", methodPath);
        }
        //系统目录
        if (methodPath.getMethodName().equals("getRootDirectory") && !methodPath.getValue().isEmpty()) {
            hookSingleFile(aClass1, "getRootDirectory", methodPath);
        }
        //数据目录
        if (methodPath.getMethodName().equals("getDataDirectory") && !methodPath.getValue().isEmpty()) {
            hookSingleFile(aClass1, "getDataDirectory", methodPath);
        }
    }

    private void init() {
        appPathBundles.clear();
        appPathBundles.addAll(AppDataBaseTool.getAllPath());
    }

    private void hookLog(AppPathBundle.MethodPath methodPath) {
        XposedBridge.log("方法参数=[方法父类:" + methodPath.getParentClass() +
                ";方法名:" + methodPath.getMethodName() +
                ";值:" + methodPath.getValue() + "]");
    }

    private void hookSingleFile(Class<?> aClass, String methodName, AppPathBundle.MethodPath methodPath) {
        XposedHelpers.findAndHookMethod(aClass, methodName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                File files = new File(methodPath.getValue());
                param.setResult(files);
                super.afterHookedMethod(param);
            }
        });
    }
}
