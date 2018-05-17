
//SPI˫��ͨ�� �ӻ�
//����0x06,PA0~3����
//��������ͷ�ļ�
#include <avr/io.h>
#include <avr/interrupt.h>

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
/*------��������------*/
void spi_write(uchar sData);
uchar spi_read(void);

uchar receiveDat;

//�˿ڳ�ʼ��
void port_init(void)
{
	PORTB |= (1 << PB2) | (1 << PB3) |
			(1 << PB4) | (1 << PB5); //SCK MISO MOSI SS ʹ������     
	DDRB &= ~((1 << PB5) | (1 << PB3) | (1 << PB2));   // SCK MOSI SS ��Ϊ����MISO ��Ϊ���
	DDRC = 0xff;
	PORTC = 0x00;
	ledRedOn();
	ledGreenOff();
}

void spi_init(void) //spi��ʼ��
{ 
   DDRB |= (1 << PB4) | (~(1 << PB5)) | (~(1<<PB3))|(~(1<<PB2));
   SPCR = 0xC1; //���ݵ� MSB ���ȷ���
   SPSR = 0x00;
}

SIGNAL(SIG_SPI) //һ���ֽڷ��ͻ��������ж�
{
	ledRedOff();
	receiveDat = SPDR;//spi_read();
}

//����:ʹ��SPI����һ���ֽ�
void spi_write(uchar sData)
{
   SPDR = sData;
   //while(!(SPSR & BIT(SPIF)));
   //sData=SPDR;//���ӻ�������������
}


//����:ʹ��SPI����һ���ֽ�
uchar spi_read(void)
{
   SPDR = 0x00;
   while(!(SPSR & BIT(SPIF)));
   return SPDR;
} 


void init_devices(void)
{
   cli(); //��ֹ�����ж�
   MCUCR = 0x00;
   MCUCSR = 0x80;//��ֹJTAG
   GICR   = 0x00;
   port_init();
   spi_init();
   sei();//��ȫ���ж�
}
//������
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