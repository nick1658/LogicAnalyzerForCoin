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
    Enumeration<CommPortIdentifier> portListEnum;//ö����
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
	    	/* open������ͨѶ�˿ڣ����һ��CommPort����
	    	 * ��ʹ�����ռ�˿ڡ�����˿���������Ӧ�ó���ռ
	    	 * �ã���ʹ��CommPortOwnershipListener�¼�����
	    	 * ������һ��PORT_OWNERSHIP_REQUESTED�¼���ÿ
	    	 * ���˿ڶ�����һ��InputStream ��һ��OutputStream��
	    	 * ����˿�����open�����򿪵ģ���ô�κε�getInputStream
	    	 * ����������ͬ�����������󣬳�����close�����á���������
	    	 * ������һ��ΪӦ�ó��������ڶ����������ڶ˿ڴ�ʱ�����ȴ��ĺ�������*/
			// TODO: handle exception
	    	System.out.println("�򿪴��� " + portNum);

	    	portCOM = CommPortIdentifier.getPortIdentifier(portNum);
	        serialPort = (SerialPort) portCOM.open("MyAnalyzer", 2000);
	    } 
	    catch (PortInUseException e)
	    {
	    	System.out.println("�ô��ڱ�ռ�ã��볢���������ڣ�");
	    	return true;
	    }
	    catch (Exception e) 
	    {
			// TODO: handle exception
	    	System.out.println("û�з��ִ����豸");
	    	return true;
		}
	    try 
	    {
	        inputStream = serialPort.getInputStream();/*��ȡ�˿ڵ�����������*/
	        OutputStream output = serialPort.getOutputStream(); 
	        serialPut=new DataOutputStream(output); 
	    } 
	    catch (IOException e) {}
	
	    try 
	    {
	        serialPort.addEventListener(this);/*ע��һ��SerialPortEventListener�¼������������¼�*/
	    } 
	    catch (TooManyListenersException e) {}
	
	    serialPort.notifyOnDataAvailable(true);/*���ݿ���*/
	
	    try 
	    {
	        serialPort.setSerialPortParams(115200,
	            SerialPort.DATABITS_8,
	            SerialPort.STOPBITS_1,
	            SerialPort.PARITY_NONE);/*���ô��ڳ�ʼ�������������ǲ����ʣ�����λ��ֹͣλ��У��*/
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
    //�����¼�
    public void serialEvent(SerialPortEvent event) 
    {
          switch(event.getEventType()) 
          {
          case SerialPortEvent.BI:/*Break interrupt,ͨѶ�ж�*/
          case SerialPortEvent.OE:/*Overrun error����λ����*/
          case SerialPortEvent.FE:/*Framing error����֡����*/
          case SerialPortEvent.PE:/*Parity error��У�����*/
          case SerialPortEvent.CD:/*Carrier detect���ز����*/
          case SerialPortEvent.CTS:/*Clear to send���������*/
          case SerialPortEvent.DSR:/*Data set ready�������豸����*/
          case SerialPortEvent.RI:/*Ring indicator������ָʾ*/
          case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*Output buffer is empty��������������*/
              break;
          /*Data available at the serial port���˿��п������ݡ������������飬������ն�*/
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

