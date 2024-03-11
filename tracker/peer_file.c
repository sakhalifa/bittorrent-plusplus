#include "peer_file.h"

void pfl_insert(struct peer_file_list* files, struct peer_file *current)
{
    struct peer_file_list *pfl = (struct peer_file_list *)malloc(sizeof(struct peer_file_list));
    pfl->current = current;
    pfl->next = files;
    files = pfl;
}

