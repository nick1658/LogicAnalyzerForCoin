/* Name: main.c
 * Project: AVR bootloader HID
 * Author: Christian Starkjohann
 * Creation Date: 2007-03-19
 * Tabsize: 4
 * Copyright: (c) 2007 by OBJECTIVE DEVELOPMENT Software GmbH
 * License: Proprietary, free under certain conditions. See Documentation.
 * This Revision: $Id: main.c 787 2010-05-30 20:54:25Z cs $
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include "usbcalls.h"

/* ------------------------------------------------------------------------- */
static char dataBuffer[65536 + 256];    /* buffer for file data */
static int  startAddress, endAddress;
static int count;
static int  parseUntilColon(FILE *fp)
{
int c;

    do{
        c = getc(fp);
    }while(c != ':' && c != EOF);
    return c;
}

static int  parseHex(FILE *fp, int numDigits)
{
int     i;
char    temp[9];

    for(i = 0; i < numDigits; i++)
        temp[i] = getc(fp);
    temp[i] = 0;
    return strtol(temp, NULL, 16);
}

/* ------------------------------------------------------------------------- */

static int  parseIntelHex(char *hexfile, char buffer[65536 + 256], int *startAddr, int *endAddr)
{
int     address, base, d, segment, i, lineLen, sum;
FILE    *input;

    input = fopen(hexfile, "r");
    if(input == NULL){
        fprintf(stderr, "error opening %s: %s\n", hexfile, strerror(errno));
        return 1;
    }
    while(parseUntilColon(input) == ':')
	{
        sum = 0;
        sum += lineLen = parseHex(input, 2);
        base = address = parseHex(input, 4);
        sum += address >> 8;
        sum += address;
        sum += segment = parseHex(input, 2);  /* segment value? */
        if(segment != 0)    /* ignore lines where this byte is not 0 */
            continue;
        for(i = 0; i < lineLen ; i++)
		{
            d = parseHex(input, 2);
            buffer[address++] = d;
			count++;
            sum += d;
        }
        sum += parseHex(input, 2);
        if((sum & 0xff) != 0){
            fprintf(stderr, "Warning: Checksum error between address 0x%x and 0x%x\n", base, address);
        }
        if(*startAddr > base)
            *startAddr = base;
        if(*endAddr < address)
            *endAddr = address;
    }
    fclose(input);
    return 0;
}
static void printUsage(char *pname)
{
    fprintf(stderr, "usage: %s [-r] [<intel-hexfile>]\n", pname);
}

int main(int argc, char **argv)
{
char    *file = NULL;

    if(argc < 2)
	{
        printUsage(argv[0]);
        return 1;
    }
    if(strcmp(argv[1], "-h") == 0 || strcmp(argv[1], "--help") == 0)
	{
        printUsage(argv[0]);
        return 1;
    }
    if(strcmp(argv[1], "-r") == 0)
	{
        if(argc >= 3)
		{
            file = argv[2];
        }
    }
	else
	{
        file = argv[1];
    }
	count = 0;
    startAddress = sizeof(dataBuffer);
    endAddress = 0;
    if(file != NULL)
	{   // an upload file was given, load the data
        memset(dataBuffer, -1, sizeof(dataBuffer));
        if(parseIntelHex(file, dataBuffer, &startAddress, &endAddress))
            return 1;
        if(startAddress >= endAddress)
		{
            fprintf(stderr, "No data in input file, exiting.\n");
            return 0;
        }
		if (count < 1024)
			printf ("程序大小: %d 字节\n", count);
		else if (count < 1024 * 1024)
			printf ("程序大小: %d K字节\n", count / 1024);
    }
    return 0;
}

/* ------------------------------------------------------------------------- */


