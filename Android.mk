LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := samples

# Only compile source java files in this apk.
LOCAL_SRC_FILES := $(call all-java-files-under, src)

#pour pouvoir mettre parent="Theme.MaterialComponents.NoActionBar" dans res/values/styles.xml
LOCAL_STATIC_ANDROID_LIBRARIES += \
    com.google.android.material_material \
    androidx.lifecycle_lifecycle-livedata \
    androidx.lifecycle_lifecycle-viewmodel

LOCAL_PACKAGE_NAME := LocGatt

LOCAL_SDK_VERSION := current

LOCAL_MIN_SDK_VERSION := 26

LOCAL_DEX_PREOPT := false

include $(BUILD_PACKAGE)

# Use the following include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
