//
// Created by 21998 on 2022/8/28.
//
#include <jni.h>
#include <cstdio>
#include <string>

static bool isPro = false;

bool isAppPro() {
    return isPro;
}

void log(JNIEnv *e,jlong data){
    jclass aClass = e->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pId = e->GetStaticMethodID(aClass, "logLong", "(J)V");
    e->CallStaticVoidMethod(aClass,pId,data);
}

void log(JNIEnv *e,jstring data){
    jclass aClass = e->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pId = e->GetStaticMethodID(aClass, "logStr", "(Ljava/lang/String;)V");
    e->CallStaticVoidMethod(aClass,pId,data);
}

jstring getToken(JNIEnv *env){
    jclass aClass = env->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pId = env->GetStaticMethodID(aClass, "getToken", "()Ljava/lang/String;");
    jobject i = env->CallStaticObjectMethod(aClass, pId);
    return (jstring) i;
}

void setToken(JNIEnv *env,jstring token){
    jclass aClass = env->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pId = env->GetStaticMethodID(aClass, "setToken", "(Ljava/lang/String;)V");
    env->CallStaticVoidMethod(aClass, pId,token);
}

void setTime(JNIEnv *env,jlong data){
    jclass aClass = env->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pId = env->GetStaticMethodID(aClass, "setLong", "(Ljava/lang/String;J)V");
    env->CallStaticVoidMethod(aClass, pId,getToken(env),data);
}

jlong getTime(JNIEnv *env){
    jstring pJstring = getToken(env);
    jclass aClass = env->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pId = env->GetStaticMethodID(aClass, "getLong", "(Ljava/lang/String;)J");
    jlong i = env->CallStaticLongMethod(aClass, pId,pJstring);
    return i;
}

jlong getSystemTime(JNIEnv *env){
    jclass pJclass = env->FindClass("java/lang/System");
    jmethodID pId = env->GetStaticMethodID(pJclass, "currentTimeMillis", "()J");
    jlong i = env->CallStaticLongMethod(pJclass, pId);
    return i;
}

void checkPro(JNIEnv *env){
    jstring pJobject = getToken(env);
    jstring utf = env->NewStringUTF("");
    jclass pJclass = env->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pId = env->GetStaticMethodID(pJclass, "cmpstr",
                                           "(Ljava/lang/String;Ljava/lang/String;)Z");
    jboolean isTokenNull = env->CallStaticBooleanMethod(pJclass, pId, pJobject,utf);
    if (isTokenNull == 0){
        jlong i = getTime(env);
        jlong systemTime = getSystemTime(env);
        isPro = systemTime <= i;
    } else{
        isPro = false;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_breeze_app_tulip_MainActivity_initApp(JNIEnv *env, jobject obj) {
    jclass pJclass = env->FindClass("breeze/app/tulip/MainActivity");
    jclass aClass = env->FindClass("breeze/app/tulip/utils/AppUtils");
    if (aClass != nullptr) {
        jmethodID pId_s = env->GetStaticMethodID(aClass, "signature",
                                                 "(Landroid/content/Context;)I");
        jint i = env->CallStaticIntMethod(aClass, pId_s, obj);
        //637747552    -597434845
        if (i == 637747552 || i == -597434845 || i != 0) {
            if (pJclass != nullptr) {
                checkPro(env);
                jmethodID pId = env->GetMethodID(pJclass, "init", "()V");
                jmethodID pId2 = env->GetMethodID(pJclass, "initData", "()V");
                env->CallVoidMethod(obj, pId);
                env->CallVoidMethod(obj, pId2);
            }
        } else {
            jmethodID pId = env->GetStaticMethodID(aClass, "exit", "()V");
            env->CallStaticVoidMethod(aClass, pId);
        }
    }
}

extern "C"
JNIEXPORT jstring JNICALL
Java_breeze_app_tulip_activity_MainToolFragment_updateUri(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("http://www.libreeze.top/tulip/update.php");
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_breeze_app_tulip_MainActivity_isPro(JNIEnv *env, jobject thiz) {
    return isAppPro();
}

extern "C"
JNIEXPORT void JNICALL
Java_breeze_app_tulip_activity_AppStorageConfigActivity_initActivity(JNIEnv *env, jobject thiz) {
    checkPro(env);
    //运行initData方法
    jclass pJclass = env->FindClass("breeze/app/tulip/activity/AppStorageConfigActivity");
    jmethodID pId = env->GetMethodID(pJclass, "initData", "()V");
    env->CallVoidMethod(thiz, pId);
    //获取参数
    jfieldID pJfieldId = env->GetFieldID(pJclass, "look_log", "Landroid/widget/Button;");
    jobject pJobject = env->GetObjectField(thiz, pJfieldId);
    jclass buttonClass = env->FindClass("android/widget/Button");
    jmethodID pJmethodId = env->GetMethodID(buttonClass, "setEnabled", "(Z)V");
    env->CallVoidMethod(pJobject, pJmethodId, isAppPro());
}

//AppEditLayout用于判断是否为高级版使用
extern "C"
JNIEXPORT void JNICALL
Java_breeze_app_tulip_widget_AppEditLayout_setData(JNIEnv *env, jobject thiz, jstring mn) {
    jstring pro_methods = env->NewStringUTF("getRootDirectory;getDataDirectory;getDataDir;getCacheDir;getFilesDir;getCodeCacheDir");
    jclass pJclass = env->FindClass("breeze/app/tulip/widget/AppEditLayout");
    jmethodID pId = env->GetMethodID(pJclass, "setEnabled", "(Z)V");
    //判断字符串包含
    jclass aClass = env->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pJmethodId = env->GetStaticMethodID(aClass, "isContainStr",
                                                  "(Ljava/lang/String;Ljava/lang/String;)Z");
    jboolean i = env->CallStaticBooleanMethod(aClass, pJmethodId, pro_methods, mn);
    if (i != 0 && !isAppPro()) {
        //包含
        env->CallVoidMethod(thiz, pId, false);
    }
}


//高级功能界面
extern "C"
JNIEXPORT void JNICALL
Java_breeze_app_tulip_activity_ProActivity_initView(JNIEnv *env, jobject thiz) {
    checkPro(env);
    //运行initData方法
    jclass pJclass = env->FindClass("breeze/app/tulip/activity/ProActivity");
    jmethodID pId = env->GetMethodID(pJclass, "initData", "()V");
    env->CallVoidMethod(thiz, pId);
    if (isAppPro()){
        jmethodID pJmethodId = env->GetMethodID(pJclass, "isPro", "()V");
        env->CallVoidMethod(thiz,pJmethodId);
        jmethodID id = env->GetMethodID(pJclass, "showDetail", "(Ljava/lang/String;J)V");
        env->CallVoidMethod(thiz,id,getToken(env),getTime(env));
    } else{
        jmethodID pJmethodId = env->GetMethodID(pJclass, "isntPro", "()V");
        env->CallVoidMethod(thiz,pJmethodId);
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_breeze_app_tulip_database_AppMySQLTool_initData(JNIEnv *env, jobject thiz) {
    checkPro(env);
    jclass pJclass = env->FindClass("breeze/app/tulip/database/AppMySQLTool");
    jmethodID pId = env->GetMethodID(pJclass, "connectMySQL",
                                     "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    jstring host = env->NewStringUTF(
            "jdbc:mysql://hk1.panel.net.cn:3306/s9271372?serverTimezone=GMT");
    jstring username = env->NewStringUTF("s9271372");
    jstring password = env->NewStringUTF("258369");
    env->CallVoidMethod(thiz,pId,host,username,password);
}

extern "C"
JNIEXPORT void JNICALL
Java_breeze_app_tulip_activity_ProActivity_submit(JNIEnv *env, jobject thiz, jstring token,jlong data) {
    setToken(env,token);
    setTime(env,data);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_breeze_app_tulip_activity_ProActivity_pay(JNIEnv *env, jobject thiz, jobject tools) {
    jclass pJclass = env->FindClass("brz/breeze/payment/BPayTools");
    jclass aClass = env->FindClass("breeze/app/tulip/utils/AppUtils");
    jmethodID pId = env->GetStaticMethodID(aClass, "getPM", "()Ljava/lang/String;");
    jobject pJobject = (jstring)env->CallStaticObjectMethod(aClass, pId);
    if (pJclass!= nullptr){
        jmethodID setAmount = env->GetMethodID(pJclass, "setAmount", "(Ljava/lang/String;)V");
        env->CallVoidMethod(tools,setAmount,(jstring)pJobject);
        return tools;
    }
    return nullptr;
}