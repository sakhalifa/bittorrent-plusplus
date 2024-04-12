void pfl_insert(struct peer_file_list *files, struct peer_file *current);
void pfl_remove(struct peer_file_list *files, struct peer_file *current);

/***
 * Linked list for peer files.
 * A Hashmap implementation could be interesting.
 */
struct peer_file_list {
	struct peer_file *current;
	struct peer_file_list *next;
};

struct peer_file {
	char *name;
	char *hash;
	unsigned long long length;
	unsigned int piece_size;
	struct seeders_list *seeders;
};