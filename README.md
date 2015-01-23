Project
=======

To run the jar file type in the command:

java -jar MinCostMaxFlow.jar <path to the txt file> 

This will run the algorithm without the load balancing. To add load balancing, add "balanced" to the end of the command:

java -jar MinCostMaxFlow.jar <path to the txt file> balanced

and load balanced algorithm will run. Either way the program will notify which algorithm - balanced or not - it is performing. 


The program will not extend or modify any preference lists and will not force-assign projects which have not been selected or assigned. In the case that there is a reader with capacity greater than it's preference list size, the program will output an error message with the reader id and terminate. If any reader has a preference list size lower than 2x it's capacity, the program will output a warning with the readers id, BUT will continue and attempt in finding a solution. 

if there are projects left which had not been assigned, they will show up in the output at the bottom alongside the statistics. 
