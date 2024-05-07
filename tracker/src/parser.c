#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "file.h"
#include "parser.h"

void *error_command(char *error) {
	printf("Error: unknown command\n");
	if (error != NULL) {
		printf("\t%s\n", error);
	}
	return NULL;
}

char **string_to_list_key(
    char *string, char **list, int *size, const char *separator) {
	char *key = strtok(string, separator);
	while (key != NULL) {
		list        = realloc(list, sizeof(char *) * (*size + 1));
		list[*size] = malloc(sizeof(char) * (strlen(key) + 1));
		list[*size] = strcpy(list[*size], key);
		*size += 1;
		key = strtok(NULL, separator);
	}
	return list;
}

struct file **string_to_list_file(
    char *string, struct file **files, int *size, const char *separator) {
	char *file = strtok(string, separator); // Filename
	while (file != NULL) {
		*size += 1;
		files            = realloc(files, *size * sizeof(struct file *));
		int filesize     = atoi(strtok(NULL, separator));
		int piecesize    = atoi(strtok(NULL, separator));
		char *key        = strtok(NULL, separator);
		files[*size - 1] = create_file(file, filesize, piecesize, key);

		// store the file inside the list
		file = strtok(NULL, separator);
	}
	return files;
}

enum comparator convert_char_comparator(char c) {
	switch (c) {
	case ('<'): return LT;
	case ('='): return EQ;
	case ('>'): return GT;
	default: error_command("Comparator unknown");
	}
	return 0;
}

struct criteria **string_to_list_criteria(
    char *string, struct criteria *crit[], int *size, const char *separator) {
	char *save_p;
	char *criteria = strtok_r(string, separator, &save_p);
	char *quote    = "\"";
	while (criteria != NULL) {
		*size += 1;
		crit               = realloc(crit, *size * sizeof(struct criteria *));
		struct criteria *c = malloc(sizeof(struct criteria));
		char *elmt         = strtok(criteria, quote);
		int elmt_len       = strlen(elmt);
		c->comp =
		    convert_char_comparator(elmt[elmt_len - 1]); // copy comparator
		elmt[elmt_len - 1] = '\0';
		c->element         = malloc(sizeof(char) * (strlen(elmt) + 1));
		strcpy(c->element, elmt);
		char *value = strtok(NULL, quote);
		c->value    = malloc(sizeof(char) * (strlen(value) + 1));
		strcpy(c->value, value);
		crit[*size - 1] = c;
		criteria        = strtok_r(NULL, separator,
		           &save_p); // set criteria to next elmt (since strtok was used, need
		// to reset to string)
	}
	return crit;
}

struct command *parsing(char *command) {
	const char *separator         = " "; // Separator in command is blank " "
	char *right_bracket_separator = "]";
	char *left_bracket_separator  = "[";
	char *command_name            = strtok(command, separator);

	// Check command is not empty
	if (command_name == NULL) {
		return error_command("Empty command");
	}

	// ANNOUNCE command (connection command to announce owned and leeched files)
	if (strcmp(command_name, "announce") == 0) {

		// Check if next word is "listen", otherwise error
		char *check = strtok(NULL, separator);
		if (check == NULL || strcmp(check, "listen") != 0) {
			return error_command("listen");
		}

		int port = atoi(strtok(NULL, separator));

		// Check if next word is "seed", otherwise error
		check = strtok(NULL, separator);
		if (check == NULL || strcmp(check, "seed") != 0) {
			return error_command("seed");
		}

		char *string_files = strtok(NULL, right_bracket_separator);
		// Check left Bracket for files
		if (string_files[0] != '[') {
			return error_command("Missing bracket");
		}

		// Check if next word is "leech", otherwise error
		check = strtok(NULL, separator);
		if (check == NULL || strcmp(check, "leech") != 0) {
			return error_command(NULL);
		}

		char *leech_key = strtok(NULL, right_bracket_separator);
		// Check left Bracket for leech
		if (leech_key[0] != '[') {
			return error_command("Missing bracket");
		}

		// Remove left brackets
		string_files = strtok(string_files, left_bracket_separator);
		leech_key    = strtok(leech_key, left_bracket_separator);

		// Get owned files
		struct file **files = malloc(sizeof(struct file *));
		int size_file       = 0;
		files = string_to_list_file(string_files, files, &size_file, separator);

		// Get leeched keys
		char **keys  = malloc(sizeof(char *));
		int size_key = 0;
		keys = string_to_list_key(leech_key, keys, &size_key, separator);

		struct command *command = malloc(sizeof(struct command));

		struct announce *arg = malloc(sizeof(struct announce));

		arg->port      = port;
		arg->nb_file   = size_file;
		arg->file_list = files;
		arg->nb_key    = size_key;
		arg->key_list  = keys;

		command->command_name = ANNOUNCE;
		command->command_arg  = (void *)arg;
		return command;
	}

	// LOOK command (search files according to specific criteria)
	else if (strcmp(command_name, "look") == 0) {

		char *criterias = strtok(NULL, right_bracket_separator);

		// Check left Bracket for seed
		if (criterias[0] != '[') {
			return error_command("Missing bracket");
		}

		criterias = strtok(criterias, left_bracket_separator);

		struct command *command = malloc(sizeof(struct command));

		struct look *arg = malloc(sizeof(struct look));

		int size = 0;
		if (criterias != NULL) {
			// Get criteria list
			struct criteria **crit = malloc(
			    sizeof(struct criteria *)); // TO BE FREED (and its elements)
			crit = string_to_list_criteria(criterias, crit, &size, separator);
			arg->criteria = crit;
		}

		arg->nb_criteria = size;

		command->command_name = LOOK;
		command->command_arg  = (void *)arg;

		return command;
	}

	// GETFILE command (get peers who own a specific key)
	else if (strcmp(command_name, "getfile") == 0) {
		char *key = strtok(NULL, separator);

		struct command *command = malloc(sizeof(struct command));

		struct getfile *arg = malloc(sizeof(struct getfile));

		arg->key = strdup(key);

		command->command_name = GETFILE;
		command->command_arg  = (void *)arg;

		return command;
	}

	// UPDATE command (update self seeded and leeched files)
	else if (strcmp(command_name, "update") == 0) {
		// Check if next word is "seed", otherwise error
		char *check = strtok(NULL, separator);
		if (check == NULL || strcmp(check, "seed") != 0) {
			return error_command(NULL);
		}

		char *seed_key = strtok(NULL, right_bracket_separator);

		// Check left Bracket for seed
		if (seed_key[0] != '[') {
			return error_command("Missing bracket");
		}

		// Check if next word is "leech", otherwise error
		check = strtok(NULL, separator);
		if (check == NULL || strcmp(check, "leech") != 0) {
			return error_command(NULL);
		}

		char *leech_key = strtok(NULL, right_bracket_separator);

		// Check left Bracket for leech
		if (leech_key[0] != '[') {
			return error_command("Missing bracket");
		}

		seed_key  = strtok(seed_key, left_bracket_separator);
		leech_key = strtok(leech_key, left_bracket_separator);

		long int size_update = 0;

		if (seed_key != NULL) {
			size_update += strlen(seed_key);
		}
		if (leech_key != NULL) {
			size_update += strlen(leech_key);
		}

		char *all_key = malloc(sizeof(char) *
		    (size_update + 1 + ((seed_key != NULL) && (leech_key != NULL))));
		if (seed_key != NULL) {
			strcpy(all_key, seed_key);
		}
		if (leech_key != NULL) {
			if (seed_key != NULL) {
				strcat(all_key, " ");
				strcat(all_key, leech_key);
			} else {
				strcpy(all_key, leech_key);
			}
		}
		// Need to manually put \0 in the string if there are no seed/leech keys
		// in order to "initialize" all_key string. Otherwise it's filled with
		// random bullsh*t
		if ((seed_key == NULL) && (leech_key == NULL)) {
			all_key[0] = '\0';
		}

		// Get all keys
		char **list = malloc(sizeof(char *)); // TO BE FREED (and its elements)
		int size    = 0;
		list        = string_to_list_key(all_key, list, &size, separator);

		free(all_key);

		struct command *command = malloc(sizeof(struct command));

		struct update *arg = malloc(sizeof(struct update));

		arg->nb_key   = size;
		arg->key_list = list;

		command->command_name = UPDATE;
		command->command_arg  = (void *)arg;

		return command;
	}

	// Unknown command
	else {
		return error_command(NULL);
	}

	return NULL;
}
