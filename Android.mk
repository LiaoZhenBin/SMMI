LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_JAVA_LIBRARIES += mediatek-framework
LOCAL_JAVA_LIBRARIES += telephony-common


LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_STATIC_JAVA_LIBRARIES := guava \
    android-support-v4 \
	services.core \
	libhttpclient
      
LOCAL_PACKAGE_NAME := SMMI
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
#include $(call all-makefiles-under,$(LOCAL_PATH))

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libhttpclient:lib/org.apache.http.legacy.jar
include $(BUILD_MULTI_PREBUILT)
