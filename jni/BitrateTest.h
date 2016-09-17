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
* @file    BitrateTest.h
* @brief
*
*/
#pragma once

#ifndef _BITRATE_TEST_H
#define _BITRATE_TEST_H

#include "QcomOmxPublicInterface.h"

// This function outputs encoded data to a file, but also keeps track of the number of frames
// written.  After a certain number of frames is written, it sends a command to change the bitrate
// on the fly to illustrate how to programmatically update the bitrate in code.
//
// This function can be used as an output callback function regardless of how the input is provided.
// 
int handleOutputEncodedToFile(void *obj, void *buffer, size_t bufferSize, void *iomxBuffer, void *userData);

// By encoding from one file to another, analysis tools can show the bitrate change on a single
// consistent file.  Modification of the various bitrates and frame setpoints can be made in order
// to experiment with what kind of results will be achieved at various resolutions.
//
// To see the different types of results, this test can be integrated into a shell script
// or coded into an APK by way of the NDK.
//
int bitrate_test(const char *fileFrom, const char *fileTo, int width, int height);

#endif
