import gnu.io.NoSuchPortException;

import java.io.IOException;




public class LogicAnalyzer
{
    private static byte[] readByteArray;
	private static byte[] writeByteArray;

	public static void main(String[] args) throws IOException, NoSuchPortException 
    {
		
    	/*UsbPort usbPort = new UsbPort();
    	if (usbPort.openUsb(UsbPort.USBDEV_SHARED_VENDOR, 
    						UsbPort.USBDEV_SHARED_PRODUCT) == UsbPort.USBDEV_ERROR)
    	{
    		System.out.println ("UsbOpen Error!");
    	}
    	else 
		{
    		System.out.println ("UsbOpen Success!");
    		readByteArray = new byte[64];
    		writeByteArray = new byte[64];
    		for (int i = 0; i < 64; i++)
    		{
    			writeByteArray[i] = 'B';
    		}
    		usbPort.writeUsb(writeByteArray, writeByteArray.length);
    		usbPort.readUsb(readByteArray, readByteArray.length);
            new Gui();
    		usbPort.closeUsb();
		}*/
		 new Gui();
	}
}
