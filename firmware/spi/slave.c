
//SPI双机通信 从机
//发送0x06,PA0~3接收
//包含所需头文件
#include <avr/io.h>
#include <avr/interrupt.h>

/*------宏定义------*/
#define uchar unsigned char
#define uint unsigned int
#define BIT(x) (1<<(x))
#define NOP() asm("nop")
#define WDR() asm("wdr")

#define ledRedOn()    PORTC &= ~(1 << PC1)
#define ledRedOff()   PORTC |= (1 << PC1)
#define ledGreenOn()  PORTC &= ~(1 << PC0)
#define ledGreenOff() PORTC |= (1 << PC0)
/*------函数定义------*/
void spi_write(uchar sData);
uchar spi_read(void);

uchar receiveDat;

//端口初始化
void port_init(void)
{
	PORTB |= (1 << PB2) | (1 << PB3) |
			(1 << PB4) | (1 << PB5); //SCK MISO MOSI SS 使能上拉     
	DDRB &= ~((1 << PB5) | (1 << PB3) | (1 << PB2));   // SCK MOSI SS 置为输入MISO 置为输出
	DDRC = 0xff;
	PORTC = 0x00;
	ledRedOn();
	ledGreenOff();
}

void spi_init(void) //spi初始化
{ 
   DDRB |= (1 << PB4) | (~(1 << PB5)) | (~(1<<PB3))|(~(1<<PB2));
   SPCR = 0xC1; //数据的 MSB 首先发送
   SPSR = 0x00;
}

SIGNAL(SIG_SPI) //一个字节发送或接收完成中断
{
	ledRedOff();
	receiveDat = SPDR;//spi_read();
}

//功能:使用SPI发送一个字节
void spi_write(uchar sData)
{
   SPDR = sData;
   //while(!(SPSR & BIT(SPIF)));
   //sData=SPDR;//读从机发回来的数据
}


//功能:使用SPI接收一个字节
uchar spi_read(void)
{
   SPDR = 0x00;
   while(!(SPSR & BIT(SPIF)));
   return SPDR;
} 


void init_devices(void)
{
   cli(); //禁止所有中断
   MCUCR = 0x00;
   MCUCSR = 0x80;//禁止JTAG
   GICR   = 0x00;
   port_init();
   spi_init();
   sei();//开全局中断
}
//主函数
int main(void)
{
	char flag = 1;
	init_devices();
	spi_write(0xfa);

	ledGreenOn();
	while(1)
	{
		if (receiveDat == 0x75)
		{
			receiveDat = 0;
			if (flag)
			{
				flag = 0;
				ledGreenOn();
			}
			if (!flag)
			{
				flag = 1;
				ledGreenOff ();
			}
		}
   }
   return 0;
} 