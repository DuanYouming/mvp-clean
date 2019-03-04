#ifndef BANDON_UART_H
#define BANDON_UART_H

#define U_CHAR    unsigned char
#define U_INT    unsigned int
#define LOG_D(...)  ((void)__android_log_print(ANDROID_LOG_INFO, "com.foxconn.bandon::UART_JNI", __VA_ARGS__))
#define JNI_CLASS "com/foxconn/bandon/uart/UartHelper"
#define BUF_SIZE 256
#define DATA_HEADER 0xFA
#define DATA_END 0XF5

#endif //BANDON_UART_H