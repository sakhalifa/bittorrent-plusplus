#include "command.h"
#include "tst.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void test_command_announce() {
	printf("\t%s", __func__);

	struct peer p;
	p.ip   = "10.10.10.10";
	p.port = 6969;

	struct peer p2;
	p2.ip   = "Heheheha";
	p2.port = 727;

	struct peer peers[2] = {p, p2};

	struct file f;
	f.filesize  = 1024;
	f.piecesize = 256;
	f.key       = strdup("ImKey");
	f.name      = strdup("ImName");
	f.nb_peers  = 2;
	f.peers     = peers;

	int size           = 1;
	struct file *files = malloc(sizeof(struct file));
	files[0]           = f;

	struct file f2;
	f2.filesize  = 1024;
	f2.piecesize = 256;
	f2.key       = strdup("ImKey2");
	f2.name      = strdup("ImName2");
	f2.nb_peers  = 2;
	f2.peers     = peers;

	struct file *files2 = malloc(sizeof(struct file));
	files2              = &f2;

	char **keys2 = malloc(sizeof(char *));
	keys2[0]     = "ImKey";

	struct announce arg;
	arg.port      = 6969;
	arg.nb_file   = 1;
	arg.file_list = files2;
	arg.key_list  = keys2;
	arg.nb_key    = 1;

	char *res = announce(arg, files, &size, &peers[0]);

	assert(strcmp(res, "ok") == 0);

	free(f.key);
	free(f.name);
	free(f2.key);
	free(f2.name);
	free(files);

	printf("\tOK\n");
}

void test_command_getfile() {
	printf("\t%s", __func__);

	char *key = "ImTheKey";
	struct getfile arg;
	arg.key = strdup(key);

	struct peer p;
	p.ip   = "10.10.10.10";
	p.port = 6969;

	struct peer p2;
	p2.ip   = "Heheheha";
	p2.port = 727;

	struct peer peers[2] = {p, p2};

	struct file f;
	f.filesize  = 1024;
	f.piecesize = 256;
	f.key       = strdup(key);
	f.name      = "ImName";
	f.nb_peers  = 2;
	f.peers     = peers;

	int size           = 1;
	struct file *files = malloc(sizeof(struct file));
	files[0]           = f;

	char *res = getfile(arg, files, &size, &p);
	assert(strcmp(res, "peers ImTheKey [10.10.10.10:6969 Heheheha:727]") == 0);

	free(res);
	free(arg.key);
	free(f.key);
	free(files);
	printf("\tOK\n");
}