#include <jni.h>
#include <string>
#include <android/log.h>
#include <fcntl.h>
#include <errno.h>
#include <termios.h>
#include <pthread.h>
#include <unistd.h>
#include "uart.h"

struct termios old_tio, new_tio;
static char *uart_dev = const_cast<char *>("/dev/ttyS1");
static int uart_fd = -1;
pthread_t listen_thread;
JavaVM *jvm = NULL;
jobject INSTANCE = NULL;
bool is_running;

static JNIEnv *get_env() {
    int status;
    JNIEnv *env;
    status = jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (status < 0) {
        status = jvm->AttachCurrentThread(&env, NULL);
        if (status < 0) {
            LOG_D("get_env():env is null");
            return NULL;
        }
    }
    return env;
}

static void set_termios(struct termios *p_new_tio, int baud_rate) {
    memset(p_new_tio, 0, sizeof(struct termios));
    p_new_tio->c_cflag = static_cast<tcflag_t>(baud_rate | CS8 | CREAD | CLOCAL);
    p_new_tio->c_cflag |= (PARODD | PARENB);
    p_new_tio->c_iflag |= INPCK;
    p_new_tio->c_iflag |= CSTOPB;
    p_new_tio->c_oflag = 0;
    p_new_tio->c_lflag = 0;
}

void reportUartData(JNIEnv *env, jbyteArray array) {
    jclass clazz = env->GetObjectClass(INSTANCE);
    if (NULL == clazz) {
        LOG_D("class not found");
        return;
    }
    const char *name = "receiveUartData";
    jmethodID methodID = env->GetMethodID(clazz, name, "([B)V");
    if (NULL == methodID) {
        LOG_D("method not found");
        return;
    }
    env->CallVoidMethod(INSTANCE, methodID, array);
    jbyte *element = env->GetByteArrayElements(array, NULL);
    env->ReleaseByteArrayElements(array, element, 0);
}

void uart_receive(JNIEnv *env, U_CHAR *recBuf, U_INT r_size) {
    LOG_D("uart_receive r_size=%d", r_size);
    jbyteArray array = env->NewByteArray(r_size);
    jbyte *element = env->GetByteArrayElements(array, NULL);
    for (int i = 0; i < r_size; i++) {
        element[i] = recBuf[i];
    }
    env->ReleaseByteArrayElements(array, element, 0);
    reportUartData(env, array);
}


void *runnable(void *arg) {
    if (NULL != arg) {
        LOG_D("arg is %p", arg);
    }
    JNIEnv *env = get_env();
    size_t r_size = 0;
    U_CHAR buff[BUF_SIZE] = {0};
    U_CHAR *rev_buf = static_cast<unsigned char *>(malloc(BUF_SIZE * 8 * sizeof(U_CHAR)));
    struct timeval select_timeout;
    unsigned int rev_size = 0;
    fd_set fdSet;
    while (true) {
        FD_ZERO(&fdSet);
        FD_SET(uart_fd, &fdSet);
        select_timeout.tv_sec = 1;
        select_timeout.tv_usec = 0;
        if (select(1 + uart_fd, &fdSet, NULL, NULL, &select_timeout) > 0) {
            if (FD_ISSET(uart_fd, &fdSet)) {
                r_size = static_cast<size_t>(read(uart_fd, buff, BUF_SIZE));
                if (r_size == 0) {
                    continue;
                }
                //LOG_D("receive uart data:%02x", buff[0]);
                if (buff[0] == DATA_HEADER) {
                    rev_size = 0;
                }
                rev_buf[rev_size] = buff[0];
                rev_size++;
                if (buff[0] == DATA_END) {
                    uart_receive(env, rev_buf, rev_size);
                }
            }
        }
        if (!is_running) {
            LOG_D("exit thread");
            if (jvm->DetachCurrentThread() != JNI_OK) {
                LOG_D("Detach Current Thread Failed");
            }
            return NULL;
        }
    }
    return NULL;
}

jboolean connect(JNIEnv *env, jobject o) {
    if (is_running) {
        LOG_D("thread is already");
        return JNI_TRUE;
    }
    if ((uart_fd = open(uart_dev, O_RDWR | O_NOCTTY)) < 0) {
        LOG_D("open uart dev fail");
        LOG_D ("Error no is : %d\n", errno);
        return JNI_FALSE;
    }
    INSTANCE = env->NewGlobalRef(o);
    tcgetattr(uart_fd, &old_tio);
    set_termios(&new_tio, B1200);
    tcflush(uart_fd, TCIFLUSH);
    tcsetattr(uart_fd, TCSANOW, &new_tio);
    env->GetJavaVM(&jvm);
    is_running = true;
    pthread_create(&listen_thread, NULL, runnable, NULL);
    LOG_D("connect");
    return JNI_TRUE;
}

void disconnect(JNIEnv *env) {
    LOG_D("disconnect");
    if (!is_running) {
        return;
    }
    env->DeleteGlobalRef(INSTANCE);
    is_running = false;
    pthread_join(listen_thread, NULL);
    tcsetattr(uart_fd, TCSANOW, &old_tio);
    close(uart_fd);
}

void send_command(JNIEnv *env, jobject /* this */, jcharArray data, jint size) {
    LOG_D("sendCommand() size:%d", size);
    jchar *array = env->GetCharArrayElements(data, NULL);
    U_CHAR buff[size];
    for (int i = 0; i < size; i++) {
        buff[i] = static_cast<unsigned char>(array[i]);
    }
    write(uart_fd, &buff[0], static_cast<size_t>(size));
    // release memory
    jchar *element = env->GetCharArrayElements(data, NULL);
    env->ReleaseCharArrayElements(data, element, 0);
}

static JNINativeMethod nativeMethod[] = {
        {"connect",     "()Z",    (void *) connect},
        {"disconnect",  "()V",    (void *) disconnect},
        {"sendCommand", "([CI)V", (void *) send_command}
};

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods, int numMethods) {
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
    jvm = vm;
    if (!registerNativeMethods(env, JNI_CLASS, nativeMethod,
                               sizeof(nativeMethod) / sizeof(nativeMethod[0]))) {
        return -1;
    }
    return JNI_VERSION_1_6;
}
