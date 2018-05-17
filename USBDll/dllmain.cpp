// dllmain.cpp : 定义 DLL 应用程序的入口点。
#include "stdafx.h"
#include "USBDll.h"
#include "usb.h"
#include "usb_operation.h"


usb_dev_handle      *handle = NULL;

//char my_buffer[64];
/*
 * Class:     UsbPort
 * Method:    openUsb
 * Signature: (SS)I
 */
JNIEXPORT jint JNICALL Java_UsbPort_openUsb
  (JNIEnv *env, jobject cls, jshort vid, jshort pid)
{
	usb_init();
	if(usbOpenDevice(&handle, vid, "www.obdev.at", pid, "LogicAnalyzer") != 0)
	{
        fprintf(stderr, 
				"Could not find USB device \"LogicAnalyzer\" with vid=0x%x pid=0x%x\n", 
				USBDEV_SHARED_VENDOR, 
				USBDEV_SHARED_PRODUCT);
        return 1;
    }
	return 0;
}

/*
 * Class:     UsbPort
 * Method:    readUsb
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_UsbPort_readUsb
  (JNIEnv *env, jobject, jbyteArray byteArray, jint len)
{
	int i, nBytes;
	char * readDeviceBuffer = new char[len]; //remenber delete this
	nBytes = usb_control_msg(handle, 
							USB_TYPE_VENDOR | USB_RECIP_DEVICE | USB_ENDPOINT_IN, 
							PSCMD_BUFFER_READ, 
							0, 
							0, 
							(char *)readDeviceBuffer, 
							len, 
							5000);
	jbyte *buffer = (*env).GetByteArrayElements(byteArray, 0);	
	for (i = 0; i < len; i++)
	{
		buffer[i] = readDeviceBuffer[i];
	}
	(*env).ReleaseByteArrayElements(byteArray, buffer, 0);
	delete readDeviceBuffer;
	return 0;
}
/*
 * Class:     UsbPort
 * Method:    writeUsb
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_UsbPort_writeUsb
  (JNIEnv *env, jobject, jbyteArray byteArray, jint len)
{
	int nBytes;
	jbyte *buffer = (*env).GetByteArrayElements(byteArray, 0);	
    nBytes = usb_control_msg(handle, 
							USB_TYPE_VENDOR | USB_RECIP_DEVICE | USB_ENDPOINT_OUT, 
							PSCMD_BUFFER_WRITE, 
							0,
							0, 
							(char *)buffer, 
							len, 
							5000);
	//printf ("\n nBytes is :%d\n", nBytes);
	return 0;
}
/*
 * Class:     UsbPort
 * Method:    closeUsb
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_UsbPort_closeUsb
  (JNIEnv *, jobject)
{
    usb_close(handle);
	return 0;
}
JNIEXPORT jbyteArray JNICALL Java_Java2dll_cTrim
  (JNIEnv *env, jclass, jbyteArray jary, jint len)
{
	jint i;
	jbyte *buffer = (*env).GetByteArrayElements(jary, 0);
	for (i = 0; i < len; i++)
	{
		printf ("%c", buffer[i]);
		buffer[i] = 'A' + i;
	}
	printf ("\n");
	(*env).ReleaseByteArrayElements(jary, buffer, 0);
	return jary;
}
