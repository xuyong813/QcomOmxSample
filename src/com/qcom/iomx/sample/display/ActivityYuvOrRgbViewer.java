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
* @file    ActivityYuvOrRgbViewer.java
* @brief
*
*/
package com.qcom.iomx.sample.display;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import com.qcom.iomx.sample.ActivityLauncher;
import com.qcom.iomx.sample.R;

public class ActivityYuvOrRgbViewer extends Activity {
	private SurfaceHolder 	 _outputSurfaceHolder = null;
	private VideoDisplayView _outputSurfaceView = null;
	private String _fileName = null;
	private static final String TAG = "ACTIVITY YUV VIEWER";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		_fileName = getIntent().getStringExtra("com.qcom.iomx.sample.fileName");
		int width = 320;
    	int height = 240;
    	String[] parts = _fileName.split("\\.");
    	for (int i = 0; i < parts.length; i++) {
    		if (parts[i].contains("x")) {
    			String[] dims = parts[i].split("x");
    			if (dims.length == 2) {
    				try {
	    				width = Integer.valueOf(dims[0]).intValue();
	    				height = Integer.valueOf(dims[1]).intValue();
	    				if (width != 0 && height != 0) {
	    					break;
	    				}
    				} catch (Exception e) {
    					// Filename may have had an 'x' in the title
    					// that did not specify dimensions.
    				}
    			}
    		}
    	}
        setContentView(R.layout.yuv_viewer);

        _outputSurfaceView = (VideoDisplayView)findViewById(R.id.surfaceOutputView);
        _outputSurfaceHolder = _outputSurfaceView.getHolder();
        _outputSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

		_outputSurfaceView.previewWidth = width;
		_outputSurfaceView.previewHeight = height;

		Log.e(TAG, "FILE: " + _fileName + " at " + width + " x " + height);

		String filePath = ActivityLauncher.getPathInStorageDirectory(_fileName);
    	_outputSurfaceView.setDisplayFilePath(filePath);
    	_outputSurfaceView.startDisplaying();
    }
    
    protected void onDestroy() {
    	super.onDestroy();
		_outputSurfaceView.stopDisplaying();
    }

}