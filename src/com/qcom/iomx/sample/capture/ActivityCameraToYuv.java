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
* @file    ActivityCameraToYuv.java
* @brief
*
*/
package com.qcom.iomx.sample.capture;

import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.qcom.iomx.sample.ActivityLauncher;
import com.qcom.iomx.sample.R;

//Camera.PreviewCallback, SurfaceHolder.Callback, OnClickListener
public class ActivityCameraToYuv extends Activity implements OnClickListener, PreviewCallback, Callback {
	boolean _isRecordingFrames;
	Button _buttonRecordFrames;
	//TextView _tvCameraStatus;

	private int _previewWidth;
	private int _previewHeight;
	private int frame=0;
	
	private String  watermarkbytes;
	
	private String hhh;
	

	//Local variables used in this Activity
	private SurfaceHolder 	 _previewSurfaceHolder = null;
	private SurfaceView 	 _previewSurfaceView = null;
	
	private static final String TAG = "ActivityCameraToYuv";

	public static final String CAMERA_PREVIEW_SIZE = "CameraPreviewSize";
	public static final String CAMERA_PREVIEW_SIZE_PROMPT = "Select Resolution";

	private FileOutputStream _fileOS = null;
	private FileOutputStream _fileOS1 = null;

    //Code to handle the camera
	private static Camera _cameraObj = null;
	private boolean _cameraRunning = false;
	private String _filePrefix = "camera";
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i("one","one");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        _buttonRecordFrames = (Button)findViewById(R.id.buttonRecordFrames);
      //  _tvCameraStatus = (TextView)findViewById(R.id.tvCameraStatus);
        
        _buttonRecordFrames.setOnClickListener(this);

    	_previewWidth = getIntent().getIntExtra("com.qcom.iomx.sample.previewWidth", 320);
    	_previewHeight = getIntent().getIntExtra("com.qcom.iomx.sample.previewHeight", 240);

    	_previewSurfaceView = (SurfaceView) findViewById(R.id.viewCameraPreview);
        _previewSurfaceView.setZOrderMediaOverlay(true);
        _previewSurfaceHolder = _previewSurfaceView.getHolder();
        _previewSurfaceHolder.addCallback(this);
        _previewSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 

		if (!ActivityLauncher.validateStorageDirectory()) {
	    //	_tvCameraStatus.setText("Cannot write to storage directory");
	    	_buttonRecordFrames.setEnabled(false);
	    	return;
		}
		
		Log.i("two","two");
		MyFileManager MF = new MyFileManager(); 
				
				
				 hhh = MF.hh;
				 Log.v(" hhh"," hhh:"+MF.hh);
    }
    public native byte[] embedwatermark(byte[] frameData,String watermarkData);

	protected void onDestroy() {
    	super.onDestroy();
    }
    
    //Start the camera if it is not already running
    //Start the camera if it is not already running
	private void startCamera() {
		if(!_cameraRunning && _cameraObj == null) {
			try {
				_cameraObj = Camera.open();

			} catch (Exception e) { 
				Log.e(TAG, "CANNOT OPEN CAMERA: " + e.getLocalizedMessage());
			}

//			if (_cameraObj == null) {
//				try {
//					_cameraObj = Camera.open(0);
//	
//				} catch (Exception e) { 
//					Log.e(TAG, "CANNOT OPEN CAMERA: " + e.getLocalizedMessage());
//				}
//			}
		}
	}
		
	//Close down the camera
	private void stopCamera() {
		if(_cameraObj != null) {
			_cameraObj.setPreviewCallback(null);
			_cameraObj.stopPreview();
	    	_cameraObj.release();
    	}
		_cameraObj = null;
		_cameraRunning = false;
	}
	
	//Set up the parameters for the camera and show preview
	private boolean setupCamera(SurfaceHolder holder) {
		boolean ret = true;
		try{
			if(_cameraObj == null || _alreadyCreated)
				return true;
			
			if(_cameraRunning)
				_cameraObj.stopPreview();

			Camera.Parameters params = _cameraObj.getParameters();

		    if (_previewSurfaceView != null) {
		    	params.setPreviewSize(_previewWidth, _previewHeight);
		    }

//		    _cameraObj.setDisplayOrientation(90);
//
//		    params.setPreviewFormat(ImageFormat.NV21);
//			params.setPictureFormat(PixelFormat.JPEG);
//			//params.setPictureSize(512, 384);
//			params.setPreviewSize(_previewWidth, _previewHeight);
//			params.set("orientation", "portrait");
//			_cameraObj.setParameters(params);
//			
//			_cameraObj.setPreviewDisplay(holder);
//			_cameraObj.setPreviewCallback(this);
//			_cameraObj.startPreview();
//			_cameraRunning = true;
//			_alreadyCreated = true;
		    
		    
		    _cameraObj.setDisplayOrientation(90);

			params.setPreviewFormat(ImageFormat.NV21);
			params.setPictureFormat(PixelFormat.JPEG);
			params.setPreviewSize(_previewWidth, _previewHeight);
			params.set("orientation", "portrait");
			params.setRotation(90);
			_cameraObj.setParameters(params);

			_cameraObj.setPreviewDisplay(holder);
			_cameraObj.setPreviewCallback(this);
			_cameraObj.startPreview();

			_cameraRunning = true;
			_alreadyCreated = true;

		}catch(Exception e) {
			e.printStackTrace();
			stopCamera();
			ret = false;
		}
		return ret;
	}
	
    protected void onPause() {
    	super.onPause();
		if (_isRecordingFrames) {
			updateRecordingStatus(false);
		}

		stopCamera();
    }
    
    protected void onResume() {
    	super.onResume();
    }

    private boolean _alreadyCreated = false;
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (holder == _previewSurfaceHolder) {
			if(!setupCamera(holder)) {
				//if the camera has not set up properly exit
				finish();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == _previewSurfaceHolder) {
			startCamera();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (holder == _previewSurfaceHolder) {
			stopCamera();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == _buttonRecordFrames) {
			updateRecordingStatus(!_isRecordingFrames);
		}
	}


    private void updateRecordingStatus(boolean shouldRecordFrames) {
		// Update the UI and fire off the appropriate thread
		// based on the type of status to update to.
    //	_tvCameraStatus.setText(shouldRecordFrames ? R.string.textViewStatusRecording : R.string.textViewStatusIdle);
    //	_buttonRecordFrames.setText(shouldRecordFrames ? "Initializing..." : "Stopping...");
    	_buttonRecordFrames.setEnabled(false);
		if (shouldRecordFrames) {
		//	_isRecordingFrames = false;
			_buttonRecordFrames.setBackgroundResource(R.drawable.videotapafter);
		//	_buttonRecordFrames.setText(R.string.actionRecordFrames);
//			if (_tvCameraStatus != null)
//				_tvCameraStatus.setText(R.string.textViewStatusIdle);
		}
			else
			{
				_buttonRecordFrames.setBackgroundResource(R.drawable.videotapbefore);
			try {
				_fileOS.close();
				_fileOS1.close();

			} catch (Exception e) {
				Log.e(TAG, "Error: filewriter close - " + e.getLocalizedMessage());
			}
			_fileOS = null;
			_fileOS1 = null;
			return;
		}
	   
	    // TODO record
	    try {
	    	//String fileName = _filePrefix + "." + _previewWidth + "x" + _previewHeight + ".yuv";
	    	//String fileName = _previewWidth + "x" + _previewHeight + ".yuv";
	    	String fileName =	"after"+_previewWidth + "x" + _previewHeight +".yuv";
	    	String fileName1= "before" + _previewWidth + "x" + _previewHeight + ".yuv";
	    	_fileOS = new FileOutputStream(ActivityLauncher.getPathInStorageDirectory(fileName));
	    	_fileOS1 = new FileOutputStream(ActivityLauncher.getPathInStorageDirectory(fileName1));
		    _isRecordingFrames = true;
//		    _buttonRecordFrames.setText(R.string.actionStopRecordingFrames);
//			if (_tvCameraStatus != null)
//				_tvCameraStatus.setText(R.string.textViewStatusRecording);

	    } catch (Exception e) {
//			if (_tvCameraStatus != null)
//				_tvCameraStatus.setText("Output stream failure. " + e.getLocalizedMessage() + "\n");
//	    	Log.e(TAG, "Error in filewriter: " + e.getLocalizedMessage());
	    }
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (_isRecordingFrames) {
			// pipe recording to file
			try {

				//	When piping to nv12 format instead of nv21,
				//  last 1/3 of bytes (UV data) need
				//  to be swapped here as follows

				int length = data.length;
				int expectedLength = (this._previewWidth * this._previewHeight * 3)/2;
				if (length >= expectedLength) {
					for (int i = this._previewWidth * this._previewHeight; i < expectedLength - 1; i += 2) {
						byte tmp = data[i];
						data[i] = data[i+1];
						data[i+1] = tmp;
					}
					if(frame==5)
					{
					_fileOS1.write(data, 0, expectedLength);
					}
					watermarkbytes= hhh.substring(20, 28);
					data = embedwatermark(data,watermarkbytes);
					if(frame==5)
					{
					_fileOS.write(data, 0, expectedLength);
					}
					frame++;
					Log.v("frame","frame:"+frame);
				}
	
			} catch (Exception e) {
		    	Log.e(TAG, "Error in file output stream: " + e.getLocalizedMessage());
//				if (_tvCameraStatus != null)
//					_tvCameraStatus.setText("Failure: " + e.getLocalizedMessage() + "\n");
		    }
		}
	}
}