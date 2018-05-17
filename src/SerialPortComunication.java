import java.io.*;
import java.util.*;
import gnu.io.*;

public class SerialPortComunication implements SerialPortEventListener 
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4595045835329959953L;
	CommPortIdentifier portId;
    CommPortIdentifier portCOM;
    Enumeration<CommPortIdentifier> portListEnum;//枚举类
    public static StringBuffer dataBuffer;
    public static StringBuffer preDataBuffer;
    public static boolean	dataStart = false;
    public static boolean	dataEnd = false;
    public static int startFlag = 0;
    
    
    InputStream inputStream;
    SerialPort serialPort;
    DataOutputStream serialPut=null; 
    boolean busy;
    int sendData = 0;

    StringBuffer buf;
    
    public SerialPortComunication()
	{
		// TODO Auto-generated constructor stub
      	  sendData = 0;
      	  busy = true;
      	  dataBuffer = null;
      	  buf = new StringBuffer();
	}
    public boolean initSerialPort(String portNum) throws NoSuchPortException
	{
	    try 
	    {
	    	/* open方法打开通讯端口，获得一个CommPort对象。
	    	 * 它使程序独占端口。如果端口正被其他应用程序占
	    	 * 用，将使用CommPortOwnershipListener事件机制
	    	 * ，传递一个PORT_OWNERSHIP_REQUESTED事件。每
	    	 * 个端口都关联一个InputStream 何一个OutputStream。
	    	 * 如果端口是用open方法打开的，那么任何的getInputStream
	    	 * 都将返回相同的数据流对象，除非有close被调用。有两个参
	    	 * 数，第一个为应用程序名；第二个参数是在端口打开时阻塞等待的毫秒数。*/
			// TODO: handle exception
	    	System.out.println("打开串口 " + portNum);

	    	portCOM = CommPortIdentifier.getPortIdentifier(portNum);
	        serialPort = (SerialPort) portCOM.open("MyAnalyzer", 2000);
	    } 
	    catch (PortInUseException e)
	    {
	    	System.out.println("该串口被占用，请尝试其他串口！");
	    	return true;
	    }
	    catch (Exception e) 
	    {
			// TODO: handle exception
	    	System.out.println("没有发现串口设备");
	    	return true;
		}
	    try 
	    {
	        inputStream = serialPort.getInputStream();/*获取端口的输入流对象*/
	        OutputStream output = serialPort.getOutputStream(); 
	        serialPut=new DataOutputStream(output); 
	    } 
	    catch (IOException e) {}
	
	    try 
	    {
	        serialPort.addEventListener(this);/*注册一个SerialPortEventListener事件来监听串口事件*/
	    } 
	    catch (TooManyListenersException e) {}
	
	    serialPort.notifyOnDataAvailable(true);/*数据可用*/
	
	    try 
	    {
	        serialPort.setSerialPortParams(115200,
	            SerialPort.DATABITS_8,
	            SerialPort.STOPBITS_1,
	            SerialPort.PARITY_NONE);/*设置串口初始化参数，依次是波特率，数据位，停止位和校验*/
	    } 
	    catch (UnsupportedCommOperationException e) 
	    {
	    	e.printStackTrace();
	    }
		return false;
	}
    public void closeSerial() throws IOException
	{
    	try
    	{
	    	serialPort.close();
	    	inputStream.close();
	    	serialPut.close();
    	}
    	catch (Exception e) 
    	{
			// TODO: handle exception
    		return;
		}
	}    
    //串口事件
    public void serialEvent(SerialPortEvent event) 
    {
          switch(event.getEventType()) 
          {
          case SerialPortEvent.BI:/*Break interrupt,通讯中断*/
          case SerialPortEvent.OE:/*Overrun error，溢位错误*/
          case SerialPortEvent.FE:/*Framing error，传帧错误*/
          case SerialPortEvent.PE:/*Parity error，校验错误*/
          case SerialPortEvent.CD:/*Carrier detect，载波检测*/
          case SerialPortEvent.CTS:/*Clear to send，清除发送*/
          case SerialPortEvent.DSR:/*Data set ready，数据设备就绪*/
          case SerialPortEvent.RI:/*Ring indicator，响铃指示*/
          case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*Output buffer is empty，输出缓冲区清空*/
              break;
          /*Data available at the serial port，端口有可用数据。读到缓冲数组，输出到终端*/
          case SerialPortEvent.DATA_AVAILABLE:
              
              try 
              {
            	  while (inputStream.available() > 0)
            	  {
            		  char temp = (char)inputStream.read();
            		  if (!dataStart)
            		  {
	            		  if ((startFlag == 0) && (temp == 's'))
	            		  {
	            			  startFlag = 1;
	            			  continue;
	            		  }
	            		  else if ((startFlag == 1) && (temp == 't'))
	            		  {
	            			  startFlag = 2;
	            			  continue;
	            		  }
	            		  else if ((startFlag == 2) && (temp == 'a'))
	            		  {
	            			  startFlag = 3;
	            			  continue;
	            		  }
	            		  else if ((startFlag == 3) && (temp == 'r'))
	            		  {
	            			  startFlag = 4;
	            			  continue;
	            		  }
	            		  else if ((startFlag == 4) && (temp == 't'))
	            		  {
	            			  startFlag = 5;
	            			  dataStart = true;
            				  busy = true;
	            			  startFlag = 0;
	            			  continue;
	            		  }
	            		  else
	            		  {
	            			  startFlag = 0;
	            			  continue;
	            		  }
            		  }
            				 
            		  if (dataStart)
            		  {
            			  if (temp == 'e')
            			  {
            				  preDataBuffer = null;
            				  preDataBuffer = dataBuffer;
        	  	      		  dataBuffer = null;
        		  	      	  dataBuffer = buf;
        		  	      	  buf = null;
        		  	      	  buf = new StringBuffer();
            				  dataStart = false;
            				  dataEnd = true;
        	  	      		  busy = false;
        		  	      	  System.out.println("rev done");
            				  break;
            			  }
            			  buf.append(temp);
            		  }
            	  }
//	  	      	  if (buf.length() == Cmd.SAMPLE_DEPTH)
//	  	      	  {
//	  	      		  dataBuffer = null;
//		  	      	  dataBuffer = buf;
//		  	      	  buf = new StringBuffer();
//		  	      	  //System.out.println("done");
//	  	      		  busy = false;
//	  	      	  }
            	  //String string = new String(readBuffer);
            	  //System.out.println(readBuffer);
            	  
	  	      	 /* while ((ch=inputStream.read()) > 0) 
	  	          {
	  	              buf.append((char)ch);  
	  	          }
	  	      	  if (buf.length() == 8192)
	  	      	  {
		  	      	  dataBufferBuffer = buf;
		  	      	  System.out.println("done");
	  	      		  busy = false;
	  	      	  }*/
              } 
              catch (IOException e){}
              break;
          }
      }
    public void sendCmd(int cmd) throws IOException
	{
		serialPut.write(cmd);
	}
    public void sendFunc (int cmd)
    {
    	try
		{
    		serialPut.write(cmd);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

