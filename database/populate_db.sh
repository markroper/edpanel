#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

DUMMY_DB_FILENAME=dev_scholar_warehouse.sql
DATABASE_NAME=scholar_warehouse

if [ ! -f "$SCRIPT_DIR/$DUMMY_DB_FILENAME" ]; then
    printf "Dummy DB population FAILED - Dummy DB file does not exist at $SCRIPT_DIR/$DUMMY_DB_FILENAME..."
    exit 1
fi

## the fifth argument enables SSL
if [ $5 ]
then 
    MYSQL_COMMAND="mysql -u$1 -p$2 --port=$3 --host=$4 --ssl-ca=$SCRIPT_DIR/../warehouse/src/main/resources/db-public-key.ca-bundle"
else 
    MYSQL_COMMAND="mysql -u$1 -p$2 --port=$3 --host=$4"
fi

echo "drop database if exists $DATABASE_NAME;" | ${MYSQL_COMMAND}

if [ $? -ne 0 ]; then
    printf "Error attempting to connect to DB instance at $4:$3\n"
    exit 1;
fi

cat $SCRIPT_DIR/$DUMMY_DB_FILENAME | ${MYSQL_COMMAND}

if [ $? -ne 0 ]; then
    printf "Error attempting to connect to DB instance at $4:$3\n"
    exit 1;
fi

NUM_STUDENTS=$(${MYSQL_COMMAND} -se "select count(*) from $DATABASE_NAME.student")

if [ $NUM_STUDENTS -gt 499 ]; then
    printf "At least 500 students in sample data set. All is well.\n"
else 
    printf "ERROR! More than 500 students expected in sample dataset...\n"
fi

