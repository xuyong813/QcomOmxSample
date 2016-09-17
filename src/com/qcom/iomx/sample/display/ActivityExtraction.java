package com.qcom.iomx.sample.display;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//import com.googlecode.javacv.FFmpegFrameGrabber;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;
//import com.googlecode.javacv.Frame;
import com.qcom.iomx.sample.ActivityLauncher;
import com.qcom.iomx.sample.R;
import com.qcom.iomx.sample.R.layout;
import com.qcom.iomx.sample.R.menu;
import com.qcom.iomx.sample.capture.ActivityCameraToH264;
import com.qcom.iomx.sample.capture.MyFileManager;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

//import marvin.image.MarvinImage;
//import marvin.video.MarvinJavaCVAdapter;
//import marvin.video.MarvinVideoInterface;
//import marvin.video.MarvinVideoInterfaceException;

public class ActivityExtraction extends Activity implements OnClickListener{

	public static final String ImageIO = null;
	private int _filePictureWidth;
	private int _filePictureHeight;
	
	public int seedd =0;
	private String _mp4Name = null;
	private String _fileName = null;
	private int num;
	private String xxx;
	
	private String _filePrefix = "camera";
	
	MyNDKExtractionThread _threadExtraction;
	private static final String TAG = "Extraction";
	
	
	Button _buttonExtraction;
	TextView _tvExtractionInput, _tvExtractionOutput, _tvExtractionResult, _tvExtractionHeader;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		
		
		setContentView(R.layout.activity_extraction);
		
		
		_buttonExtraction = (Button)findViewById(R.id.buttonExtraction);
		_buttonExtraction.setOnClickListener(this);

		_tvExtractionHeader = (TextView)findViewById(R.id.extractionHeader);
		
		_tvExtractionInput = (TextView)findViewById(R.id._tvInputFile);
		_tvExtractionInput.setText("Input File: " + _fileName);

        _tvExtractionOutput = (TextView)findViewById(R.id._tvOutputFile);
        _tvExtractionOutput.setText("Output File: " + outputFile());

        _tvExtractionResult = (TextView)findViewById(R.id._tvStatus);
		_tvExtractionResult.setText("Ready to Extract");
		
//		ActivityCameraToH264 AT = new ActivityCameraToH264();
//		num = AT.m;
		MyFileManager MM = new MyFileManager();
		num = MM.mm;
		Log.v("num","num:"+num);
		xxx = MM.hh;
		Log.v("xxx","xxx:"+xxx);
	}

	private String outputFile() {
		// TODO Auto-generated method stub
		return "WatermarkData:"+_filePictureWidth + "x" + _filePictureHeight+".txt";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_extraction, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == _buttonExtraction) {
			_buttonExtraction.setEnabled(false);
			_buttonExtraction.setText("Extracting...");
			 _threadExtraction = new MyNDKExtractionThread();
			_threadExtraction.start();
		}
		
	}
	
	
//	public native byte extractFile(byte[] frame, String outFile, int width, int height,int num,int seedd);
	public native byte extractFile(byte[] frame, String outFile, int width, int height,int num);
	Handler hRefresh = new Handler(){
		@Override
		public void handleMessage(Message msg) {
		switch(msg.what){
		     case 0:
		    	 _tvExtractionResult.setText((String)msg.obj);
		    	 File file = new File(ActivityLauncher.getPathInStorageDirectory(outputFile()));
		    	 if (file.exists() && file.canRead()) {
		    		 //_buttonOpenFileViewer.setEnabled(true);
		    	 } else {
			    	 _tvExtractionResult.append(" File cannot be read.");
		    	 }
		         break;
		     }
		}
	};

	private class MyNDKExtractionThread extends Thread {
//		private MarvinVideoInterface    videoAdapter;
//	    private MarvinImage             videoFrame;
		public void run() {
        	   
		String inputPath = ActivityLauncher.getPathInStorageDirectory(_filePictureWidth + "x" + _filePictureHeight +".dec.yuv");
		//String inputPath = ActivityLauncher.getPathInStorageDirectory( _filePictureWidth + "x" + _filePictureHeight + ".yuv");
       	String outputPath = ActivityLauncher.getPathInStorageDirectory(outputFile());
       	
       	
//        try {
//        	videoAdapter = new MarvinJavaCVAdapter();
//			videoAdapter.loadResource(inputPath);
//			} 
//        catch (MarvinVideoInterfaceException e) 
//        	{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//        	}
//        
//        try{
//            while(true)
//            	{
//                // Request a video frame
//                videoFrame = videoAdapter.getFrame();
//            //    byte[] encoded = Base64.encode(videoFrame);
//           //    String frame = (String)videoFrame;
//            	}
//        	}
//        catch(MarvinVideoInterfaceException e)
//        	{
//        	e.printStackTrace();
//        	}

//       	 char[] buffer=new char[65535]; 
//       	
//       	char outbfr;
//       	int outcnt;
//       	int bytecnt;
       	
/////////////////////////////////主要提取水印代码/////////////////////////////////
       	int framelength = (int)(_filePictureWidth*_filePictureHeight*1.5);
       	
       	Log.v("_filePictureWidth","_filePictureWidth:"+_filePictureWidth);
    	Log.v("_filePictureHeight","_filePictureHeight:"+_filePictureHeight);
    	Log.v("framelength","framelength:"+framelength);
       	
       	int freespace;    
       	byte[] freebuffer = new byte[framelength];
       	Log.v("TAG","byte[] freebuffer");
       	freespace =freebuffer.length;
       	Log.v("TAG","freespace");
       	
       		byte[] buffer = null; 
       		byte[] frame = null;
       		byte[] frame1 = null;
       		String result1 = null;
       		String result = null;
       		
//       		byte[] result1 = null;
       		ByteArrayOutputStream bos =null;
       		
       		FileInputStream fis;
			try {
				fis = new FileInputStream(inputPath);
				Log.v("TAG","fis");
			//	BufferedInputStream	 fis1 = new BufferedInputStream(fis);
			
       		bos=new ByteArrayOutputStream();
       		Log.v("TAG","bos");
		//	BufferedOutputStream bos1=new BufferedOutputStream(bos);
       		//int L =fis.available();
       //		buffer = new byte[fis.available()];
       		
       		int length = 0;
       		Log.v("TAG","--------");
       		frame1 =new byte[framelength];
       		Log.v("TAG","while");
      
       		//提取所有的水印信息
       		byte[] by =new byte[framelength];
       		int NUM=0;
       		//&&(NUM*8<96)
       		while(((length = fis.read(freebuffer,0,framelength))!= -1)){

       			for(int i=0;i<framelength;i++)
            	 {
            		frame1[i]=freebuffer[i];
            	 }
       			Log.v("TAG","result before");
       			
       			//Log.v("frame1","frame1[1]:"+frame1[1]);
       		Log.v("NUM","NUM:"+NUM);
       		byte sum = extractFile(frame1, outputPath, _filePictureWidth, _filePictureHeight,NUM);
       		by[NUM]=sum;
       		NUM++;     	
       		//seedd++;
          	Log.v("TAG","result after");          		
       		} 
       		
       		
       		//只提取一帧的信息
//       		byte[] by =new byte[framelength];
//       		fis.read(freebuffer,0,framelength);
//       		
//       		for(int i=0;i<framelength;i++)
//           	 {
//           		frame1[i]=freebuffer[i];
//           	 }
//       		byte sum=extractFile(frame1, outputPath, _filePictureWidth, _filePictureHeight);
//       		Log.v("sum","sum:"+sum);
      		

       		
       		Log.v("TAG","result ");
       		fis.close();

//       		int len = result.length();
//       		Log.v("len","len:"+len);
//       		byte[] by =new byte[len];
////       		
//       		by = result.getBytes();
//       		Log.i("by","by");
//       		System.out.println(by);
       		

       		
       		File ret = null;  
            BufferedOutputStream stream = null;  
            try {  
                ret = new File(outputPath);  
                FileOutputStream fstream = new FileOutputStream(ret);  
                stream = new BufferedOutputStream(fstream);  
                stream.write(by);  
            } catch (Exception e) {  
                // log.error("helper:get file from byte process error!");  
                e.printStackTrace();  
            } finally {  
                if (stream != null) {  
                    try {  
                        stream.close();  
                    } catch (IOException e) {  
                        // log.error("helper:get file from byte process error!");  
                        e.printStackTrace();  
                    }  
                }  
            }  
       		
       		
       		Log.v("TAG","while");
       	//	frame=bos.toByteArray();//字节流转换为一个 byte数组
       	
       		
       		
       		
       		
       		
//       		Log.v("TAG","frame");
//       		frame1 =new byte[framelength];
//       		for(int i=0;i<framelength;i++)
//         	 {
//         		frame1[i]=freebuffer[i];
//         	 }
//       		
//       		System.out.println(frame1[1]);
//        	System.out.println(frame1[3]);
//        	System.out.println(frame1[5]);
//        	System.out.println(frame1[7]);
//        	System.out.println(frame1[framelength-1]);
//        	
//       		Log.v("TAG","result before");
//       		 result = extractFile(frame1, outputPath, _filePictureWidth, _filePictureHeight);
//       		 
//       		Log.v("TAG","result after");
       		
       		
       		
       		
       		
       		
       		
//       		bos.reset();  
//       		
//       		while((length = fis.read(freebuffer))!= -1){
//           		bos.write(freebuffer, 0, length);
//           		} 
//       		
//       		result = result+result1;
       		
       		
       		
       		
       		
       		//bos.flush();
       		
       		
  /////////////////////////////////////////////////////////////////////////////////////////////     		
//       	 frame=bos.toByteArray();//字节流转换为一个 byte数组
//       		String frame_s = bos1.toString();
//       		frame = frame_s.getBytes();
//       	 int m = frame.length;
//       	 
//       	 int n = m/framelength;
//       	 frame1 =new byte[framelength];
       	  
       	  
       	  

      
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Log.v("TAG","result before");
//		String result = extractFile(frame1, outputPath, _filePictureWidth, _filePictureHeight);
		
//		Log.v("TAG","result after");
//        		//ExtractFile 如何去写
//        	
			Message msg = new Message();
			msg.what = 0;
			msg.obj = result;
			hRefresh.sendMessage(msg);
		}
	}
	
	

}


