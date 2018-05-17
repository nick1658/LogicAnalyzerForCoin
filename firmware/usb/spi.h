#ifndef SPI_H
#define SPI_H

#define uchar unsigned char
#define BUFFER_SIZE 128

void spi_write(uchar sData);
void spi_init(void); //spi≥ı ºªØ
void init_spi_device(void);
void getInterruptData (uchar **pData);

#endif