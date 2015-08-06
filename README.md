MinL: Brief Introduction
Piotr Woloszyn, 2015


Current build: 8

Build History:

	1-4: MinL prototype (provided in the MinL-prototype folder).
	
	5: Complete rewrite of the interpreter; def, inc, dec, output added to syntax; new GUI created.
	
	6: Interprter is optimized; 'loop' added to syntax; all numbers saved as doubles now instead of ints as		before.
	
	7: GUI design finished, added syntax highlight; 'if' and 'loadfile' added to syntax; Interpreter is 		optimized.
	
	8: Some error handling added to the editor; functions added to syntax; Removed most of the debug code.
	
	9(planned)): finish error handling; finish testing the syntax; start working on MinL+ (with a OO paradigm).



1) Goals

The purpose of this endeavor first and foremost is to develop a better understanding of the workings of 
programing languages and secondly to acquire programing experience by designing and developing them. In 
the case of MinL the focus is on a interpreted programing language that is functional but not Object Oriented.



2) The Editor

In order to explain how to use the editor we have to take a look at the individual elements of it, which are: 
the program run folder, the button bar, the main editor window and the console area.

	2.1 The program run folder:
	Is a small text window in the upper left corner with the text “Folder name:”, it's purpose is to 	allow the 
	user to access different programs in different folders.	It works like this: when the window is empty and we 
	save a .minl file it will be saved in the same location as the .jar file was executed from, this also 
	applies loading a file into the editor. So for instance if our .jar file is located in C:\folder and a 
	file we are trying to load is located in C:\folder\somestuff then we can't load the file unless we write 
	“somestuff” in the program run 	folder window. In general you should have all the files from the same program 
	in the same folder that is contained in the same place where the .jar file is located in.

	2.2 The button bar:
	Is a set of buttons which allow the user to: create a new .minl file, save a .minl file, load a .minl
	file, rename a .minl file, close a .minl file, run the code and exit the editor. This should be self
	explanatory, however it is important to not that you don't have to type in the “.minl” part for the
	relevant buttons.

	2.3 The main editor window:
	This is where all the code goes, it is easy to grasp if the user has had experience with text 	
	editors. The syntax is highlighted, however the comments are not so key words will be 	
	highlighted even if they are a part of the comments. There is no need for alarm since those key 	
	words will not execute. The comment lines are designated by “//” just as in java and C.
	Another thing to note is that the editor does not support a tabbed space, so when TAB is pressed 	
	two regular spaces will be added.

	2.4 The console area:
	This is where the code output will appear as well as the implemented error messages. The 
	console area also supports typing and can identify two commands: “run” and “exit” which work 
	the same way as the buttons on the button bar.



3) Syntax

	3.1 Definitions and variable assignment
	
	Variable assignment and definitions are handled by the key word “def”. The syntax is rather
	simile and does not require the data type to be specified for the variable to be defined, the
	syntax is as follows:

	def variable_name : variable_content

	So if we would want to create a variable 'x' and assign the value 12 to it we would type this:

	def x : 12

	Take note that variables have to have their value assigned on the spot, so you can't do something
	like this:

	def x		or 		def x :

	We can also assign the values of other variables, for instance:

	// if we defined before
	def x : 34
	// and now we define
	def y : var.x
	
	Now y is defined as 34, “var.x” means we are referring to a variable called “x”, if we typed
	def y : x, it would define y as the character x.

	You can also overwrite variables:

	def x : 56
	def x : hello!

	Now if were run these two lines the value assigned to the x variable will be the string “hello!”, 
	and as this example shows we don't have to worry about data types when we do that.


	3.2 Incrementation and Decrementation
	
	Incrementation and decrementation are handled by the keywords: “inc” and “dec” respectively.
	The syntax is elementary and consists of the key word followed by the variable name, the
	variable name has to be a number otherwise the editor will return an error. Code example:

	def x : 2
	inc x

	This will increment the value of the variable x by 1, so at the end of the code it will be 3.
	The very same syntax applies to decrementation:

	def y : 45
	dec y

	Now the value of y will be 44.

	
	3.3 Output
	
	Output is handled by the keyword “output”. The syntax here is a bit less simple than in the cases
	above. The syntax looks like this:

	output : variables and/or text

	So what this means is that you can output variables and/or text simultaneously. If you just want 
	to output some text you would do something like this:

	output : hello world!

	And it would output “hello world!”

	if you want to output variables you need to use the “var.” designator before the name of the
	variable, here is an example:

	def x : 6
	output : var.x

	And it will output “12”. You can intermix variables and text in your output, just as long as you
	remember that each variable reference has to use the “var.” designator, here is a more elaborate
	example:

	def x : 2
	def y : 5
	def z : 7

	output : var.x plus var.y equals var.z
	
	This will output: 2 plus 5 equals 7.

	
	3.4 Loop
	
	The loop syntax is handled by the keyword “loop”.  The syntax is as follows:

	loop x
	(
	something_happens
	)
	stop

	where x is either a number or a variable with a assigned number. The code in the brackets will
	execute x times, the “stop” keyword will be explained later on. One thing to note however is
	that the brackets are on their own lines, MinL is line sensitive, so that each line has it's syntax
	checked separately which includes brackets, putting a bracket with other code on the same line
	is prohibited.

	Let's take a look at some examples now, let's say we want to add 2 to 5 like in our previous
	example, with the loop we can do this without having to type “inc x” 5 times, we can do this
	now:

	def x : 2
	def y : 5
	def ans : var.x

	//Note: in the loop we don't have to type var.x because strings are prohibited as input
	loop x
	(
	inc ans
	)
	stop
	
	output : var.x plus var.y equals var.ans

	The output will be: 2 plus 5 equals 7. You can also nest loops; as in, put one loop inside of
	another loop, here is an example:

	def x : 2
	def y : 2
	def ans : 0

	loop x
	(
	  loop y
	  (
	    inc ans
  	)
	  stop
	)
	stop

	output : var.ans

	The output will be: 4.


	3.5 If statements
	
	If conditional statements are handled by the keyword “if” and the secondary keywords: “eq”,
	“neq”, “lt” and “gt”. The syntax is as follows:

	// eq means equal to, in this case if x is equal to y
	if eq x y
	(
	something_happens
	)
	stop

	// neq means equal to, in this case if x is not equal to y
	if neq x y
	(
	something_happens
	)
	stop
	
	// lt means equal to, in this case if x is less than y
	if lt x y
	(
	something_happens
	)
	stop

	// gt means equal to, in this case if x is greater than y
	if gt x y
	(
	something_happens
	)
	stop

	Similarly to the 'loop', 'if' doesn't use the 'var.' statement, that is because it doesn't accept strings 
	unless they are defined via variable, so you can't write:

	if eq word word

	unless word is the variable name of either a number or a string. Understandably, you can't
	compare a number with a string.

	Let's take a look at some examples:

	def x : 15
	def y : 61

	if eq x y
	(
	output : var.x is equal to var.y
	)
	stop

	This code will not output anything because the if condition is false. You can also nest 'if'
	statements:

	def x : 15
	def y : 15
	def z : 15

	if eq x y
	(
 	 if eq x z
 	 (
	    output : var.x is equal to var.y is equal to var.z
	  )
	  stop
	)
	stop

	This will printout: x is equal to y is equal to z.

	
	3.6 Loadfile
	
	This command will run the code from a different file, the syntax is as follows:

	loadfile somefile.minl

	You can specify the folder location as well:

	loadfile somefolder/somefile.minl

	That is, as long as you follow the instructions provided editor segment of this introduction.
	That 
	means the root is in the same location as the .jar file.

	The file loaded will be treated as if it has been executed, so any commands or definitions
	presented there will be added to the currently running program. Example:

	Let's say file name pi.minl has the following content:

	def aprox_pi : 3.14

	And our main file has the following one:

	loadfile pi.minl
	output : var.aprox_pi

	This will printout 3.14, because the variable aprox_pi will be added to the global variable list
	accessible from out main file.


	3.7 FUNctions
	
	Functions are handled by the keyword “f.”. The syntax is as follows:

	// For definitions:
	f.function_name argument1 argument2 etc..
	(
	some_content
	)
	stop

	// For calls:
	f.function_name argument1 argument2 etc..

	The syntax is fairly simple, you write f. followed by the name you want to give the function,
	and a set of arguments (if you want any), and then in brackets a set of commands that the
	function will execute. An example:

	// This is an add function that will add x to y by modifying the value of y
	f.add x y
	(
	  loop x
	  (
	    inc y
	  )
	  stop
	)
	stop

	// Now to call it:
	def x : 2
	f.add 6 x
	output : var.x
	stop

	This will output an 8. As simple as it appears this model has certain restrictions, this definition
	of f.add requires that y be a defined variable, specifically defined as a number, if we for instance
	imputed f.add 6 2, the function would return an error because you can increment just a 2.

	Another thing to note is that functions don't take in strings directly, the reason for this is that the
	function can only take in contiguous elements as arguments and it is better to simply put a string
	into a variable before passing it in. So to pass strings into a function we do something like this:

	def x : Hello World!

	f.HW string
	(
	output : var.string
	)
	stop

	f.HW x	
	stop

	This will print out Hello World! Two remaining points are: function cannot be nested in MinL,
	and MinL implements local variables for functions, so that variables defined in the function
	cannot be accessed from outside the function.


	3.8 Stop keyword
	
	The stop keyword acts a means of handling a design flaw in the interpreter which otherwise
	would require rewriting the whole thing from scratch. There are only two things to remember
	about it: First it's always has to be used when brackets are used, specifically right after every
	right bracket and at the end of the code file, if there are no brackets in the file there is not need
	to use the 'stop' keyword. Second, the 'stop' keyword doesn't get interpreted as long as there is
	less than 2 spaces following it so it is absolutely crucial that there are no more than those 2
	spaces following it.



4) Interpreter Design

The MinL interpreter follows a simple design philosophy, where the data is stored in 5 different Maps:
a numbers map, a strings map, a functions map for global variables and a numbers map and s strings map
for local variables in functions.The entirety of the interpreter is contained in the interpreterMainMethod
which scans the code line by line identifying what to do based on the contents of the current line being
evaluated. Unfortunately since this project is still mostly for self-learning there is more experimentation
in the design than consistency, this is most apparent when comparing the pre-build 7 pieces of code with the
newer ones.

5) What Comes Next

Taking the lessons from this project to heart my hope is to create a true Object Oriented interpreted
programing language, but for now MinL needs to be finished, it is still a incomplete project and I have
yet to experiment with OO design. MinL++; the next project, will be OO, will include arithmetic and will
not be minimalistic as the current one is.



Extra Comment:
The prototype MinL uses a different syntax. This is also the reason why the older builds are not available.
