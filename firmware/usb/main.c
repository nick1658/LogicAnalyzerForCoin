/* Name: main.c
 * Project: PowerSwitch based on AVR USB driver
 * Author: Christian Starkjohann
 * Creation Date: 2005-01-16
 * Tabsize: 44
 * Copyright: (c) 2005 by OBJECTIVE DEVELOPMENT Software GmbH
 * License: GNU GPL v2 (see License.txt) or proprietary (CommercialLicense.txt)
 * This Revision: $Id: main.c 523 2008-02-15 09:46:40Z cs $
 */

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/pgmspace.h>
#include <avr/eeprom.h>
#include <avr/wdt.h>

#include "usbdrv.h"
#include "oddebug.h"
#include <util/delay.h>

#include "spi.h"
#include "status.h"


#define NOP() asm("nop")
#define WDR() asm("wdr")

#define PSCMD_BUFFER_READ   4
#define PSCMD_BUFFER_WRITE   5

#define SAMPLE_DEPTH 8096

/*
This module implements an 8 bit parallel output controlled via USB. It is
intended to switch the power supply to computers and/or other electronic
devices.

Application examples:
- Rebooting computers located at the provider's site
- Remotely switch on/off of rarely used computers
- Rebooting other electronic equipment which is left unattended
- Control room heating from remote
*/

#define ledRedOn()    PORTC &= ~(1 << PC1)
#define ledRedOff()   PORTC |= (1 << PC1)
#define ledGreenOn()  PORTC &= ~(1 << PC0)
#define ledGreenOff() PORTC |= (1 << PC0)

#define isBusy()     PORTC &= ~(1 << PC2)
#define isReady()    PORTC |= (1 << PC2)

#define USB_NO_MSG 0xff;

uchar ready_for_config;
uchar status;
unsigned int total_bytes;
extern uchar send_buffer;
extern uchar buffer_full;
extern uchar receive_buffer[BUFFER_SIZE];
extern uchar bytesRemaining;
extern uchar currentPosition;
extern uchar buffer_index;

#if USB_CFG_IMPLEMENT_FN_READ
uchar usbFunctionRead(uchar *data, uchar len)
{
    uchar i;
    if(len > bytesRemaining)                // len is max chunk size
        len = bytesRemaining;               // send an incomplete chunk
    bytesRemaining -= len;
    for(i = 0; i < len; i++)
        data[i] = receive_buffer[currentPosition++]; // copy the data to the buffr
	ledRedOff();
    return len;                             // return real chunk size
}
#endif

#if USB_CFG_IMPLEMENT_FN_WRITE
uchar usbFunctionWrite(uchar *data, uchar len)
{
    uchar i;
    if(len > bytesRemaining)                // if this is the last incomplete chunk
        len = bytesRemaining;               // limit to the amount we can store
    bytesRemaining -= len;
    for(i = 0; i < len; i++)
        receive_buffer[currentPosition++] = data[i];

	if (bytesRemaining == 0)
	{
		if (receive_buffer[currentPosition - 1] == 0x55)
		{
			ready_for_config = 1; // <----------- debug
			ledRedOn();
		}
	}
    return bytesRemaining == 0;             // return 1 if we have all data
}

#endif

USB_PUBLIC uchar usbFunctionSetup(uchar data[8])
{
	usbRequest_t    *rq = (void *)data;

	switch (rq->bRequest)
	{
	case PSCMD_BUFFER_READ: // read
		if (buffer_full)
		{
			buffer_full = 0;
			currentPosition = 0;                // initialize position index
			bytesRemaining = rq->wLength.word;  // store the amount of data requested
			return USB_NO_MSG;// tell driver where the buffer starts
		}
		else
		{
			return 8;//no data
		}
		break;
	case PSCMD_BUFFER_WRITE: //write
		currentPosition = 0;                // initialize position index
        bytesRemaining = rq->wLength.word;  // store the amount of data requested
        if(bytesRemaining > BUFFER_SIZE) // limit to buffer size
            bytesRemaining = BUFFER_SIZE;
		return USB_NO_MSG;// tell driver where the buffer starts
		break;
	default:break;
	}

    return 0;
}

 
/* allow some inter-device compatibility */
#if !defined TCCR0 && defined TCCR0B
#define TCCR0   TCCR0B
#endif
#if !defined TIFR && defined TIFR0
#define TIFR    TIFR0
#endif

int main(void)
{
	uchar   i;

	bytesRemaining = 0;
	currentPosition = 0;
	buffer_index = 0;
	ready_for_config = 0;
	status = CONFIG;
	total_bytes = 0;
	
	DDRC = 0x07;          /* all inputs except PC0, PC1, PC2*/
	PORTC = 0xfb;
	
    //wdt_enable(WDTO_1S);
    //odDebugInit();
    DDRD = ~(1 << 2);   /* all outputs except PD2 = INT0 */
    PORTD = 0;
    PORTB = 0;          /* no pullups on USB pins */
/* We fake an USB disconnect by pulling D+ and D- to 0 during reset. This is
 * necessary if we had a watchdog reset or brownout reset to notify the host
 * that it should re-enumerate the device. Otherwise the host's and device's
 * concept of the device-ID would be out of sync.
 */
	PORTB = 0x04;          /* no pullups on USB pins */
    DDRB = ~USBMASK;    /* set all pins as outputs except USB */
	
	init_spi_device (); // init spi device
	
	ledRedOn();
	isBusy ();
    usbDeviceDisconnect();  /* enforce re-enumeration, do this while interrupts are disabled! */
    i = 0;
    while(--i)
	{         
		/* fake USB disconnect for > 500 ms */
        //wdt_reset();
        _delay_ms(2);
    }
    usbDeviceConnect();
	
    usbInit();
    sei();
    for(;;)
	{   /* main event loop */
        //wdt_reset();
        usbPoll();
		switch (status)
		{
		case CONFIG:
			if (ready_for_config)
			{
				ready_for_config = 0;
				spi_write (CONFIG);
				isReady (); // 1
				NOP ();
				NOP ();
				isBusy(); // 0
				//status = SEND_DATA;
			}
			break;
		case SEND_DATA:
			if (total_bytes < SAMPLE_DEPTH)
			{
				if (!buffer_full)
				{
					
					spi_write (SEND_DATA);
					isReady (); // 1
					NOP ();
					NOP ();
					isBusy(); // 0
				}
			}
			else
			{
				total_bytes = 0;
				status = CONFIG;
			}
			if (buffer_index == 128)
			{
				total_bytes += 128;
				buffer_index = 0;
				buffer_full = 1;
			}
			break;
		default:break;
		}
    }
    return 0;
}

