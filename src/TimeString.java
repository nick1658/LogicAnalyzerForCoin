import java.awt.Color;


public class TimeString
{
	public int startX, startY; 
	public String string;
	public Color color; 
	
	public TimeString()
	{
		// TODO Auto-generated constructor stub
		this.startX = 0;
		this.startY = 0;
		color = null;
	}
	TimeString (String string, int startX, int startY, Color color)
	{
		this.string = string;
		this.startX = startX;
		this.startY = startY;
		this.color = color;
	}
}
