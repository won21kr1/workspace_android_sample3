LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CFLAGS    := -DHAVE_CONFIG_H
LOCAL_MODULE    := gif
LOCAL_SRC_FILES := gifpp.cpp \
    gif.c \
  giflib/dgif_lib.c \
  giflib/gifalloc.c

include $(BUILD_SHARED_LIBRARY)
