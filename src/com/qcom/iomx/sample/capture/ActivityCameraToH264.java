/* ======================================================================
*  Copyright 锟?2013 Qualcomm Technologies, Inc. All Rights Reserved.
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
* @file    ActivityCameraToH264.java
* @brief
*
*/
package com.qcom.iomx.sample.capture;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.qcom.iomx.sample.ActivityLauncher;
import com.qcom.iomx.sample.R;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.h264.AvcConfigurationBox;
import com.coremedia.iso.boxes.mdat.SampleList;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.H264TrackImpl;
import com.googlecode.mp4parser.util.Path;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class ActivityCameraToH264 extends Activity implements OnClickListener, PreviewCallback, Callback {
	boolean _isRecordingFrames;
	Button _buttonRecordFrames;
	//TextView _tvCameraStatus;
	
	private MyTask mTask;

	//Code to handle the camera
	private static Camera cameraObj = null;
	private boolean cameraRunning = false;
	
	int NUM = 0;
	public static int _frame = 0;
	byte A;
	public int seed = 0;
	
	private int _previewWidth;
	private int _previewHeight;
	//yuv
	private FileOutputStream _fileOS = null;
	private FileOutputStream _fileOS1 = null;
	private String _filePrefix = "video";
	private String _filePrefix1 = "embed";
	
	private String  watermarkbytes;
	
	private String hhh;
	
	public static int m;
	
	
	 
	//Local variables used in this Activity
	private SurfaceHolder 	 _previewSurfaceHolder = null;
	private SurfaceView 	 _previewSurfaceView = null;
	
	private static final String TAG = "ActivityCameraToH264";
	private static final String TAG1="data";
	public static final String CAMERA_PREVIEW_SIZE = "CameraPreviewSize";
	public static final String CAMERA_PREVIEW_SIZE_PROMPT = "Select Resolution";
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        _buttonRecordFrames = (Button)findViewById(R.id.buttonRecordFrames);
       // _tvCameraStatus = (TextView)findViewById(R.id.tvCameraStatus);
        
        _buttonRecordFrames.setOnClickListener(this);

    	_previewWidth = getIntent().getIntExtra("com.qcom.iomx.sample.previewWidth", 320);
    	_previewHeight = getIntent().getIntExtra("com.qcom.iomx.sample.previewHeight", 240);

    	_previewSurfaceView = (SurfaceView) findViewById(R.id.viewCameraPreview);
        _previewSurfaceView.setZOrderMediaOverlay(true);
        _previewSurfaceHolder = _previewSurfaceView.getHolder();
        _previewSurfaceHolder.addCallback(this);
        _previewSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
        
        if (!ActivityLauncher.canEncode) {
        	_buttonRecordFrames.setEnabled(false);
        	//_tvCameraStatus.setText("HW-Accel encode not available");
        }

    	// Don't perform any operations if the directory is not writeable.	
		if (!ActivityLauncher.validateStorageDirectory()) {
	    	//_tvCameraStatus.setText("Cannot write to storage directory");
	    	_buttonRecordFrames.setEnabled(false);
	    	return;
		}
		
		
		
		MyFileManager MF = new MyFileManager(); 
		//		String f = MyFileManager.filename;
		//	byte[] _file = MF.watermarkbytes;
				
				
				 hhh = MF.hh;
				 Log.v(" hhh"," hhh:"+MF.hh);
				 
				 
		
		
    }

	public native String encoderStart(String outFile, int width, int height);
//	public native String encoderFrame(byte[] frameData,byte[] watermarkData, Camera camera);
	public native String encoderFrame(byte[] frameData,String watermarkData, Camera camera,int frame_num,int A);
//	public native String encoderFrame(byte[] frameData,String watermarkData, Camera camera,int frame_num);
//	public native String encoderFrame(byte[] frameData,String watermarkData, Camera camera);
	public native String encoderFinish();

	NDKThreadEncodeStart _threadEncodeStart;
	NDKThreadEncodeEnd   _threadEncodeEnd;
	
    private void updateRecordingStatus(boolean shouldRecordFrames) {
		// Update the UI and fire off the appropriate thread
		// based on the type of status to update to.
    	//_tvCameraStatus.setText(shouldRecordFrames ? R.string.textViewStatusRecording : R.string.textViewStatusIdle);
    	//_buttonRecordFrames.setText(shouldRecordFrames ? "Initializing..." : "Stopping...");
		_buttonRecordFrames.setEnabled(false);

		if (shouldRecordFrames) {
    		// start encoder
			_buttonRecordFrames.setBackgroundResource(R.drawable.videotapafter);
			_threadEncodeStart = new NDKThreadEncodeStart();
			_threadEncodeStart.run();

    	} else {
    		// stop encoder
    		
    		mTask = new MyTask();  
			mTask.execute("params");
    		_isRecordingFrames = false;
			_threadEncodeEnd = new NDKThreadEncodeEnd();
			_threadEncodeEnd.run();
		
			_buttonRecordFrames.setBackgroundResource(R.drawable.videotapbefore);
			
			//yuv
					
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
		
		try 
		{
			String fileName= _filePrefix + "." + _previewWidth + "x" + _previewHeight + ".yuv";
			String fileName1= _filePrefix1 + "." + _previewWidth + "x" + _previewHeight + ".yuv";
	    	_fileOS = new FileOutputStream(ActivityLauncher.getPathInStorageDirectory(fileName));
	    	_fileOS1 = new FileOutputStream(ActivityLauncher.getPathInStorageDirectory(fileName1));
	    	_isRecordingFrames = true;
		}
		catch (Exception e){
			
		}
    }

	@Override
	public void onClick(View v) {
		if (v == _buttonRecordFrames) {
			updateRecordingStatus(!_isRecordingFrames);
		}
	}

	
	
	
	
    protected void onPause() {
    	super.onPause();
    	stopCamera();
    }


	public void onDestroy() {
		super.onDestroy();
		if (_threadEncodeStart != null) {
			_threadEncodeStart = null;
		}

		if (_threadEncodeEnd != null) {
			_threadEncodeEnd = null;
		}
	}
	
    //Start the camera if it is not already running
	private void startCamera() {
		if(!cameraRunning && cameraObj == null) {
			try {
				cameraObj = Camera.open();

			} catch (Exception e) { 
				Log.e(TAG, "CANNOT OPEN CAMERA: " + e.getLocalizedMessage());
			}

//			if (cameraObj == null) {
//				try {
//					cameraObj = Camera.open(0);
//	
//				} catch (Exception e) { 
//					Log.e(TAG, "CANNOT OPEN CAMERA: " + e.getLocalizedMessage());
//				}
//			}
		}
	}
	
	//Close down the camera
	private void stopCamera() {
		if(cameraObj != null) {
			cameraObj.setPreviewCallback(null);
			cameraObj.stopPreview();
	    	cameraObj.release();
    	}
		cameraObj = null;
		cameraRunning = false;
	}
	
	//Set up the parameters for the camera and show preview
	private boolean setupCamera(SurfaceHolder holder) {
		boolean ret = true;
		try{
			Camera.Parameters params = cameraObj.getParameters();
			if(cameraObj == null || alreadyCreated) {
				Log.v(TAG, "CAMERA SETUP ABORT");
				return true;
			}
			
			if(cameraRunning) {
				cameraObj.stopPreview();
			}
			
		    cameraObj.setDisplayOrientation(90);

			params.setPreviewFormat(ImageFormat.NV21);
			params.setPictureFormat(PixelFormat.JPEG);
			params.setPreviewSize(_previewWidth, _previewHeight);
			params.set("orientation", "portrait");
			params.setRotation(90);
			cameraObj.setParameters(params);

			cameraObj.setPreviewDisplay(holder);
			cameraObj.setPreviewCallback(this);
			cameraObj.startPreview();

			cameraRunning = true;
			alreadyCreated = true;

		} catch (Exception e) {

			Log.e(TAG, "CAMERA SETUP FAIL!");
			e.printStackTrace();
			stopCamera();
			ret = false;
		}
		return ret;
	}

    private boolean alreadyCreated = false;
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (holder == _previewSurfaceHolder) {
			if(!setupCamera(holder)) {
				//if the camera has not set up properly exit
				finish();
			}
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == _previewSurfaceHolder) {
			startCamera();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (holder == _previewSurfaceHolder) {
			stopCamera();
		}
	}

	Handler hRefresh = new Handler(){
		@Override
		public void handleMessage(Message msg) {
		switch(msg.what){
		     case 0:
		    	//_tvCameraStatus.setText("Record: " + (String)msg.obj);
		    	String message = (String)msg.obj;
		    	if (message.equals("No errors.")) {
			    //	_buttonRecordFrames.setText(R.string.actionStopRecordingFrames);
					_isRecordingFrames = true;		    		
		    	} else {
			    	//_buttonRecordFrames.setText(R.string.actionRecordFrames);		    		
		    	}
				_buttonRecordFrames.setEnabled(true);
		        break;

		     case 1:
			 //   _tvCameraStatus.setText("Finish: " + (String)msg.obj);
				_buttonRecordFrames.setEnabled(true);
		    //	_buttonRecordFrames.setText(R.string.actionRecordFrames);
		        break;
		
		     case 2: {
		    	ByteBuffer byteBuffer = (ByteBuffer)msg.obj;
		    	byte[] bytes = byteBuffer.array(); 
		    	
		    	ByteBuffer byteBuffer1 = (ByteBuffer)msg.obj;
		    	String Str = byteBuffer.toString(); 
		 //   byte[] Str =	byteBuffer.array();
		    	encoderFrame(bytes,Str, cameraObj,_frame,A);
		    	_frame++;
		    	//seed++;
		     }	break;
			}
		}
	};

	
	
	 
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (_isRecordingFrames) {
			// If piping to nv12 format instead of nv21, last 1/3 of bytes (UV data) need
			// to be swapped here
			
					
//	//一张图片替换每一帧		
//			File image = new File("./sdcard/lena.jpg");
//
//			FileInputStream fis;
//			int length = 0;
//			try {
//				fis = new FileInputStream(image);
//				
//				 length = fis.available();   
//				 data = new byte[length];   
//				fis.read(data);
//				fis.close();
//				
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//				   
//			Log.v(" length","length"+length);
			
				
		int length = data.length;
		
			
//			 m = hhh.length();
//			 Log.v(" hhh.length()"," hhh.length():"+m);
//			 
//			 if(8+8*_frame<m) 
//			 {	 
//			 if((m)%8 ==0)
//			     {
//				 A=5;
//			    watermarkbytes = hhh.substring(8*_frame, 8+8*_frame);
//			     }
//			 else
//			     {
//			    	 int n= (m)/8;
//			    	 int r = (m)%8;
//			    	 
//			    	 for(int i=0;i<r;i++)
//				    	 {
//			    		 watermarkbytes = watermarkbytes+'0';
//				    	 }
//			    	 A=5;
//			    	 watermarkbytes = hhh.substring(8*_frame, 8+8*_frame);
//			     }
//			 }
//			 
//			 else if(8+8*_frame>m)
//			 {
//				A=0;
//				 watermarkbytes = " ";
//				 //watermarkbytes =null;
//			 }
//			 
//		  //   Log.v("_frame","_frame:"+_frame);
//			 Log.v("watermarkbytes","watermarkbytes:"+watermarkbytes);
		
			
		//	 Log.v("_file","_file:"+_file);
		
		watermarkbytes = hhh.substring(0, 8);
		A=5;
		//watermarkbytes="11111111";
			int expectedLength = (this._previewWidth * this._previewHeight * 3)/2;//why????
			if (length >= expectedLength) {
				for (int i = this._previewWidth * this._previewHeight; i < expectedLength - 1; i += 2) {
					byte tmp = data[i];
					data[i] = data[i+1];
					data[i+1] = tmp;
				}
			}
			
			
			
			try {
				
					_fileOS.write(data, 0, expectedLength);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			//	 Log.v(TAG1,data[1]);
             //   System.out.println(data[1]);
			int width = this._previewWidth;
			int height = this._previewHeight;
			//encoderFrame(data,watermarkbytes, camera,_frame);
			Log.v("_frame","_frame:"+_frame);
			encoderFrame(data,watermarkbytes, camera,_frame,A);
			_frame++;

			
		}
		//Log.v("_frame","_frame:"+_frame);
	}

	private class NDKThreadEncodeStart extends Thread {
		public void run() {
			try { Thread.sleep(1000); } catch (Exception e) {};
    		// start encoder
			Log.v(TAG, "Encoder Start at " + _previewWidth + " x " + _previewHeight);
			String fileName = "/camframes." + _previewWidth + "x" + _previewHeight + ".h264";
			String filePath = ActivityLauncher.getPathInStorageDirectory(fileName);	
    		String result = encoderStart(filePath, _previewWidth, _previewHeight);
			Message msg = new Message();
			msg.what = 0;
			msg.obj = result;
			hRefresh.sendMessage(msg);
			Log.v(TAG, "ENCODER START");
    	}
	}

	private class MyTask extends AsyncTask<String, Integer, String>{
		
		@Override
		protected String doInBackground(String... arg0) {
			
			H264TrackImpl h264Track = null;
	 		try {
	 			h264Track = new H264TrackImpl(new FileDataSourceImpl("/storage/sdcard0/qcom"+"/camframes." + _previewWidth + "x" + _previewHeight + ".h264"));
	 			Log.v(TAG, "create the h264Track");
	 		} catch (FileNotFoundException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		} catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	 	        //AACTrackImpl aacTrack = new AACTrackImpl(new FileInputStream("/home/sannies2/Downloads/lv.aac").getChannel());
	 	        Movie m = new Movie();
	 	        m.addTrack(h264Track);
	 	        Log.v(TAG, "add track to the m");
	 	        
	 	        //获取当前时间
	 	       Date now = new Date(System.currentTimeMillis());
	 	       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	 	      String nowtime = df.format(now);
	 	      
	 	   
	 	        
	 	      //  File f =new File("/storage/sdcard0/qcom/" + _previewWidth + "x" + _previewHeight+nowtime+".mp4");
	 	       File f =new File("/storage/sdcard0/qcom/" + _previewWidth + "x" + _previewHeight+".mp4");
	 	        //m.addTrack(aacTrack);
	 	        
	 	        Container out = new DefaultMp4Builder().build(m);
	             Log.v(TAG, "create out");
	 			try {
	 				Log.v(TAG, "fos");
	 				FileOutputStream fos = new FileOutputStream(f);
	 				Log.v(TAG, "write the mp4 file");
	 				FileChannel fc = fos.getChannel();
	 				out.writeContainer(fc);
	 				fos.close();


	 			} catch (FileNotFoundException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 			} catch (IOException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 			}
			
	 			
	 			
	 			
	 			
	 			
	 			
	 			//反复用
	 			
	 			
	 			IsoFile isoFile;
	 			try {
	 			    Log.v(TAG, "oncreate");

	 				//isoFile = new IsoFile("/storage/sdcard0/qcom/h264_output.mp4");
	 			    isoFile = new IsoFile("/storage/sdcard0/qcom/"+ _previewWidth + "x" + _previewHeight +".mp4");
	 			    
	 				Log.v(TAG, "ouput.mp4");
	 				TrackBox trackBox = (TrackBox) Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/../../../../../");
	 			    SampleList sl = new SampleList(trackBox);
	 			    //FileChannel fc = new FileOutputStream("/storage/sdcard0/qcom/ifout.h264").getChannel();
	 			    FileChannel fc = new FileOutputStream("/storage/sdcard0/qcom/"+_previewWidth + "x" + _previewHeight +".h264").getChannel();
	 			    Log.v(TAG, "ifout.h264");
	 			    ByteBuffer separator = ByteBuffer.wrap(new byte[]{0, 0, 0, 1});

	 		        fc.write((ByteBuffer) separator.rewind());
	 		        // Write SPS
	 		        fc.write(ByteBuffer.wrap(
	 		                ((AvcConfigurationBox) Path.getPath(trackBox, "mdia/minf/stbl/stsd/avc1/avcC")
	 		                ).getSequenceParameterSets().get(0)));
	 		        
	 			    Log.v(TAG, "SPS.h264");

	 		        // Warning:
	 		        // There might be more than one SPS (I've never seen that but it is possible)

	 		        fc.write((ByteBuffer) separator.rewind());
	 		        // Write PPS
	 		        fc.write(ByteBuffer.wrap(
	 		                ((AvcConfigurationBox) Path.getPath(trackBox, "mdia/minf/stbl/stsd/avc1/avcC")
	 		                ).getPictureParameterSets().get(0)));
	 		        
	 			    Log.v(TAG, "PPS.h264");

	 		        // Warning:
	 		        // There might be more than one PPS (I've never seen that but it is possible)

	 		        int lengthSize = ((AvcConfigurationBox) Path.getPath(trackBox, "mdia/minf/stbl/stsd/avc1/avcC")).getLengthSizeMinusOne() + 1;
	 			    Log.v(TAG, "lengthSize"+lengthSize);
	 			    for (Sample sample : sl) {
	 			    	
	 		            ByteBuffer bb = sample.asByteBuffer();
	 		            bb.rewind();
	 		            Log.v(TAG, "bb.remaining() "+bb.remaining() );
	 		            while (bb.remaining() > 0) {
	 		            	Log.v(TAG, "bb.remaining");
	 		                int length = (int) IsoTypeReaderVariable.read(bb, lengthSize);
	 		                fc.write((ByteBuffer) separator.rewind());
	 		                fc.write((ByteBuffer) bb.slice().limit(length));
	 		                bb.position(bb.position() + length);
	 		            }


	 		        }
	 		        fc.close();
	 			} catch (IOException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 			}
	 			
	 			
	 			
	 			
			
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	private class NDKThreadEncodeEnd extends Thread {
		public void run() {
			// stop encoder
			try { Thread.sleep(1000); } catch (Exception e) {};
	    	
			String result = encoderFinish();	    		   
			Message msg = new Message();
			msg.what = 1;
			msg.obj = result;
			hRefresh.sendMessage(msg);
			
		 	
	 			
			Log.v(TAG, "ENCODER END");
		}
	}
}
