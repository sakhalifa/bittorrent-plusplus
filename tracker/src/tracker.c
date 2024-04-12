#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

void error(char *msg) {
	perror(msg);
	exit(1);
}

int main(int argc, char const *argv[]) {

	fd_set master_fd;
	fd_set read_fds;

	FD_ZERO(&master_fd);
	FD_ZERO(&read_fds);

	int socketfd, port, fdmax;
	struct sockaddr_in serv_addr, cli_addr;

	if (argc < 2) {
		fprintf(stderr, "ERROR, no port provided\n");
		exit(1);
	}

	socketfd = socket(AF_INET, SOCK_STREAM, 0); // SOCK_STREAM = TCP
	if (socketfd < 0)
		error("ERROR opening socket");

	bzero((char *)&serv_addr, sizeof(serv_addr));
	port = atoi(argv[1]);

	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr =
	    inet_addr("127.0.0.1"); // TODO: set to config settings instead
	serv_addr.sin_port = htons(port);

	if (bind(socketfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
		error("ERROR binding");

	if (listen(socketfd, 10) == -1) {
		perror("listen");
		exit(3);
	}

	FD_SET(socketfd, &master_fd);

	fdmax = socketfd;

	int n;
	char buffer[256];

	// main loop
	for (;;) {
		read_fds = master_fd;
		if (select(fdmax + 1, &read_fds, NULL, NULL, NULL) == -1) {
			perror("select");
			exit(1);
		}
		for (int i = 3; i <= fdmax; i++) {
			if (FD_ISSET(i, &read_fds)) {
				if (i == socketfd) {
					// handle new connections
					int addrlen = sizeof cli_addr;
					int newfd   = accept(
                        socketfd, (struct sockaddr *)&cli_addr, &addrlen);
					if (newfd == -1) {
						perror("accept");
					} else {
						FD_SET(newfd, &master_fd);
						if (newfd > fdmax) {
							fdmax = newfd;
							fprintf(stderr, "tracker: socket %d connected\n",
							    newfd);
						}
					}
				} else {
					if ((n = recv(i, buffer, 256, 0)) <= 0) {
						if (n == 0) {
							fprintf(stderr, "tracker: socket %d disconnected\n",
							    i); // connection closed
						} else {
							perror("recv");
						}
						close(i);
						FD_CLR(i, &master_fd);
					} else {
						if (write(STDERR_FILENO, buffer, n) < 0) {
							perror("write");
						}
					}
				}
			}
		}
	}
	return 0;
}
