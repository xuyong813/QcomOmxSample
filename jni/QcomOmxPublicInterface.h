/* ======================================================================
*  Copyright 锟�2013 Qualcomm Technologies, Inc. All Rights Reserved.
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
* @file    QcomOmxPublicMain.h
* @brief
*
*/
#pragma once

#ifndef _QCOM_OMX_PUBLIC_INTERFACE_H
#define _QCOM_OMX_PUBLIC_INTERFACE_H

// Change this define to 1 if building on a Gingerbread tree
#define BUILD_FOR_GINGERBREAD 0

// Change this define to 1 if building on a Honeycomb or up tree
#define BUILD_FOR_HONEYCOMB_AND_UP 1

// Change this define to 1 if building on a JB tree
#define BUILD_FOR_JELLYBEAN 1

#if !BUILD_FOR_HONEYCOMB_AND_UP

// Change this define to 0 if not using the iOMX renderer to display data;
// Note that this code is ignored if we are building for Honeycomb
#define ENABLE_OMX_RENDERER 0

#endif


// Change this define to 1 for verbose logging during development
#define LOG_VERBOSE 01

// Change this define to 1 if OMX_QCOMExtns.h file has defined
// the struct QOMX_VIDEO_DECODER_PICTURE_ORDER; used in QcomOmxInterfaceDecoder.cpp
//
#define USE_QCOM_PICTURE_ORDER  0

#include <jni.h>
#include <sys/time.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

typedef enum {
	kQcomOmxInterfaceErrorSuccess,
	kQcomOmxInterfaceErrorCouldNotAcquireMediaPlayerService,
	kQcomOmxInterfaceErrorCouldNotCreateQcomOmxInterface,
	kQcomOmxInterfaceErrorCouldNotAcquireiOmxInterface,
	kQcomOmxInterfaceErrorCouldNotAllocateOmxComponent,
	kQcomOmxInterfaceErrorCouldNotCreateQcOmxCodecObserver,
	kQcomOmxInterfaceErrorOmxComponentNotFound,
	kQcomOmxInterfaceErrorBuffersNotRegistered,

	kQcomOmxInterfaceErrorFillingBuffer,
	kQcomOmxInterfaceErrorPortSettingChangeFail,

	kQcomOmxInterfaceErrorInvalidState,
	kQcomOmxInterfaceErrorIncorrectStateReached,
	kQcomOmxInterfaceErrorOmxStateNotExecuting,

	kQcomOmxInterfaceErrorCouldNotGetPortDefinition,
	kQcomOmxInterfaceErrorCouldNotSetPortDefinition,
	kQcomOmxInterfaceErrorCouldNotAllocateMemory,
	kQcomOmxInterfaceErrorCouldNotRequestIFrame,
	kQcomOmxInterfaceErrorCouldNotGetBitRateParameters,
	kQcomOmxInterfaceErrorCouldNotSetBitRateParameters,
	kQcomOmxInterfaceErrorCouldNotSetCodecConfiguration,

	kQcomOmxInterfaceErrorCouldNotSendCommandState,
	kQcomOmxInterfaceErrorCouldNotSetExecutionState,

	kQcomOmxInterfaceErrorCouldNotCreateEventThread,
	kQcomOmxInterfaceErrorCouldNotCreateOutputThread,

	kQcomOmxInterfaceErrorBadBufferFound,
	kQcomOmxInterfaceErrorBufferAlreadyOwned,
	kQcomOmxInterfaceErrorBufferNotEmptied,
	kQcomOmxInterfaceErrorOmxEventError,

	kQcomOmxInterfaceErrorInvalidEncodeParameters,
	kQcomOmxInterfaceErrorCouldNotSetupRenderer,

	kQcomOmxInterfaceErrorSettingDecoderPictureOrder,
	kQcomOmxInterfaceErrorSettingFramePackingFormat,

	kQcomOmxInterfaceErrorSeeLogs

} QcomOmxInterfaceError;

// DecoderPictureOrder
//     Enumeration of decoding picture order types for outputting frames from the decoder
//
typedef enum {
       kDecoderPictureOrderDisplay = 0,
       kDecoderPictureOrderDecode
} DecoderPictureOrder;

// FramePackingFormat
//     Enumeration of frame-packing types for sending frames to the encoder
//
typedef enum {
       kFramePackingFormatUnspecified = 0,
       kFramePackingFormatOneCompleteFrame,
       kFramePackingFormatArbitrary
} FramePackingFormat;

// VideoCompressionFormat
//     Enumeration of video compression formats for sending frames to the encoder
//
typedef enum {
       kVideoCompressionFormatAVC = 0,
       kVideoCompressionFormatMPEG4,
       kVideoCompressionFormatH263,
       kVideoCompressionFormatWMV
} VideoCompressionFormat;

const char *resultDescription(int result);

// encoderParams
//     Populate this struct and pass it in to encoder instantiation
//     to set the common parameters of an encoder component
//
struct encoderParams {
	int frameWidth;
	int frameHeight;
 	int frameRate;
	int rateControl;
	int bitRate;
	const char *codecString;
	int frame_num;
	char* cur_frame_buf;
};

// HardwareBase
//     Enumeration of recognized hardware for this sample code
//
typedef enum {
	kHardwareBaseUnknown,
	kHardwareBaseUncatalogued,
	kHardwareBase7x30,
	kHardwareBase8x55,
	kHardwareBase8x60,
    kHardwareBase8x74
} HardwareBase;

// OMXOperationType
//     Enumeration of encoding and decoding operation types
//
typedef enum {
	kOMXOperationTypeDecode = 0,
	kOMXOperationTypeEncode
} OMXOperationType;

// resultDescription
//     Returns a human-readable string to interpret a QcomOmxInterfaceError result code (see QcomOmxCommon.h)
//
const char *resultDescription(int result);

// These are callbacks defined for interacting with the component through the sample
//
// The input callback is used to signal when an input buffer is available.  The parameters
// used in the callback are:
//    obj - the component sample instance
//    userdata - an optionally user-provided userdata object.  Example: if reading from a file,
//               the file handle may have been set as the userdata
//
typedef int (*_QcomOmxInputCallback)(void *obj, void *userData);

// The output callback is used to signal when an output buffer contains processed data.  The parameters
// used in the callback are:
//    obj - the component sample instance
//    buffer - the buffer data
//    bufferSize - length in bytes of the filled buffer
//    iomxBuffer - pointer to the actual OpenMAX IL buffer object.  Used when conducting
//                 operations where the actual buffer needs to be used and not just the
//                 buffer bytes.
//    userdata - an optionally user-provided userdata object.  Example: if writing to a file,
//               the file handle may have been set as the userdata
//
//    return value:
//		  0 to return the buffer to the component, 1 to hold onto the buffer before sending
//		  back to the component.
//
typedef int (*_QcomOmxOutputCallback)(void *obj, void *buffer, size_t bufferSize, void *iomxBuffer, void *userData);

//  Query for the omx component availability.
//
//  This call validates whether or not the requested omx component is available
//  on the device.
//
//  Parameters:
//    hardwareCodecString - name of the component to ask for
//                          h.264 qcom encoder: OMX.qcom.video.encoder.avc
//                          h.264 qcom decoder: OMX.qcom.video.decoder.avc
//
//  return value:
//      true if the component is available, false otherwise
//
bool omx_component_is_available(const char *hardwareCodecString);

// C wrapper calls to creating the encoder and decoder
//
// For the encoder parameters, see encoderParams above
void *encoder_create(int *errorCode, encoderParams *params);

// For the decoder, a codecString can be used to specify which codec to expect.  Setting to NULL
// will keep the decoder in H.264 mode.
void *decoder_create(int *errorCode, const char *codecString = NULL);

// init will start the background threading for the component and set it to executing state
int   omx_interface_init(void *obj);

// deinit turns off the component
int   omx_interface_deinit(void *obj);

// destroy deletes the component from memory space
int   omx_interface_destroy(void *obj);

// error returns the last error found by the component sample, if any.  Use this call
// to short-circuit any asynchronous threaded operations 
int   omx_interface_error(void *obj);

// these calls are wrappers to set up the input and output callbacks with optional userData to
// be used by the callback functions.
int   omx_interface_register_input_callback(void *obj, _QcomOmxInputCallback function, void *userData = NULL);
int   omx_interface_register_output_callback(void *obj, _QcomOmxOutputCallback function, void *userData = NULL);

// standard call to send input data to the component.
// Parameters:
//	  obj - component sample instance
//    buffer - byte-array of data to send to the component
//    size   - size in bytes of the buffer
//    timeStampLFile - used to mark the timing of the clip
int   omx_interface_send_input_data(void *obj, void *buffer, size_t size, int timeStampLfile);

// standard call to flag completion of input data.  When the flag comes back through
// the fillbuffer call, it knows to shut down the running threads.
int   omx_interface_send_end_of_input_flag(void *obj, int timeStampLfile);

// More efficient calls to sending the input data directly to the buffer.  In this scenario,
// an open input buffer is reserved and then repeatedly submitted until an error or success occurs.
// At the end, the final buffer can be sent much like send_end_of_input_flag does
//
// reserve_input_buffer params:
//     obj - component sample instance
//     buffer - pointer to an OpenMAX buffer.  Upon success, the
//				pointer will reference an OpenMAX buffer that will
//              need to be sent back to the component for processing.
//     memPtr - pointer to an uninstantiated byte array.  Upon success, the
//				pointer will reference an empty buffer to be filled.
//    timeStampLFile - used to mark the timing of the clip
//              
int	  omx_interface_reserve_input_buffer(void *obj, void **buffer, void **memPtr);

// send_input_buffer params:
//     obj - component sample instance
//     buffer - pointer to the OpenMAX buffer acquired from reserve_input_buffer.
//     size - length of the buffer in bytes.
//
//
int   omx_interface_send_input_buffer(void *obj, void *buffer, int size, long timeStampFile);

// standard call to flag completion of input data.  When the flag comes back through
// the fillbuffer call, it knows to shut down the running threads.
int   omx_interface_send_final_buffer(void *obj, void *buffer, long timeStampFile);

// Hardware Base Version: get an enumeration to determine if the hardware supports this sample code
// and also to know parameters needed in order to configure it if necessary
int   getHardwareBaseVersion();

// Test use case for decoding a file using the above calls
int decode_file(const char *fileFrom, const char *fileTo, int width, int height, const char *codecString = NULL);

// Test use case for encoding a file using the above calls
int encode_file(const char *fileFrom, const char *fileTo, encoderParams *params);

// Convenience method for setting up and tearing down the input semaphore
int omx_setup_input_semaphore(void *omx);
int omx_teardown_input_semaphore();

int omx_send_data_frame_to_encoder(void *omx, void *frameBytes, int width, int height, long timeStamp);

// omx_interface_decode_from_file
//
//     This is a single call that can be made to continuously decode from a file
//     until all output is reached.  Note that this is call will continuously feed
//     the component until all data is output, which will prevent user input from
//     being handled when run directly from an Android apk.  This is provided as an
//     example to illustrate how to use it, and developers will need to implement
//     their own mechanisms for handling interrupts.
//
int	  omx_interface_decode_from_file(void *obj, const char *fileFrom);


#endif
