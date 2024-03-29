#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#include "peer.h"
#include "peer_file.h"

void error(char *msg) {
	perror(msg);
	exit(1);
}

int main(int argc, char const *argv[]) {
	const struct peer_file_list *FILES = NULL;
	return 0;
}
