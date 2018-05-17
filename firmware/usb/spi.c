
//SPI双机通信 从机
//发送0x06,PA0~3接收
//包含所需头文件
#include <avr/io.h>
#include <avr/interrupt.h>
#include "spi.h"
#include "status.h"

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

#define isBusy()    PORTC &= ~(1 << PC2)
#define isReady()    PORTC |= (1 << PC2)

/*------函数定义------*/
void spi_write(uchar sData);
uchar receive_buffer[BUFFER_SIZE];
uchar spi_read(void);

uchar send_buffer;
uchar buffer_full;
uchar bytesRemaining;
uchar currentPosition;
uchar buffer_index;

extern uchar status;

//端口初始化
void port_init(void)
{
	PORTB |= (1 << PB2) | (1 << PB3) |
			(1 << PB4) | (1 << PB5); //SCK MISO MOSI SS 使能上拉     
	DDRB &= ~((1 << PB5) | (1 << PB3) | (1 << PB2) | (0 << PB4));   // SCK MOSI SS 置为输入MISO 置为输出
	DDRC = 0xff;
	PORTC = 0x00;
	ledRedOn();
	ledGreenOff();
}

void spi_init(void) //spi初始化
{ 
	SPCR = 0xC1; //数据的 MSB 首先发送
	SPSR = 0x00;
}

SIGNAL(SIG_SPI) //一个字节发送或接收完成中断
{
	switch (SPDR)
	{
	case 0xa1:
		currentPosition = 0;
		SPDR = receive_buffer[currentPosition++];
		break;
	case 0xff:
		SPDR = receive_buffer[currentPosition++];
		break;
	case 0xa2:
		status = SEND_DATA;
		//buffer_index = 0;
		break;
	case 0xa4:
		break;
	default:
		receive_buffer[buffer_index++] = SPDR;
		break;
	}
}

//功能:使用SPI发送一个字节
void spi_write(uchar sData)
{
   SPDR = sData;
}

/*
//功能:使用SPI接收一个字节
uchar spi_read(void)
{
   SPDR = 0x00;
   while(!(SPSR & BIT(SPIF)));
   return SPDR;
} */

void init_spi_device(void)
{
	cli(); //禁止所有中断
	MCUCR = 0x00;
	MCUCSR = 0x80;//禁止JTAG
	GICR   = 0x00;
	send_buffer = 0;
	port_init();
	spi_init();
	spi_write(0xfa);
}