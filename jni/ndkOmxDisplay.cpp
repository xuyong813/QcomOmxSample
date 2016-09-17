/* ======================================================================
*  Copyright � 2013 Qualcomm Technologies, Inc. All Rights Reserved.
*  QTI Proprietary and Confidential.
*  =====================================================================
*
*  Licensed by Qualcomm, Inc. under the Snapdragon SDK for Android license.
*
*  You may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*    https://developer.qualcomm.com/snapdragon-sdk-license
*
*  This Qualcomm software is supplied to you by Qualcomm Inc. in consideration
*  of your agreement to the licensing terms.  Your use, installation, modification
*  or redistribution of this Qualcomm software constitutes acceptance of these terms.
*  If you do not agree with these terms, please do not use, install, modify or
*  redistribute this Qualcomm software.
*
*  Qualcomm grants you a personal, non-exclusive license, under Qualcomm's
*  copyrights in this original Qualcomm software, to use, reproduce, modify
*  and redistribute the Qualcomm Software, with or without modifications, in
*  source and/or binary forms; provided that if you redistribute the Qualcomm
*  Software in its entirety and without modifications, you must retain this
*  notice and the following text and disclaimers in all such redistributions
*  of the Qualcomm Software.
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*
* @file    ndkOmxDisplay.cpp
* @brief
*
*/
#include <string.h>
#include <jni.h>
#include "QcomOmxPublicInterface.h"
#include "QcomOmxRenderer.h"
#include <android/log.h>
#include <semaphore.h>

#ifdef __cplusplus
extern "C" {
#endif

long 		  g_timeStamp;

void		 *g_decoder = NULL;
FILE 		 *f = NULL;

QcomRenderer *g_renderer = NULL;

// decoderIsAvailable
//
//    Creates and initializes a decoder instance.  Once set up, the instance
//    can be fed from any input source.
//
jint Java_com_qcom_iomx_sample_display_ActivityH264Viewer_decoderIsAvailable
  	(JNIEnv *env, jclass cls) {
	
  	bool isAvailable = omx_component_is_available("OMX.qcom.video.decoder.avc");
  	return isAvailable;
}

// decoderStart
//
//    Creates and initializes a decoder instance.  Once set up, the instance
//    can be fed from any input source.
//
jstring Java_com_qcom_iomx_sample_display_ActivityH264Viewer_decoderStart
  	(JNIEnv *env, jclass cls, jobject surface, jint inW, jint inH, jint outW, jint outH, jint rotation) {

	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "%d x %d -> %d x %d", inW, inH, outW, outH);

	int status = 0;
	g_decoder = decoder_create(&status);

	// Initialize the decoder, watching out for any errors.
	status = omx_interface_init(g_decoder);
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "DECODE FROM FILE INITED (NULL? %d)", g_decoder == NULL);
	if (status != 0) {
		// If there's any failure, clean up and send the error back.
		omx_interface_deinit(g_decoder);
		omx_interface_destroy(g_decoder);
		return env->NewStringUTF(resultDescription(status));
	}

	// Otherwise setup the surface renderer
	if (g_renderer != NULL) {
		delete_renderer(g_renderer);
		g_renderer = NULL;
	}

	g_renderer = omx_interface_create_renderer(g_decoder, env, surface, outW, outH, inW, inH);
	if (status != 0) {
		omx_interface_deinit(g_decoder);
		omx_interface_destroy(g_decoder);
		return env->NewStringUTF(resultDescription(status));
	}

	// For the file viewer, setup the output callback to call the appropriate
	// surface render method.
	omx_interface_register_output_callback(g_decoder, omx_surface_render, g_renderer);

	const char *description = resultDescription(status);
	return env->NewStringUTF(description);
}

void Java_com_qcom_iomx_sample_display_ActivityH264Viewer_deleteRenderer(JNIEnv *env, jclass cls) {
	if (g_renderer != NULL) {
		delete_renderer(g_renderer);
		g_renderer = NULL;
	}
}

// decodeFile
//
//    Sends a file path to an initialized decoder instance and
//    reads the entire file directly into the decoder until complete.
//    Auto-cleans up when done.
//
jstring Java_com_qcom_iomx_sample_display_ActivityH264Viewer_decodeFile
  	(JNIEnv *env, jclass cls, jstring infile, jint width, jint height) {
	const char *inputFileName = env->GetStringUTFChars(infile,0);	
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "DECODE FROM FILE START");

	int status = 0;

	status = omx_interface_decode_from_file(g_decoder, inputFileName);
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "DECODE FROM FILE RESULT");

	return env->NewStringUTF(resultDescription(status));
}

#ifdef __cplusplus
}
#endif
