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
* @file    ActivityLauncher.java
* @brief
*
*/
package com.qcom.iomx.sample;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.qcom.iomx.sample.capture.ActivityCapture;
import com.qcom.iomx.sample.display.ActivityDecodeAndDisplay;

public class ActivityLauncher extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	Button _buttonCapture, _buttonDecodeAndDisplay,_buttonExtraction, _buttonExit;
	
	public static boolean canEncode = false;
	public static boolean canDecode = false;
	public static boolean canRender = false;
	public static boolean decodeFormatIsRgb = false;
	public static final String EXTRACTION_WATERMARKDATA_TO_FILE = "Extraction Watermarkdata to File";

	private static final String TAG = "QCOM SAMPLE LAUNCHER";	

	public static final int CAMERA_NEW_PREVIEW_SIZE = 1;
	
	// Configure this to a different location if you do not want to use the sdcard
	private static String storagePath = Environment.getExternalStorageDirectory().getPath() + "/qcom";
	
	public static String getPathInStorageDirectory(String path) {
		return storagePath + "/" + path;
	}
	
	public static String getStorageDirectory() {
		return storagePath;
	}

	public static boolean validateStorageDirectory() {
		File dir = new File(getStorageDirectory());
		if (dir.exists() || dir.mkdir()) {
        	return true;
		}
        return false;
	}
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        _buttonCapture = (Button)findViewById(R.id.buttonCapture);
        _buttonCapture.setOnClickListener(this);
        
        _buttonDecodeAndDisplay = (Button)findViewById(R.id.buttonDecodeAndDisplay);
        _buttonDecodeAndDisplay.setOnClickListener(this);
        
        _buttonExtraction = (Button)findViewById(R.id.buttonExtraction);
        _buttonExtraction.setOnClickListener(this);
        
        _buttonExit = (Button)findViewById(R.id.buttonExit);
        _buttonExit.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == _buttonCapture) {
			Intent intent = new Intent(this, ActivityCapture.class);
			startActivity(intent);
			return;
		}

		if (v == _buttonDecodeAndDisplay) {
			Intent intent = new Intent(this, ActivityDecodeAndDisplay.class);
			startActivity(intent);
			return;
		}

		if (v ==  _buttonExtraction){
			Intent intent = new Intent(this, ActivityMenuSelector.class);
			intent.putExtra("com.qcom.iomx.sample.fileIntention", EXTRACTION_WATERMARKDATA_TO_FILE);
			intent.putExtra("com.qcom.iomx.sample.menuPrompt", "Select file to extraction");
			startActivity(intent);
			return;
		}
		
		
		if (v == _buttonExit) {
			int pid = android.os.Process.myPid(); 
			android.os.Process.killProcess(pid); 
			return;
		}
	}

    public static native int encoderIsAvailable();
	public static native int decoderIsAvailable();
	public static native int decodeYuvIsAvailable();
	public static native int hardwareVersionIs8x60();

    
	// Load up the appropriate libraries.  Note that this is a basic
	// unverified method of testing the software for OS version, and
	// should be replaced with more a accurate implementation for
	// commercial product.
	//
	// Applications should also fallback to a software solution if
	// finding that the OS version does not have a matching library.
	//
	// If only targeting one OS, the other unsupported OSes should
	// be commented out of this code.
	//
	static {
		switch(android.os.Build.VERSION.SDK_INT) {
			case 8:  // FY
			case 9:  // GB
			case 10: // GB
				Log.e(TAG, "BUILD VERSION = Compatible " + android.os.Build.VERSION.SDK_INT);
				try {
					System.loadLibrary("qcomomxsample");
					System.loadLibrary("qcomomx4ndk_encode_decode");

					canEncode = true;
					canDecode = decodeYuvIsAvailable() == 1;
					Log.e(TAG, "CAN DECODE ON FY / GB: " + canDecode);

					try {
						if (android.os.Build.VERSION.SDK_INT == 8) { // FROYO
							System.loadLibrary("qcomrenderer_froyo");
							System.loadLibrary("qcomomx4ndk_display_froyo");

						} else { // GB 9 or 10
							System.loadLibrary("qcomrenderer_gb");
							System.loadLibrary("qcomomx4ndk_display_gb");
						}
						canRender = true;

					} catch (UnsatisfiedLinkError e) {
						canRender = false;
					}

				} catch (UnsatisfiedLinkError e) {
					canEncode = canDecode = false;
				}
				break;

			case 11: // HC
			case 12: // HC 12
			case 13:
				Log.e(TAG, "BUILD VERSION = Compatible " + android.os.Build.VERSION.SDK_INT);
				try {
					System.loadLibrary("qcomomxsample_hc");
					System.loadLibrary("qcomomx4ndk_encode_decode_hc");
	
					canEncode = canDecode = true;
					decodeFormatIsRgb = hardwareVersionIs8x60() == 1;
	
				} catch (UnsatisfiedLinkError e) {
					canEncode = canDecode = false;
				} break;
			
			case 14: // ICS
			case 15: // ICS
				Log.e(TAG, "BUILD VERSION = Compatible " + android.os.Build.VERSION.SDK_INT);
				try {
					System.loadLibrary("qcomomxsample_ics");
					System.loadLibrary("qcomomx4ndk_encode_decode_ics");
	
					canEncode = canDecode = true;
					decodeFormatIsRgb = hardwareVersionIs8x60() == 1;
	
				} catch (UnsatisfiedLinkError e) {
					canEncode = canDecode = false;
				} break;
				
			case 16: // JB
			case 17: // JB
				Log.e(TAG, "BUILD VERSION = Compatible " + android.os.Build.VERSION.SDK_INT);
				try {
					System.loadLibrary("qcomomxsample_jb");
					System.loadLibrary("qcomomx4ndk_encode_decode_jb");
	
					canEncode = canDecode = true;
					decodeFormatIsRgb = hardwareVersionIs8x60() == 1;
	
				} catch (UnsatisfiedLinkError e) {
					canEncode = canDecode = false;
				} break;

			default:
				Log.e(TAG, "BUILD VERSION Error: Build version not found! " + android.os.Build.VERSION.SDK_INT);
				break;
		}
	}
}