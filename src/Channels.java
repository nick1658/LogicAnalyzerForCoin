
public class Channels
{
	public ChannelConfig[] channelArray;
	
	public Channels(int signalWidth)
	{
		// TODO Auto-generated constructor stub
		channelArray = new ChannelConfig[8];
		for (int i = 0; i < 8; i++)
		{
			channelArray[i] =  new ChannelConfig( 
					SignalConfig.busStartX, 
					SignalConfig.busStartY + SignalConfig.busDistance * i, 
				  	SignalConfig.busStartX + signalWidth,
					SignalConfig.busStartY + SignalConfig.busDistance * i, 
					SignalConfig.busStartY + SignalConfig.busDistance * i,
					true,
					false
				  	);
		}
	}
}
