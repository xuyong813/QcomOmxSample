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
* @file    ndkOmxEncodeDecode.cpp
* @brief
*
*/
#include <string.h>
#include <math.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <jni.h>
#include "QcomOmxPublicInterface.h"
#include <android/log.h>
#include <semaphore.h>

#include <android/log.h>

#define LOG    "CPP-jni"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG,__VA_ARGS__)

// In some cases a bitrate test may be found to be useful.  In order to perform
// a bitrate test, the following steps must be performed.
//    1. Rebuild the shared library from source if necessary, modifying
//       BitrateTest.cpp searching for BITRATE CONFIGURATION to set custom
//       values for the different bitrates and frame intervals.  If doing so,
//       the newly built shared library needs to be recopied to the lib/ folder.
//    2. Copy BitrateTest.h from the qcomomxsample directory in source
//       to the jni/ directory in the project.
//    3. Set the USE_BITRATE_ENCODING_TEST flag to 1.
//
#define USE_BITRATE_ENCODING_TEST 0

#if USE_BITRATE_ENCODING_TEST
#include "BitrateTest.h"
#endif

#ifdef __cplusplus
extern "C" {
#endif

void 		 *g_encoder = NULL;
encoderParams g_params;
long 		  g_timeStamp;

FILE 		 *f = NULL;


///////////////////////////////////////////////////////////////////////////////////
// Common Capture and Decode Availability Functions
///////////////////////////////////////////////////////////////////////////////////

// hardwareVersionIs8x60
//
//    Tests hardware version.
//
jint Java_com_qcom_iomx_sample_ActivityLauncher_hardwareVersionIs8x60
  	(JNIEnv *env, jclass cls) {
  	
  	int hardwareVersion = getHardwareBaseVersion();
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "HW VERSION = %d", hardwareVersion);
  	int is8x60Hardware = hardwareVersion == kHardwareBase8x60;
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "IS 8x60 = %d", is8x60Hardware);
  	return is8x60Hardware;
}

// encoderIsAvailable
//
//    Creates and initializes a decoder instance.  Once set up, the instance
//    can be fed from any input source.
//
jint Java_com_qcom_iomx_sample_ActivityLauncher_encoderIsAvailable
  	(JNIEnv *env, jclass cls) {
	
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "QUERY ENCODER AVC A");
  	bool isAvailable = omx_component_is_available("OMX.qcom.video.encoder.avc");

	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "QUERY ENCODER AVC B");
  	return isAvailable ? 1 : 0;
}

// decoderIsAvailable
//
//    Creates and initializes a decoder instance.  Once set up, the instance
//    can be fed from any input source.
//
jint Java_com_qcom_iomx_sample_ActivityLauncher_decoderIsAvailable
  	(JNIEnv *env, jclass cls) {
	
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "QUERY DECODER AVC A");
  	bool isAvailable = omx_component_is_available("OMX.qcom.video.decoder.avc");

	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "QUERY DECODER AVC B %d", isAvailable);
  	return isAvailable ? 1 : 0;
}

// decodeYuvIsAvailable
//
//    Returns 1 if the hardware can decode to yuv, 0 otherwise.
//
jint Java_com_qcom_iomx_sample_ActivityLauncher_decodeYuvIsAvailable(JNIEnv *env, jclass cls) {
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "BASE V: %d", getHardwareBaseVersion());
	return getHardwareBaseVersion() == kHardwareBase8x60 ? 0 : 1;
}

///////////////////////////////////////////////////////////////////////////////////
// Capture / Encoder Functions
///////////////////////////////////////////////////////////////////////////////////

// handleOutputEncoded
//
//     Output callback to write an encoded video frame to a file handle
//
int handleOutputEncoded(void *obj, void *buffer, size_t bufferSize, void *iomxBuffer, void *userData) {
	if (bufferSize) {
		int outputSize = bufferSize;
		int size = fwrite(buffer, 1, outputSize, (FILE *)userData);
		if (size != outputSize) {
			// Log error
			__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "FILE OUTPUT FAILED: actual size %d vs expected size %d", size, outputSize);
		}
	}
	return 0;
}

// encoderStart
//
//    Initializes the encoder by passing in the name of a file that will
//    receive the output of the encoder.  The width and height are provided
//    as parameters, while the frame rate, rate control, and bitrate are
//    filled with some default parameters.
//
//    The frame rate, rate control, and bitrate settings should be modified
//    or configured to suit the needs of the application, as these values
//    are used for example purposes only.
//
jstring Java_com_qcom_iomx_sample_capture_ActivityCameraToH264_encoderStart
  	(JNIEnv *env, jclass cls, jstring outfile, jint width, jint height) {

	g_encoder = NULL;
	g_timeStamp = 0;

	g_params.frameWidth = width;
	g_params.frameHeight = height;
	g_params.frameRate = 10;
	g_params.rateControl = 3;
	g_params.bitRate = width * height * 3;
	g_params.codecString = NULL;

	const char *outputFileName = env->GetStringUTFChars(outfile,0);
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "FRAME WxH = %dx%d TO %s", width, height, outputFileName);

	f = fopen(outputFileName, "wb");
	
	int status = 0;
	g_encoder = encoder_create(&status, &g_params);
	if (g_encoder == NULL) {
		return env->NewStringUTF(resultDescription(status));
	}

	// The input semaphore is important; by using it, the procedure for 
	// feeding the component will wait for an available input buffer when
	// the component is busy.
	// 
	omx_setup_input_semaphore(g_encoder);

#if USE_BITRATE_ENCODING_TEST
	// Uses the variable bitrate encoding as given in BitrateTest.cpp.  The
	// bitrate setting in g_params is ignored in this version.
	omx_interface_register_output_callback(g_encoder, handleOutputEncodedToFile, f);

#else
	// Default implementation uses arbitrary bitrate of width * height * 3 kbps
	omx_interface_register_output_callback(g_encoder, handleOutputEncoded, f);

#endif

	// Initialize the component and set to the execution state, waiting
	// for frames from the camera.
	status = omx_interface_init(g_encoder);

	// Return the state
	const char *result = resultDescription(status);
	return env->NewStringUTF(result);
}



// encoderFrame
//




//embed watermark

jbyteArray Java_com_qcom_iomx_sample_capture_ActivityCameraToYuv_embedwatermark
  	(JNIEnv *env, jclass cls, jbyteArray frameData,jstring watermarkData) {

		const char *watermarkBytes = env->GetStringUTFChars(watermarkData,0);
		 LOGI("watermarkBytes");


	jbyte *frameBytes = (jbyte *)env->GetByteArrayElements(frameData, 0);
	//(unsigned short *)frameBytes = (jbyte *)env->GetByteArrayElements(frameData, 0);
		 LOGI("frameBytes");

		 int watermarklen = strlen(watermarkBytes);

	LOGI("嵌入的水印信息如下watermarkBytes：");
	for(int i=0;i<watermarklen;i++)
	{
		LOGF("watermarkBytes[i] :%d",watermarkBytes[i]-48);
	}
	    jsize  framesize = env->GetArrayLength(frameData);
		int framele = (int)framesize;
		int framelen = framele/1.5;
		LOGF("framelen %d",framelen );

		LOGI("嵌入水印之前的yuv数据");
			for(int i=0;i<10;i++)
				{
					LOGF("frameBytes:%d",frameBytes[i]);
				}

		jbyte *e;
		jbyte *p;
		jbyte *w;


	    e=(jbyte *)malloc(framelen*sizeof(jbyte));
	    memset(e,0,framelen*sizeof(jbyte));

	    p=(jbyte *)malloc(framelen*sizeof(jbyte));
	    memset(p,0,framelen*sizeof(jbyte));

	    w=(jbyte *)malloc(framelen*sizeof(jbyte));
	    memset(w,0,framelen*sizeof(jbyte));
	    LOGI("e,p,w");


	        int i,j,cr;
	    	for (i=0;i<framelen;i++)
	    	{
	    		*(e+i)=0;

	    	}
	    	LOGI("e");
	    	cr=	framelen/watermarklen;
	    	LOGF("cr %d ",cr);
	    	for (i=0;i<watermarklen;i++)
	    	{
	    		for (j=cr*i;j<(i+1)*cr;j++)
	    		{
	    			*(e+j)=jbyte(short(watermarkBytes[i])-48);
	    		}
	    	}

	    	for (i=0;i<framelen;i++)

	    	{
	    		if (*(e+i)==0)
	    		{
	    			*(e+i)=-1;
	    		}

	    	}

//	    	for(int i=0;i<5;i++){
//	    		LOGF("e[i] %d ",e[i]);
//	    	}

	    	    int k=0;
	    		int num;
	    		int NUM = 0;

	    		unsigned int seed =5;
	    		srand(seed);
	    		while (k<framelen)
	    		{

	    			*(p+k)=rand()%2;
	    			if(*(p+k)>=0.5)
	    			{
	    				*(p+k)=1;
	    			}
	    			else
	    			{
	    				*(p+k)=-1;
	    			}

	    			k++;
	    		}

	    		FILE* fp=NULL;
	    		if((fp=fopen("/sdcard//embedppp.txt","wb"))!=NULL)
	    		for(i=0;i<framelen;i++)
	    		fprintf(fp,"%d %s",p[i]," ");
	    		fclose(fp);


	    		for(int i =0;i<5;i++)
	    			{
	    			    LOGF("p(i) %d",*(p+i));
	    			}


	    		int m,n;

	        for (m=0;m<framelen;m++)

	    				{
	    					*(w+m)=*(p+m)*(*(e+m));

	    				}
	        for(int i =0;i<5;i++)
	        	    	{
	        	    		LOGF("w(i) %d",*(w+i));
	        	    	}
	        jbyte A =5;
	            	// short A=5;

	            	short frameBytess[framelen];
	            	 short amont=0;
	            	 short sum;
//	            	short frameBytess[framelen];
//	            	for (int i=0;i<framelen;i++)
//	            	{
//	            		*(frameBytess+i)=*(frameBytes+i)&255;
//	            	}
//	            	for(int i=0;i<10;i++){
//	            		LOGF("frameBytess[i] %d",*(frameBytess+i));
//	            	}

	            //	unsigned short frameBytess[framelen];
	                for (i=0;i<framelen;i++)
	            	{

	                	amont=short(frameBytes[i]&255);
	                	        	sum=amont+A*w[i];
	                	        	if(sum>255)
	                	        	{
	                	        		frameBytes[i]=jbyte(255);
	                	        	}
	                	        	else if(sum<0)
	                	        	{
	                	        		frameBytes[i]=0;
	                	        	}
	                	        	else
	                	        	{
	                	        		frameBytes[i]=frameBytes[i]+A*w[i];
	                	        	}
	            	}

	                LOGI("嵌入水印之后的yuv数据");
	                        for (int m=0;m<10;m++)
	                            		{
	                            			LOGF("frameBytes[m] %d",frameBytes[m]);
	                            		}


	                free(e);
	                free(p);
	                free(w);
//	        for(int i =0;i<5;i++)
//	        	    {
//	        	       LOGF("frameBytess(i) %d",*(frameBytess+i));
//	        	     }

//	        FILE* f=NULL;
//	        if((f=fopen("/sdcard//framebytes.txt","wb"))!=NULL)
//	        for(i=0;i<framelen;i++)
//	        fprintf(f,"%d %s",frameBytes[i]," ");
//	        fclose(f);

	    	return frameData;
}







//    Sends a single video frame for encoding processing and embedding watermarkdata
//
jstring Java_com_qcom_iomx_sample_capture_ActivityCameraToH264_encoderFrame
  	(JNIEnv *env, jclass cls, jbyteArray frameData,jstring watermarkData, jobject camera,jint num,jbyte A) {


	 const char *watermarkBytes = env->GetStringUTFChars(watermarkData,0);
	 LOGI("watermarkBytes");
	 jbyte *frameBytes = (jbyte *)env->GetByteArrayElements(frameData, 0);
	 LOGI("frameBytes");

	 int watermarklen = strlen(watermarkBytes);
	 LOGF("watermarklen %d",watermarklen);

	 LOGF("帧数num %d",num);
	 LOGI("嵌入的水印信息如下watermarkBytes：");
	 for(int i=0;i<watermarklen;i++)
	 {
	LOGF("watermarkBytes[i] :%d",short(watermarkBytes[i])-48);
	 }


    jsize  framesize = env->GetArrayLength(frameData);
	int framelen1 = (int)framesize;
	int framelen = framelen1/1.5;
//	LOGF("framelen %d",framelen );

	LOGI("嵌入水印之前的yuv数据");
	for(int i=0;i<10;i++)
		{
			LOGF("frameBytes:%d",frameBytes[i]);
		}

   	jbyte *e;
	jbyte *p;
	jbyte *w;

    e=(jbyte *)malloc(framelen*sizeof(jbyte));
    p=(jbyte *)malloc(framelen*sizeof(jbyte));
    w=(jbyte *)malloc(framelen*sizeof(jbyte));

    memset(e,0,framelen*sizeof(jbyte));
    memset(p,0,framelen*sizeof(jbyte));
    memset(w,0,framelen*sizeof(jbyte));


//     short *e;
//     short *p;
//     short *w;
//
//        e=( short *)malloc(framelen*sizeof( short));
//        p=( short *)malloc(framelen*sizeof( short));
//        w=( short *)malloc(framelen*sizeof( short));
//
//        memset(e,0,framelen*sizeof( short));
//        memset(p,0,framelen*sizeof( short));
//        memset(w,0,framelen*sizeof( short));



     //对水印信息进行扩频
        int i,j,cr;
    	for (i=0;i<framelen;i++)
    	{
    		//*(e+i)=0;
    		e[i]=0;
    	}
    	LOGI("e");

    	cr=	framelen/watermarklen;
    	for (i=0;i<watermarklen;i++)
    	{
    		for (j=cr*i;j<(i+1)*cr;j++)
    		{
    			*(e+j)=jbyte(short(watermarkBytes[i])-48);
    			//*(e+j)=short(watermarkBytes[i])-48;
    		}
    	}

    	for (i=0;i<framelen;i++)

    	{
    		if (*(e+i)==0)
    	//	if(e[i]==0)
    		{
    			*(e+i)=-1;
    			//e[i]=-1;
    		}
    	}

    		//密钥控制的伪随机序列p
    	    int k=0;
    		int NUM = 0;

    		unsigned int seed =5;
    		//LOGF("seed %d",seed);
    		srand(seed);
    		while (k<framelen)
    		{
    			*(p+k)=rand()%2;
    			if(*(p+k)>=0.5)
    			{
    				*(p+k)=1;
    			}
    			else
    			{
    				*(p+k)=-1;
    			}
    			k++;
    		}


   		FILE* fp=NULL;
   		if((fp=fopen("/sdcard//embedextract_p.txt","wb"))!=NULL)
   	    for(i=0;i<framelen;i++)
   	    fprintf(fp,"%d %s",p[i]," ");
	    fclose(fp);




    		LOGI("嵌入时获得的伪随机码");
    		for(int i =0;i<5;i++)
    			{
    			    LOGF("p(i) %d",p[i]);
    			}
    		//得到的水印信息w
    		for (int m=0;m<framelen;m++)
    		{
    			*(w+m)=*(p+m)*(*(e+m));
    			//w[m]=p[m]*e[m];
    		}


    		FILE* fpp=NULL;
    		   		if((fpp=fopen("/sdcard//Cr_matrx.txt","wb"))!=NULL)
    		   	    for(i=0;i<framelen;i++)
    		   	    fprintf(fpp,"%d %s",e[i]," ");
    			    fclose(fpp);
    		FILE* fw=NULL;
    			    if((fw=fopen("/sdcard//w.txt","wb"))!=NULL)
    			    for(i=0;i<framelen;i++)
    			    fprintf(fw,"%d %s",w[i]," ");
    			    fclose(fw);





//    		unsigned short a=(unsigned short) *(frameBytes+0);
//    		unsigned short b=(unsigned short) *(frameBytes+1);
//    		LOGF("*(frameBytes+0) %d",*(frameBytes+0));
//    		LOGF("a %d",a);
//    		LOGF("*(frameBytes+1) %d",*(frameBytes+1));
//    		LOGF("b %d",b);
    	//嵌入强度A
    //	jbyte A =5;
    	// short A=5;

    	short frameBytess[framelen];
    	 short amont=0;
    	 short sum;
        for (i=0;i<framelen;i++)
    	{

//        	if(*(frameBytes+i)>=(-A)&&*(frameBytes+i)<=-1&&*(w+i) == 1)
//        	{
//        		*(frameBytes+i) = -1;
//        		//*(frameBytes+i) =*(frameBytes+i)+0;
//        	}
//
//        	else if(*(frameBytes+i)<=(A-1)&&*(frameBytes+i)>=0&&*(w+i)==-1)
//        	{
//        		*(frameBytes+i) =0;
//        	}
//
//        	else
//        	{
//        		*(frameBytes+i)=jbyte(*(frameBytes+i)+A*(*(w+i)));
//        	}




//
//        	//(unsigned char)frameBytes[i]= (unsigned char)(frameBytes[i]&255);
//        	//frameBytess[i] = (unsigned char)(frameBytes[i]&255);
//        	frameBytess[i]=short(frameBytes[i]&255);
//        	amont= short(frameBytess[i]+A*w[i]);
//        //	frameBytes[i]= frameBytes[i]+A*w[i];
//        	if(amont>=255)
//        	{
//        		frameBytess[i] = 255;
//        	}
//        	else if(amont<=0)
//        	{
//        		frameBytess[i]=0;
//        	}
//        	else
//        	{
//        		frameBytess[i]=amont;
//        	}
//        	frameBytes[i]=jbyte(frameBytess[i]);
//
////        	if(frameBytess[i]>=128)
////        	{
////          		frameBytes[i]=frameBytess[i]-256;
////        	}
////        	else
////        	{
////        		frameBytes[i]=frameBytess[i];
////        	}
// //       	frameBytes[i]=jbyte(frameBytess[i]);





        	amont=(short)(frameBytes[i]&255);
        	sum=(short)(amont+A*w[i]);
        	if(sum>255)
        	{
        		frameBytes[i]=(jbyte)(255);
        	}
        	else if(sum<0)
        	{
        		frameBytes[i]=0;
        	}
        	else
        	{
        		frameBytes[i]=(jbyte)(frameBytes[i]+A*w[i]);
        	}




    	}
        LOGI("嵌入水印之后的yuv数据");
        for (int m=0;m<10;m++)
            		{
            			LOGF("frameBytes[m] %d",frameBytes[m]);
            		}
//        jstring nn="num";
////        jstring path;
//////        = "/sdcard//embeded1"+nn+".txt";
////        strcat( path,"/sdcard//embeded1",nn,".txt");





        if(num==0)
        {
        	FILE* f0=NULL;
        	   		if((f0=fopen("/sdcard//embeded0.txt","wb"))!=NULL)
        	   	    for(i=0;i<framelen;i++)
        	   	    fprintf(f0,"%d %s",frameBytes[i]," ");
        		    fclose(f0);
       }
        if(num==1)
               {
        						FILE* f1=NULL;
        	        	   		if((f1=fopen("/sdcard//embeded1.txt","wb"))!=NULL)
        	        	   	    for(i=0;i<framelen;i++)
        	        	   	    fprintf(f1,"%d %s",frameBytes[i]," ");
        	        		    fclose(f1);
              }
        if(num==2)
                       {
                						FILE* f2=NULL;
                	        	   		if((f2=fopen("/sdcard//embeded2.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f2,"%d %s",frameBytes[i]," ");
                	        		    fclose(f2);
                      }
        if(num==3)
                       {
                						FILE* f3=NULL;
                	        	   		if((f3=fopen("/sdcard//embeded3.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f3,"%d %s",frameBytes[i]," ");
                	        		    fclose(f3);
                      }
        if(num==4)
                       {
                						FILE* f4=NULL;
                	        	   		if((f4=fopen("/sdcard//embeded4.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f4,"%d %s",frameBytes[i]," ");
                	        		    fclose(f4);
                      }
        if(num==5)
                       {
                						FILE* f5=NULL;
                	        	   		if((f5=fopen("/sdcard//embeded5.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f5,"%d %s",frameBytes[i]," ");
                	        		    fclose(f5);
                      }
        if(num==6)
                       {
                						FILE* f6=NULL;
                	        	   		if((f6=fopen("/sdcard//embeded6.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f6,"%d %s",frameBytes[i]," ");
                	        		    fclose(f6);
                      }
        if(num==7)
                       {
                						FILE* f7=NULL;
                	        	   		if((f7=fopen("/sdcard//embeded7.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f7,"%d %s",frameBytes[i]," ");
                	        		    fclose(f7);
                      }
        if(num==8)
                       {
                						FILE* f8=NULL;
                	        	   		if((f8=fopen("/sdcard//embeded8.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f8,"%d %s",frameBytes[i]," ");
                	        		    fclose(f8);
                      }
        if(num==9)
                       {
                						FILE* f9=NULL;
                	        	   		if((f9=fopen("/sdcard//embeded9.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f9,"%d %s",frameBytes[i]," ");
                	        		    fclose(f9);
                      }
        if(num==10)
                       {
                						FILE* f10=NULL;
                	        	   		if((f10=fopen("/sdcard//embeded10.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f10,"%d %s",frameBytes[i]," ");
                	        		    fclose(f10);
                      }
        if(num==11)
                       {
                						FILE* f11=NULL;
                	        	   		if((f11=fopen("/sdcard//embeded11.txt","wb"))!=NULL)
                	        	   	    for(i=0;i<framelen;i++)
                	        	   	    fprintf(f11,"%d %s",frameBytes[i]," ");
                	        		    fclose(f11);
                      }if(num==12)
                      {
               						FILE* f12=NULL;
               	        	   		if((f12=fopen("/sdcard//embeded12.txt","wb"))!=NULL)
               	        	   	    for(i=0;i<framelen;i++)
               	        	   	    fprintf(f12,"%d %s",frameBytes[i]," ");
               	        		    fclose(f12);
                     }if(num==13)
                     {
              						FILE* f13=NULL;
              	        	   		if((f13=fopen("/sdcard//embeded13.txt","wb"))!=NULL)
              	        	   	    for(i=0;i<framelen;i++)
              	        	   	    fprintf(f13,"%d %s",frameBytes[i]," ");
              	        		    fclose(f13);
                    }if(num==14)
                    {
             						FILE* f14=NULL;
             	        	   		if((f14=fopen("/sdcard//embeded14.txt","wb"))!=NULL)
             	        	   	    for(i=0;i<framelen;i++)
             	        	   	    fprintf(f14,"%d %s",frameBytes[i]," ");
             	        		    fclose(f14);
                   }if(num==15)
                   {
            						FILE* f15=NULL;
            	        	   		if((f15=fopen("/sdcard//embeded15.txt","wb"))!=NULL)
            	        	   	    for(i=0;i<framelen;i++)
            	        	   	    fprintf(f15,"%d %s",frameBytes[i]," ");
            	        		    fclose(f15);
                  }if(num==16)
                  {
           						FILE* f16=NULL;
           	        	   		if((f16=fopen("/sdcard//embeded16.txt","wb"))!=NULL)
           	        	   	    for(i=0;i<framelen;i++)
           	        	   	    fprintf(f16,"%d %s",frameBytes[i]," ");
           	        		    fclose(f16);
                 }






        free(e);
        free(p);
        free(w);



	// Directly sends the bytes to the encoder    (unsigned short *)frameBytes
	omx_send_data_frame_to_encoder(g_encoder,(unsigned short *)frameBytes ,
                                   g_params.frameWidth, g_params.frameHeight, g_timeStamp);

    // Sets the timestamp to 1/30 of a frame.  Actual applications
    // should use more accurately measured timestamping.
	g_timeStamp += 1000000/30;
	
	// Free up the byte elements that were referenced
	env->ReleaseByteArrayElements(frameData, frameBytes, 0);
	return env->NewStringUTF("Frame!");
}

// encoderFinish
//
//    Stops and tears down an encoder instance.
//
jstring Java_com_qcom_iomx_sample_capture_ActivityCameraToH264_encoderFinish
  	(JNIEnv *env, jclass cls) {
  	fclose(f);

	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "ABOUT TO FINISH ENCODER");

	int status = 0;
	do {
		// Send the end of input flag until unable to.  This could be better
		// handled by a semaphore instead of a tight while loop.
		status = omx_interface_send_end_of_input_flag(g_encoder, g_timeStamp);

	} while (status != 0);
		
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "ENCODER DE-INIT");

	// Clean up the input semaphore
	omx_teardown_input_semaphore();

	// Deinitialize the component.
	status = omx_interface_deinit(g_encoder);
	if (status == 0) {
		// On success, destroy the component
		__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "ENCODER DESTROY");
		status = omx_interface_destroy(g_encoder);
	}

	// Return status back to the application.
	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "ENCODER STATUS CHECK");
	const char *result = resultDescription(status);

	__android_log_print(ANDROID_LOG_ERROR,"QCOMOMXINTERFACE", "FINISHED");
	return env->NewStringUTF(result);
}

///////////////////////////////////////////////////////////////////////////////////
// File Direct Decode
///////////////////////////////////////////////////////////////////////////////////

jstring Java_com_qcom_iomx_sample_display_ActivityH264Decoder_decodeFile
  	(JNIEnv *env, jclass cls, jstring infile, jstring outfile, jint width, jint height) {
	const char *inputFileName = env->GetStringUTFChars(infile,0);
	const char *outputFileName = env->GetStringUTFChars(outfile,0);

	__android_log_print(ANDROID_LOG_ERROR,"LOG TAG", "DECODING FROM %s TO %s\n", inputFileName, outputFileName);
	const char *result = resultDescription(decode_file(inputFileName, outputFileName, width, height));

	__android_log_print(ANDROID_LOG_ERROR,"LOG TAG", "RESULT: %s\n", result);
	return env->NewStringUTF(result);
}

///////////////////////////////////////////////////////////////////////////////////
// File Direct Extract
///////////////////////////////////////////////////////////////////////////////////

//	int f[9]={-1,-1,-1,-1,8,-1,-1,-1,-1};
short lvbo(int i,int *f, short *frame,int width, int height)
{
	short A;
	short sum[9];
	int j,n;

	A=0;

	if (i==0)
	{
		sum[0]=0;
		sum[1]=0;
		sum[2]=0;
		sum[3]=0;
		sum[4]=*frame;
		LOGF("f[4] %d",f[4]);
		LOGF("sum[4] %d",sum[4]);

		sum[5]=*(frame+1);
		LOGF("f[5] %d",f[5]);
		LOGF("sum[5] %d",sum[5]);

		sum[6]=0;

		sum[7]=*(frame+width);
		LOGF("f[7] %d",f[7]);
		LOGF("sum[7] %d",sum[7]);

		sum[8]=*(frame+width+1);
		LOGF("f[8] %d",f[8]);
		LOGF("sum[8] %d",sum[8]);

		for (j=0;j<9;j++)
		{
			A=A+sum[j]*f[j];
		}
		LOGF("A %d",A);
	}
	else if (i==width-1)
			{
				sum[0]=0;
				sum[1]=0;
				sum[2]=0;
				sum[3]=*(frame+width-2);
				sum[4]=*(frame+width-1);
				sum[5]=0;
				sum[6]=*(frame+2*width-2);
				sum[7]=*(frame+2*width-1);
				sum[8]=0;
				for (j=0;j<9;j++)
				{
					A=A+sum[j]*f[j];
				}
			}
	else if (i==width*(height-1))
			{
				sum[0]=0;
				sum[1]=*(frame+i-width);
				sum[2]=*(frame+i-width+1);
				sum[3]=0;
				sum[4]=*(frame+i);
				sum[5]=*(frame+i+1);
				sum[6]=0;
				sum[7]=0;
				sum[8]=0;
				for (j=0;j<9;j++)
				{
					A=A+sum[j]*f[j];
				}
			}
	else if (i==width*height-1)
			{
				sum[0]=*(frame+i-width-1);
				sum[1]=*(frame+i-width);
				sum[2]=0;
				sum[3]=*(frame+i-1);
				sum[4]=*(frame+i);
				sum[5]=0;
				sum[6]=0;
				sum[7]=0;
				sum[8]=0;
				for (j=0;j<9;j++)
				{
					A=A+sum[j]*f[j];
				}
			}
	else if (i>0&&i<width-1)
			{
				sum[0]=0;
				sum[1]=0;
				sum[2]=0;
				sum[3]=*(frame+i-1);
				sum[4]=*(frame+i);
				sum[5]=*(frame+i+1);
				sum[6]=*(frame+i+width-1);
				sum[7]=*(frame+i+width);
				sum[8]=*(frame+i+width+1);
				for (j=0;j<9;j++)
				{
					A=A+sum[j]*f[j];
				}
			}
	else if (i>(height-1)*width&&i<width*height-1)
			{
				sum[0]=*(frame+i-width-1);
				sum[1]=*(frame+i-width);
				sum[2]=*(frame+i-width+1);
				sum[3]=*(frame+i-1);
				sum[4]=*(frame+i);
				sum[5]=*(frame+i+1);
				sum[6]=0;
				sum[7]=0;
				sum[8]=0;
				for (j=0;j<9;j++)
				{
					A=A+sum[j]*f[j];
				}
			}
	else if (i%width==0&&i!=0&&i!=(height-1)*width)
			{
				sum[0]=0;
				sum[1]=*(frame+i-width);
				sum[2]=*(frame+i-width+1);
				sum[3]=0;
				sum[4]=*(frame+i);
				sum[5]=*(frame+i+1);
				sum[6]=0;
				sum[7]=*(frame+i+width);
				sum[8]=*(frame+i+width+1);
				for (j=0;j<9;j++)
				{
					A=A+sum[j]*f[j];
				}
			}
	else if ((i+1)%width==0&&i!=(width-1)&&i!=width*height-1)
			{
				sum[0]=*(frame+i-width-1);
				sum[1]=*(frame+i-width);
				sum[2]=0;
				sum[3]=*(frame+i-1);
				sum[4]=*(frame+i);
				sum[5]=0;
				sum[6]=*(frame+i+width-1);
				sum[7]=*(frame+i+width);
				sum[8]=0;
				for (j=0;j<9;j++)
				{
					A=A+sum[j]*f[j];
				}
			}
	else
	{
		sum[0]=*(frame+i-width-1);
		sum[1]=*(frame+i-width);
		sum[2]=*(frame+i-width+1);
		sum[3]=*(frame+i-1);
		sum[4]=*(frame+i);
		sum[5]=*(frame+i+1);
		sum[6]=*(frame+i+width-1);
		sum[7]=*(frame+i+width);
		sum[8]=*(frame+i+width+1);
		for (j=0;j<9;j++)
		{
			A=A+sum[j]*f[j];
		}
	}

//	if(A>=255)
//		{
//			A=255;
//		}
//		else if (A<=0)
//		{
//			A=0;
//		}

	return A;
}


//Extract watermark

jbyte Java_com_qcom_iomx_sample_display_ActivityExtraction_extractFile
  	(JNIEnv *env, jclass cls, jbyteArray frame1, jstring outfile, jint width, jint height,jint frame_num) {


		g_params.frameWidth = width;
		LOGF("g_params.frameWidth:%d",g_params.frameWidth );
		g_params.frameHeight = height;
		LOGF("g_params.frameHeight:%d",g_params.frameHeight );
		int len = int(g_params.frameWidth*g_params.frameHeight);
		LOGF("len %d",len);

	jbyte *frameByte = (jbyte *)env->GetByteArrayElements(frame1, 0);

	for(int i =0;i<5;i++)
			{
				LOGF("frameByte[i] :%d",frameByte[i]);
				//LOGF("frameBytess[i] :%d",frameBytess[i]);
			}
	jsize  framesize = env->GetArrayLength(frame1);
	int framele = (int)framesize;
	int framelen = framele/1.5;
	LOGF("framelen %d",framelen );

		 short frameBytess[len];
		for(int i=0;i<framelen;i++)
		{
			frameBytess[i] =frameByte[i]&255;
		}
		for(int i =0;i<5;i++)
		{
			//LOGF("frameByte[i] :%d",frameByte[i]);
			LOGF("frameBytess[i] :%d",frameBytess[i]);
		}


//lvbo
	short *frame0;
	int f[9]={-1,-1,-1,-1,8,-1,-1,-1,-1};
	frame0=(short *)malloc(len*sizeof(short));
	memset(frame0,0,len*sizeof(short));
	for (int j=0;j<len;j++)
		{
			*(frame0+j)=lvbo(j,f,frameBytess,width,height);
		}
	for (int j=0;j<framelen;j++)
		{
			*(frameBytess+j)=*(frame0+j);
//			if(j==0)
//				{
//					LOGF("*(frameBytess+j) %d",*(frameBytess+j));
//				}
		}
	free(frame0);

	LOGI("滤波之后的大小");
	for(int i =0;i<10;i++)
		{
			LOGF("frameBytess[i] :%d",frameBytess[i]);
		}

	const char *outputFileName = env->GetStringUTFChars(outfile,0);

	short *p;
	p=(short *)malloc(len*sizeof(short));
	memset(p,0,len*sizeof(short));

	unsigned int seed =5;
	srand(seed);
	//LOGF("seedd %d",seedd);
		int	i=0;
	while (i<len)
		{
		*(p+i)=rand()%2;
		    if(*(p+i)>=0.5)
		    	{
		    		*(p+i)=1;
		    	}
		    else
		    	{
		    		*(p+i)=-1;
		    	}
		    i++;
		}

	FILE* fp=NULL;
	   		if((fp=fopen("/sdcard//ppp.txt","wb"))!=NULL)
	   	    for(i=0;i<framelen;i++)
	   	    fprintf(fp,"%d %s",p[i]," ");
		    fclose(fp);


	LOGI("提取时获得的伪随机码");
	for(int i =0;i<20;i++)
	{
	    		LOGF("p(i) %d",*(p+i));

	}


		    	int j,cr;
		    	int x = 8;
		    	long int e1[x];				/////////////////
		    	int b[x];				/////////////////
		    	jbyte sum;
		    	cr=(width*height)/x;   //////////////////
		    	LOGF("cr:%d",cr);
		    	sum=0;

		    	for (int i=0;i<x;i++) //////////////////
		    	{
		    		e1[i]=0;

		    	}

		    	for (int i=0;i<x;i++)     //////////////////
		    	{
		    		long int e = 0;
		    		for (j=cr*i;j<(i+1)*cr;j++)
		    			{
		    				e=e+*(frameBytess+j)*p[j];


//		    				 if(i==0)
//		    				    {
//		    				    	LOGF("frameBytess[j] %d",frameBytess[j]);
//
//		    				    }
		    			 }
		    		e1[i] = e;
		    	}
		    	for(int i=0;i<x;i++) //////////////////
		    	{
		    		LOGF("e1:%ld",e1[i]);
		    	}

		    	for (int i=0;i<x;i++)	//////////////////
		    	{
		    		if (e1[i]>0)
		   	    		{
		    				 e1[i]=1;
		    			}
		    		else{
		    				e1[i]=0;
		    			}

		    	}
		    	LOGF("frame_num: %d",frame_num);
		    	LOGI("提取获得的水印信息如下：");
		       for(int i=0;i<x;i++) //////////////////
		    	{
		    		LOGF("e1:%ld",e1[i]);
		    	}
		       free(p);

		       for (j=0;j<x;j++)   ///////////////////
		    		{
		    			b[j]=e1[j];
		    			if (b[j]==1)
		    			{
		    				sum=sum+pow(2,j);
		    				b[j]=0;
		    			}
		    		}
		    		LOGF("sum:%d",sum);

		    return sum;

		    env->ReleaseByteArrayElements(frame1, frameByte, 0);


	}


#ifdef __cplusplus
}
#endif
