#!/bin/bash

# (N, L, v, r, rc, maxTime, noise)

# Number of exact parameters required to run the script
PARAMS_REQUIRED=7

# Paths to required files
PROJECT_FOLDER="$HOME/Programs/idea_workspace/off-lattice"
RELATIVE_BIN_FOLDER="$PROJECT_FOLDER/resources/bin"

OUTPUT_FOLDER="$RELATIVE_BIN_FOLDER/output"
STATIC_PATH="$OUTPUT_FOLDER/static.dat"
DYNAMIC_PATH="$OUTPUT_FOLDER/dynamic.dat"
SIM_OUTPUT_PATH="$OUTPUT_FOLDER/output.dat"
OVITO_OUTPUT_PATH="$OUTPUT_FOLDER/graphics.xyz"

JAR="java -jar $PROJECT_FOLDER/core/target/off-lattice.jar"

# Start of Script
START_TIME=$(date +%s)

if [ $# -ne ${PARAMS_REQUIRED} ]; then
	echo "This script requires $PARAMS_REQUIRED parameters (N, L, v, r, rc, maxTime, noise)"
	exit 1
fi

# Assign arguments to readable variables
N=$1
L=$2
V=$3
R=$4
RC=$5
MAX_TIME=$6
NOISE=$7

# Generate static.dat
echo -e "Generating static.dat... "
${JAR} gen staticdat ${N} ${L} ${V} ${R}

# Generate dynamic.dat
echo -e "Generating dynamic.dat... "
${JAR} gen dynamicdat ${STATIC_PATH}

# Generate output.dat
echo -e "Generating output.dat... "
${JAR} lattice ${STATIC_PATH} ${DYNAMIC_PATH} ${RC} ${MAX_TIME} ${NOISE}

# Generate graphics.xyz
echo -e "Generating graphics.xyz... "
${JAR} gen ovito ${STATIC_PATH} ${SIM_OUTPUT_PATH}

END_TIME=$(date +%s)

EXECUTION_TIME=`expr $END_TIME - $START_TIME`
EXECUTION_TIME_FILE="${OUTPUT_FOLDER}/execution_time.statistics"
rm -f ${EXECUTION_TIME_FILE}
touch ${EXECUTION_TIME_FILE}
echo -e "Execution time: ${EXECUTION_TIME} seconds\r" >> ${EXECUTION_TIME_FILE}
echo -e "Execution time: ${EXECUTION_TIME} seconds"

# Move output folder to parent project's folder
mkdir -p ${PROJECT_FOLDER}/output
mv ${OUTPUT_FOLDER}/* ${PROJECT_FOLDER}/output
rm -r ${OUTPUT_FOLDER}

# Move log folder to parent project's folder
mkdir -p ${PROJECT_FOLDER}/logs
mv logs/* ${PROJECT_FOLDER}/logs
rm -r logs
