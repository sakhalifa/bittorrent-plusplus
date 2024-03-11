#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include "peer_file.h"
#include "peer.h"

void error(char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char const *argv[])
{
    const struct peer_file_list *FILES = NULL;
    return 0;
}
