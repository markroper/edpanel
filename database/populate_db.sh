#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

DUMMY_DB_FILENAME=dev_scholar_warehouse.sql
DATABASE_NAME=@databasename

if [ ! -f "$SCRIPT_DIR/$DUMMY_DB_FILENAME" ]; then
    printf "Dummy DB population FAILED - Dummy DB file does not exist at $SCRIPT_DIR/$DUMMY_DB_FILENAME..."
    exit 1
fi

## the fifth argument enables SSL
if [ $5 ]
then
    echo "drop database $DATABASE_NAME;" | mysql -u$1 -p$2 --port=$3 --host=$4 --ssl-ca=$SCRIPT_DIR/../warehouse/src/main/resources/db-public-key.ca-bundle
else
    echo "drop database $DATABASE_NAME;" | mysql -u$1 -p$2 --port=$3 --host=$4
fi

if [ $? -ne 0 ]; then
    printf "Error attempting to connect to DB instance at $4:$3\n"
    exit 1;
fi

##
if [ $5 ]
then
    cat $SCRIPT_DIR/$DUMMY_DB_FILENAME | mysql -u$1 -p$2 --port=$3 --host=$4 --ssl-ca=$SCRIPT_DIR/../warehouse/src/main/resources/db-public-key.ca-bundle
else
    cat $SCRIPT_DIR/$DUMMY_DB_FILENAME | mysql -u$1 -p$2 --port=$3 --host=$4
fi

if [ $? -ne 0 ]; then
    printf "Error attempting to connect to DB instance at $4:$3\n"
    exit 1;
fi

##
if [ $5 ]
then
    NUM_STUDENTS=$(mysql -u$1 -p$2 --port=$3 --host=$4 --ssl-ca=$SCRIPT_DIR/../warehouse/src/main/resources/db-public-key.ca-bundle -se "select count(*) from $DATABASE_NAME.student")
else
    NUM_STUDENTS=$(mysql -u$1 -p$2 --port=$3 --host=$4 -se "select count(*) from $DATABASE_NAME.student")
fi

if [ $NUM_STUDENTS -gt 499 ]; then
    printf "At least 500 students in sample data set. All is well.\n"
else 
    printf "ERROR! More than 500 students expected in sample dataset...\n"
fi

