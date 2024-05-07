#include <arpa/inet.h>
#include <netinet/in.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#include "command.h"
#include "parser.h"

#define MAX_PEER 10

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

	fprintf(stderr, "\nsocketfd %d\n", socketfd);

	bzero((char *)&serv_addr, sizeof(serv_addr));
	port = atoi(argv[1]);

	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr =
	    inet_addr("0.0.0.0"); // TODO: set to config settings instead
	serv_addr.sin_port = htons(port);

	if (bind(socketfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
		error("ERROR binding");

	if (listen(socketfd, MAX_PEER) == -1) {
		perror("listen");
		exit(3);
	}

	FD_SET(socketfd, &master_fd);

	fdmax = socketfd;

	int n;
	int count_error[MAX_PEER];
	char buffer[256];
	int *nb_file = malloc(sizeof(int));
	*nb_file     = 1;

	struct file **files = malloc(sizeof(struct file *) * (*nb_file));
	struct peer *peers[MAX_PEER + 3];

	// this will work for now, find another solution later
	struct file *root_file = malloc(sizeof(struct file));
	root_file->filesize    = 42;
	root_file->piecesize   = 42;
	root_file->key         = strdup("root");
	root_file->name        = strdup("root");
	root_file->nb_peers    = 1;

	struct peer *root_peer = malloc(sizeof(struct peer));
	root_peer->ip          = strdup("0.0.0.0");
	root_peer->port        = 42;
	root_file->peers       = &root_peer;

	files[0] = root_file;

	// main loop
	for (;;) {
		read_fds = master_fd;
		if (select(fdmax + 1, &read_fds, NULL, NULL, NULL) == -1) {
			perror("select");
			exit(1);
		}
		for (int i = 3; i <= fdmax; i++) {
			if (FD_ISSET(i, &read_fds)) {
				fprintf(stderr, "\nsocketfd %d\n", i);

				if (i == socketfd) {
					// handle new connections
					int addrlen = sizeof cli_addr;
					int newfd   = accept(socketfd, (struct sockaddr *)&cli_addr,
					      (socklen_t *)&addrlen);
					if (newfd == -1) {
						perror("accept");
					} else {
						FD_SET(newfd, &master_fd);
						if (newfd > fdmax) {
							fdmax          = newfd;
							struct peer *p = malloc(sizeof(struct peer));
							p->ip          = inet_ntoa(cli_addr.sin_addr);
							peers[newfd]   = p;
							fprintf(stderr,
							    "tracker: socket %d connected from ip %s\n",
							    newfd, p->ip);
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
						count_error[i] = 0;
					} else {
						struct command *c;
						char *response;
						buffer[n - 2] = '\0';
						if ((c = parsing(buffer)) != NULL) {
							count_error[i] = 0;
							switch (c->command_name) {
							case ANNOUNCE:
								response =
								    announce(*(struct announce *)c->command_arg,
								        files, nb_file, peers[i]);
								send(i, response, strlen(response), 0);
								send(i, "\n", 2, 0);
								break;
							case LOOK:
								response =
								    look(*(struct look *)c->command_arg,
								        files, nb_file, peers[i]);
								send(i, response, strlen(response), 0);
								send(i, "\n", 2, 0);
								break;
							case GETFILE:
								response =
								    getfile(*(struct getfile *)c->command_arg,
								        files, nb_file, peers[i]);
								send(i, response, strlen(response), 0);
								send(i, "\n", 2, 0);
								break;
							case UPDATE:
								response =
								    update(*(struct update *)c->command_arg,
								        files, nb_file, peers[i]);
								send(i, response, strlen(response), 0);
								send(i, "\n", 2, 0);
								break;
							default: break;
							}
						} else {
							count_error[i]++;
							char *error_message = "Unknown command\n";
							char *close_message = "Too many errors, connection "
							                      "closed by the tracker\n";
							send(i, error_message, strlen(error_message), 0);

							// close connection after 3 failed attempts
							if (count_error[i] > 2) {
								send(
								    i, close_message, strlen(close_message), 0);
								FD_CLR(i, &master_fd);
								close(i);
								count_error[i] = 0;
							}
						}
					}
				}
			}
		}
	}
	return 0;
}
