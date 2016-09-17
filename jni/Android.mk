# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Qualcomm Note:
#     Modify this makefile to only build the shared libraries based
#     on the Android OS shared libraries you have pre-built in
#     the Android source.
#

LOCAL_PATH:= $(call my-dir)

###########################################################
# Common libraries across froyo / gb

#include $(CLEAR_VARS)
#LOCAL_MODULE    := qcomomxintermediate
#LOCAL_SRC_FILES := ../lib/libqcomomxsample.so
#include $(PREBUILT_SHARED_LIBRARY)
#
#include $(CLEAR_VARS)
#LOCAL_MODULE            := qcomomx4ndk_encode_decode
#LOCAL_SHARED_LIBRARIES  := qcomomxintermediate
#LOCAL_LDLIBS            := -llog
#LOCAL_SRC_FILES         := ndkOmxEncodeDecode.cpp
#LOCAL_CPPFLAGS          += -fexceptions
#include $(BUILD_SHARED_LIBRARY)

###########################################################

###########################################################
# Comment this whole block out if not supplying a
# Gingerbread-built shared library libqcomomxrenderer_gb.so
# for rendering
#
#include $(CLEAR_VARS)
#LOCAL_MODULE    := qcomomxrenderer_gb
#LOCAL_SRC_FILES := ../lib/libqcomrenderer_gb.so 
#include $(PREBUILT_SHARED_LIBRARY)
#
#include $(CLEAR_VARS)
#LOCAL_MODULE            := qcomomx4ndk_display_gb
#LOCAL_SHARED_LIBRARIES  := qcomomxintermediate qcomomxrenderer_gb
#LOCAL_LDLIBS            := -llog
#LOCAL_SRC_FILES         := ndkOmxDisplay.cpp
#LOCAL_CPPFLAGS          += -fexceptions
#include $(BUILD_SHARED_LIBRARY)

###########################################################

###########################################################
# Common libraries across froyo / gb

#include $(CLEAR_VARS)
#LOCAL_MODULE    := qcomomxintermediate
#LOCAL_SRC_FILES := ../lib/libqcomomxsample.so
#include $(PREBUILT_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE    		:= qcomomx4ndk_encode_decode
#LOCAL_SHARED_LIBRARIES 	:= qcomomxintermediate
#LOCAL_LDLIBS 			:= -llog
#LOCAL_SRC_FILES 		:= ndkOmxEncodeDecode.cpp
#LOCAL_CPPFLAGS 			+= -fexceptions
#include $(BUILD_SHARED_LIBRARY)

###########################################################

###########################################################
# Common libraries for jb

include $(CLEAR_VARS)
LOCAL_MODULE    := qcomomxintermediate_jb
LOCAL_SRC_FILES := ../lib/libqcomomxsample_jb.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE                    := qcomomx4ndk_encode_decode_jb
LOCAL_SHARED_LIBRARIES  := qcomomxintermediate_jb
LOCAL_LDLIBS                    := -llog
LOCAL_SRC_FILES                 := ndkOmxEncodeDecode.cpp
LOCAL_CPPFLAGS                  += -fexceptions
include $(BUILD_SHARED_LIBRARY)

###########################################################

###########################################################
# Common libraries for ics

#include $(CLEAR_VARS)
#LOCAL_MODULE    := qcomomxintermediate_ics
#LOCAL_SRC_FILES := ../lib/libqcomomxsample_ics.so
#include $(PREBUILT_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE                    := qcomomx4ndk_encode_decode_ics
#LOCAL_SHARED_LIBRARIES  := qcomomxintermediate_ics
#LOCAL_LDLIBS                    := -llog
#LOCAL_SRC_FILES                 := ndkOmxEncodeDecode.cpp
#LOCAL_CPPFLAGS                  += -fexceptions
#include $(BUILD_SHARED_LIBRARY)

###########################################################

###########################################################
# Common libraries for hc

#include $(CLEAR_VARS)
#LOCAL_MODULE    := qcomomxintermediate_hc
#LOCAL_SRC_FILES := ../lib/libqcomomxsample_hc.so
#include $(PREBUILT_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE    		:= qcomomx4ndk_encode_decode_hc
#LOCAL_SHARED_LIBRARIES 	:= qcomomxintermediate_hc
#LOCAL_LDLIBS 			:= -llog
#LOCAL_SRC_FILES 		:= ndkOmxEncodeDecode.cpp
#LOCAL_CPPFLAGS 			+= -fexceptions
#include $(BUILD_SHARED_LIBRARY)

###########################################################

###########################################################
# Comment this whole block out if not supplying a
# Gingerbread-built shared library libqcomomxrenderer_gb.so
# for rendering
#
#include $(CLEAR_VARS)
#LOCAL_MODULE    := qcomomxrenderer_gb
#LOCAL_SRC_FILES := ../lib/libqcomrenderer_gb.so 
#include $(PREBUILT_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE    		:= qcomomx4ndk_display_gb
#LOCAL_SHARED_LIBRARIES 	:= qcomomxintermediate qcomomxrenderer_gb
#LOCAL_LDLIBS 			:= -llog
#LOCAL_SRC_FILES 		:= ndkOmxDisplay.cpp
#LOCAL_CPPFLAGS 			+= -fexceptions
#include $(BUILD_SHARED_LIBRARY)

###########################################################

###########################################################
# Comment this whole block out if not supplying a
# Froyo-built shared library libqcomomxrenderer_froyo.so
# for rendering

#include $(CLEAR_VARS)
#LOCAL_MODULE    := qcomomxrenderer_froyo
#LOCAL_SRC_FILES := ../lib/libqcomrenderer_froyo.so
#include $(PREBUILT_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE    		:= qcomomx4ndk_display_froyo
#LOCAL_SHARED_LIBRARIES 	:= qcomomxintermediate qcomomxrenderer_froyo
#LOCAL_LDLIBS 			:= -llog
#LOCAL_SRC_FILES 		:= ndkOmxDisplay.cpp
#LOCAL_CPPFLAGS 			+= -fexceptions
#include $(BUILD_SHARED_LIBRARY)

###########################################################






