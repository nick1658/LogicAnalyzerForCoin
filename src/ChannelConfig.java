
public class ChannelConfig
{
	public int startX, startY;
	public int endX, endY;
	public int positionY;
	public boolean lowLevel;
	public boolean levelChanged;
	public ChannelConfig()
	{
		// TODO Auto-generated constructor stub
		startX = 0;
		startY = 0;
		endX = 0;
		endY = 0;
		lowLevel = true;
		levelChanged = false;
	}
	public ChannelConfig(
			int startX, int startY,
			int endX, int endY,
			int positionY,
			boolean lowLevel, boolean levelChanged)
	{
		// TODO Auto-generated constructor stub
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.lowLevel = true;
		this.levelChanged = levelChanged;
		this.positionY = positionY;
	}
}
