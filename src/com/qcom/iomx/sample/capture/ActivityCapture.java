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
* @file    ActivityCapture.java
* @brief
*
*/
package com.qcom.iomx.sample.capture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.qcom.iomx.sample.capture.ActivityCapture;
import com.qcom.iomx.sample.capture.MyFileManager;
import com.qcom.iomx.sample.ActivityLauncher;
import com.qcom.iomx.sample.ActivityMenuSelector;
import com.qcom.iomx.sample.R;

public class ActivityCapture extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	Button _buttonCameraToH264, _buttonCameraToYuv, _buttonPreviewSize,_buttonAddWatermark;
	
	int _previewWidth = 320;
	int _previewHeight = 240;
	
	public static final int CAMERA_NEW_PREVIEW_SIZE = 1;
	public static final String CAMERA_FILE_VIEWER = "CameraFileViewer";
	public static final String CAMERA_PREVIEW_SIZE = "CameraPreviewSize";
	public static final String CAMERA_PREVIEW_SIZE_PROMPT = "Select Resolution";
	
	public static final int FILE_RESULT_CODE = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);
        
       _buttonCameraToH264 = (Button)findViewById(R.id.buttonCameraToH264);
        _buttonCameraToH264.setOnClickListener(this);
        
        _buttonCameraToYuv = (Button)findViewById(R.id.buttonCameraToYuv);
        _buttonCameraToYuv.setOnClickListener(this);

        _buttonPreviewSize = (Button)findViewById(R.id.buttonSetPreviewSize);
        _buttonPreviewSize.setOnClickListener(this);
        
        _buttonAddWatermark = (Button)findViewById(R.id.buttonAddWatermark);
        _buttonAddWatermark.setOnClickListener(this);

        if (!ActivityLauncher.canEncode) {
        	_buttonCameraToH264.setEnabled(false);
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == _buttonCameraToH264) {
			Intent intent = new Intent(this, ActivityCameraToH264.class);
			//Intent intent = new Intent(this, ActivityCameraToYuv.class);
			intent.putExtra("com.qcom.iomx.sample.previewWidth", _previewWidth);
			intent.putExtra("com.qcom.iomx.sample.previewHeight", _previewHeight);
			startActivity(intent);
			return;
		}

		if (v == _buttonCameraToYuv) {
			Intent intent = new Intent(this, ActivityCameraToYuv.class);
			intent.putExtra("com.qcom.iomx.sample.previewWidth", _previewWidth);
			intent.putExtra("com.qcom.iomx.sample.previewHeight", _previewHeight);
			startActivity(intent);
			return;
		}
		
		if (v == _buttonPreviewSize) {
			Intent intent = new Intent(this, ActivityMenuSelector.class);
			intent.putExtra("com.qcom.iomx.sample.fileIntention", CAMERA_PREVIEW_SIZE);
			intent.putExtra("com.qcom.iomx.sample.menuPrompt", CAMERA_PREVIEW_SIZE_PROMPT);
			startActivityForResult(intent, CAMERA_NEW_PREVIEW_SIZE);
			return;
		}
		//***********************************
		if(v == _buttonAddWatermark)
		{
			Intent intent = new Intent(this,MyFileManager.class);
			intent.putExtra("com.qcom.iomx.sample.previewWidth", _previewWidth);
			intent.putExtra("com.qcom.iomx.sample.previewHeight", _previewHeight);
			startActivity(intent);
			return;
		}
	}

	private void updateDisplay() {
		_buttonPreviewSize.setText("Camera Resolution: " + _previewWidth + " x " +_previewHeight);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
//		if(FILE_RESULT_CODE == requestCode){
//			Bundle bundle = null;
//			if(data!=null&&(bundle=data.getExtras())!=null){
//				textView.setText("选择文件夹为�?+bundle.getString("file"));
//			}
		
		if (resultCode != RESULT_OK) {
			return;
		}

		switch(requestCode) {
			case CAMERA_NEW_PREVIEW_SIZE: {
				_previewWidth = data.getIntExtra("com.qcom.iomx.sample.width", 320);
				_previewHeight = data.getIntExtra("com.qcom.iomx.sample.height", 240);
				updateDisplay();
			}
			
			
			break;
			
			
		}
	}
}
