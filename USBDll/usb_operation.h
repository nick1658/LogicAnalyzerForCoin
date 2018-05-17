#ifndef _USB_OPERATION_
#define _USB_OPERATION_

#define USBDEV_SHARED_VENDOR    0x16C0  /* VOTI */
#define USBDEV_SHARED_PRODUCT   0x05DC  /* Obdev's free shared PID */
/* Use obdev's generic shared VID/PID pair and follow the rules outlined
 * in firmware/usbdrv/USBID-License.txt.
 */

#define PSCMD_ECHO  0
#define PSCMD_GET   1
#define PSCMD_ON    2
#define PSCMD_OFF   3
#define PSCMD_BUFFER_READ   4
#define PSCMD_BUFFER_WRITE   5
/* These are the vendor specific SETUP commands implemented by our USB device */

#define USB_ERROR_NOTFOUND  1
#define USB_ERROR_ACCESS    2
#define USB_ERROR_IO        3

int usbOpenDevice(usb_dev_handle **device, int vendor, char *vendorName, int product, char *productName);

#endif