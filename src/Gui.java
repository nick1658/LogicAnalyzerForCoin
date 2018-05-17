import gnu.io.NoSuchPortException;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;  
import javax.imageio.ImageReadParam;  
import javax.imageio.ImageReader;  
import javax.imageio.metadata.IIOMetadata;  
import javax.imageio.stream.ImageInputStream;  
  
import com.sun.image.codec.jpeg.JPEGCodec;  
import com.sun.image.codec.jpeg.JPEGImageEncoder;  



public class Gui extends JFrame
{

    /**
	 * 
	 */

    final static String LOOKANDFEEL = "System"; 
	private static final long serialVersionUID = 1L;
	Color color = new Color(0, 255, 0);
    Color colorBackGround = new Color(20, 30, 25);
    private DefaultMutableTreeNode bus0Tree    =   new  DefaultMutableTreeNode( "Bus0" ,  true );
    private DefaultMutableTreeNode bus1Tree    =   new  DefaultMutableTreeNode( "Bus1" ,  true );
    
	private JTree bus0RootTree;
	private JTree bus1RootTree;
	
	private DefaultMutableTreeNode bus00  =   new  DefaultMutableTreeNode ( "Bus0[0]" );
	private DefaultMutableTreeNode bus01  =   new  DefaultMutableTreeNode ( "Bus0[1]" );
	private DefaultMutableTreeNode bus02  =   new  DefaultMutableTreeNode ( "Bus0[2]" );
	private DefaultMutableTreeNode bus03  =   new  DefaultMutableTreeNode ( "Bus0[3]" );
	private DefaultMutableTreeNode bus04  =   new  DefaultMutableTreeNode ( "Bus0[4]" );
	private DefaultMutableTreeNode bus05  =   new  DefaultMutableTreeNode ( "Bus0[5]" );
	private DefaultMutableTreeNode bus06  =   new  DefaultMutableTreeNode ( "Bus0[6]" );
	private DefaultMutableTreeNode bus07  =   new  DefaultMutableTreeNode ( "Bus0[7]" );

	private DefaultMutableTreeNode bus10  =   new  DefaultMutableTreeNode ( "Bus1[0]" );
	private DefaultMutableTreeNode bus11  =   new  DefaultMutableTreeNode ( "Bus1[1]" );
	private DefaultMutableTreeNode bus12  =   new  DefaultMutableTreeNode ( "Bus1[2]" );
	private DefaultMutableTreeNode bus13  =   new  DefaultMutableTreeNode ( "Bus1[3]" );
	private DefaultMutableTreeNode bus14  =   new  DefaultMutableTreeNode ( "Bus1[4]" );
	private DefaultMutableTreeNode bus15  =   new  DefaultMutableTreeNode ( "Bus1[5]" );
	private DefaultMutableTreeNode bus16  =   new  DefaultMutableTreeNode ( "Bus1[6]" );
	private DefaultMutableTreeNode bus17  =   new  DefaultMutableTreeNode ( "Bus1[7]" );
	
    private SignalPanel bus0SignalPanel = new SignalPanel ();
    private SignalPanel bus1SignalPanel = new SignalPanel ();
    private TimePanel timePanel = new TimePanel();
    
    private Container contentPane;
    
    private JPanel busPanel;   
    
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenu menuOp;
    private JMenuItem menuItemNew;
    private JMenuItem menuItemOpen;
    private JMenuItem menuItemSave;
    private JMenuItem menuItemOpenSerial; 
    private JMenuItem menuItemCloseSerial; 
    private JMenuItem menuItemGetData; 
    
    private JToolBar jToolBar;
    String[] iconFiles = { "start.gif", "stop.gif", "ZoomIn.png", "ZoomOut.png",
    					"AllView.png", "Setting.png", "TriPoint.gif"};
    String[] buttonLabels = { "开始采样", "停止", "放大", "缩小",
    		"缩至视图", "设置触发条件", "到触发点"};
    ImageIcon[] icons = new ImageIcon[iconFiles.length];
    JButton[] buttons = new JButton[buttonLabels.length];
    
    JPopupMenu popupMenu;
    
    private JScrollBar jSignalScrollBar;
	private JSplitPane splitPane5;
	private JSplitPane splitPane7;
	private JSplitPane splitPane6;
	private JSplitPane splitPane4;
	private JSplitPane splitPane3;
	private JSplitPane splitPane2;
	private JSplitPane splitPane1;
	
	
    private SerialPortComunication serialPortComunication;
	protected int pointPosition;
	private int signalWidth;
	protected int selectX, selectY;
	
	Robot robot;
	BufferedImage img;
	
	protected boolean mousePressed = false;
	
    private static void initLookAndFeel() {  
        String lookAndFeel = null;  
  
        if (LOOKANDFEEL != null) {  
            if (LOOKANDFEEL.equals("Metal")) {  
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();  
            } else if (LOOKANDFEEL.equals("System")) {  
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();  
            } else if (LOOKANDFEEL.equals("Motif")) {  
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";  
            } else if (LOOKANDFEEL.equals("GTK+")) { //new in 1.4.2  
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";  
            } else {  
                System.err.println("Unexpected value of LOOKANDFEEL specified: "  
                                   + LOOKANDFEEL);  
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();  
            }  
  
            try {  
                UIManager.setLookAndFeel(lookAndFeel);  
            } catch (ClassNotFoundException e) {  
                System.err.println("Couldn't find class for specified look and feel:"  
                                   + lookAndFeel);  
                System.err.println("Did you include the L&F library in the class path?");  
                System.err.println("Using the default look and feel.");  
            } catch (UnsupportedLookAndFeelException e) {  
                System.err.println("Can't use the specified look and feel ("  
                                   + lookAndFeel  
                                   + ") on this platform.");  
                System.err.println("Using the default look and feel.");  
            } catch (Exception e) {  
                System.err.println("Couldn't get specified look and feel ("  
                                   + lookAndFeel  
                                   + "), for some reason.");  
                System.err.println("Using the default look and feel.");  
                e.printStackTrace();  
            }  
        }  
    } 
	
    private void configJScrollBar()
	{
    	jSignalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		jSignalScrollBar.addAdjustmentListener(new AdjustmentListener()
		{
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent evt)
			{
				//Adjustable source = evt.getAdjustable();
			    if (evt.getValueIsAdjusting()) 
			    {
			    	return;
			    }
			    /*
			    int orient = source.getOrientation();
			    if (orient == Adjustable.HORIZONTAL) 
			    {
			    	System.out.println("from horizontal scrollbar"); 
			    }
			    else 
			    {
			    	System.out.println("from vertical scrollbar");
			    }*/
			    int type = evt.getAdjustmentType();
			    switch (type)
			    {
			    case AdjustmentEvent.UNIT_INCREMENT:
			        System.out.println("Scrollbar was increased by one unit");
			        break;
			    case AdjustmentEvent.UNIT_DECREMENT:
			        System.out.println("Scrollbar was decreased by one unit");
			        break;
			    case AdjustmentEvent.BLOCK_INCREMENT:
			        System.out.println("Scrollbar was increased by one block");
			        break;
			    case AdjustmentEvent.BLOCK_DECREMENT:
			        System.out.println("Scrollbar was decreased by one block");
			        break;
			    case AdjustmentEvent.TRACK:
				    int steps = evt.getValue();
			        //System.out.println("The knob on the scrollbar was dragged");
				    	//timeRulerStart = steps * SignalConfig.signalPanePix / signalWidth;
				    	//startPoint = steps * SignalConfig.signalPanePix / signalWidth;

			    		pointPosition = steps *  signalWidth;

			  	  		bus0SignalPanel.displayStartPoint = pointPosition;
			  	  		bus1SignalPanel.displayStartPoint = pointPosition;
			    		System.out.println(pointPosition);
				    	timePanel.drawTimeLine(pointPosition,
				    						   signalWidth);
				    	timePanel.repaint();
				    	if (SerialPortComunication.dataBuffer != null)
				    	{
							if (bus0SignalPanel.myPointBuffersH.size () > pointPosition)
							{
								bus0SignalPanel.currentSignalWidth = signalWidth;
								bus0SignalPanel.repaint();
							}
							if (bus1SignalPanel.myPointBuffersH.size () > pointPosition)
							{
								bus1SignalPanel.currentSignalWidth = signalWidth;
								bus1SignalPanel.repaint();
							}
				    	}
				    	if (SerialPortComunication.preDataBuffer != null)
				    	{
							if (bus1SignalPanel.myPointBuffersH.size () > pointPosition)
							{
								bus1SignalPanel.currentSignalWidth = signalWidth;
								bus1SignalPanel.repaint();
							}
				    	}
			        break;
			    }
			}
		});
	}
    private void configJMenuBar()
	{
    	menuBar = new JMenuBar();
		
		menuFile = new JMenu("文件");
		menuOp = new JMenu("操作");
		
		menuItemNew = new JMenuItem("新建");
		menuItemOpen = new JMenuItem("打开");
		menuItemSave = new JMenuItem("保存");
		
		menuItemOpenSerial = new JMenuItem("打开串口"); 
		menuItemCloseSerial = new JMenuItem("关闭串口"); 
		menuItemGetData = new JMenuItem("开始采样"); 
		
		menuFile.add(menuItemNew);
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemSave);
		
		menuOp.add(menuItemOpenSerial);
		menuOp.add(menuItemCloseSerial);
		menuOp.add(menuItemGetData);
		
		menuBar.add(menuFile);
		menuBar.add(menuOp);
		menuItemNew.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				System.out.println("New Clicked");
				repaint();
			}
		});		
		menuItemOpen.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				System.out.println("Open Clicked");
			}
			
		});		
		menuItemSave.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				System.out.println("Save Clicked");
			}
			
		});
		menuItemOpenSerial.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				try
				{
					serialPortComunication.initSerialPort("COM6");
				}
				catch (NoSuchPortException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		menuItemCloseSerial.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				try
				{
					serialPortComunication.closeSerial();
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					return;
				}
			}
		});
		menuItemGetData.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				startSample ();
			}
		});
		//this.setJMenuBar(menuBar);
	}    
    private void configJPopupMenu()
	{
    	//右键菜单
		popupMenu = new JPopupMenu("PopupMenu");
		

		JMenuItem saveImageMenuItem = new JMenuItem("保存为图片");
	    popupMenu.add(saveImageMenuItem);
		
		JMenuItem MoveUpMenuItem = new JMenuItem("上移350");
	    popupMenu.add(MoveUpMenuItem);

		JMenuItem MoveDownMenuItem = new JMenuItem("下移350");
	    popupMenu.add(MoveDownMenuItem);
	    
		JMenuItem ZoomInMenuItem = new JMenuItem("放大");
	    popupMenu.add(ZoomInMenuItem);
	    // Copy
	    JMenuItem ZoomOutMenuItem = new JMenuItem("缩小");
	    popupMenu.add(ZoomOutMenuItem);
	    // Separator
	    popupMenu.addSeparator();
	    // Paste
	    JMenuItem trigPointMenuItem = new JMenuItem("到触发点");
	    popupMenu.add(trigPointMenuItem);
	    // Separator
	    popupMenu.addSeparator();
	    // Find
	    JMenuItem rulerOneMenuItem = new JMenuItem("放置标尺1");
	    popupMenu.add(rulerOneMenuItem);
	    JMenuItem rulerTwoMenuItem = new JMenuItem("放置标尺2");
	    popupMenu.add(rulerTwoMenuItem);
	    //
	    popupMenu.addSeparator();

	    JMenuItem counterMenuItem = new JMenuItem("计数界面");
	    popupMenu.add(counterMenuItem);
	    JMenuItem debugMenuItem = new JMenuItem("测试界面");
	    popupMenu.add(debugMenuItem);
	    JMenuItem daValueMenuItem = new JMenuItem("特征值界面");
	    popupMenu.add(daValueMenuItem);
	    JMenuItem sensorMenuItem = new JMenuItem("传感器界面");
	    popupMenu.add(sensorMenuItem);
	    JMenuItem simCountMenuItem = new JMenuItem("模拟计数界面");
	    popupMenu.add(simCountMenuItem);
	    popupMenu.addSeparator();
	    JMenuItem noPrintMenuItem = new JMenuItem("关闭串口");
	    popupMenu.add(noPrintMenuItem);
	    JMenuItem PrintNgMenuItem = new JMenuItem("打开串口");
	    popupMenu.add(PrintNgMenuItem);
	    


		counterMenuItem.addActionListener(new ActionListener ()
	    {
			public void actionPerformed(ActionEvent e)
			{
				serialPortComunication.sendFunc(Cmd.CMD_JSJM);
			}
	    	
	    });
		debugMenuItem.addActionListener(new ActionListener ()
	    {
			public void actionPerformed(ActionEvent e)
			{
				serialPortComunication.sendFunc(Cmd.CMD_TZCY);
			}
	    	
	    });
		daValueMenuItem.addActionListener(new ActionListener ()
	    {
			public void actionPerformed(ActionEvent e)
			{
				serialPortComunication.sendFunc(Cmd.CMD_TZBC);
			}
	    	
	    });
		sensorMenuItem.addActionListener(new ActionListener ()
	    {
			public void actionPerformed(ActionEvent e)
			{
				serialPortComunication.sendFunc(Cmd.CMD_JZTS);
			}
	    	
	    });
		simCountMenuItem.addActionListener(new ActionListener ()
	    {
			public void actionPerformed(ActionEvent e)
			{
				serialPortComunication.sendFunc(Cmd.CMD_MNJS);
			}
	    	
	    });
		noPrintMenuItem.addActionListener(new ActionListener ()
	    {
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					serialPortComunication.closeSerial();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
//				if (noPrintMenuItem.getText() == "打印波形")
//				{
//					noPrintMenuItem.setText("不打印波形");
//					serialPortComunication.sendFunc(Cmd.CMD_PRINT);
//				}
//				else
//				{
//					noPrintMenuItem.setText("打印波形");
//					serialPortComunication.sendFunc(Cmd.CMD_NOPRINT);
//				}
			}
	    	
	    });
		PrintNgMenuItem.addActionListener(new ActionListener ()
	    {
			public void actionPerformed(ActionEvent e)
			{
				startSample ();
			}
	    	
	    });
	    
	    
		saveImageMenuItem.addActionListener(new ActionListener ()
	    {
			public void actionPerformed(ActionEvent e)
			{
				SimpleDateFormat df = new SimpleDateFormat(SignalConfig.fileNameFormat);//设置日期格式
				System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
				try
				{
					ImageIO.write(img, "PNG", new File(SignalConfig.imagePath + df.format(new Date()) + ".png"));
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//.write(img, "JPG","文件保存路径");//第三个参数是File类型
			}
	    	
	    });

	    trigPointMenuItem.addActionListener(new ActionListener ()
	    {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				pointPosition = 0;
	  	  		bus0SignalPanel.displayStartPoint = 0;
	  	  		bus1SignalPanel.displayStartPoint = 0;
	    		System.out.println(pointPosition);
		    	timePanel.drawTimeLine(pointPosition,
		    						   signalWidth);
		    	timePanel.repaint();
		    	if (SerialPortComunication.dataBuffer != null)
		    	{
					if (bus0SignalPanel.myPointBuffersH.size () > pointPosition)
					{
						bus0SignalPanel.currentSignalWidth = signalWidth;
						bus0SignalPanel.repaint();
					}
					if (bus1SignalPanel.myPointBuffersH.size () > pointPosition)
					{
						bus1SignalPanel.currentSignalWidth = signalWidth;
						bus1SignalPanel.repaint();
					}
		    	}
		    	if (SerialPortComunication.preDataBuffer != null)
		    	{
					if (bus1SignalPanel.myPointBuffersH.size () > pointPosition)
					{
						bus1SignalPanel.currentSignalWidth = signalWidth;
						bus1SignalPanel.repaint();
					}
		    	}
	  	  		repaint();
			}
	    	
	    });
	    rulerOneMenuItem.addActionListener(new ActionListener ()
	    {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				bus0SignalPanel.addRuler1ToSignalPane(selectY);
				bus1SignalPanel.addRuler1ToSignalPane(selectY);
	  	  		repaint();
			}
	    	
	    });
	    rulerTwoMenuItem.addActionListener(new ActionListener ()
	    {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				bus0SignalPanel.addRuler2ToSignalPane(selectY);
				bus1SignalPanel.addRuler2ToSignalPane(selectY);
	  	  		repaint();
			}
	    	
	    });
	    ZoomInMenuItem.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				zoomIn();
			}
		});
	    ZoomOutMenuItem.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				zoomOut();
			}
		});

	    MoveUpMenuItem.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				bus0SignalPanel.setMoveValue(-350);
				bus1SignalPanel.setMoveValue(-350);
	  			repaint();
			}
		});
	    MoveDownMenuItem.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				bus0SignalPanel.setMoveValue(0);
				bus1SignalPanel.setMoveValue(0);
	  			repaint();
			}
		});
	}
    private void configJToolBar()
	{
    	jToolBar = new JToolBar();
	    for (int i = 0; i < buttonLabels.length; ++i) 
	    {
	      icons[i] = new ImageIcon(iconFiles[i]);
	      buttons[i] = new JButton(icons[i]);
	      buttons[i].setToolTipText(buttonLabels[i]);
	      //if (i == 3)
	        //jToolBar.addSeparator();
	      jToolBar.add(buttons[i]);
	    }

		buttons[0].addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				startSample();
			}
		});
		buttons[1].addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				try
				{
					serialPortComunication.closeSerial();
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					return;
				}
			}
		});
		buttons[2].addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				zoomIn();
			}
		});
		buttons[3].addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				zoomOut();
			}
		});
	}
    private void configSignalPanel (int StartX)
    {
    	bus0SignalPanel.selectedPoint = StartX;
    	bus1SignalPanel.selectedPoint = StartX;

    	bus0SignalPanel.diffMinDaValueH = bus1SignalPanel.diffMinDaValueH = Math.abs( bus0SignalPanel.minDaValueH - bus1SignalPanel.minDaValueH);
    	bus0SignalPanel.diffMinDaValueM = bus1SignalPanel.diffMinDaValueM = Math.abs( bus0SignalPanel.minDaValueM - bus1SignalPanel.minDaValueM);
    	bus0SignalPanel.diffMinDaValueL = bus1SignalPanel.diffMinDaValueL = Math.abs( bus0SignalPanel.minDaValueL - bus1SignalPanel.minDaValueL);

    	bus0SignalPanel.diffMaxDaValueH = bus1SignalPanel.diffMaxDaValueH = Math.abs( bus0SignalPanel.maxDaValueH - bus1SignalPanel.maxDaValueH);
    	bus0SignalPanel.diffMaxDaValueM = bus1SignalPanel.diffMaxDaValueM = Math.abs( bus0SignalPanel.maxDaValueM - bus1SignalPanel.maxDaValueM);
    	bus0SignalPanel.diffMaxDaValueL = bus1SignalPanel.diffMaxDaValueL = Math.abs( bus0SignalPanel.maxDaValueL - bus1SignalPanel.maxDaValueL);
    	
    	bus0SignalPanel.diffDaValueH = bus1SignalPanel.diffDaValueH = Math.abs( bus0SignalPanel.getCurrentMyPointBuffersH(StartX) - 
				   									bus1SignalPanel.getCurrentMyPointBuffersH(StartX));
    	bus0SignalPanel.diffDaValueM = bus1SignalPanel.diffDaValueM = Math.abs( bus0SignalPanel.getCurrentMyPointBuffersM(StartX) - 
													bus1SignalPanel.getCurrentMyPointBuffersM(StartX));
    	bus0SignalPanel.diffDaValueL = bus1SignalPanel.diffDaValueL = Math.abs( bus0SignalPanel.getCurrentMyPointBuffersL(StartX) - 
													bus1SignalPanel.getCurrentMyPointBuffersL(StartX));
    	
    }
    private void configBusPanel()
	{
    	bus0Tree.add(bus00);
		bus0Tree.add(bus01);
		bus0Tree.add(bus02);
		bus0Tree.add(bus03);
		bus0Tree.add(bus04);
		bus0Tree.add(bus05);
		bus0Tree.add(bus06);
		bus0Tree.add(bus07);
		

		bus1Tree.add(bus10);
		bus1Tree.add(bus11);
		bus1Tree.add(bus12);
		bus1Tree.add(bus13);
		bus1Tree.add(bus14);
		bus1Tree.add(bus15);
		bus1Tree.add(bus16);
		bus1Tree.add(bus17);
		
		bus0RootTree = new JTree(bus0Tree);
		bus1RootTree = new JTree(bus1Tree);
		
		busPanel = new JPanel();
		busPanel.setLayout(new GridLayout(2, 1, 5, 5));
		busPanel.add(bus0RootTree);
		busPanel.add(bus1RootTree);
	}
    private void configMainPanel()
	{
        
    	//bus0SignalPanel.setLayout(new BorderLayout());
    	//bus0SignalPanel.add(jSignalScrollBar, BorderLayout.SOUTH);
    	
	    contentPane = getContentPane();
	    contentPane.setBackground(color);                       
	
	    JLabel label3=new JLabel("Label 3",JLabel.CENTER);                            
	    label3.setBackground(Color.LIGHT_GRAY);
	    label3.setOpaque(true); 
	    
	    JLabel label4=new JLabel("Label 4",JLabel.CENTER);                            
	    label3.setBackground(Color.LIGHT_GRAY);
	    label3.setOpaque(true); 
	    
	    JLabel label5=new JLabel("Label 5",JLabel.CENTER);                            
	    label5.setBackground(Color.LIGHT_GRAY);
	    label5.setOpaque(true); 
	     
	    
	    splitPane7 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, bus0RootTree, bus1RootTree);
	    splitPane7.setEnabled(false);
	    splitPane7.setDividerLocation(0.5);
	    splitPane7.setResizeWeight(0.5);
	    splitPane7.setDividerSize(6);//设置分隔线宽度的大小，以pixel为计算单位。
	    
	    JButton button0 = new JButton("I'm a Swing button!");  
	    JButton button1 = new JButton("I'm a Swing button!");  
	    JButton button2 = new JButton("I'm a Swing button!");  
	    JButton button3 = new JButton("I'm a Swing button!");  

	    Checkbox t1 = new Checkbox("Visual Basic"); 

	    //splitPane6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, bus0Signalpanel, bus1Signalpanel);
//	    bus0SignalPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 5));
//	    bus0SignalPanel.add (t1);
//	    bus0SignalPanel.add (button0);
//	    bus0SignalPanel.add (button1);
//	    bus0SignalPanel.add (button2);
//	    bus0SignalPanel.add (button3);

	    //splitPane6 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, bus0SignalPanel, bus1SignalPanel);
	    splitPane6 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, bus0SignalPanel, null);
	    //splitPane6.setEnabled(false);
	    splitPane6.setDividerLocation(0.5);
	    splitPane6.setResizeWeight(0.5); 
	    
	    splitPane6.setDividerSize(0);//设置分隔线宽度的大小，以pixel为计算单位。
	    
	    splitPane6.addMouseMotionListener(new MouseMotionListener()
    	{
  	  	   public void mouseMoved (MouseEvent e)
  	  	   {
  	  	   }
  	  	   public void mouseDragged (MouseEvent e)
  	  	   {
  	  		   selectX = e.getX();
	  		   selectY = e.getY();
	  		   if (mousePressed)
	  		   {
	  	  		   bus0SignalPanel.addLineToSignalPanel(selectX, selectY);
	  	  		   bus1SignalPanel.addLineToSignalPanel(selectX, selectY);
	  	  		   
	  	  		   selectX = (selectX - SignalConfig.signalStartX) / signalWidth;
	  	  		   
	  	  		   configSignalPanel (selectX);
//	  			   System.out.println(selectX);
//	  			   System.out.println(bus0SignalPanel.getCurrentMyPointBuffersH(selectX));
//	  			   System.out.println(bus0SignalPanel.getCurrentMyPointBuffersM(selectX));
//	  			   System.out.println(bus0SignalPanel.getCurrentMyPointBuffersL(selectX));
	  			   repaint();
	  		   }
  	  	   }
    	});
	    
	    splitPane6.addMouseListener(new MouseAdapter()
    	{
  	  	   public void mouseClicked(MouseEvent e)
  	  	   {
  	  	   }
  	  	   public void mousePressed(MouseEvent e)
  	  	   {
  	  		   selectX = e.getX();
  	  		   selectY = e.getY();
  	  		   mousePressed = true;
  	  		   System.out.println((selectX - SignalConfig.signalStartX) / signalWidth);
  	  		   if (e.getModifiers() == MouseEvent.BUTTON1_MASK)//mouse left key
  	  		   {
	  	  		   bus0SignalPanel.addLineToSignalPanel(selectX, selectY);
	  	  		   bus1SignalPanel.addLineToSignalPanel(selectX, selectY);
	  	  		   
	  	  		   selectX = (selectX - SignalConfig.signalStartX) / signalWidth;

	  	  		   configSignalPanel (selectX);
//	  			   System.out.println(selectX);
//	  			   System.out.println(bus0SignalPanel.getCurrentMyPointBuffersH(selectX));
//	  			   System.out.println(bus0SignalPanel.getCurrentMyPointBuffersM(selectX));
//	  			   System.out.println(bus0SignalPanel.getCurrentMyPointBuffersL(selectX));
	  	  		   repaint();
  	  		   }
  	  		   else if(e.getModifiers()==MouseEvent.BUTTON2_MASK)//mouse center key
  	  		   {
  	  			   System.out.println ("center");
  	  		   }
  	  		   else if(e.getModifiers()==MouseEvent.BUTTON3_MASK) //mouse right key
  	  		   {
  	  			   //System.out.println ("right");Robot robot;
  					try
  					{
  						robot = null;
  						robot = new Robot();
  						try 
  						{
  							img = null;
  						    img = robot.createScreenCapture(
  						    		new Rectangle(SignalConfig.imageStartX, SignalConfig.imageStartY,
  						    					SignalConfig.imageEndX, SignalConfig.imageEndY)); //screen capture, screenshot
  						} catch (IllegalArgumentException e1) 
  						{
  						    e1.printStackTrace();
  						}
  					}
  					catch (AWTException e2)
  					{
  						// TODO Auto-generated catch block
  						e2.printStackTrace();
  					}
  		  	  		selectX = e.getX();
  	  		   }
  	       }
  	  	   public void mouseReleased(MouseEvent e)
  	  	   {
  	  		   mousePressed = false;
  	       }
    	});
	    
	    this.addKeyListener(new KeyAdapter ()
	    {
            public void keyPressed(KeyEvent e)
            {
                switch(e.getKeyCode())
                {
                	case KeyEvent.VK_DOWN:
                		break;
                	case KeyEvent.VK_UP:
                		break;
                	case KeyEvent.VK_LEFT:
                		selectX = selectX * signalWidth + SignalConfig.signalStartX - 1;
                		if (selectX < 0)
                		{
                			selectX = 0;
                		}
                		bus0SignalPanel.addLineToSignalPanel(selectX, selectY);
	 	  	  		   	bus1SignalPanel.addLineToSignalPanel(selectX, selectY);
		  	  		   
	 	  	  		   	selectX = (selectX - SignalConfig.signalStartX) / signalWidth;
	
	 	  	  		   	configSignalPanel (selectX);
	 	  	  		   	repaint();
                		break;
                	case KeyEvent.VK_RIGHT:	  	
                		selectX = selectX * signalWidth + SignalConfig.signalStartX + 1;
                		bus0SignalPanel.addLineToSignalPanel(selectX, selectY);
	 	  	  		   	bus1SignalPanel.addLineToSignalPanel(selectX, selectY);
		  	  		   
	 	  	  		   	selectX = (selectX - SignalConfig.signalStartX) / signalWidth;
	
	 	  	  		   	configSignalPanel (selectX);
	 	  	  		   	repaint();
	 	  	  		   	break;
                	default: break;
                }
               
            }
	    });
	    
	    splitPane6.setComponentPopupMenu(popupMenu);
	    
	    splitPane6.addMouseWheelListener(new MouseWheelListener()
		{

			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				// TODO Auto-generated method stub
			    int steps = e.getWheelRotation();
	    		pointPosition += steps *  signalWidth * 32;
	    		
	    		if (pointPosition < 0)
	    		{
	    			pointPosition = 0;
	    			return;
	    		}

	  	  		bus0SignalPanel.displayStartPoint = pointPosition;
	  	  		bus1SignalPanel.displayStartPoint = pointPosition;
	    		System.out.println(pointPosition);
		    	timePanel.drawTimeLine(pointPosition,
		    						   signalWidth);
		    	timePanel.repaint();
		    	if (SerialPortComunication.dataBuffer != null)
		    	{
					if (bus0SignalPanel.myPointBuffersH.size () > pointPosition)
					{
						bus0SignalPanel.currentSignalWidth = signalWidth;
						bus0SignalPanel.repaint();
					}
		    	}
		    	if (SerialPortComunication.preDataBuffer != null)
		    	{
					if (bus1SignalPanel.myPointBuffersH.size () > pointPosition)
					{
						bus1SignalPanel.currentSignalWidth = signalWidth;
						bus1SignalPanel.repaint();
					}
		    	}
//			    if (pointPosition + steps >= 0)
//			    {
//			    	int i, j;
//			    	pointPosition += steps;
//			    	i = (pointPosition % 
//							(SignalConfig.signalPanePix / signalWidth));
//			    	if (i == 0)
//			    	{
//			    		j = (pointPosition / 
//								(SignalConfig.signalPanePix / signalWidth));
//			    		jSignalScrollBar.setValue(j);
//			    	}
//			    }
//		    	timePanel.drawTimeLine(pointPosition, signalWidth);
//			    timePanel.repaint();
//			    if (SerialPortComunication.dataBuffer != null)
//			    {
//				    bus0Signalpanel.prepareLine(SerialPortComunication.dataBuffer, pointPosition, signalWidth);
//					bus0Signalpanel.repaint();
//			    }
			}
		});
	    splitPane5 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, timePanel, splitPane6);
	    //splitPane5 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, timePanel, bus0Signalpanel);
	    splitPane5.setEnabled(false);
	    splitPane5.setDividerLocation(22);
	    splitPane5.setDividerSize(1);//设置分隔线宽度的大小，以pixel为计算单位。
	    
	    splitPane4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, label5, splitPane7);
	    splitPane4.setEnabled(false);
	    splitPane4.setDividerLocation(40);
	    splitPane4.setDividerSize(1);//设置分隔线宽度的大小，以pixel为计算单位。
	    
	    splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,label3, label4);
	    
	    splitPane3.setDividerLocation(550);
	    splitPane3.setOneTouchExpandable(true);
	    splitPane3.setDividerSize(10);//设置分隔线宽度的大小，以pixel为计算单位。
	    
	    /*加入label1,label2到splitPane1中，并设置此splitPane1为水平分割且具有Continuous Layout的
	     *功能。
	     */
	    //splitPane2=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,false, splitPane4, splitPane5);
	    splitPane2=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,false, null, splitPane5);
	    /*设置splitPane1的分隔线位置，0.3是相对于splitPane1的大小而定，因此这个值的范围在0.0～1.0
	     *中。若你使用整数值来设置splitPane的分隔线位置，如第34行所示，则所定义的值以pixel为计算单位
	     */
	    splitPane2.setDividerLocation(100);
	
	    /*设置JSplitPane是否可以展开或收起(如同文件总管一般)，设为true表示打开此功能。
	     */
	    splitPane2.setOneTouchExpandable(true);
	    splitPane2.setDividerSize(10);//设置分隔线宽度的大小，以pixel为计算单位。
	     
	    //splitPane1=new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,splitPane2,splitPane3);
	    splitPane1=new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,splitPane2, null);
	    splitPane1.setDividerLocation(990);//<-----------------------------------------------------
	    //设置JSplitPane是否可以展开或收起(如同文件总管一般),设为true表示打开此功能.
	    splitPane1.setOneTouchExpandable(true);
	    splitPane1.setDividerSize(10);

	    //contentPane.add(splitPane1);
	    contentPane.add(splitPane6);
	    //contentPane.add("North", jToolBar);

		serialPortComunication = new SerialPortComunication();
    	timePanel.drawTimeLine(pointPosition, signalWidth);
	}
    private void zoomIn()
	{

    	bus0SignalPanel.selectedPoint *= signalWidth;
    	bus1SignalPanel.selectedPoint *= signalWidth;

		if (signalWidth < 128)
			signalWidth *= 2;
		selectX /= 2;
		pointPosition += selectX;

    	bus0SignalPanel.selectedPoint /= signalWidth;
		bus0SignalPanel.currentSignalWidth = signalWidth;
    	bus1SignalPanel.selectedPoint /= signalWidth;
		bus1SignalPanel.currentSignalWidth = signalWidth;
    	timePanel.drawTimeLine(pointPosition, signalWidth);
    	timePanel.repaint();
    	bus0SignalPanel.repaint ();
    	bus1SignalPanel.repaint ();
	}
    private void zoomOut()
	{
    	if (signalWidth == 1)
    		return;
    	bus0SignalPanel.selectedPoint *= signalWidth;
    	bus1SignalPanel.selectedPoint *= signalWidth;
    	signalWidth /= 2;
		if (signalWidth == 0)
			signalWidth = 1;

		pointPosition -= selectX;
		if (pointPosition < 0)
			pointPosition = 0;

    	bus0SignalPanel.selectedPoint /= signalWidth;
		bus0SignalPanel.currentSignalWidth = signalWidth;
    	bus1SignalPanel.selectedPoint /= signalWidth;
		bus1SignalPanel.currentSignalWidth = signalWidth;
    	//bus0Signalpanel.prepareLine(SerialPortComunication.dataBuffer,  pointPosition, signalWidth);
    	timePanel.drawTimeLine(pointPosition, signalWidth);
    	timePanel.repaint();
    	bus0SignalPanel.repaint ();
    	bus1SignalPanel.repaint ();
	}
    private void startSample ()
    {
    	try
		{
			if (serialPortComunication.initSerialPort("COM6"))
				return;
			//serialPortComunication.sendCmd(Cmd.CMD_START);
		}
		catch (NoSuchPortException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    Gui () throws NoSuchPortException, IOException
    {
		// TODO Auto-generated constructor stub
    	selectX = 1;
    	signalWidth = SignalConfig.signalWidth;
    	pointPosition = 0;
    	bus0SignalPanel = new SignalPanel(selectX * signalWidth);
    	bus1SignalPanel = new SignalPanel(selectX * signalWidth);
    	initLookAndFeel ();
		//工具栏***********************************************
		configJToolBar();
    	//滚动条***********************************************
    	configJScrollBar();
    	//菜单栏***********************************************
    	configJMenuBar();
		//弹出式菜单*******************************************
		configJPopupMenu();
		//总线*************************************************
		configBusPanel();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true); 
		setLayout(new BorderLayout());
        setLocation(300,150); 	
        setTitle("Logic Analyzer");
		//主面板***********************************************
		configMainPanel();
		pack();
		setSize(800, 600);	
		setLocation(160, 100);
		setVisible(true);	
        setExtendedState(Frame.MAXIMIZED_BOTH);
        
	    addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
               System.exit(0);        
            }        
        }); 
	    

	    //main loop
//	    SerialPortComunication.dataBuffer = new StringBuffer ();
//	    for (int i = 0; i < 4000; i++)
//	    {
//	    	SerialPortComunication.dataBuffer.append(i%10);
//	    	if (i > 0 && i % 3 == 0)
//	    	{
//	    		SerialPortComunication.dataBuffer.append(' ');
//	    	}
//	    }
//	    SerialPortComunication.dataBuffer.append('1');
//	    SerialPortComunication.dataBuffer.append('2');
//	    SerialPortComunication.dataBuffer.append('3');
//		serialPortComunication.busy = false;
		
	    startSample ();
	    
        // run in a second  
        final long timeInterval = 50;  
        Runnable runnable = new Runnable() {  
            public void run() {  
                while (true) {  
                    // ------- code for task to run  
                    //System.out.println("Hello !!");  
	       			 if (serialPortComunication.busy == false)
	    			 {
	    				 System.out.println ("repaint wave begin");
	    				 serialPortComunication.busy = true;

	    				 if (SerialPortComunication.dataBuffer != null){
	    					 try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw  
	    							SimpleDateFormat df = new SimpleDateFormat(SignalConfig.fileNameFormat);//设置日期格式
	    							System.out.println(df.format(new Date()));// new Date()为获取当前系统时间

	    			                /* 读入TXT文件 */  
//	    			                String pathname = "D:\\twitter\\13_9_6\\dataset\\en\\input.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径  
//	    			                File filename = new File(pathname); // 要读取以上路径的input。txt文件  
//	    			                InputStreamReader reader = new InputStreamReader(  
//	    			                        new FileInputStream(filename)); // 建立一个输入流对象reader  
//	    			                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
//	    			                String line = "";  
//	    			                line = br.readLine();  
//	    			                while (line != null) {  
//	    			                    line = br.readLine(); // 一次读入一行数据  
//	    			                }  

	    			                /* 写入Txt文件 */  
	    			                File writename = new File(SignalConfig.imageDataPath + df.format(new Date()) + ".txt"); // 相对路径，如果没有则要建立一个新的output。txt文件  
	    			                writename.createNewFile(); // 创建新文件  
	    			                BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
	    			                out.write (SerialPortComunication.dataBuffer.toString());
	    			                out.flush(); // 把缓存区内容压入文件  
	    			                out.close(); // 最后记得关闭文件  

	    			            } catch (Exception e) {  
	    			                e.printStackTrace();  
	    			            }  
	    				 	bus0SignalPanel.prepareLine(SerialPortComunication.dataBuffer,  pointPosition, signalWidth);
	    				 }
	    				 if (SerialPortComunication.preDataBuffer != null)
	    					 bus1SignalPanel.prepareLine(SerialPortComunication.preDataBuffer,  pointPosition, signalWidth);

	  	  	  		   configSignalPanel (selectX);
	    				 
	    				 repaint();
	    				 System.out.println ("repaint ALL wave end");
	    			 }
                    // ------- ends here  
                    try {  
                        Thread.sleep(timeInterval);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        };  
        Thread thread = new Thread(runnable);  
        thread.start(); 
	      
//	    while (true)
//		{
//			//while (serialPortComunication.busy);
//			//System.out.println("done");
//			//serialPortComunication.closeSerial();
//			 if (serialPortComunication.busy == false)
//			 {
//				 System.out.println ("repaint wave begin");
//				 serialPortComunication.busy = true;
//				 bus0Signalpanel.prepareLine(SerialPortComunication.dataBuffer,  pointPosition, signalWidth);
//				 bus0Signalpanel.repaint();
//				 System.out.println ("repaint wave end");
//			 }
//		}
    } 
}

