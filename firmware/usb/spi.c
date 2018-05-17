
//SPI˫��ͨ�� �ӻ�
//����0x06,PA0~3����
//��������ͷ�ļ�
#include <avr/io.h>
#include <avr/interrupt.h>
#include "spi.h"
#include "status.h"

/*------�궨��------*/
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

/*------��������------*/
void spi_write(uchar sData);
uchar receive_buffer[BUFFER_SIZE];
uchar spi_read(void);

uchar send_buffer;
uchar buffer_full;
uchar bytesRemaining;
uchar currentPosition;
uchar buffer_index;

extern uchar status;

//�˿ڳ�ʼ��
void port_init(void)
{
	PORTB |= (1 << PB2) | (1 << PB3) |
			(1 << PB4) | (1 << PB5); //SCK MISO MOSI SS ʹ������     
	DDRB &= ~((1 << PB5) | (1 << PB3) | (1 << PB2) | (0 << PB4));   // SCK MOSI SS ��Ϊ����MISO ��Ϊ���
	DDRC = 0xff;
	PORTC = 0x00;
	ledRedOn();
	ledGreenOff();
}

void spi_init(void) //spi��ʼ��
{ 
	SPCR = 0xC1; //���ݵ� MSB ���ȷ���
	SPSR = 0x00;
}

SIGNAL(SIG_SPI) //һ���ֽڷ��ͻ��������ж�
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

//����:ʹ��SPI����һ���ֽ�
void spi_write(uchar sData)
{
   SPDR = sData;
}

/*
//����:ʹ��SPI����һ���ֽ�
uchar spi_read(void)
{
   SPDR = 0x00;
   while(!(SPSR & BIT(SPIF)));
   return SPDR;
} */

void init_spi_device(void)
{
	cli(); //��ֹ�����ж�
	MCUCR = 0x00;
	MCUCSR = 0x80;//��ֹJTAG
	GICR   = 0x00;
	send_buffer = 0;
	port_init();
	spi_init();
	spi_write(0xfa);
}