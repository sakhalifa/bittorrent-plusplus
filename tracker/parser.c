#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "parser.h"
#include "command.h"
#include "file.h"

void error_command(char *error)
{
    printf("Error: unknown command\n");
    if (error != NULL)
    {
        printf("\t%s\n", error);
    }
    exit(1);
}

void string_to_list(char *string, char **list, int *size, const char *separator)
{
    char * key = strtok(string, separator);
    while (key != NULL)
    {
        *size += 1;
        list = realloc(list, *size * sizeof(char *));
        list[*size - 1] = malloc(strlen(key) + 1);
        strcpy(list[*size - 1], key);
        key = strtok(NULL, separator);
    }
}

void parsing(char *command)
{
    const char *separator = " "; // Separator in command is blank " "
    char *command_name = strtok(command, separator);

    // ANNOUNCE command (connection command to announce owned and leeched files)
    if (strcmp(command_name, "announce") == 0)
    {
        strtok(NULL, separator); // get rid of "listen"
        int port = atoi(strtok(NULL, separator));

        printf("port %d\n", port);
    }

    // LOOK command (search files according to specific criteria)
    else if (strcmp(command_name, "look") == 0)
    {
        printf("look\n");
    }

    // GETFILE command (get peers who own a specific key)
    else if (strcmp(command_name, "getfile") == 0)
    {
        char *key = strtok(NULL, separator);

        // getfile(key);
        printf("key : %s\n", key);
    }

    // UPDATE command (update self seeded and leeched files)
    else if (strcmp(command_name, "update") == 0)
    {
        // Check command word "seed"
        if (strcmp(strtok(NULL, separator), "seed") != 0)
        {
            error_command(NULL);
        }

        char *right_bracket_separator = "]";
        char *left_bracket_separator = "[";

        char *seed_key = strtok(NULL, right_bracket_separator);

        // Check left Bracket for seed
        if (seed_key[0] != '[')
        {
            error_command("Missing bracket");
        }

        // Check command word "leech"
        if (strcmp(strtok(NULL, separator), "leech") != 0)
        {
            error_command(NULL);
        }

        char *leech_key = strtok(NULL, right_bracket_separator);

        // Check left Bracket for leech
        if (leech_key[0] != '[')
        {
            error_command("Missing bracket");
        }

        seed_key = strtok(seed_key, left_bracket_separator);
        leech_key = strtok(leech_key, left_bracket_separator);
        printf("seed %s, leech %s\n", seed_key, leech_key);

        char **list = malloc(sizeof(char *)); // TO BE FREED (and its elements)
        int size = 0;
        string_to_list(seed_key, list, &size, separator);
        char **list2 = malloc(sizeof(char *)); // TO BE FREED (and its elements)
        int size2 = 0;
        string_to_list(leech_key, list2, &size2, separator);

        // Append list2 to list
        list = realloc(list, (size + size2) * sizeof(char *));
        for (int i = 0; i < size2; i++){
            list[size + i] = malloc(strlen(list2[i]) + 1);
            strcpy(list[size + i], list2[i]);
            free(list2[i]);
        }
        free(list2);
        size += size2;

        // list contains all keys and has a size of 'size'

    }

    // Unknown command
    else
    {
        error_command(NULL);
    }
}

int main()
{
    char f[] = "update seed [az az5 868a] leech [56az 6azd hthjjoi]";
    parsing(f);
}