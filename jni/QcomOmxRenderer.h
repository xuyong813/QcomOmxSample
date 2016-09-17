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
* @file    QcomOmxRenderer.h
* @brief
*
*/
#pragma once

#ifndef _QCOM_OMX_RENDERER_H
#define _QCOM_OMX_RENDERER_H

#include "QcomOmxPublicInterface.h"

class QcomRenderer;
QcomRenderer *omx_interface_create_renderer(void *obj, JNIEnv *env, jobject javaSurface, 
									int fileWidth, int fileHeight, 
									int outputWidth, int outputHeight, 
									int hwVersion = 0);

int omx_interface_render(void *obj, QcomRenderer *renderer, void *iomxBuffer);

int delete_renderer(QcomRenderer *renderer);

// omx_surface_render
//
//     This follows the _QcomOmxOutputCallback declaration.  The buffer parameter is a pointer
//     to the bufferInfo while the iomxBuffer parameter corresponds to the iomxBuffer item inside the buffer.
//
//     The iomxBuffer needs to be sent to the render() call, but the buffer encapsulation needs to be held onto
//     in order to be able to update state when it's time to send it back to the component.
//
int   omx_surface_render(void *obj, void *buffer, size_t bufferSize, void *iomxBuffer, void *userData);
int	  omx_surface_render_immediate(void *obj, void *buffer, size_t bufferSize, void *iomxBuffer, void *userData);

#endif
