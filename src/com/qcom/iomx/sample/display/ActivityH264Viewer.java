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
* @file    ActivityH264Viewer.java
* @brief
*
*/
package com.qcom.iomx.sample.display;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.qcom.iomx.sample.ActivityLauncher;
import com.qcom.iomx.sample.R;

public class ActivityH264Viewer extends Activity implements SurfaceHolder.Callback {
	private SurfaceHolder 	 _outputSurfaceHolder = null;
	private SurfaceView		 _outputSurfaceView = null;
	
	private int _filePictureWidth;
	private int _filePictureHeight;
	
	private String _fileName = null;
	
	private static String TAG = "Activity File Viewer";
	
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
    	_filePictureWidth = width;
    	_filePictureHeight = height;
			
		setContentView(R.layout.h264_viewer);
        _outputSurfaceView = (SurfaceView)findViewById(R.id.surfaceOutputView);
        _outputSurfaceHolder = _outputSurfaceView.getHolder();
        _outputSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        _outputSurfaceHolder.addCallback(this);
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    }

	public native String decoderStart(Surface surface, int inputW, int inputH, int outputW, int outputH, int rotation);
	public native String decodeFile(String inFile);
	public native void deleteRenderer();


	NDKThreadDecoderInit _threadDecoderInit;
	NDKThreadDecodeFile _threadDecodeFile;

	Handler hRefresh = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			     case 0: // create decoder
			    	Log.v(TAG, "Decoder start: " + (String)msg.obj);
			    	if (((String)msg.obj).equals("No errors.")) {
			    		_threadDecodeFile = new NDKThreadDecodeFile();
			    		_threadDecodeFile.run();
			    	}
			        break;
			
			     case 1: {
			    	Log.v(TAG, "Decoder done: " + (String)msg.obj);
			    	finish();
			     }	break;
			}
		}
	};
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == _outputSurfaceHolder) {
			// Don't start / initialize the decoder until the surface
			// is ready for it
			_threadDecoderInit = new NDKThreadDecoderInit();
			_threadDecoderInit.run();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	// Decoder initialization thread
	private class NDKThreadDecoderInit extends Thread {
		public void run() {
			try { Thread.sleep(1000); } catch (Exception e) {};

			int inW = _filePictureWidth;
			int inH = _filePictureHeight;
			
			int outW = _filePictureWidth;
			int outH = _filePictureHeight;
			
			Log.v(TAG, "QCOMOMXINTERFACE - DECODER INIT " + 
					outW + "x" + outH + "; " + inW + "x" + inH);

			String result = decoderStart(_outputSurfaceHolder.getSurface(),inW, inH, outW, outH, 90);

	    	Message msg = new Message();
			msg.what = 0;
			msg.obj = result;
			hRefresh.sendMessage(msg);
			Log.v(TAG, "QCOMOMXINTERFACE - DECODER INIT COMPLETE");
		}
	}
	
	// Decoder thread
	private class NDKThreadDecodeFile extends Thread {
		public void run() {
			try { Thread.sleep(1000); } catch (Exception e) {};

			String filePath = ActivityLauncher.getPathInStorageDirectory(_fileName);
			String result = decodeFile(filePath);
			deleteRenderer();
			Message msg = new Message();
			msg.what = 1;
			msg.obj = result;
			hRefresh.sendMessage(msg);
		}
	}
}
