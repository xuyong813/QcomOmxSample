package com.qcom.iomx.sample.capture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.qcom.iomx.sample.capture.ActivityCapture;
import com.qcom.iomx.sample.capture.MyAdapter;
import com.qcom.iomx.sample.capture.MyFileManager;
import com.qcom.iomx.sample.R;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyFileManager extends ListActivity{
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/";
	private String curPath = "/";
	private TextView mPath;

	//public static String watermarkbytes = null;
	public static String watermarkbytes = null;
	//public static byte[] watermarkbytes = null;
	public static byte[] watermark = null;
	public static File fileData = null;
	private final static String TAG = "bb";
	public static String filename = null;
	
	public static String hh ; 
	public static int mm;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.fileselect);
		mPath = (TextView) findViewById(R.id.mPath);
		Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
		buttonConfirm.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent data = new Intent(MyFileManager.this, ActivityCapture.class);
				Bundle bundle = new Bundle();
				bundle.putString("file", curPath);
				data.putExtras(bundle);
				setResult(2, data);
				finish();

			}
		});
		Button buttonCancle = (Button) findViewById(R.id.buttonCancle);
		buttonCancle.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		getFileDir(rootPath);
	}

	private void getFileDir(String filePath) {
		mPath.setText(filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filePath);
		File[] files = f.listFiles();

		if (!filePath.equals(rootPath)) {
			items.add("b1");
			paths.add(rootPath);
			items.add("b2");
			paths.add(f.getParent());
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			items.add(file.getName());
			paths.add(file.getPath());
		}

		setListAdapter(new MyAdapter(this, items, paths));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		File fileData = new File(paths.get(position));
		if (fileData.isDirectory()) {
			curPath = paths.get(position);
			getFileDir(paths.get(position));
		} else {
			//openFile(file);
			// filename = fileData.getName();
			 
			 //   System.out.println(filename);
			//readFile(filename);
			  // System.out.println("readFile");
			  // Log.v(TAG,"readFile");
			 //  Log.v(TAG,watermarkData);
			   //System.out.println(readFile(filename));
			
			
			
			String h = "";  
			FileInputStream fis;
			try {
				fis = new FileInputStream(fileData);
				int length = fis.available();   
				byte [] buffer = new byte[length];   
				fis.read(buffer);   
				
				Log.v("buffer[1]","buffer[1]"+buffer[1]);
	      // h = EncodingUtils.getString(buffer, "UTF8"); 
	         
	         
//	     h = EncodingUtils.getString(buffer, "UTF8"); 
//	     watermarkbytes=h.getBytes("UTF8");
	         
	     String ZERO="00000000"; 
	     System.out.println(buffer.length);
	     ////////////////////
	     
	     String ss = "";
	     Log.v("buffer.length","buffer.length:"+buffer.length);
	     for(int i=0;i<buffer.length;i++)
	     {
	    	 Log.v("buffer","buffer[i]"+buffer[i]);
	    	 String s = Integer.toBinaryString(buffer[i]);
	    	 
	    	 if (s.length() > 8) { 
	    		 s = s.substring(s.length() - 8); 
	    		 } 
	    		 else if (s.length() < 8) { 
	    		 s = ZERO.substring(s.length()) + s; 
	    		 }
	     h=h+s;
	    	 
//移位运算，取余
//	    	 for(int j=0;j<8;j++)
//	    	 {
//	    		
//	     byte b = (byte) (( buffer[i]>>>j)%2);
//	     String s = String.valueOf(b);
//	     ss = ss + s;
//	    	 }
//	     hh = hh + ss;
	     }   
	     
	     
	     
	      mm = h.length();
	     hh = h.substring(0, mm);
	     Log.v(" hh"," hh:"+hh);
	     System.out.println(hh); ////////////////////
	    
	    
	     Log.v(" hh.length()"," hh.length():"+hh.length());
	   //  watermark=h.getBytes("UTF8");
	    // System.out.println(watermark); 
	     
	    // byte[] watermark  = hex2byte(h);
	     
//	     byte[] watermarkbytes = hex2byte(h);
	     
	   ///  System.out.println(watermark);
	     
	//     byte[] watermarkbytes = new byte[8];
	     
	//     int m = watermark.length;
	    // Log.v("m","m");
	//     System.out.println(m);///////////////////////
	     
//	     for(int i=0;i<8;i++)
//    	 {
////	    		// for(int j=0;j<8;j++)
////	    		// {
//    			 watermarkbytes[i]= watermark[i];
//    			 System.out.println(watermarkbytes[i]);///////////////////////
//}
	     
	     
	     
	     
	     
	     
//	     ActivityCameraToH264 AT = new ActivityCameraToH264();
//	     int frame_num =AT._frame;
//	     Log.v("AT._frame","AT._frame:"+AT._frame);
//	     Log.v("frame_num","frame_num:"+frame_num);
//	     
//	     if((m-4)%8 ==0)
//	     {
//	     watermarkbytes = h.substring(4+9*frame_num, 12+9*frame_num);
//	     }
//	     else
//	     {
//	    	int n= (m-4)/8;
//	    	 int r = (m-4)%8;
//	    	 
//	    	 for(int i=0;i<r;i++)
//		    	 {
//	    		 watermarkbytes = watermarkbytes+'0';
//		    	 }
//	    	 watermarkbytes = h.substring(4+9*frame_num, 12+9*frame_num);
//	     }
//	     Log.v("watermarkbytes","watermarkbytes:"+watermarkbytes);
//
//	        Log.v(TAG,h);
	  
	     
	     
	     
	     
	         fis.close();   
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			
			
			
			
		}
	}

	
	
	public static byte[] hex2byte(String str) {   
		  if (str == null){  
		   return null;  
		  }  
		    
		  str = str.trim();  
		  int len = str.length();  
		    
		  if (len == 0 || len % 2 == 1){  
		   return null;  
		  }  
		    
		  byte[] b = new byte[len / 2];  
		  try {  
		       for (int i = 0; i < str.length(); i += 2) {  
		            b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();  
		       }  
		       return b;  
		  } catch (Exception e) {  
		   return null;  
		  }  
		}  
	
	
	
	
//	public byte[] readFile(String f) {
//		// TODO Auto-generated method stub
//		
//		 	//File file = new File(f);
//	        InputStream in = null;
//		
//		
//		try {  
//			
//							//in = new FileInputStream(file);
//							//int tempbyte;
//							
//							
//							
//							
//				            //ByteArrayOutputStream byteArray = new ByteArrayOutputStream();  
//				            //byte[] bys= new byte[1024];  
//				           // FileInputStream fis = openFileInput(f);  
//				           // int len;  
//				           // while((len = fis.read(bys))!=-1){  
//				           //     byteArray.write(bys, 0, len);  
//				           // }  
//			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();  
//			
//			byte[] tempbytes = new byte[1024];
//            int byteread = 0;
//             in = new FileInputStream(f);
//           // FileInputStream in = openFileInput(f);
//            //ReadFromFile.showAvailableBytes(in);
//            // 读入多个字节到字节数组中，byteread为一次读入的字节数
//            while ((byteread = in.read(tempbytes)) != -1) {
//            	byteArray.write(tempbytes, 0, byteread);
//			
//					            //String watermarkData1;
//					           // watermarkData1 = new String(byteArray.toByteArray());   
//           byte[] watermarkData = byteArray.toByteArray();
//           
//				           
//				          // byte[] watermarkData =byteArray.read(tempbytes, 0, byteread);
//				          
//				            
//				           // Log.v(TAG,"watermarkData");
//            }
//		}
//         catch (Exception e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();  
//        }
//		
//		 finally {
//	            if (in != null) {
//	                try 
//	                {
//	                    in.close();
//	                } catch (IOException e1) 
//	                {
//	                }
//	            
//		 
//		
//		
//						//		Bundle bundle= new Bundle();
//						//		 bundle.putString("file1", "watermarkdata");
//						//		 Intent intent =new Intent(this,ActivityCameraToH264.class);
//						//		 intent.putExtras(bundle);
//						//		 startActivity(intent);
//	                	
//		
//	}
//	
//	            
//		 }
//						 //System.out.println(watermarkData);
//						
//					
//					//private String StrToBinstr(String str) {
//					//		// TODO Auto-generated method stub
//					//	char[] strChar=str.toCharArray();
//					//    String result="";
//					//    for(int i=0;i<strChar.length;i++){
//					//        result +=Integer.toBinaryString(strChar[i])+ " ";
//					//    }
//					//    return result;
//					//	}
//
//
//		return watermarkData;
//		//return null;
//		}



//	private String ByteArrayToBinaryString(byte[] byteArray) {
//		// TODO Auto-generated method stub
//		 int capacity = byteArray.length * 8;
//         StringBuilder sb = new StringBuilder(capacity);
//         for (int i = 0; i < byteArray.length; i++)
//         {
//             sb.append(byte2BinString(byteArray[i]));
//         }
//         return sb.toString();
//	}
//
//	private Object byte2BinString(byte b) {
//		// TODO Auto-generated method stub
//		 return Convert.toString(b, 2).PadLeft(8, '0');
//	}

	
	
	
	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

}
