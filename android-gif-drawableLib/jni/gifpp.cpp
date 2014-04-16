#include <string.h>
#include <jni.h>
#include "gif.h"

extern "C" {
    JNIEXPORT jboolean JNICALL
    Java_pl_droidsonroids_gif_GifDrawable_isNeedRedraw(JNIEnv* env, jobject cls, jobject gifInfo, jintArray metaData);
    JNIEXPORT jboolean JNICALL
    Java_pl_droidsonroids_gif_GifDrawable_renderCanvas(JNIEnv* env, jobject cls, jobject canvas, jobject paint,
                                jobject gifInfo, jintArray metaData);
};

JNIEXPORT jboolean JNICALL
Java_pl_droidsonroids_gif_GifDrawable_isNeedRedraw(JNIEnv* env, jobject cls, jobject gifInfo, jintArray metaData)
{

  GifInfo* info = (GifInfo*) gifInfo;
  if (info == NULL)
    return false;

  jint *rawMetaData = env->GetIntArrayElements(metaData, 0);
  bool needRedraw = false;
  unsigned long rt = getRealTime();

  if (rt >= info->nextStartTime && info->currentLoop < info->loopCount){
    needRedraw = true;
  }else{
    rawMetaData[4] = (int) (rt - info->nextStartTime);
  }
    env->ReleaseIntArrayElements(metaData, rawMetaData, 0);
  return needRedraw;
}

JNIEXPORT jboolean JNICALL
Java_pl_droidsonroids_gif_GifDrawable_renderCanvas(JNIEnv* env, jobject cls, jobject canvas, jobject paint,
                            jobject gifInfo, jintArray metaData)
{
  GifInfo* info = (GifInfo*) gifInfo;
  if (info == NULL)
    return false;

  bool needRedraw = false;
  unsigned long rt = getRealTime();

  if (rt >= info->nextStartTime && info->currentLoop < info->loopCount)
  {
    if (++info->currentIndex >= info->gifFilePtr->ImageCount)
      info->currentIndex = 0;
    needRedraw = true;
  }
  jint *rawMetaData = env->GetIntArrayElements(metaData, 0);

    // 全局变量;
    static jclass jCanvasObj = NULL;
    if (jCanvasObj == NULL) {
        jclass localRefCls =
            env->FindClass("android/graphics/Canvas");
        jCanvasObj = (jclass) env->NewGlobalRef(localRefCls);
        env->DeleteLocalRef(localRefCls);
    }
  // 绘图
    jmethodID  methodId  = env->GetMethodID(jCanvasObj, "drawBitmap", "([IIIIIIIZLandroid/graphics/Paint;)V");

  if (needRedraw)
  {
      jintArray jPixels = env->NewIntArray(rawMetaData[0] * rawMetaData[1]);
        jint *pixels = env->GetIntArrayElements(jPixels, 0);
        getBitmap((argb*) pixels, info, env);
        rawMetaData[3] = info->gifFilePtr->Error;
        env->ReleaseIntArrayElements(jPixels, pixels, 0);
        env->CallVoidMethod(canvas, methodId, jPixels, 0, rawMetaData[0], 0, 0, rawMetaData[0], rawMetaData[1], true, paint);

    int scaledDuration = info->infos[info->currentIndex].duration;
    if (info->speedFactor != 1.0)
      scaledDuration /= info->speedFactor;
    info->nextStartTime = rt + scaledDuration;
    rawMetaData[4] = scaledDuration;
    env->DeleteLocalRef(jPixels);
    jPixels = NULL;
  }
  else
    rawMetaData[4] = (int) (rt - info->nextStartTime);


  env->ReleaseIntArrayElements(metaData, rawMetaData, 0);

    return needRedraw;
}
