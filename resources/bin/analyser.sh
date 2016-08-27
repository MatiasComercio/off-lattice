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

function gen_va_noise_results {
    local OUTPUT_TABLE_PREFIX

    # e.g. "va_noise_all_NXXX.csv"
    local OUTPUT_TABLE_PATH="$VA_NOISE_RESULTS$1$OUTPUT_TABLE_TYPE"

    # Delete and create output table file
    rm -f ${OUTPUT_TABLE_PATH}
    touch ${OUTPUT_TABLE_PATH}

    # Add N number to the file
    echo -e "N, $1\r" >> ${OUTPUT_TABLE_PATH}

    # Add identifiers of columns to the start of the file
    echo -e "noise, E(Va) = |Va| = mean(Va), SD(Va) = sqrt(|Va^2|-|Va|^2)\r" >> ${OUTPUT_TABLE_PATH}

    echo ${OUTPUT_TABLE_PATH}
}


#################

# Start of Script
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

echo -e "************************************"
for N in 40 100 400 4000 10000 ; do
    echo -e "Running analyser with N = $N..."
    echo -en "  Generating va_noise_results file...  "
    OUTPUT_TABLE_PATH=`gen_va_noise_results ${N}`
    echo -e "  [DONE]\n"

    echo -en "  Generating static.dat file...  "
    gen_static
    validate_exit_status

    BACKUP_DIR_N="${OUTPUT_FOLDER}/it_results/N${N}"
    mkdir -p ${BACKUP_DIR_N}

    for NOISE in 0 0.1 0.5 1 2 2.5 4 5 ; do
        echo -e "  ------------------------------------"
        echo -e "  Running analyser with NOISE = $NOISE..."
        VA_MEAN=0
        VA_SD=0

        BACKUP_DIR_NOISE="${BACKUP_DIR_N}/NOISE${NOISE}"
        mkdir -p ${BACKUP_DIR_NOISE}

        for (( i = 1; i <= ${C_ITERATIONS}; i++ )) ; do
            echo -en "    Generating dynamic.dat file...  "
            gen_dynamic
            validate_exit_status

            echo -en "    Generating output.dat file...  "
            gen_output
            validate_exit_status

            VA=`tail -n 1 ${SIM_T_VA_PATH}` # get the last va value

            VA_MEAN=$(bc <<< "scale=2;$VA_MEAN + $VA")
	        VA_SD=$(bc <<< "scale=2;$VA_SD + $VA * $VA")

            # Backup current iteration
            BACKUP_DIR="${BACKUP_DIR_NOISE}/I${i}"
            mkdir -p ${BACKUP_DIR}

            cp ${STATIC_PATH} ${BACKUP_DIR}/
            cp ${DYNAMIC_PATH} ${BACKUP_DIR}/
            cp ${SIM_OUTPUT_PATH} ${BACKUP_DIR}/
            cp ${SIM_T_VA_PATH} ${BACKUP_DIR}/

	        PERCENTAGE_COMPLETED=$(bc <<< "scale=2;$i/$C_ITERATIONS * 100")
            echo -e "      ** Completed: $PERCENTAGE_COMPLETED% ** \r" # A % completed value
        done
        echo -e "  [DONE]"

        VA_MEAN=$(bc <<< "scale=2;$VA_MEAN/$C_ITERATIONS")
	    VA_SD=$(bc <<< "scale=2;sqrt($VA_SD/$C_ITERATIONS - $VA_MEAN * $VA_MEAN)")
	    ROW="$NOISE, $VA_MEAN, $VA_SD"
	    echo -e "${ROW}\r" >> ${OUTPUT_TABLE_PATH}
    done
    echo -e "  ------------------------------------"
    echo -e "[DONE]"
    echo -e "************************************"
done

# Move output folder to parent project's folder
mkdir -p ${PROJECT_FOLDER}/output
mv ${OUTPUT_FOLDER}/* ${PROJECT_FOLDER}/output
rm -r ${OUTPUT_FOLDER}

# Move log folder to parent project's folder
mkdir -p ${PROJECT_FOLDER}/logs
mv logs/* ${PROJECT_FOLDER}/logs
rm -r logs




