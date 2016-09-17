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
* @file    ActivityDecodeAndDisplay.java
* @brief
*
*/
package com.qcom.iomx.sample.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.qcom.iomx.sample.ActivityLauncher;
import com.qcom.iomx.sample.ActivityMenuSelector;
import com.qcom.iomx.sample.R;

public class ActivityDecodeAndDisplay extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	Button _buttonH264Viewer, _buttonDecoder, _buttonYuvViewer, _buttonRgbViewer;
	
	int _previewWidth = 320;
	int _previewHeight = 240;
	
	public static final String DECODER_H264_VIEWER = "Decoder H264 Viewer";
	public static final String DECODER_H264_TO_FILE = "Decoder H264 to File";
	public static final String DECODER_YUV_VIEWER = "Decoder Yuv Viewer";
	public static final String DECODER_RGB_VIEWER = "Decoder Rgb Viewer";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        
        _buttonH264Viewer = (Button)findViewById(R.id.buttonH264Viewer);
        _buttonH264Viewer.setOnClickListener(this);
        
        _buttonDecoder = (Button)findViewById(R.id.buttonDecode);
        _buttonDecoder.setOnClickListener(this);
        
        _buttonYuvViewer = (Button)findViewById(R.id.buttonYuvViewer);
        _buttonYuvViewer.setOnClickListener(this);

        _buttonRgbViewer = (Button)findViewById(R.id.buttonRgbViewer);
        _buttonRgbViewer.setOnClickListener(this);
        
        // Default: not visible
		_buttonRgbViewer.setVisibility(View.GONE);

		if (!ActivityLauncher.canDecode) {
        	_buttonDecoder.setEnabled(false);
        } else {
        	if (ActivityLauncher.decodeFormatIsRgb) {
        		_buttonDecoder.setText(R.string.h264toRgbDecode);
        		_buttonRgbViewer.setVisibility(View.VISIBLE);
        	} else {
        		_buttonDecoder.setText(R.string.h264toYuvDecode);        		
        	}     	
        }

        if (!ActivityLauncher.canRender) {
        	_buttonH264Viewer.setEnabled(false);
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == _buttonH264Viewer) {
			Intent intent = new Intent(this, ActivityMenuSelector.class);
			intent.putExtra("com.qcom.iomx.sample.fileIntention", DECODER_H264_VIEWER);
			intent.putExtra("com.qcom.iomx.sample.menuPrompt", "Select h.264 file to view");
			startActivity(intent);
			return;
		}

		if (v == _buttonDecoder) {
			Intent intent = new Intent(this, ActivityMenuSelector.class);
			intent.putExtra("com.qcom.iomx.sample.fileIntention", DECODER_H264_TO_FILE);
			intent.putExtra("com.qcom.iomx.sample.menuPrompt", "Select h.264 file to decode");
			startActivity(intent);
			return;
		}

		if (v == _buttonYuvViewer) {
			Intent intent = new Intent(this, ActivityMenuSelector.class);
			intent.putExtra("com.qcom.iomx.sample.fileIntention", DECODER_YUV_VIEWER);
			intent.putExtra("com.qcom.iomx.sample.menuPrompt", "Select yuv file to view");
			startActivity(intent);
			return;
		}
		
		if (v == _buttonRgbViewer) {
			Intent intent = new Intent(this, ActivityMenuSelector.class);
			intent.putExtra("com.qcom.iomx.sample.fileIntention", DECODER_RGB_VIEWER);
			intent.putExtra("com.qcom.iomx.sample.menuPrompt", "Select rgb file to view");
			startActivity(intent);
			return;
		}
	}
}