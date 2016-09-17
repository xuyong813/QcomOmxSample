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
* @file    ActivityMenuSelector.java
* @brief
*
*/
package com.qcom.iomx.sample;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.qcom.iomx.sample.capture.ActivityCameraToH264;
import com.qcom.iomx.sample.display.ActivityDecodeAndDisplay;
import com.qcom.iomx.sample.display.ActivityExtraction;
import com.qcom.iomx.sample.display.ActivityH264Decoder;
import com.qcom.iomx.sample.display.ActivityH264Viewer;
import com.qcom.iomx.sample.display.ActivityYuvOrRgbViewer;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qcom.iomx.sample.*;
import com.qcom.iomx.sample.display.ActivityExtraction;
public class ActivityMenuSelector extends Activity implements OnItemClickListener {
	private TextView _textViewPrompt;
	private ListView _listView;
	private String _intention;
	private static final String TAG = "MENU SELECTOR";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.menuselector);
        _textViewPrompt = (TextView)findViewById(R.id.textViewMenuPrompt);
        _listView = (ListView)findViewById(R.id.listViewMenu);
        
        Intent intent = getIntent();
        _intention = intent != null ? 
        			 intent.getStringExtra("com.qcom.iomx.sample.fileIntention") : null;

        String fileIntention = _intention;
        
        if (fileIntention != null) {
        	_listView.setScrollContainer(true);
        	_listView.setScrollingCacheEnabled(false);
        	_listView.setOnItemClickListener(this);

            if (fileIntention.equals(ActivityDecodeAndDisplay.DECODER_H264_TO_FILE) ||
            	fileIntention.equals(ActivityDecodeAndDisplay.DECODER_H264_VIEWER)||
            	fileIntention.equals(ActivityLauncher.EXTRACTION_WATERMARKDATA_TO_FILE)) {

            	// For h.264 viewers, only allow files with h264 extension plus files
            	// with a dimension in the filename between two dots, e.g.
            	// 		camframes.320x240.h264
            	//

            	// Don't perform any operations if the directory is not writeable.	
        		if (!ActivityLauncher.validateStorageDirectory()) {
        			_textViewPrompt.setText("Cannot read from storage directory");
        	    	return;
        		}

            	String prompt = getIntent().getStringExtra("com.qcom.iomx.sample.menuPrompt");
                _textViewPrompt.setText(prompt);

            	Vector<String> files = new Vector<String>(); 
            	
            	File dataPath = new File(ActivityLauncher.getStorageDirectory());

            	String[] fileList = dataPath.list();
            	for (int i = 0; i < fileList.length; i++) {
            		String mp4Name = fileList[i];
            		//String fileName = fileList[i];;
            		if (mp4Name.endsWith("mp4") &&
            			new File(dataPath.getAbsolutePath() + "/" + mp4Name).isFile()) 
            		{
            	    	String[] parts = mp4Name.split("\\.");
            	    	//fileName.endsWith("h264");
            	    	for (int j = 0; j < parts.length; j++) {
            	    		if (parts[j].contains("x")) {
            	    			String[] dims = parts[j].split("x");
            	    			if (dims.length == 2) {
                        			files.add(mp4Name);
                    	    		break;
            	    			}
            	    		}
            	    	}
            		}
            	}
            	
            	
            	
//            	for (int i = 0; i < fileList.length; i++) {
//            		String fileName = fileList[i];
//            		if (fileName.endsWith("h264") &&
//            			new File(dataPath.getAbsolutePath() + "/" + fileName).isFile()) {
//            	    	String[] parts = fileName.split("\\.");
//            	    	for (int j = 0; j < parts.length; j++) {
//            	    		if (parts[j].contains("x")) {
//            	    			String[] dims = parts[j].split("x");
//            	    			if (dims.length == 2) {
//                        			files.add(fileName);
//                    	    		break;
//            	    			}
//            	    		}
//            	    	}
//            		}
//            	}
            	
            	
            	

            	if (files.size() > 0) {
            		Object fileArray[] = files.toArray();
            		Arrays.sort(fileArray);
    	        	ArrayAdapter<Object> aAdapter = new ArrayAdapter<Object>(this, R.layout.menuselector_listitem, fileArray);
    	        	_listView.setAdapter(aAdapter);
    	        	_listView.setOnItemClickListener(this);
            	} else {
    	        	_textViewPrompt.setText("No files in storage directory.");
            	}
                
            } else if (fileIntention.equals(ActivityDecodeAndDisplay.DECODER_YUV_VIEWER) ||
            		   fileIntention.equals(ActivityDecodeAndDisplay.DECODER_RGB_VIEWER)) {

            	// For yuv and rgb viewers, only allow files with the respective extension
            	// plus files with a dimension in the filename between two dots, e.g.
            	// 		camframes.320x240.yuv
            	// 		camframes.320x240.rgb
            	//

            	// Don't perform any operations if the directory is not writeable.	
        		if (!ActivityLauncher.validateStorageDirectory()) {
        			_textViewPrompt.setText("Cannot read from storage directory");
        	    	return;
        		}

            	String prompt = getIntent().getStringExtra("com.qcom.iomx.sample.menuPrompt");
                _textViewPrompt.setText(prompt);

            	Vector<String> files = new Vector<String>(); 
            	
            	File dataPath = new File(ActivityLauncher.getStorageDirectory());

            	String[] fileList = dataPath.list();
            	String extension = fileIntention.equals(ActivityDecodeAndDisplay.DECODER_RGB_VIEWER) ? "rgb" : "yuv";
            	for (int i = 0; i < fileList.length; i++) {
            		String fileName = fileList[i];
            		if (fileName.endsWith(extension) &&
            			new File(dataPath.getAbsolutePath() + "/" + fileName).isFile()) {
            	    	String[] parts = fileName.split("\\.");
            	    	for (int j = 0; j < parts.length; j++) {
            	    		if (parts[j].contains("x")) {
            	    			String[] dims = parts[j].split("x");
            	    			if (dims.length == 2) {
                        			files.add(fileName);
                    	    		break;
            	    			}
            	    		}
            	    	}
            		}
            	}

            	if (files.size() > 0) {
            		Object fileArray[] = files.toArray();
            		Arrays.sort(fileArray);
    	        	ArrayAdapter<Object> aAdapter = new ArrayAdapter<Object>(this, R.layout.menuselector_listitem, fileArray);
    	        	_listView.setAdapter(aAdapter);
    	        	_listView.setOnItemClickListener(this);
            	} else {
    	        	_textViewPrompt.setText("No files in storage directory.");
            	}
                
            } else if (fileIntention.equals(ActivityCameraToH264.CAMERA_PREVIEW_SIZE)) {	

            	// For preview size selection, show the different preview sizes available
            	// for the camera.
            	
            	String prompt = getIntent().getStringExtra("com.qcom.iomx.sample.menuPrompt");
                _textViewPrompt.setText(prompt);
	
	            Camera camera = null;
				try {
					camera = Camera.open();

				} catch (Exception e) { 
					Log.e(TAG, "CANNOT OPEN CAMERA: " + e.getLocalizedMessage());
				}

//				if (camera == null) {
//					try {
//						camera = Camera.open(0);
//		
//					} catch (Exception e) { 
//						Log.e(TAG, "CANNOT OPEN CAMERA: " + e.getLocalizedMessage());
//					}
//				}

	            Camera.Parameters params = camera.getParameters();
	        	List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
	            camera.release();

	            if (sizeList != null) {
		            Vector<String> sizeStrings = new Vector<String>();
		            Iterator<Camera.Size> iterator = sizeList.iterator();
		            while (iterator.hasNext()) {
		            	Camera.Size size = iterator.next();
		                sizeStrings.add(size.width + "x" + size.height);
		            }
		            _listView.setAdapter(new ArrayAdapter<Object>(this, R.layout.menuselector_listitem, sizeStrings.toArray()));
	            }
        	}
    	}
	}

	
	
	
	
	
	
	
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		if (_intention.equals(ActivityDecodeAndDisplay.DECODER_H264_VIEWER) ||
			_intention.equals(ActivityDecodeAndDisplay.DECODER_H264_TO_FILE) ||
			_intention.equals(ActivityDecodeAndDisplay.DECODER_YUV_VIEWER) ||
			_intention.equals(ActivityDecodeAndDisplay.DECODER_RGB_VIEWER)) {

			Intent resultIntent = null;
			if (_intention.equals(ActivityDecodeAndDisplay.DECODER_H264_VIEWER)) {
				resultIntent = new Intent(this, ActivityH264Viewer.class);

			} else if (_intention.equals(ActivityDecodeAndDisplay.DECODER_H264_TO_FILE)) {
				resultIntent = new Intent(this, ActivityH264Decoder.class);

			} else {
				resultIntent = new Intent(this, ActivityYuvOrRgbViewer.class);
			}
		

			TextView tView = (TextView)view;
			String fileName = tView.getText().toString();
        	resultIntent.putExtra("com.qcom.iomx.sample.fileName", fileName);
			startActivity(resultIntent);
			return;

		} 
		else if (_intention.equals(ActivityCameraToH264.CAMERA_PREVIEW_SIZE)) {
			TextView tView = (TextView)view;
			String[] dims = tView.getText().toString().split("x");
			int width = Integer.valueOf(dims[0]).intValue();
			int height = Integer.valueOf(dims[1]).intValue();
	
			Intent resultIntent = new Intent();
			resultIntent.putExtra("com.qcom.iomx.sample.width", width);
			resultIntent.putExtra("com.qcom.iomx.sample.height", height);
			setResult(RESULT_OK, resultIntent); 
			finish();
		}
		else if (_intention.equals(ActivityLauncher.EXTRACTION_WATERMARKDATA_TO_FILE)) {
			
			//try{
			
			Intent intent = new Intent(this, ActivityExtraction.class);
			 
			 TextView tView = (TextView)view;
			 String fileName = tView.getText().toString();
	        intent.putExtra("com.qcom.iomx.sample.fileName", fileName);
			startActivity(intent);
			return;
			//}
			//catch(Exception e)
			//{
			//	e.printStackTrace();
			//}
		}
	}
}
