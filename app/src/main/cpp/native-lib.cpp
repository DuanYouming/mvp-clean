#include <jni.h>
#include <string>
#include <android/log.h>
#define LOG_D(...)  ((void)__android_log_print(ANDROID_LOG_INFO, "JNI_TEST", __VA_ARGS__))
#define JNI_CLASS "com/foxconn/bandon/Jni"


jstring stringFromJNI(JNIEnv *env, jobject /* this */,jint size) {
    std::string hello = "Hello from C++";
     LOG_D("size:%d",size);
    return env->NewStringUTF(hello.c_str());
}


static JNINativeMethod nativeMethod[] = {
        {"stringFromJNI",     "(I)Ljava/lang/String;",  (void *) stringFromJNI}
};


static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    LOG_D("JNI_OnLoad");
    if (NULL != reserved) {
        LOG_D("reserved is not null");
    }
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    if (!registerNativeMethods(env, JNI_CLASS, nativeMethod,
                               sizeof(nativeMethod) / sizeof(nativeMethod[0]))) {
        return -1;
    }
    return JNI_VERSION_1_6;
}