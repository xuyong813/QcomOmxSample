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
* @file    ActivityH264Decoder.java
* @brief
*
*/
package com.qcom.iomx.sample.display;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.qcom.iomx.sample.ActivityLauncher;
import com.qcom.iomx.sample.R;

public class ActivityH264Decoder extends Activity implements OnClickListener  {	
	private int _filePictureWidth;
	private int _filePictureHeight;
	
	private String _mp4Name = null;
	
	private String _fileName = null;
	
	private static String TAG = "Activity Decoder";
	    
    /** Called when the activity is first created. */
	Button _buttonDecode, _buttonOpenFileViewer;
	TextView _tvDecoderInput, _tvDecoderOutput, _tvDecoderResult, _tvDecoderHeader;

	MyNDKDecodeThread _threadDecode;
//	 //获取当前时间
//     Date now = new Date(System.currentTimeMillis());
//     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//    String nowtime = df.format(now);
    
	private String outputFile() {
		return _filePictureWidth + "x" + _filePictureHeight+ (ActivityLauncher.decodeFormatIsRgb ? ".dec.rgb" : ".dec.yuv");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        _fileName = getIntent().getStringExtra("com.qcom.iomx.sample.fileName");

		_mp4Name = getIntent().getStringExtra("com.qcom.iomx.sample.mp4Name");
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
			
		setContentView(R.layout.decoder);

		_buttonOpenFileViewer = (Button)findViewById(R.id.buttonOpenYuv);
		_buttonOpenFileViewer.setOnClickListener(this);
		_buttonOpenFileViewer.setEnabled(false);
		
		_buttonDecode = (Button)findViewById(R.id.buttonDecode);
		_buttonDecode.setOnClickListener(this);

		_tvDecoderHeader = (TextView)findViewById(R.id.extractionHeader);
		
		_tvDecoderInput = (TextView)findViewById(R.id.tvInputFile);
        _tvDecoderInput.setText("Input File: " + _fileName);

        _tvDecoderOutput = (TextView)findViewById(R.id.tvOutputFile);
        _tvDecoderOutput.setText("Output File: " + outputFile());

        _tvDecoderResult = (TextView)findViewById(R.id.tvStatus);
		_tvDecoderResult.setText("Ready to Decode");
		
		if (!ActivityLauncher.canDecode) {
			_tvDecoderResult.setText("HW-Accel Decoder not available");		
			_buttonDecode.setEnabled(false);
			_buttonOpenFileViewer.setEnabled(false);
		} else {
        	if (ActivityLauncher.decodeFormatIsRgb) {
        		_tvDecoderHeader.setText(R.string.headerH264ToRgb);
        		_buttonOpenFileViewer.setText(R.string.openRgb);
        	} else {
        		_tvDecoderHeader.setText(R.string.headerH264ToYuv);
        		_buttonOpenFileViewer.setText(R.string.openYuv);        		
        	}			
		}
    }

	public void onDestroy() {
		super.onDestroy();

		if (_threadDecode != null) {
			_threadDecode = null;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == _buttonDecode) {
			_buttonDecode.setEnabled(false);
			_tvDecoderResult.setText("Decoding...");
			_threadDecode = new MyNDKDecodeThread();
			_threadDecode.start();
		}
		
		if (v == _buttonOpenFileViewer) {
			Intent resultIntent = new Intent(this, ActivityYuvOrRgbViewer.class);
        	resultIntent.putExtra("com.qcom.iomx.sample.fileName", outputFile());
			startActivity(resultIntent);
		}
	}
	
	public native String decodeFile(String inFile, String outFile, int width, int height);

	Handler hRefresh = new Handler(){
		@Override
		public void handleMessage(Message msg) {
		switch(msg.what){
		     case 0:
		    	 _tvDecoderResult.setText((String)msg.obj);
		    	 File file = new File(ActivityLauncher.getPathInStorageDirectory(outputFile()));
		    	 if (file.exists() && file.canRead()) {
		    		 _buttonOpenFileViewer.setEnabled(true);
		    	 } else {
			    	 _tvDecoderResult.append(" File cannot be read.");
		    	 }
		         break;
		     }
		}
	};

	private class MyNDKDecodeThread extends Thread {
		public void run() {
        	//String inputPath = ActivityLauncher.getPathInStorageDirectory(_fileName);
			String inputPath = ActivityLauncher.getPathInStorageDirectory(_filePictureWidth + "x" + _filePictureHeight +".h264");
        	String outputPath = ActivityLauncher.getPathInStorageDirectory(outputFile());
			String result = decodeFile(inputPath, outputPath, _filePictureWidth, _filePictureHeight);
			Message msg = new Message();
			msg.what = 0;
			msg.obj = result;
			hRefresh.sendMessage(msg);
		}
	}
}
