import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;



public class TimePanel extends JPanel
{
	private static int timeStringStart = 10;
	private static final long serialVersionUID = 1L;
	private ArrayList<Line> timeRuler;
	private ArrayList<TimeString> timeStrings;
	protected void paintComponent(Graphics g)
	{		
		super.paintComponent(g);
		int i = 0;
		
		g.setColor(new Color(255, 255, 255));
		if (timeRuler != null)
		{
			i = timeRuler.size();
			while ((i--) > 0)
			{
				Line line = timeRuler.get(i);
				g.setColor(line.color);
				g.drawLine(line.startX, line.startY, line.endX, line.endY);
			}
		}
		if (timeStrings != null)
		{
			TimeString timeString;
			i = timeStrings.size();
			while ((i--) > 0)
			{
				timeString = timeStrings.get(i);	
				g.drawString(timeString.string, timeString.startX, timeString.startY);
			}
		}
	}
	public void drawTimeLine (int startTime, int signalWidth)
	{
		int i = 0;
		int j = 0;
		int m = 0;
		int n = 0;
		int h = 0;
		int preStringLoc = 0;
		Line pointBuffer;
		timeRuler = null;
		timeStrings = null;
		timeRuler = new ArrayList<Line>();
		timeStrings = new ArrayList<TimeString>();

		for (i = 0; i < (1800/signalWidth); i++)
		{
			h = i + startTime;
			if (h % 5 == 0)
			{
				m = i * (signalWidth) + SignalConfig.signalStartX;

				j = 1;
				if (h % 10 == 0)
				{
					if (m - preStringLoc > 40 || i == 0)
					{
						n = 8;
						String unit = null;
						float tempStartTime = (i + startTime) * 40;
						/*if (tempStartTime < 1000)
						{
							unit = "ns";
						}
						else */
						if (tempStartTime <1000000)
						{
							unit = "us";
							tempStartTime /= 1000.0;
						}
						else if (tempStartTime <1000000000)
						{
							unit = "ms";
							tempStartTime /= 1000.0;
							tempStartTime /= 1000.0;
						}
						else 
						{
							unit = "s";
							tempStartTime /= 1000.0;
							tempStartTime /= 1000.0;
							tempStartTime /= 1000.0;
						}
						timeStrings.add(new TimeString(tempStartTime + unit,
								m, 
								timeStringStart,
								new Color(255, 255, 255)));
						preStringLoc = m;
					}
				}
				else 
				{
					n = 4;
				}
			}
			else 
			{
				j = 0;
			}
			pointBuffer = new Line(
					signalWidth * (i) + 1 + SignalConfig.signalStartX, 
					30 - (j * n) - timeStringStart,
					signalWidth * (i) + 1 + SignalConfig.signalStartX, 
					30 - timeStringStart, 
					new Color(255, 255, 255));//,  boarder)
			timeRuler.add(pointBuffer);
		}
	}
	public TimePanel()
	{
		// TODO Auto-generated constructor stub
		super();
		this.setBackground(new Color(20, 30, 25));
	}	
}
