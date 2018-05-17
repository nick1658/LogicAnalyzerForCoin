import java.awt.Color;


public class Line
{
	 
	private static final long serialVersionUID = 1L;
	public int startX, startY, endX, endY; 
	Color color; 
	public Line()
	{
		// TODO Auto-generated constructor stub
		this.startX = 0; 
		this.startY = 0 ; 
		this.endX = 0;
		this.endY = 0;
		this.color = null; 
	}
	Line(int startX, int startY, int endX, int endY, Color color)//, int boarder) 
	{ 
		this.startX = startX; 
		this.startY = startY ; 
		this.endX = endX;
		this.endY = endY;
		this.color = color; 
		//this.boarder = boarder; 
	} 
} 

 
