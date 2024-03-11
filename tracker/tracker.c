#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

void error(char *msg)
{
    perror(msg);
    exit(1);
}

struct peer_file{
    char* name;
    char* hash;
    unsigned long long length;
};

struct peer{
    char* ip;
    unsigned short port;
    //hashmap peer_files
};

int main(int argc, char const *argv[])
{
    /* code */
    return 0;
}
