LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS) 
# give module name

LOCAL_MODULE := pcaptest  
LOCAL_SRC_FILES := pcaptest.c
# list your C files to compile
LOCAL_C_INCLUDES := libpcap2
LOCAL_STATIC_LIBRARIES := libpcap
LOCAL_LDLIBS := -ldl -llog

# this option will build executables instead of building library for android application.

include $(BUILD_EXECUTABLE)

include libpcap2/Android.mk
