
public class UsbPort
{
	static int USBDEV_ERROR = 1;
	static int USBDEV_SUCCESS = 1;
	static short USBDEV_SHARED_VENDOR = 0x16C0;
	static short USBDEV_SHARED_PRODUCT = 0x05dc;
	static 
	{
		boolean notFound = false;
		try
		{
			System.loadLibrary("USBPort/Debug/USBDll");
		}
		catch (UnsatisfiedLinkError e) 
		{
			// TODO: handle exception
			notFound = true;
		}
		if (notFound)
		{
			try
			{
				System.loadLibrary("../USBPort/Debug/USBDll");
			}
			catch (UnsatisfiedLinkError e)
			{
				// TODO: handle exception
				System.out.println ("Not Found the Dll file \"USBDll\"");
			}
		}
		
	}
	public native int openUsb(short vid, short pid);
	public native int readUsb(byte[] readByteArray, int len);
	public native int writeUsb(byte[] writeByteArray, int len);
	public native int closeUsb ();
	public UsbPort()
	{
		// TODO Auto-generated constructor stub
	}
}
