# Launch parameters:
* -d debug mode
* -m (values per line) prints the memory after running with the specified values per line. Defaults to 10
* -mem (number) sets the amount of memory, default to 30

# Commands:
## Memory Operations
* SET (mem location) (integer reference) - sets the memory location to the specified integer
* POINT (integer reference) - Adds the integer reference to the pointer's current location. If the pointer is at $5, POINT 4 will make it go to $9, and POINT -4 will make it go to $1. The pointer starts at 0.
* APOINT (mem location) - Sets the pointer to the memory location specified
* ADD (integer reference) (mem location) - Adds the integer to the memory location
* SUB (integer reference) (mem location) - Subtracts the integer from the memory location (Alias: REM)
* MUL (integer reference) (mem location) - Multiplies the memory location by the integer
* DIV (integer reference) (mem location) - Divides the memory location by the integer
* MOD (integer reference) (mem location) - The remainder of the division of the memory location by the integer

## Output
* OUT (integer reference) - Outputs the value in the specified integer
* AOUT (integer reference) - Outputs the ASCII character corresponding to the specified integer
* SOUT (string) - Outputs the string. Allows for control characters \r, \n and \t.
* COUT (mem location) (integer) - Outputs n values, the amount specified in (integer), starting from (mem location)
* ACOUT (mem location) (integer) - Same as above, but outputs the ascii character for each value and stops if it finds a 0
* CACOUT (mem location) (integer) - Same as above, but doesn't stop when it finds a 0

## Input
* IN (mem location) - Gets input and stores it in the specified memory location
* INVAL (mem location) - Keeps asking for input until an integer is given, and stores given integer in the specified memory location
* INLINE (mem location) (integer) - Gets a string as input with length (integer). If the string is smaller than the given length, appends a zero to the end.

## Flow Control
* LBL (Integer) - Sets a label with the ID being the specified integer
* GOTO (integer reference) - Goes to the label with the ID being the specified integer
* GOTOIF (condition) (integer reference) - Goes to the label with the ID being the specified integer if the condition is
true
* END - Ends the program execution.


# Memory locations and references:
You can refer or specify a memory location using the prefix '$'. $0 will correspond to the 1st memory address. If you use
an address above the amount of memory specified with -mem or below 0, the interpreter will crash.
«SET $0 10» will set the value in the memory address 0 to 10.

«ADD $0 $1» will add the value in the memory address 0 (in this case it would be 10) to the memory address 1.
So if $1 is 0, it will become 10.
You can also use $P to get the memory address pointed at by the pointer.

# Labels:
The line "LBL 10" will create a label with the ID 10. You **can't** use references to memory addresses **as label IDs**.

You can go to a label using GOTO, for example «GOTO 10» would go to the label with the ID 10. You **can** use references to memory
addresses **in GOTO**, so if $0 has the value 10 «GOTO $0» would go to the label with the ID 10. If you go to a label
that doesn't exist, the interpreter will crash.

# Conditions:
There are 4 possible operators to compare numbers:
* $0<5 - true if the value in $0 is smaller than 5
* $0>$1 - true if the value in $0 is bigger than $1
* 5=$1 - true if the value in $1 is 5
* $0#$1 - true if the value in $0 is different from the value in $1