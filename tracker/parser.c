#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "command.h"
#include "file.h"
#include "parser.h"

void error_command(char *error) {
	printf("Error: unknown command\n");
	if (error != NULL) {
		printf("\t%s\n", error);
	}
	exit(1);
}

void string_to_list_key(
    char *string, char **list, int *size, const char *separator) {
	char *key = strtok(string, separator);
	while (key != NULL) {
		*size += 1;
		list            = realloc(list, *size * sizeof(char *));
		list[*size - 1] = malloc(strlen(key) + 1);
		strcpy(list[*size - 1], key);
		key = strtok(NULL, separator);
	}
}

void string_to_list_file(
    char *string, struct file *files[], int *size, const char *separator) {
	char *file = strtok(string, separator); // Filename
	while (file != NULL) {
		*size += 1;
		files             = realloc(files, *size * sizeof(struct file *));
		struct file *elmt = malloc(sizeof(struct file));
		elmt->name        = strdup(file); // Copy name
		elmt->filesize    = atoi(strtok(NULL, separator)); // Copy filesize
		elmt->piecesize   = atoi(strtok(NULL, separator)); // Copy piecesize
		elmt->key         = strdup(strtok(NULL, separator)); // Copy key
		files[*size - 1]  = elmt; // store the file inside the list

		file = strtok(NULL, separator);
	}
}

void parsing(char *command) {
	const char *separator         = " "; // Separator in command is blank " "
	char *right_bracket_separator = "]";
	char *left_bracket_separator  = "[";
	char *command_name            = strtok(command, separator);

	// ANNOUNCE command (connection command to announce owned and leeched files)
	if (strcmp(command_name, "announce") == 0) {
		// Check if next word is "listen", otherwise error
		if (strcmp(strtok(NULL, separator), "listen") != 0) {
			error_command(NULL);
		}
		int port = atoi(strtok(NULL, separator));

		// Check if next word is "seed", otherwise error
		if (strcmp(strtok(NULL, separator), "seed") != 0) {
			error_command(NULL);
		}

		char *string_files = strtok(NULL, right_bracket_separator);

		// Check left Bracket for files
		if (string_files[0] != '[') {
			error_command("Missing bracket");
		}

		// Check if next word is "leech", otherwise error
		if (strcmp(strtok(NULL, separator), "leech") != 0) {
			error_command(NULL);
		}

		char *leech_key = strtok(NULL, right_bracket_separator);

		// Check left Bracket for leech
		if (leech_key[0] != '[') {
			error_command("Missing bracket");
		}

		string_files = strtok(string_files, left_bracket_separator);
		leech_key    = strtok(leech_key, left_bracket_separator);

		// Get owned files
		struct file **files = malloc(sizeof(struct file *));
		int size_file       = 0;
		string_to_list_file(string_files, files, &size_file, separator);

		// Get leeched keys
		char **keys  = malloc(sizeof(char *)); // TO BE FREED (and its elements)
		int size_key = 0;
		string_to_list_key(leech_key, keys, &size_key, separator);

		// announce_listen(port, files, size_file, keys, size_key);

	}

	// LOOK command (search files according to specific criteria)
	else if (strcmp(command_name, "look") == 0) {
		printf("look\n");
	}

	// GETFILE command (get peers who own a specific key)
	else if (strcmp(command_name, "getfile") == 0) {
		char *key = strtok(NULL, separator);

		// getfile(key);
	}

	// UPDATE command (update self seeded and leeched files)
	else if (strcmp(command_name, "update") == 0) {
		// Check if next word is "seed", otherwise error
		if (strcmp(strtok(NULL, separator), "seed") != 0) {
			error_command(NULL);
		}

		char *seed_key = strtok(NULL, right_bracket_separator);

		// Check left Bracket for seed
		if (seed_key[0] != '[') {
			error_command("Missing bracket");
		}

		// Check if next word is "leech", otherwise error
		if (strcmp(strtok(NULL, separator), "leech") != 0) {
			error_command(NULL);
		}

		char *leech_key = strtok(NULL, right_bracket_separator);

		// Check left Bracket for leech
		if (leech_key[0] != '[') {
			error_command("Missing bracket");
		}

		seed_key  = strtok(seed_key, left_bracket_separator);
		leech_key = strtok(leech_key, left_bracket_separator);

		// Get owned keys
		char **list = malloc(sizeof(char *)); // TO BE FREED (and its elements)
		int size    = 0;
		string_to_list_key(seed_key, list, &size, separator);

		// Get leeched keys
		char **list2 = malloc(sizeof(char *)); // TO BE FREED (and its elements)
		int size2    = 0;
		string_to_list_key(leech_key, list2, &size2, separator);

		// Append list2 to list
		list = realloc(list, (size + size2) * sizeof(char *));
		for (int i = 0; i < size2; i++) {
			list[size + i] = malloc(strlen(list2[i]) + 1);
			strcpy(list[size + i], list2[i]);
			free(list2[i]);
		}
		free(list2);
		size += size2;

		// update(list, size);
	}

	// Unknown command
	else {
		error_command(NULL);
	}
}

int main() {
	char f[] = "announce listen 1896 seed [jojo 8 7 ae jojo2 9 10 eeee] leech "
	           "[aad azd ed]";
	parsing(f);
}