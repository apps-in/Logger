#include <jni.h>

//
// Created by Igor on 20.07.2022.
//


extern "C"
JNIEXPORT jint JNICALL
Java_apps_in_loggerapp_MainActivity_throwException(JNIEnv *env, jclass clazz) {
    int temp[3];
    temp[423523523523] = 5;
    int n = temp[4];
}