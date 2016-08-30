# Off Lattice
Java Implementation of the Off Lattice Automaton
## Build
To build the project, it is necessary to have Maven and Java 1.8 installed.
Then, run

    $ mvn clean package
    
## Execution
To run the program, from the root folder

    $ java -jar core/target/off-lattice.jar <arguments>

## Simulation
Bash files were added to $PROJECT_FOLDER/resources/bin.
These files allow doing several simulations and taking some statistics about different systems, specifying N, L, v, r, rc, maxTime (for one simulation), noise and cIterations (the number of times you want to run different simulations with the same parameters).

Please open these files and read the description at their top to check which variables are required and what do they mean.
Simulation's output folder will be at $PROJECT_FOLDER/output.

**IMPORTANT**
Make sure to correctly modify the $PROJECT_FOLDER variable of the bash files at the resources/bin/ folder for the scripts to work correctly.
Current value is: 
    *PROJECT_FOLDER="$HOME/Programs/idea_workspace/off-lattice"*

###Usage example
From the PROJECT_FOLDER/resources/bin folder, run

    $ ./off-lattice.sh 400 10 0.03 0 1 1000 0
    
or 

    $ ./analyser.sh 20 0.03 0 1 1000 20

