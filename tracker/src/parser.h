#ifndef PARSER_H
#define PARSER_H

enum comparator { LT = -1, EQ = 0, GT = 1 };

struct criteria {
	char *element; // Name of the criteria
	enum comparator comp; // Comparator
	char *value; // value to compare to
};

int parsing(char *command);

#endif