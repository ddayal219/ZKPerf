#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/epoll.h>
#include <sys/eventfd.h>
#include <sys/time.h>
#include <time.h>
#include <limits.h>
#include <pthread.h>
#include <accord.h>
#include "util.h"

#define dprintf(fmt, args...)						\
do {									\
	fprintf(stdout, "%s(%d) " fmt, __FUNCTION__, __LINE__, ##args);	\
} while (0)

#define eprintf(fmt, args...)						\
do {									\
	fprintf(stderr, "%s(%d) " fmt, __FUNCTION__, __LINE__, ##args);	\
} while (0)

char localhost[HOST_NAME_MAX];
char *remotehost;
int n_threads;
int n_requests;
int msg_size;
uint32_t *read_msg_size;
int sync_mode;
static int total_n_handled;
static int total_n_launched;
static size_t total_n_bytes;
int i;
	struct timeval start, end, total;
	char buffer[10];
	double throughput;
	char prefix[256], url[256], iurl[256];
	char *buf;
	char *readbuf;
	int ret = -1, prefix_len;
	int tm;
	struct acrd_aiocb *acb;
	struct request_info *ri;
	

uint32_t mask = ACRD_EVENT_ALL | ACRD_EVENT_PREFIX;
	char *hostname;
	struct acrd_watch_info *info;

struct acrd_handle *bh;
struct request_info {
	struct timeval started;
	struct acrd_handle *bh;
};
static void acrdbench_aio_cb(struct acrd_handle *h, struct acrd_aiocb *acb, void *arg)
{
	int n_handled;

	/*if (acb->result != ACRD_SUCCESS)
		printf("error in callback, %d\n", acb->result);
	else
		printf("successfull callback");*/
	//printf("call back");

	__sync_add_and_fetch(&total_n_bytes, msg_size);
	n_handled = __sync_add_and_fetch(&total_n_handled, 1);
	if (n_handled && (n_handled % 10000) == 0)
	{//	printf("%d requests done\n", n_handled);
	}
}
static void watch_cb(struct acrd_handle *bh, struct acrd_watch_info *info, void *arg)
{
	//printf("%s\n", info->path);
	strcpy(iurl,info->path);
	int readret=0,delret=0;
	againred:
	readret=acrd_aio_read(ri->bh,info->path,&readbuf,&read_msg_size,0,NULL,acb);
	//printf("readret %d\n",readret);
	if (readret != ACRD_SUCCESS) {
			if (readret == ACRD_ERR_AGAIN) {
				usleep(100000);
				//printf("going to again");
				goto againred;
			}
			else
			eprintf("err, %d\n", readret);
			exit(1);
	} 
	
	againdel:
	delret=acrd_aio_del(ri->bh,url,NULL,acb);
	if (delret != ACRD_SUCCESS) {
			if (delret == ACRD_ERR_AGAIN) {
				usleep(100000);
				//printf("going to again");
				goto againdel;
			}
			else
			eprintf("err, %d\n", delret);
			exit(1);
	} 
		
}
int main(int argc, char *argv[])
{
	int cmp;
	buf = calloc(1, msg_size);
	readbuf = calloc(1, msg_size);
	memset(buf, 0xFF, msg_size);
	tm = (unsigned)time(NULL);
	prefix_len = sprintf(prefix, "/tmp/%s/", localhost);
	strcpy(url, prefix);

	total_n_handled = 0;
	total_n_launched = 0;
	total_n_bytes = 0;
	if (argc < 6) {
		printf("usage: acrdbench [host] [n_threads] [n_reqs] "
			"[msg_size] [sync_mode]\n");
		return 1;
	}
	gethostname(localhost, sizeof(localhost));
	setvbuf(stdout, NULL, _IONBF, 0);
	remotehost = argv[1];
	n_threads = atoi(argv[2]);
	n_requests = atoi(argv[3]);
	msg_size = atoi(argv[4]);
	if (!strcasecmp(argv[5], "nosync"))
		sync_mode = 0;
	else if (!strcasecmp(argv[5], "sync"))
		sync_mode = ACRD_FLAG_SYNC;
	else {
		fprintf(stderr, "sync mode must be \"nosync\" or \"sync\"\n");
		return 1;
	}
	gettimeofday(&start, NULL);
	ri = malloc(sizeof(*ri));
	ri->bh = acrd_init(remotehost, 9090, NULL, NULL, NULL);
	bh = acrd_init(remotehost, 9090, NULL, NULL, NULL);
	if (!ri->bh) {
		eprintf("failed to initialize library\n");
		pthread_exit(NULL);
	}
//	printf("%d",n_requests);
	acb = acrd_aio_setup(ri->bh, acrdbench_aio_cb, NULL);
	info = acrd_add_watch(bh, "/tmp//", mask, watch_cb, NULL);
//	printf("here");
	for (i = 0; i < n_requests; i++) {
	//printf("%d\n",i);	
	sprintf(url + prefix_len, "%d", __sync_add_and_fetch(&total_n_launched, 1));
	
		again:
		ret = acrd_aio_write(ri->bh, url, buf, msg_size, 0, ACRD_FLAG_CREATE | 		sync_mode,acb); 

		if (ret != ACRD_SUCCESS) {
			if (ret == ACRD_ERR_AGAIN) {
				usleep(100000);
				//printf("going to again");
				goto again;
			}
			eprintf("err, %d\n", ret);
			exit(1);
			} 
	}
//printf("%s\n",url);
//printf("%s\n",iurl);
cmp=strcmp(url,iurl);
//printf("%d\n",cmp);
while(cmp!=0)
{
//printf("sleeping");
cmp=strcmp(url,iurl);
usleep(1000);
//printf("%d",cmp);
}
	//acrd_aio_flush(ri->bh);
	gettimeofday(&end, NULL);
	timersub(&end, &start, &total);
	throughput = n_requests /
		(total.tv_sec + ((double)total.tv_usec)/1000000.0);
printf("%d,%d.%06d,%.2f\n",n_requests, (int)total.tv_sec, (int)total.tv_usec,throughput);
	free(buf);
	free(readbuf);
	return 0;
}

