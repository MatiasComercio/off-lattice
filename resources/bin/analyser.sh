#!/bin/bash

# (L, v, r, rc, maxTime, noise, cIterations)

## Constants

PROJECT_FOLDER="$HOME/Programs/idea_workspace/off-lattice"
RELATIVE_BIN_FOLDER="$PROJECT_FOLDER/resources/bin"
OUTPUT_FOLDER="$RELATIVE_BIN_FOLDER/output"

STATIC_PATH="$OUTPUT_FOLDER/static.dat"
DYNAMIC_PATH="$OUTPUT_FOLDER/dynamic.dat"
SIM_OUTPUT_PATH="$OUTPUT_FOLDER/output.dat"
SIM_T_VA_PATH="$OUTPUT_FOLDER/t_va.csv"
OVITO_OUTPUT_PATH="$OUTPUT_FOLDER/graphics.xyz"
SCRIPT_LOGGER="$OUTPUT_FOLDER/analysis.sh.log"

RESULTS_FOLDER="$OUTPUT_FOLDER/results"

# va_noise_all_NXXX.csv
VA_NOISE_ALL="$RESULTS_FOLDER/va_noise_all_N"
# va_noise_results_NXXX.csv
VA_NOISE_RESULTS="$RESULTS_FOLDER/va_noise_results_N"
VA_N_RESULTS="$RESULTS_FOLDER/va_N_results_NOISE"

JAR="java -jar $PROJECT_FOLDER/core/target/off-lattice.jar"

# Extension of the output table
OUTPUT_TABLE_TYPE=".csv"

PARAMS_REQUIRED=6

##########################
## Functions

# Generate static.dat
function gen_static {
    ${JAR} gen staticdat ${N} ${L} ${V} ${R} >> ${SCRIPT_LOGGER} 2>&1
}

# Generate dynamic.dat
function gen_dynamic {
    ${JAR} gen dynamicdat ${STATIC_PATH} >> ${SCRIPT_LOGGER} 2>&1
}

# Generate output.dat
function gen_output {
    ${JAR} lattice ${STATIC_PATH} ${DYNAMIC_PATH} ${RC} ${MAX_TIME} ${NOISE} >> ${SCRIPT_LOGGER} 2>&1
}

function validate_exit_status {
    if [ $? -ne 0 ]
    then
        echo "[FAIL] - check ${SCRIPT_LOGGER} file for more information"
        echo "Simulation will continue although, but problems may arise due to the previous one"
    else
        echo "[DONE]"
    fi
}

function write_constants {
  echo -e "L, $L\r" >> ${OUTPUT_TABLE_PATH}
  echo -e "V, $V\r" >> ${OUTPUT_TABLE_PATH}
  echo -e "rc, $RC\r" >> ${OUTPUT_TABLE_PATH}
}

function gen_va_noise_results {
    local OUTPUT_TABLE_PREFIX

    # e.g. "va_noise_all_NXXX.csv"
    local OUTPUT_TABLE_PATH="$VA_NOISE_RESULTS$1$OUTPUT_TABLE_TYPE"

    # Delete and create output table file
    rm -f ${OUTPUT_TABLE_PATH}
    touch ${OUTPUT_TABLE_PATH}

    # Add N number to the file
    echo -e "N, $1\r" >> ${OUTPUT_TABLE_PATH}

    # write constants L number to the file
    write_constants

    # Add identifiers of columns to the start of the file
    echo -e "noise, E(Va) = |Va| = mean(Va), SD(Va) = sqrt(|Va^2|-|Va|^2)\r" >> ${OUTPUT_TABLE_PATH}

    echo ${OUTPUT_TABLE_PATH}
}

function gen_va_n_results {
    local OUTPUT_TABLE_PREFIX

    # e.g. "va_noise_all_NXXX.csv"
    local OUTPUT_TABLE_PATH="$VA_N_RESULTS$1$OUTPUT_TABLE_TYPE"

    # Delete and create output table file
    rm -f ${OUTPUT_TABLE_PATH}
    touch ${OUTPUT_TABLE_PATH}

    # Add NOISE number to the file
    echo -e "NOISE, $1\r" >> ${OUTPUT_TABLE_PATH}

    write_constants

    # Add identifiers of columns to the start of the file
    echo -e "N, E(Va) = |Va| = mean(Va), SD(Va) = sqrt(|Va^2|-|Va|^2)\r" >> ${OUTPUT_TABLE_PATH}

    echo ${OUTPUT_TABLE_PATH}
}

#################

# Start of Script
START_TIME=$(date +%s)

if [ $# -ne ${PARAMS_REQUIRED} ]; then
	echo "This script requires $PARAMS_REQUIRED parameters (L, v, r, rc, maxTime, cIterations)"
	exit 1
fi

# Assign arguments to readable variables
L=$1
V=$2
R=$3
RC=$4
MAX_TIME=$5
C_ITERATIONS=$6

# create results folder
mkdir -p ${RESULTS_FOLDER}

############################################################
############################################################
############################################################
## Parameters for figure a
#N_ARRAY=(40 100 400 4000 10000)
#N_ARRAY_LENGTH=${#N_ARRAY[*]}
#
#L_ARRAY=(3.1 5 10 31.6 50) # should have the same length as the N array
#
#NOISE_ARRAY=(0 0.5 1 1.5 2 2.5 3 3.5 4 4.5 5)
#NOISE_ARRAY_LENGTH=${#NOISE_ARRAY[*]}
#
#declare -A MAX_TIME_ARRAY
## N=40
#MAX_TIME_ARRAY["40","0"]=20;
#MAX_TIME_ARRAY["40","0.5"]=20;
#MAX_TIME_ARRAY["40","1"]=20;
#MAX_TIME_ARRAY["40","1.5"]=70;
#MAX_TIME_ARRAY["40","2"]=70;
#MAX_TIME_ARRAY["40","2.5"]=100;
#MAX_TIME_ARRAY["40","3"]=100;
#MAX_TIME_ARRAY["40","3.5"]=150;
#MAX_TIME_ARRAY["40","4"]=150;
#MAX_TIME_ARRAY["40","4.5"]=600;
#MAX_TIME_ARRAY["40","5"]=600;
#
## N=100
#MAX_TIME_ARRAY["100","0"]=50;
#MAX_TIME_ARRAY["100","0.5"]=50;
#MAX_TIME_ARRAY["100","1"]=50;
#MAX_TIME_ARRAY["100","1.5"]=100;
#MAX_TIME_ARRAY["100","2"]=100;
#MAX_TIME_ARRAY["100","2.5"]=500;
#MAX_TIME_ARRAY["100","3"]=500;
#MAX_TIME_ARRAY["100","3.5"]=600;
#MAX_TIME_ARRAY["100","4"]=600;
#MAX_TIME_ARRAY["100","4.5"]=500;
#MAX_TIME_ARRAY["100","5"]=500;
#
## N=400
#MAX_TIME_ARRAY["400","0"]=150;
#MAX_TIME_ARRAY["400","0.5"]=150;
#MAX_TIME_ARRAY["400","1"]=150;
#MAX_TIME_ARRAY["400","1.5"]=1000;
#MAX_TIME_ARRAY["400","2"]=1000;
#MAX_TIME_ARRAY["400","2.5"]=2000;
#MAX_TIME_ARRAY["400","3"]=2000;
#MAX_TIME_ARRAY["400","3.5"]=1000;
#MAX_TIME_ARRAY["400","4"]=1000;
#MAX_TIME_ARRAY["400","4.5"]=400;
#MAX_TIME_ARRAY["400","5"]=400;
#
## N=4000
#MAX_TIME_ARRAY["4000","0"]=1700;
#MAX_TIME_ARRAY["4000","0.5"]=1700;
#MAX_TIME_ARRAY["4000","1"]=1700;
#MAX_TIME_ARRAY["4000","1.5"]=2000;
#MAX_TIME_ARRAY["4000","2"]=2000;
#MAX_TIME_ARRAY["4000","2.5"]=3000;
#MAX_TIME_ARRAY["4000","3"]=3000;
#MAX_TIME_ARRAY["4000","3.5"]=2000;
#MAX_TIME_ARRAY["4000","4"]=2000;
#MAX_TIME_ARRAY["4000","4.5"]=200;
#MAX_TIME_ARRAY["4000","5"]=200;
#
## N=10000
#MAX_TIME_ARRAY["10000","0"]=4000;
#MAX_TIME_ARRAY["10000","0.5"]=4000;
#MAX_TIME_ARRAY["10000","1"]=4000;
#MAX_TIME_ARRAY["10000","1.5"]=3500;
#MAX_TIME_ARRAY["10000","2"]=3500;
#MAX_TIME_ARRAY["10000","2.5"]=5000;
#MAX_TIME_ARRAY["10000","3"]=5000;
#MAX_TIME_ARRAY["10000","3.5"]=300;
#MAX_TIME_ARRAY["10000","4"]=300;
#MAX_TIME_ARRAY["10000","4.5"]=300;
#MAX_TIME_ARRAY["10000","5"]=300;
#
#############################################################

## Parameters for figure b
N_ARRAY=(40 100 300 500 700 900 1100 1300 1500 2000 3000 4000)
N_ARRAY_LENGTH=${#N_ARRAY[*]}

L=20

NOISE_ARRAY=(2.25)
NOISE_ARRAY_LENGTH=${#NOISE_ARRAY[*]}

declare -A MAX_TIME_ARRAY

MAX_TIME_ARRAY["40","2.25"]=2000;
MAX_TIME_ARRAY["100","2.25"]=2000;
MAX_TIME_ARRAY["300","2.25"]=2000;
MAX_TIME_ARRAY["500","2.25"]=1300;
MAX_TIME_ARRAY["700","2.25"]=1600;
MAX_TIME_ARRAY["900","2.25"]=1700;
MAX_TIME_ARRAY["1100","2.25"]=1200;
MAX_TIME_ARRAY["1300","2.25"]=2000;
MAX_TIME_ARRAY["1500","2.25"]=1000;
MAX_TIME_ARRAY["2000","2.25"]=1000;
MAX_TIME_ARRAY["3000","2.25"]=500;
MAX_TIME_ARRAY["4000","2.25"]=850;



############################################################
############################################################
############################################################
I_VA_MEAN=0
I_VA_SD=1

# declare Associative arrays (like hashmaps): http://wiki.bash-hackers.org/syntax/arrays
declare -A NOISE_VA_ARRAY
declare -A N_VA_ARRAY

# Initialize arrays for storing VA_MEAN & VA_SD
for (( i = 0; i < ${N_ARRAY_LENGTH}; i++ )); do
  for (( j = 0; j < ${NOISE_ARRAY_LENGTH}; j++ )); do
    NOISE_VA_ARRAY["$i,$j,$I_VA_MEAN"]=0 # N_INDEX, NOISE_INDEX, I_VA_MEAN
    NOISE_VA_ARRAY["$i,$j,$I_VA_SD"]=0 # N_INDEX, NOISE_INDEX, I_VA_SD

    N_VA_ARRAY[${j},${i},${I_VA_MEAN}]=0 # NOISE_INDEX, N_INDEX, I_VA_MEAN
    N_VA_ARRAY[${j},${i},${I_VA_SD}]=0 # NOISE_INDEX, N_INDEX, I_VA_SD
  done
done

echo -e "************************************"
for (( i = 0; i < ${N_ARRAY_LENGTH}; i++ )); do
  N=${N_ARRAY[${i}]}
  # L=${L_ARRAY[${i}]} # only for figure a

  echo -e "Running analyser with N = $N..."
  echo -en "  Generating va_noise_results file...  "
  OUTPUT_TABLE_PATH=`gen_va_noise_results ${N}`
  echo -e "  [DONE]\n"

  echo -en "  Generating static.dat file...  "
  gen_static
  validate_exit_status

  BACKUP_DIR_N="${OUTPUT_FOLDER}/it_results/N${N}"
  mkdir -p ${BACKUP_DIR_N}

  for (( j = 0; j < ${NOISE_ARRAY_LENGTH}; j++ )); do
    NOISE=${NOISE_ARRAY[${j}]}
    MAX_TIME=${MAX_TIME_ARRAY["$N,$NOISE"]}

    echo -e "  ------------------------------------"
    echo -e "  Running analyser with NOISE = $NOISE..."

    BACKUP_DIR_NOISE="${BACKUP_DIR_N}/NOISE${NOISE}"
    mkdir -p ${BACKUP_DIR_NOISE}

    for (( k = 1; k <= ${C_ITERATIONS}; k++ )) ; do
      echo -en "    Generating dynamic.dat file...  "
      gen_dynamic
      validate_exit_status

      echo -en "    Generating output.dat file...  "
      gen_output
      validate_exit_status

      VA=`tail -n 1 ${SIM_T_VA_PATH}` # get the last va value

      NOISE_VA_ARRAY["$i,$j,$I_VA_MEAN"]=$(bc <<< "scale=6;${NOISE_VA_ARRAY["$i,$j,$I_VA_MEAN"]} + $VA")
      NOISE_VA_ARRAY["$i,$j,$I_VA_SD"]=$(bc <<< "scale=6;${NOISE_VA_ARRAY["$i,$j,$I_VA_SD"]} + $VA * $VA")

      N_VA_ARRAY["$j,$i,$I_VA_MEAN"]=$(bc <<< "scale=6;${N_VA_ARRAY["$j,$i,$I_VA_MEAN"]} + $VA")
      N_VA_ARRAY["$j,$i,$I_VA_SD"]=$(bc <<< "scale=6;${N_VA_ARRAY["$j,$i,$I_VA_SD"]} + $VA * $VA")

      # +++xcomment: DO NOT BACKUP ANYTHING, EXCEPT NEEDED! output.dat is too large
      # Backup current iteration
      BACKUP_DIR="${BACKUP_DIR_NOISE}/I${k}"
      mkdir -p ${BACKUP_DIR}

      # cp ${STATIC_PATH} ${BACKUP_DIR}/
      # cp ${DYNAMIC_PATH} ${BACKUP_DIR}/
      # cp ${SIM_OUTPUT_PATH} ${BACKUP_DIR}/
      cp ${SIM_T_VA_PATH} ${BACKUP_DIR}/

      PERCENTAGE_COMPLETED=$(bc <<< "scale=6;$k/$C_ITERATIONS * 100")
      echo -e "      ** Completed: $PERCENTAGE_COMPLETED% ** \r" # A % completed value
    done
    echo -e "  [DONE]"

    NOISE_VA_ARRAY["$i,$j,$I_VA_MEAN"]=$(bc <<< "scale=6;${NOISE_VA_ARRAY["$i,$j,$I_VA_MEAN"]}/$C_ITERATIONS")
    NOISE_VA_ARRAY["$i,$j,$I_VA_SD"]=$(bc <<< "scale=6;sqrt(${NOISE_VA_ARRAY["$i,$j,$I_VA_SD"]}/$C_ITERATIONS - ${NOISE_VA_ARRAY["$i,$j,$I_VA_MEAN"]} * ${NOISE_VA_ARRAY["$i,$j,$I_VA_MEAN"]})")


    N_VA_ARRAY["$j,$i,$I_VA_MEAN"]=$(bc <<< "scale=6;${N_VA_ARRAY["$j,$i,$I_VA_MEAN"]}/$C_ITERATIONS")
    N_VA_ARRAY["$j,$i,$I_VA_SD"]=$(bc <<< "scale=6;sqrt(${N_VA_ARRAY["$j,$i,$I_VA_SD"]}/$C_ITERATIONS - ${N_VA_ARRAY["$j,$i,$I_VA_MEAN"]} * ${N_VA_ARRAY["$j,$i,$I_VA_MEAN"]})")

    ROW="$NOISE, ${NOISE_VA_ARRAY["$i,$j,$I_VA_MEAN"]}, ${NOISE_VA_ARRAY["$i,$j,$I_VA_SD"]}"
    echo -e "${ROW}\r" >> ${OUTPUT_TABLE_PATH}
  done

  echo -e "  ------------------------------------"
  echo -e "[DONE]"
  echo -e "************************************"
done

# generate the other file
for (( j = 0; j < ${NOISE_ARRAY_LENGTH}; j++ )); do
  NOISE=${NOISE_ARRAY[$j]}
  OUTPUT_TABLE_PATH=`gen_va_n_results ${NOISE}`
  for (( i = 0; i < ${N_ARRAY_LENGTH}; i++ )); do
    N=${N_ARRAY[$i]}
    ROW="$N, ${N_VA_ARRAY["$j,$i,$I_VA_MEAN"]}, ${N_VA_ARRAY["$j,$i,$I_VA_SD"]}"
    echo -e "${ROW}\r" >> ${OUTPUT_TABLE_PATH}
  done
done

END_TIME=$(date +%s)

EXECUTION_TIME=`expr $END_TIME - $START_TIME`
EXECUTION_TIME_FILE="${OUTPUT_FOLDER}/execution_time.statistics"
rm -f ${EXECUTION_TIME_FILE}
touch ${EXECUTION_TIME_FILE}
echo -e "Execution time: ${EXECUTION_TIME} seconds\r" >> ${EXECUTION_TIME_FILE}
echo -e "Execution time: ${EXECUTION_TIME} seconds"

# Move output folder to parent project's folder
DATE_TIME=$(date +%Y-%m-%d_%T)
CURRENT_OUTPUT=${PROJECT_FOLDER}/output/${DATE_TIME}
mkdir -p ${CURRENT_OUTPUT}
mv ${OUTPUT_FOLDER}/* ${CURRENT_OUTPUT}
rm -r ${OUTPUT_FOLDER}
rm ${CURRENT_OUTPUT}/*.dat ${CURRENT_OUTPUT}/*.csv # Remove last iteration files

# Move log folder to parent project's folder
mkdir -p ${CURRENT_OUTPUT}/logs
mv logs/* ${CURRENT_OUTPUT}/logs
rm -r logs
