/*libpcap test program*/
#include <pcap.h> 
#include <string.h> 
#include <stdlib.h> 
//#include <netinet/in.h>
//#include <net/ieee80211_radiotap.h>
#include <stdio.h>
#include <errno.h>
#include "radiotap-parser.h"
//This header is probably architecture specific, included in radiotap-parser.h
//#include "ieee80211_radiotap.h"
#define MAXBYTES2CAPTURE 2048 

//declare the processpacket function
void processPacket(u_char *arg, const struct pcap_pkthdr* pkthdr, const u_char * packet);



int main(int argc, char *argv[] ){ 
    
 int i=0, count=0; 
 pcap_t *handle = NULL; 
 char errbuf[PCAP_ERRBUF_SIZE], *device=NULL; 
 memset(errbuf,0,PCAP_ERRBUF_SIZE); 

 if( argc > 1){  /* If user supplied interface name, use it. */
    device = argv[1];
 }
 else{  /* Get the name of the first device suitable for capture */ 

    if ( (device = pcap_lookupdev(errbuf)) == NULL){
        fprintf(stderr, "ERROR: %s\n", errbuf);
        exit(1);
    }
 }

 printf("Opening device %s\n", device); 
 
 /* Open device in promiscuous mode */ 
 if ( (handle = pcap_open_live(device, MAXBYTES2CAPTURE, 1,  512, errbuf)) == NULL){
    fprintf(stderr, "ERROR: %s\n", errbuf);
    exit(1);
 }

 /* datalink types */	
 int *dlt_buf;         /* array of supported data link types */
 int num;              /* number of supported link type */
 int x;                /* counter for for loop */

 num = pcap_list_datalinks(handle, &dlt_buf);

 for (i=0; i<num; i++) {
     printf("%d - %s - %s\n",dlt_buf[i],
               pcap_datalink_val_to_name(dlt_buf[i]),
               pcap_datalink_val_to_description(dlt_buf[i]));
  }
	
  printf("Trying to set datalink type...\n");
 /* Set datalink type to radiotap header format */
 if (pcap_set_datalink (handle, DLT_IEEE802_11_RADIO) == -1) {
			printf("Setting datalink type failed.\n");
      pcap_perror (handle, "Error on pcap_set_datalink: ");
      exit (1);
 }

 printf("Starting capture loop....\n");
 /* Loop forever & call processPacket() for every received packet*/ 
 if ( pcap_loop(handle, -1, processPacket, (u_char *)&count) == -1){
    fprintf(stderr, "ERROR: %s\n", pcap_geterr(handle) );
    exit(1);
 }


 exit(0);
}

/* processPacket(): Callback function called by pcap_loop() everytime a packet */
/* arrives to the network card.                                              */
void processPacket(u_char *arg, const struct pcap_pkthdr* pkthdr, const u_char * packet){ 
/* pkthdr is usefull for the packet information such as length or fragment size */
/* packet is a pointer to start byte of the packet, use typecasting and pointer magic to extract the data from the packet */					 
/* use IEEE80211_RADIOTAP_HDRLEN to skip past the radiotap header */		
/* use the radiotap parser functions to extract data from the radiotap frame */
/* extract the source  mac-address from the encapsulated 802.11 frame */	
	
	//Struct pointing to the radiotap header
	struct ieee80211_radiotap_header* radiotapheader;
	//Struct pointing to the iterator used by the radiotap parser functions
	struct ieee80211_radiotap_iterator iterator;

	//load the radiotap header in the struct
	radiotapheader = (struct ieee80211_radiotap_header*) packet;
	
	//Print some capture info
	printf("Packet capture lenght: %d \n", pkthdr->caplen);
	printf("Packet original length: %d \n", pkthdr->len);
	
	//Init the radiotap iterator
	//Don't know what the max_length argument is for, it has to be bigger than the radiotapheader->it_len field,
 	//use the the size of the captured packet pkthdr->caplen since this is the maximum amount bytes the iterator may read in any case.
	int init = ieee80211_radiotap_iterator_init(&iterator, radiotapheader, pkthdr->caplen);
	//print the return value
	printf("Radiotap init status: %d \n", init);
	if(init != 0) {
		exit(1);

	}
	
	//Loop through the radiotap data, data indexes are defined in the ieee80211_radiotap_type enum in ieee80211_radiotap.h
	
	while(ieee80211_radiotap_iterator_next(&iterator) != -1) {
		
			
		printf("Radiotap data index: %d \n", iterator.this_arg_index);		
		if(iterator.this_arg_index == 12) {
			printf("Found antenna signal DB, what now?\n");
		}
		

	} 	



	//int i=0, *counter = (int *)arg; 

	//printf("Packet Count: %d\n", ++(*counter)); 
	//printf("Received Packet Size: %d\n", pkthdr->len); 
	//printf("Payload:\n"); 
	//for (i=0; i<pkthdr->len; i++){ 
	//
	//   if ( isprint(packet[i]) ) /* If it is a printable character, print it */
	//       printf("%c ", packet[i]); 
	//   else 
	//      printf(". "); 
	//   
	//    if( (i%16 == 0 && i!=0) || i==pkthdr->len-1 ) 
	//       printf("\n"); 
	//} 
	//return; 
} 


/* Radiotap parser functions from radiotap-parser.c */

/*
 * Radiotap header iteration
 *   implemented in src/radiotap-parser.c
 *
 * call __ieee80211_radiotap_iterator_init() to init a semi-opaque iterator
 * struct ieee80211_radiotap_iterator (no need to init the struct beforehand)
 * then loop calling __ieee80211_radiotap_iterator_next()... it returns -1
 * if there are no more args in the header, or the next argument type index
 * that is present.  The iterator's this_arg member points to the start of the
 * argument associated with the current argument index that is present,
 * which can be found in the iterator's this_arg_index member.  This arg
 * index corresponds to the IEEE80211_RADIOTAP_... defines.
 */


int ieee80211_radiotap_iterator_init(
	struct ieee80211_radiotap_iterator * iterator,
	struct ieee80211_radiotap_header * radiotap_header,
	int max_length)
{
	if(iterator == NULL) {
		printf("iterator_init(): iterator is NULL \n");		
		return (-EINVAL);
	}

	if(radiotap_header == NULL) {
		printf("iterator_init(): radiotapheader is NULL \n");
		return (-EINVAL);
	}
	/* Linux only supports version 0 radiotap format */

	if (radiotap_header->it_version) {
		printf("iterator_init(): wrong version \n");
		return (-EINVAL);
	}
	/* sanity check for allowed length and radiotap length field */

	if (max_length < (le16_to_cpu(radiotap_header->it_len))) {
		printf("iterator_init(): sanity check failed \n");
		return (-EINVAL);
	}

	iterator->rtheader = radiotap_header;
	iterator->max_length = le16_to_cpu(radiotap_header->it_len);
	iterator->arg_index = 0;
	iterator->bitmap_shifter = le32_to_cpu(radiotap_header->it_present);
	iterator->arg = ((u8 *)radiotap_header) +
			sizeof (struct ieee80211_radiotap_header);
	iterator->this_arg = 0;

	/* find payload start allowing for extended bitmap(s) */

	if (unlikely(iterator->bitmap_shifter &
	    IEEE80211_RADIOTAP_PRESENT_EXTEND_MASK)) {
		while (le32_to_cpu(*((u32 *)iterator->arg)) &
		    IEEE80211_RADIOTAP_PRESENT_EXTEND_MASK) {
			iterator->arg += sizeof (u32);

			/*
			 * check for insanity where the present bitmaps
			 * keep claiming to extend up to or even beyond the
			 * stated radiotap header length
			 */

			if ((((void*)iterator->arg) - ((void*)iterator->rtheader)) >
			    iterator->max_length)
				return (-EINVAL);

		}

		iterator->arg += sizeof (u32);

		/*
		 * no need to check again for blowing past stated radiotap
		 * header length, becuase ieee80211_radiotap_iterator_next
		 * checks it before it is dereferenced
		 */

	}

	/* we are all initialized happily */

	return (0);
}


/**
 * ieee80211_radiotap_iterator_next - return next radiotap parser iterator arg
 * @iterator: radiotap_iterator to move to next arg (if any)
 *
 * Returns: next present arg index on success or negative if no more or error
 *
 * This function returns the next radiotap arg index (IEEE80211_RADIOTAP_...)
 * and sets iterator->this_arg to point to the payload for the arg.  It takes
 * care of alignment handling and extended present fields.  interator->this_arg
 * can be changed by the caller.  The args pointed to are in little-endian
 * format.
 */

int ieee80211_radiotap_iterator_next(
	struct ieee80211_radiotap_iterator * iterator)
{

	/*
	 * small length lookup table for all radiotap types we heard of
	 * starting from b0 in the bitmap, so we can walk the payload
	 * area of the radiotap header
	 *
	 * There is a requirement to pad args, so that args
	 * of a given length must begin at a boundary of that length
	 * -- but note that compound args are allowed (eg, 2 x u16
	 * for IEEE80211_RADIOTAP_CHANNEL) so total arg length is not
	 * a reliable indicator of alignment requirement.
	 *
	 * upper nybble: content alignment for arg
	 * lower nybble: content length for arg
	 */

	static const u8 rt_sizes[] = {
		[IEEE80211_RADIOTAP_TSFT] = 0x88,
		[IEEE80211_RADIOTAP_FLAGS] = 0x11,
		[IEEE80211_RADIOTAP_RATE] = 0x11,
		[IEEE80211_RADIOTAP_CHANNEL] = 0x24,
		[IEEE80211_RADIOTAP_FHSS] = 0x22,
		[IEEE80211_RADIOTAP_DBM_ANTSIGNAL] = 0x11,
		[IEEE80211_RADIOTAP_DBM_ANTNOISE] = 0x11,
		[IEEE80211_RADIOTAP_LOCK_QUALITY] = 0x22,
		[IEEE80211_RADIOTAP_TX_ATTENUATION] = 0x22,
		[IEEE80211_RADIOTAP_DB_TX_ATTENUATION] = 0x22,
		[IEEE80211_RADIOTAP_DBM_TX_POWER] = 0x11,
		[IEEE80211_RADIOTAP_ANTENNA] = 0x11,
		[IEEE80211_RADIOTAP_DB_ANTSIGNAL] = 0x11,
		[IEEE80211_RADIOTAP_DB_ANTNOISE] = 0x11
		/*
		 * add more here as they are defined in
		 * include/net/ieee80211_radiotap.h
		 */
	};

	/*
	 * for every radiotap entry we can at
	 * least skip (by knowing the length)...
	 */

	while (iterator->arg_index < (int)sizeof (rt_sizes)) {
		int hit = 0;

		if (!(iterator->bitmap_shifter & 1))
			goto next_entry; /* arg not present */

		/*
		 * arg is present, account for alignment padding
		 *  8-bit args can be at any alignment
		 * 16-bit args must start on 16-bit boundary
		 * 32-bit args must start on 32-bit boundary
		 * 64-bit args must start on 64-bit boundary
		 *
		 * note that total arg size can differ from alignment of
		 * elements inside arg, so we use upper nybble of length
		 * table to base alignment on
		 *
		 * also note: these alignments are ** relative to the
		 * start of the radiotap header **.  There is no guarantee
		 * that the radiotap header itself is aligned on any
		 * kind of boundary.
		 */

		if ((((void*)iterator->arg)-((void*)iterator->rtheader)) &
		    ((rt_sizes[iterator->arg_index] >> 4) - 1))
			iterator->arg_index +=
				(rt_sizes[iterator->arg_index] >> 4) -
				((((void*)iterator->arg) -
				((void*)iterator->rtheader)) &
				((rt_sizes[iterator->arg_index] >> 4) - 1));

		/*
		 * this is what we will return to user, but we need to
		 * move on first so next call has something fresh to test
		 */

		iterator->this_arg_index = iterator->arg_index;
		iterator->this_arg = iterator->arg;
		hit = 1;

		/* internally move on the size of this arg */

		iterator->arg += rt_sizes[iterator->arg_index] & 0x0f;

		/*
		 * check for insanity where we are given a bitmap that
		 * claims to have more arg content than the length of the
		 * radiotap section.  We will normally end up equalling this
		 * max_length on the last arg, never exceeding it.
		 */

		if ((((void*)iterator->arg) - ((void*)iterator->rtheader)) >
		    iterator->max_length) {
				printf("iterator_next(): sanity check failed!\n");
				return (-EINVAL);
		}
	next_entry:

		iterator->arg_index++;
		if (unlikely((iterator->arg_index & 31) == 0)) {
			/* completed current u32 bitmap */
			if (iterator->bitmap_shifter & 1) {
				/* b31 was set, there is more */
				/* move to next u32 bitmap */
				iterator->bitmap_shifter = le32_to_cpu(
					*iterator->next_bitmap);
				iterator->next_bitmap++;
			} else {
				/* no more bitmaps: end */
				iterator->arg_index = sizeof (rt_sizes);
			}
		} else { /* just try the next bit */
			iterator->bitmap_shifter >>= 1;
		}

		/* if we found a valid arg earlier, return it now */

		if (hit)
			return (iterator->this_arg_index);

	}

	/* we don't know how to handle any more args, we're done */

	return (-1);
}



