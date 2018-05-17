import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;



public class SignalPanel extends JPanel// implements MouseListener, MouseMotionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Line> lineBuffers;
	public ArrayList<MyPoint> myPointBuffersH;
	public ArrayList<MyPoint> myPointBuffersM;
	public ArrayList<MyPoint> myPointBuffersL;
	private Channels channels;
	private static Line timeLine;
	private static Line levelLine;
	private static Line ruler1Line;
	private static Line ruler2Line;
	private static Line trigLine;
	private ArrayList<TimeString> timeStrings;
	private byte preSecondData;
	int displayStartPoint;
	int selectedPoint;
	int currentSignalWidth;
	
	int diffDaValueH;
	int diffDaValueM;
	int diffDaValueL;
	
	int diffMinDaValueH;
	int diffMinDaValueM;
	int diffMinDaValueL;
	
	int diffMaxDaValueH;
	int diffMaxDaValueM;
	int diffMaxDaValueL;
	
	int minDaValueH;
	int minDaValueM;
	int minDaValueL;
	int maxDaValueH;
	int maxDaValueM;
	int maxDaValueL;
	
	int minValueIndexH;
	int minValueIndexM;
	int minValueIndexL;

	public int	levelMoveValue	= 0;

	public void setMoveValue (int value)
	{
		levelMoveValue = value;
	}
	//private static int timePoint;	
	public SignalPanel()
	{
		// TODO Auto-generated constructor stub
		super();
		this.setBackground(new Color(20, 30, 25));
		channels = null;
		lineBuffers = null;
		timeStrings = null;
		preSecondData = 0;
		displayStartPoint = 0;
		selectedPoint = 0;
		currentSignalWidth = SignalConfig.signalWidth;
		minDaValueH = 1024;
		minDaValueM = 1024;
		minDaValueL = 1024;
		maxDaValueH = 0;
		maxDaValueM = 0;
		maxDaValueL = 0;
		
		minValueIndexH = 0;
		minValueIndexM = 0;
		minValueIndexL = 0;
		
		diffDaValueH = 0;
		diffDaValueM = 0;
		diffDaValueL = 0;	
		
		diffMinDaValueH = 0;
		diffMinDaValueM = 0;
		diffMinDaValueL = 0;
		
		diffMaxDaValueH = 0;
		diffMaxDaValueM = 0;
		diffMaxDaValueL = 0;
		
		timeLine = new Line(
				400, 
				0, 
				400, 
				1000, 
				new Color(255, 255, 0));
		levelLine = new Line(
				0, 
				0, 
				1920, 
				0, 
				new Color(255, 255, 0));
	}
	public SignalPanel(int X)
	{
		// TODO Auto-generated constructor stub
		super();
		this.setBackground(new Color(20, 30, 25));
		channels = null;
		lineBuffers = null;
		timeStrings = null;
		preSecondData = 0;
		selectedPoint = 0;
		currentSignalWidth = SignalConfig.signalWidth;
		minDaValueH = 1024;
		minDaValueM = 1024;
		minDaValueL = 1024;
		maxDaValueH = 0;
		maxDaValueM = 0;
		maxDaValueL = 0;
		diffDaValueH = 0;
		diffDaValueM = 0;
		diffDaValueL = 0;
		timeLine = new Line(
				X, 
				0, 
				X, 
				1000, 
				new Color(255, 255, 0));
	}
	private void signalDrawLine (Graphics g, Line line)
	{
		g.setColor(line.color);
		g.drawLine(line.startX, line.startY, line.endX, line.endY);
	}
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int i = 0, j = 0, tempX = 0;
		if (lineBuffers != null)
		{
			i = lineBuffers.size();
			while ((i--) > 0)
			{
				Line line = lineBuffers.get(i);
				g.setColor(line.color);
				g.drawLine(line.startX, line.startY, line.endX, line.endY);
			}
		}

		if (levelLine != null)
		{
			int tempLineStartX = 30;
			int tempLineStartY = 0;
			
			int tempStartX = SignalConfig.currentPointStringStartX;
			int tempStartY = SignalConfig.infoStringStartY + 10;

			g.setColor(new Color(200, 0, 0));
			
			if ((minValueIndexH - displayStartPoint) > 0)
			{
				g.drawLine(SignalConfig.signalStartX + (minValueIndexH - displayStartPoint) * currentSignalWidth, 0,
						   SignalConfig.signalStartX + (minValueIndexH - displayStartPoint) * currentSignalWidth, 1080);
			}
			
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("Min"), tempStartX, tempStartY);
			g.drawString(new String ("Max"), tempStartX + 45, tempStartY);

			g.setColor(new Color(0, 200, 0));
			g.drawString(new String ("H=" + (minDaValueH)), tempStartX, tempStartY + 20);
			g.drawString(new String ("H=" + (maxDaValueH)), tempStartX + 45, tempStartY + 20);
			g.setColor(new Color(200, 0, 0));
			g.drawString(new String ("M=" + (minDaValueM)), tempStartX, tempStartY + 40);
			g.drawString(new String ("M=" + (maxDaValueM)), tempStartX + 45, tempStartY + 40);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("L=" + (minDaValueL)), tempStartX, tempStartY + 60);
			g.drawString(new String ("L=" + (maxDaValueL)), tempStartX + 45, tempStartY + 60);
			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = tempStartY + 70;
			g.drawLine(0, tempStartY + tempLineStartY, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY);
			g.drawLine(0, tempStartY + tempLineStartY - 1, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY - 1);

			

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = SignalConfig.infoStringStartY + 100;
			g.drawString(new String ("Min Diff"), tempStartX - 10, tempStartY);
			g.setColor(new Color(0, 200, 0));
			g.drawString(new String ("H=" + (diffMinDaValueH)), tempStartX, tempStartY + 20);
			g.setColor(new Color(200, 0, 0));
			g.drawString(new String ("M=" + (diffMinDaValueM)), tempStartX, tempStartY + 40);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("L=" + (diffMinDaValueL)), tempStartX, tempStartY + 60);
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = tempStartY + 70;
			g.drawLine(0, tempStartY + tempLineStartY, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY);
			g.drawLine(0, tempStartY + tempLineStartY - 1, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY - 1);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = SignalConfig.infoStringStartY + 190;
			g.drawString(new String ("Max Diff"), tempStartX - 10, tempStartY);
			g.setColor(new Color(0, 200, 0));
			g.drawString(new String ("H=" + (diffMaxDaValueH)), tempStartX, tempStartY + 20);
			g.setColor(new Color(200, 0, 0));
			g.drawString(new String ("M=" + (diffMaxDaValueM)), tempStartX, tempStartY + 40);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("L=" + (diffMaxDaValueL)), tempStartX, tempStartY + 60);
			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = tempStartY + 70;
			g.drawLine(0, tempStartY + tempLineStartY, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY);
			g.drawLine(0, tempStartY + tempLineStartY - 1, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY - 1);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = SignalConfig.infoStringStartY + 280;
			g.drawString(new String ("Coin Diff"), tempStartX - 15, tempStartY);
			g.setColor(new Color(0, 200, 0));
			g.drawString(new String ("H=" + (diffDaValueH)), tempStartX, tempStartY + 20);
			g.setColor(new Color(200, 0, 0));
			g.drawString(new String ("M=" + (diffDaValueM)), tempStartX, tempStartY + 40);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("L=" + (diffDaValueL)), tempStartX, tempStartY + 60);
			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = tempStartY + 70;
			g.drawLine(0, tempStartY + tempLineStartY, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY);
			g.drawLine(0, tempStartY + tempLineStartY - 1, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY - 1);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = SignalConfig.infoStringStartY + 370;
			g.drawString(new String ("Current"), tempStartX - 10, tempStartY);
			g.setColor(new Color(0, 200, 0));
			g.drawString(new String ("H=" + (getCurrentMyPointBuffersH(selectedPoint))), tempStartX, tempStartY + 20);
			g.setColor(new Color(200, 0, 0));
			g.drawString(new String ("M=" + (getCurrentMyPointBuffersM(selectedPoint))), tempStartX, tempStartY + 40);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("L=" + (getCurrentMyPointBuffersL(selectedPoint))), tempStartX, tempStartY + 60);
			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX - 0;
			tempStartY = tempStartY + 70;
			g.drawLine(0, tempStartY + tempLineStartY, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY);
			g.drawLine(0, tempStartY + tempLineStartY - 1, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY - 1);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = SignalConfig.infoStringStartY + 460;
			g.drawString(new String ("Chanel Diff"), tempStartX - 10, tempStartY);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("L - H=  " + (getCurrentMyPointBuffersL(selectedPoint) - getCurrentMyPointBuffersH(selectedPoint))), tempStartX, tempStartY + 20);
			g.setColor(new Color(200, 0, 0));
			g.drawString(new String ("M - H=  " + (getCurrentMyPointBuffersM(selectedPoint)-  getCurrentMyPointBuffersH(selectedPoint))), tempStartX, tempStartY + 40);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("L - M=  " + (getCurrentMyPointBuffersL(selectedPoint) - getCurrentMyPointBuffersM(selectedPoint))), tempStartX, tempStartY + 60);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = tempStartY + 70;
			g.drawLine(0, tempStartY + tempLineStartY, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY);
			g.drawLine(0, tempStartY + tempLineStartY - 1, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY - 1);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = SignalConfig.infoStringStartY + 550;
			g.drawString(new String ("Current-Level"), tempStartX - 22, tempStartY);
			g.setColor(new Color(0, 200, 0));
			g.drawString(new String ("H=" + (getCurrentMyPointBuffersH(selectedPoint) - (SignalConfig.levelThreshold - levelLine.startY + 10+levelMoveValue))), tempStartX, tempStartY + 20);
			g.setColor(new Color(200, 0, 0));
			g.drawString(new String ("M=" + (getCurrentMyPointBuffersM(selectedPoint) - (SignalConfig.levelThreshold - levelLine.startY + 10+levelMoveValue))), tempStartX, tempStartY + 40);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("L=" + (getCurrentMyPointBuffersL(selectedPoint) - (SignalConfig.levelThreshold - levelLine.startY + 10+levelMoveValue))), tempStartX, tempStartY + 60);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = tempStartY + 70;
			g.drawLine(0, tempStartY + tempLineStartY, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY);
			g.drawLine(0, tempStartY + tempLineStartY - 1, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY - 1);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = SignalConfig.infoStringStartY + 640;
			g.drawString(new String ("Selected Point"), tempStartX - 22, tempStartY);
			g.setColor(new Color(0, 200, 0));
			g.drawString(new String ("Point=" + (selectedPoint + displayStartPoint)), tempStartX, tempStartY + 20);
			g.setColor(new Color(200, 200, 200));
			g.drawString(new String ("Level=" + (SignalConfig.levelThreshold - levelLine.startY + 10 +levelMoveValue)), tempStartX, tempStartY + 40);
			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = tempStartY + 50;
			g.drawLine(0, tempStartY + tempLineStartY, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY);
			g.drawLine(0, tempStartY + tempLineStartY - 1, SignalConfig.signalStartX - tempLineStartX, tempStartY + tempLineStartY - 1);

			g.setColor(new Color(200, 200, 200));
			tempStartX = SignalConfig.currentPointStringStartX + 20;
			tempStartY = SignalConfig.infoStringStartY + 710;
			g.drawString(new String ("Ruler1 - Ruler2"), tempStartX - 22, tempStartY);
			g.setColor(new Color(200, 200, 200));
			if (ruler1Line != null && ruler2Line != null)
				g.drawString(new String ("Level=" + (Math.abs(ruler1Line.startY - ruler2Line.startY))), tempStartX, tempStartY + 20);
			
			
			for (int i1 = 0; i1 <= SignalConfig.levelThreshold;)
			{
				int offset = 0;
				if ((i1 / 10) % 2 == 0)
				{
					g.setColor(new Color (64, 64, 64));
				}
				else
				{
					g.setColor(new Color (80, 80, 80));
				}
				
				if (SignalConfig.levelThreshold - i1 < 10)
				{
					offset = 21;
				}
				else if (SignalConfig.levelThreshold - i1 < 100)
				{
					offset = 14;
				}
				else if (SignalConfig.levelThreshold - i1 < 1000)
				{
					offset = 7;
				}
				else
				{
					offset = 0;
				}
				g.drawString(new String ("" + (SignalConfig.levelThreshold - i1+levelMoveValue)), SignalConfig.levelStringStartX + offset, SignalConfig.signalStartY - SignalConfig.levelThreshold + 4 + i1);
				g.drawLine(SignalConfig.signalStartX, SignalConfig.signalStartY - SignalConfig.levelThreshold + i1, 1920, SignalConfig.signalStartY - SignalConfig.levelThreshold + i1);
				i1 += 10;
			}
			g.setColor(levelLine.color);
			g.drawLine(levelLine.startX + SignalConfig.signalStartX, levelLine.startY, levelLine.endX + SignalConfig.signalStartX, levelLine.endY);
		}
		if (timeLine != null)
		{
			if (timeLine.startX < SignalConfig.signalStartX)
			{
				timeLine.startX = timeLine.endX = SignalConfig.signalStartX + 1;
			}
			signalDrawLine(g, timeLine);
			
//			g.setColor(new Color(255, 255, 255));
//			g.drawLine(SignalConfig.signalStartX, 0, SignalConfig.signalStartX, 1080);
//			g.drawLine(SignalConfig.signalStartX - 1, 0, SignalConfig.signalStartX - 1, 1080);
//			g.setColor(new Color(255, 0, 0));
//			g.drawLine(SignalConfig.signalEndX, 0, SignalConfig.signalEndX, 1080);
//			g.drawLine(SignalConfig.signalEndX - 1, 0, SignalConfig.signalEndX - 1, 1080);
		}
		if (ruler1Line != null)
		{
			if (ruler1Line.startX < SignalConfig.signalStartX)
			{
				ruler1Line.startX = SignalConfig.signalStartX + 1;
			}
			signalDrawLine(g, ruler1Line);
		}
		if (ruler2Line != null)
		{
			if (ruler2Line.startX < SignalConfig.signalStartX)
			{
				ruler2Line.startX = SignalConfig.signalStartX + 1;
			}
			signalDrawLine(g, ruler2Line);
		}
		if (trigLine != null)
		{
			if (trigLine.startX < SignalConfig.signalStartX)
			{
				trigLine.startX = trigLine.endX = SignalConfig.signalStartX + 1;
			}
			signalDrawLine(g, trigLine);
		}
		
		if (myPointBuffersH != null  && displayStartPoint < myPointBuffersH.size())
		{
			i = myPointBuffersH.size();
			j = displayStartPoint;
			tempX = 0;

			g.setColor(new Color(0, 200, 0));
			MyPoint myPointPrePointH;
			
			if (j > 0)
			{
				myPointPrePointH = myPointBuffersH.get(j - 1);
				myPointPrePointH.pointX = SignalConfig.signalStartX;
			}
			else
			{
				myPointPrePointH = new MyPoint (SignalConfig.signalStartX, SignalConfig.signalStartY - SignalConfig.levelThreshold);
			}
			while (j < i)
			{
				
				MyPoint myPointH = myPointBuffersH.get(j);
				g.drawLine( myPointPrePointH.pointX, myPointPrePointH.pointY+levelMoveValue, 
						 currentSignalWidth * (tempX) + SignalConfig.signalStartX, myPointH.pointY+levelMoveValue);
				myPointPrePointH = null;
				myPointPrePointH = new MyPoint (currentSignalWidth * (tempX) + SignalConfig.signalStartX, myPointH.pointY);
				j++;
				tempX++;
			}
		}
		if (myPointBuffersM != null  && displayStartPoint < myPointBuffersH.size())
		{
			i = myPointBuffersM.size();
			j = displayStartPoint;
			tempX = 0;

			g.setColor(new Color(200, 0, 0));
			MyPoint myPointPrePointM;
			if (j > 0)
			{
				myPointPrePointM = myPointBuffersM.get(j - 1);
				myPointPrePointM.pointX = SignalConfig.signalStartX;
			}
			else
			{
				myPointPrePointM = new MyPoint (SignalConfig.signalStartX, SignalConfig.signalStartY - SignalConfig.levelThreshold);
			}
			while (j < i)
			{
				MyPoint myPointM = myPointBuffersM.get(j);
				g.drawLine( myPointPrePointM.pointX, myPointPrePointM.pointY+levelMoveValue, 
						currentSignalWidth * (tempX) + SignalConfig.signalStartX, myPointM.pointY+levelMoveValue);
				myPointPrePointM = null;
				myPointPrePointM = new MyPoint (currentSignalWidth * (tempX) + SignalConfig.signalStartX, myPointM.pointY+SignalConfig.levelMoveValue);
				j++;
				tempX++;
			}
		}
		if (myPointBuffersL != null  && displayStartPoint < myPointBuffersH.size())
		{
			i = myPointBuffersL.size();
			j = displayStartPoint;
			tempX = 0;

			g.setColor(new Color(200, 200, 200));
			MyPoint myPointPrePointL;
			if (j > 0)
			{
				myPointPrePointL = myPointBuffersL.get(j - 1);
				myPointPrePointL.pointX = SignalConfig.signalStartX;
			}
			else
			{
				myPointPrePointL = new MyPoint (SignalConfig.signalStartX, SignalConfig.signalStartY - SignalConfig.levelThreshold);
			}
			while (j < i)
			{
				MyPoint myPointL = myPointBuffersL.get(j);
				g.drawLine( myPointPrePointL.pointX, myPointPrePointL.pointY+levelMoveValue, 
						currentSignalWidth * (tempX) + SignalConfig.signalStartX, myPointL.pointY+levelMoveValue);
				myPointPrePointL = null;
				myPointPrePointL = new MyPoint (currentSignalWidth * (tempX) + SignalConfig.signalStartX, myPointL.pointY);
				j++;
				tempX++;
			}
		}
		
		if (timeStrings != null)
		{
			TimeString timeString;
			i = timeStrings.size();
			while ((i--) > 0)
			{
				timeString = timeStrings.get(i);	
				g.setColor(timeString.color);
				g.drawString(timeString.string, timeString.startX + SignalConfig.signalStartX, timeString.startY);
			}
		}
	}
	

	public void prepareLine (StringBuffer dataBuffer, int startPoint, int signalWidth)
	{
		//byte data = 0;
		//byte preData = preSecondData;
		//int preStringLoc = 0;
		minDaValueH = 1024;
		minDaValueM = 1024;
		minDaValueL = 1024;
		maxDaValueH = 0;
		maxDaValueM = 0;
		maxDaValueL = 0;
		MyPoint myPointBufferH, myPointBufferM, myPointBufferL;
		int dataCount;
		
		lineBuffers = null; // release memory
		channels = null;
		timeStrings = null;
		
		lineBuffers = new ArrayList<Line>();
		myPointBuffersH = new ArrayList<MyPoint>();
		myPointBuffersM = new ArrayList<MyPoint>();
		myPointBuffersL = new ArrayList<MyPoint>();
		
		channels = new Channels(signalWidth);
		timeStrings = new ArrayList<TimeString>();

		//int start_x_temp = 0;
		if (dataBuffer == null)
			return;
		dataCount = dataBuffer.length();
		StringBuffer stringBufferTemp = new StringBuffer ();
		char tempChar = 0;
		int daValue = 0, daValueLine = 0;
		int tempX	= 0;
		int pointIndex = 0;
		startPoint = 0;
		for (int i = 0; i < dataCount; i++)
		{
			tempChar = (char)dataBuffer.charAt(startPoint + i);
			if (tempChar == '\t')
			{
				daValue = Integer.valueOf(stringBufferTemp.toString());
				stringBufferTemp = null;
				stringBufferTemp = new StringBuffer ();
				
				daValue = SignalConfig.signalStartY - daValue;
				daValueLine = daValue;
				daValueLine *= SignalConfig.signal_P;
				
				if (pointIndex == 0)
				{
					myPointBufferH = new MyPoint(0, daValueLine);
					myPointBuffersH.add(myPointBufferH);
					myPointBufferH = null;
					
					daValue = SignalConfig.signalStartY - daValue;
					if (daValue > maxDaValueH)
					{
						maxDaValueH = daValue;
					}
					else if (daValue < minDaValueH)
					{
						minDaValueH = daValue;
						minValueIndexH = myPointBuffersH.size() - 1;
					}
				}
				else if (pointIndex == 1)
				{
					myPointBufferM = new MyPoint(0, daValueLine);
					myPointBuffersM.add(myPointBufferM);
					myPointBufferM = null;
					
					daValue = SignalConfig.signalStartY - daValue;
					if (daValue > maxDaValueM)
					{
						maxDaValueM = daValue;
					}
					else if (daValue < minDaValueM)
					{
						minDaValueM = daValue;
						minValueIndexM = myPointBuffersM.size() - 1;
					}
				}
				else if (pointIndex == 2)
				{
					myPointBufferL = new MyPoint(0, daValueLine);
					myPointBuffersL.add(myPointBufferL);
					myPointBufferL = null;
					
					daValue = SignalConfig.signalStartY - daValue;
					if (daValue > maxDaValueL)
					{
						maxDaValueL = daValue;
					}
					else if (daValue < minDaValueL)
					{
						minDaValueL = daValue;
						minValueIndexL = myPointBuffersL.size() - 1;
					}
				}
				pointIndex++;
			}
			else if (tempChar == '\n')
			{
				tempX++;
				pointIndex = 0;
			}
			else if (tempChar == '\r')
			{
				continue;
			}
			else
			{
				stringBufferTemp.append(tempChar);
			}
		}
		//daValue = daValue + 1;

		System.out.println("myPointBuffersH size=" + myPointBuffersH.size ());
		System.out.println("myPointBuffersM size=" + myPointBuffersM.size ());
		System.out.println("myPointBuffersL size=" + myPointBuffersL.size ());
	}
	

	int getCurrentMyPointBuffersH (int index)
	{
		index += displayStartPoint;
		if (myPointBuffersH != null && index < myPointBuffersH.size() && index >= 0)
			return SignalConfig.signalStartY - myPointBuffersH.get(index).pointY;
		else
			return 0;
	}
	int getCurrentMyPointBuffersM (int index)
	{
		index += displayStartPoint;
		if (myPointBuffersH != null && index < myPointBuffersM.size() && index >= 0)
			return SignalConfig.signalStartY - myPointBuffersM.get(index).pointY;
		else
			return 0;
	}
	int getCurrentMyPointBuffersL (int index)
	{
		index += displayStartPoint;
		if (myPointBuffersH != null && index < myPointBuffersL.size() && index >= 0)
			return SignalConfig.signalStartY - myPointBuffersL.get(index).pointY;
		else
			return 0;
	}
	public void addLineToSignalPanel(int startX, int startY)
	{
		timeLine = null;
		levelLine = null;
		timeLine = new Line(
					startX, 
					0, 
					startX, 
					1080, 
					new Color(255, 255, 0));
		levelLine = new Line(
				0, 
				startY, 
				1920, 
				startY, 
				new Color(255, 255, 0));
	}
	public void addRuler1ToSignalPane (int startY)
	{
		ruler1Line = null;
		ruler1Line = new Line(
				0, 
				startY, 
				1920, 
				startY, 
				new Color(200, 255, 255));
	}
	public void addRuler2ToSignalPane (int startY)
	{
		ruler2Line = null;
		ruler2Line = new Line(
				0, 
				startY, 
				1920, 
				startY, 
				new Color(255, 255, 200));
	}
	public void addTrigToSignalPane (int startX)
	{
		
	}
	
}
