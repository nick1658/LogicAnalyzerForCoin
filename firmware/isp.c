
/* Name: main.c
* Project: AVR-Doper
* Author: Christian Starkjohann <cs@obdev.at>
* Creation Date: 2006-06-21
* Tabsize: 4
* Copyright: (c) 2006 by Christian Starkjohann, all rights reserved.
* License: Proprietary, see documentation.
* Revision: $Id: main.c 223 2006-07-18 09:28:13Z cs $
*/

/*
General Description:
本模块实现硬件初始化和USB接口
*/

#include "hardware.h"
#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/pgmspace.h>
#include <avr/wdt.h>
#include <util/delay.h>
#include <string.h>
#include <avr/eeprom.h>


#include "utils.h"
#include "usbdrv.h"
#include "oddebug.h"
#include "stk500protocol.h"
#include "vreg.h"
#include "serial.h"

/* ------------------------------------------------------------------------- */
extern unsigned char eeprom_sckDuration;
extern void timerMsDelay(unsigned char ms);
extern unsigned char sckDuration;
/*---------- CDC class requests: --------------*/
enum {
SEND_ENCAPSULATED_COMMAND = 0,
GET_ENCAPSULATED_RESPONSE,
SET_COMM_FEATURE,
GET_COMM_FEATURE,
CLEAR_COMM_FEATURE,
SET_LINE_CODING = 0x20,
GET_LINE_CODING,
SET_CONTROL_LINE_STATE,
SEND_BREAK
};

/* defines for 'requestType' */
#define REQUEST_TYPE_LINE_CODING 0 /* CDC GET/SET_LINE_CODING */
#define REQUEST_TYPE_HID_FIRST 1 /* first block of HID reporting */
#define REQUEST_TYPE_HID_SUBSEQUENT 2 /* subsequent block of HID reporting */
#define REQUEST_TYPE_HID_DEBUGDATA 3 /* read/write data from/to debug interface */
#define REQUEST_TYPE_VENDOR 4 /* vendor request for get/set debug data */

/* ------------------------------------------------------------------------- */

#if ENABLE_CDC_INTERFACE
static uchar modeBuffer[7] = {0x80, 0x25, 0, 0, 0, 0, 8}; /* default: 9600 bps, 8n1 */
#endif
static uchar requestType;

#if ENABLE_CDC_INTERFACE
static PROGMEM char deviceDescrCDC[] = { /* USB 设备描述符 */
18, /* sizeof(usbDescriptorDevice): 描述符长度 */
USBDESCR_DEVICE, /* 描述答类型 */
0x01, 0x01, /* USB 版本 */
0x02, /* 设备类型: CDC */
0, /* 子类型 */
0, /* 协议 */
8, /* 最大包长 */
USB_CFG_VENDOR_ID, /* 2 bytes */
0xe1, 0x05, /* 2 bytes: shared PID for CDC-ACM devices */
USB_CFG_DEVICE_VERSION, /* 2 bytes */
1, /* 制造商字符串 index */
2, /* 生产字符串 index */
0, /* 序列号字符串 index */
1, /* 配置字数量 */
};

static PROGMEM char configDescrCDC[] = { /* USB 配置描述符 */
9, /* sizeof(usbDescriptorConfiguration): 描述符长度 */
USBDESCR_CONFIG, /* 描述答类型 */
67, 0, /* total length of data returned (including inlined descriptors) */
2, /* number of interfaces in this configuration */
1, /* index of this configuration */
0, /* configuration name string index */
#if USB_CFG_IS_SELF_POWERED
USBATTR_SELFPOWER, /* attributes */
#else
USBATTR_BUSPOWER, /* attributes */
#endif
USB_CFG_MAX_BUS_POWER/2, /* max USB current in 2mA units */

/* interface descriptors follow inline: */
/* Interface Descriptor for CDC-ACM Control */
9, /* sizeof(usbDescrInterface): length of descriptor in bytes */
USBDESCR_INTERFACE, /* descriptor type */
0, /* index of this interface */
0, /* alternate setting for this interface */
1, /* endpoints excl 0: number of endpoint descriptors to follow */
USB_CFG_INTERFACE_CLASS, /* see usbconfig.h */
USB_CFG_INTERFACE_SUBCLASS,
USB_CFG_INTERFACE_PROTOCOL,
0, /* string index for interface */

/* CDC Class-Specific descriptors */
5, /* sizeof(usbDescrCDC_HeaderFn): length of descriptor in bytes */
0x24, /* descriptor type */
0, /* Subtype: header functional descriptor */
0x10, 0x01, /* CDC spec release number in BCD */

4, /* sizeof(usbDescrCDC_AcmFn): length of descriptor in bytes */
0x24, /* descriptor type */
2, /* Subtype: abstract control management functional descriptor */
0x02, /* capabilities: SET_LINE_CODING, GET_LINE_CODING, SET_CONTROL_LINE_STATE */

5, /* sizeof(usbDescrCDC_UnionFn): length of descriptor in bytes */
0x24, /* descriptor type */
6, /* Subtype: union functional descriptor */
0, /* CDC_COMM_INTF_ID: master interface (control) */
1, /* CDC_DATA_INTF_ID: slave interface (data) */

5, /* sizeof(usbDescrCDC_CallMgtFn): length of descriptor in bytes */
0x24, /* descriptor type */
1, /* Subtype: call management functional descriptor */
0x03, /* capabilities: allows management on data interface, handles call management by itself */
1, /* CDC_DATA_INTF_ID: interface used for call management */

/* Endpoint Descriptor */
7, /* sizeof(usbDescrEndpoint) */
USBDESCR_ENDPOINT, /* descriptor type = endpoint */
0x83, /* IN endpoint number 3 */
0x03, /* attrib: Interrupt endpoint */
8, 0, /* maximum packet size */
100, /* in ms */

/* Interface Descriptor for CDC-ACM Data */
9, /* sizeof(usbDescrInterface): length of descriptor in bytes */
USBDESCR_INTERFACE, /* descriptor type */
1, /* index of this interface */
0, /* alternate setting for this interface */
2, /* endpoints excl 0: number of endpoint descriptors to follow */
0x0a, /* Data Interface Class Codes */
0, /* interface subclass */
0, /* Data Interface Class Protocol Codes */
0, /* string index for interface */

/* Endpoint Descriptor */
7, /* sizeof(usbDescrEndpoint) */
USBDESCR_ENDPOINT, /* descriptor type = endpoint */
0x01, /* OUT endpoint number 1 */
0x02, /* attrib: Bulk endpoint */
8, 0, /* maximum packet size */
0, /* in ms */

/* Endpoint Descriptor */
7, /* sizeof(usbDescrEndpoint) */
USBDESCR_ENDPOINT, /* descriptor type = endpoint */
0x81, /* IN endpoint number 1 */
0x02, /* attrib: Bulk endpoint */
8, 0, /* maximum packet size */
0, /* in ms */
};
#endif /* ENABLE_CDC_INTERFACE */

/* ------------------------------------------------------------------------- */
uchar usbFunctionDescriptor(usbRequest_t *rq)
{
uchar *p = NULL, len = 0 ;

#if ENABLE_CDC_INTERFACE
if(rq->wValue.bytes[1] == USBDESCR_DEVICE){
p = (uchar *)deviceDescrCDC;
len = sizeof(deviceDescrCDC);
}else{ /* must be config descriptor */
p = (uchar *)configDescrCDC;
len = sizeof(configDescrCDC);
}
#endif
usbMsgPtr = p;
return len;
}


/* ------------------------------------------------------------------------- */
/* ----------------------------- USB interface ----------------------------- */
/* ------------------------------------------------------------------------- */

uchar usbFunctionSetup(uchar data[8])
{
usbRequest_t *rq = (void *)data;
uchar rqType = rq->bmRequestType & USBRQ_TYPE_MASK;

if(rqType == USBRQ_TYPE_CLASS)
{ /* class request type */
#if ENABLE_CDC_INTERFACE
if(rq->bRequest == GET_LINE_CODING || rq->bRequest == SET_LINE_CODING){
requestType = REQUEST_TYPE_LINE_CODING;
return 0xff;
}
#endif
}
return 0;
}

uchar usbFunctionRead(uchar *data, uchar len)
{
if(requestType == REQUEST_TYPE_LINE_CODING)
{
#if ENABLE_CDC_INTERFACE
/* return the "virtual" configuration */
memcpy(data, modeBuffer, 7);
return 7;
#endif
}
return 0; /* error -> terminate transfer */
}

uchar usbFunctionWrite(uchar *data, uchar len)
{
if(requestType == REQUEST_TYPE_LINE_CODING)
{
#if ENABLE_CDC_INTERFACE
/* Don't know why data toggling is reset when line coding is changed, but it is... */
usbTxPacketCnt1 = 1; /* enforce DATA0 token for next transfer */
/* store the line configuration so that we can return it on request */
memcpy(modeBuffer, data, 7);
return 1;
#endif
}
return 1; /* error -> accept everything until end */
}

void usbFunctionWriteOut(uchar *data, uchar len)
{
#if ENABLE_CDC_INTERFACE
do{ /* len must be at least 1 character, the driver discards zero sized packets */
stkSetRxChar(*data++);
}while(--len);
#endif
}

/* ------------------------------------------------------------------------- */
/* ------------------------------------------------------------------------- */
/* ------------------------------------------------------------------------- */


/***********************************************************
20 脚 HVSP / PP 连接器:
1 .... GND
2 .... Vtarget
3 .... HVSP SCI
4 .... RESET
16 ... HVSP SDO
18 ... HVSP SII
20 ... HVSP SDI

* Timer 用途:
* Timer 0 [8 bit]:
* 1/64 prescaler for timer interrupt
* Timer 1 [16 bit]:
* PWM 用于高压产生 -> fastPWM mode
* f = 23.4 kHz -> prescaler = 1, 9 bit
* Timer 2 [8 bit]:
* 为目标板提供时钟
**************************************************************/

static void hardwareInit(void)
{
unsigned char i, j;
//-------------------------------------------------------------------------------------
// SCK MISO MOSI ISP_RST SMPS_OUT HVSP_SUPPLY
PORTB= 0x10;// 0 1 0 0 0 0
DDRB = 0x28;// 1 0 1 0 0 0
//-------------------------------------------------------------------------------------
// ISP_RESET _HVSP_HVRESET _HVSP_SCI LED ADC_VTARGET ADC_SMPS
PORTC= 0x00;// 0 0 0 0 0 0
DDRC = 0x3C;// 1 1 1 1 0 0
//-------------------------------------------------------------------------------------
// HVSP_SDO HVSP_SDI HVSP_SII JUMPER USB_D- USB_D+ TXD(ISP_RXD) RXD(ISP_TXD)
PORTD= 0x10;// 0 0 0 1 0 0 1 1
DDRD = 0x0C;// 0 0 0 0 1 1 0 1
//------------------------------------------------------------------------------------------
j = 0;
while(--j){ /* USB Reset by device only required on Watchdog Reset */
i = 0;
while(--i); /* delay >10ms for USB reset */
}

DDRD &=~( (1<<_USB_DPLUS)|(1<<_USB_DMINUS) );

/*-------------- T0 配置: ~ 1.365 ms interrupt -----------------*/
TCCR0 = 3; // 1/64 分频
TIMSK = (1 << TOIE0); // 使能 timer0 溢出中断

/*-------------- T1 配置: ~23.4 kHz PWM (9 bit) ----------------*/
TCCR1A = UTIL_BIN8(1000, 0010); // OC1A = PWM, OC1B disconnected, 9 bit
TCCR1B = UTIL_BIN8(0000, 1001); // 9 bit, prescaler=1
OCR1A = 1; // 设占空比为最小

/*-------------- T2 配置 ---------------------------------------*/
TCCR2 = UTIL_BIN8(0000, 1001); // OC2 disconnected, prescaler=1
OCR2 = 0; // 产生 3 MHz 时钟
}

int main(void)
{
	wdt_enable(WDTO_1S);
	odDebugInit();

	hardwareInit();
	vregInit();
	usbInit();

	sckDuration=eeprom_read_byte(&eeprom_sckDuration);
	if(sckDuration==0xFF) sckDuration=3;

	sei();
	//---------------------------
	PORTC |= (1<<PC2); // 亮灯
	timerMsDelay(250);
	PORTC &=~(1<<PC2); // 灭灯
	//---------------------------
	for(;;)
	{ /* main event loop */
		wdt_reset();
		usbPoll();
		#if ENABLE_CDC_INTERFACE
		if( usbInterruptIsReady())
		{
			static uchar sendEmptyFrame = 1, buffer[8];
			// 开始用空帧是因为主机吃掉了第一个包 -- don't know why...
			int c;
			uchar i = 0;
			while(i < 8 && (c = stkGetTxByte()) >= 0)
			{ 
				buffer[i++] = c; 
			}
			if(i > 0 || sendEmptyFrame)
			{
				sendEmptyFrame = i; /* send an empty block after last data block to indicate transfer end */
				usbSetInterrupt(buffer, i);
			}
		}
		#endif

	}
return 0;
}
