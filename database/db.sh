#!/bin/bash
## arguments: this_script.sh database-command username password port host [enableSSL]
##
## This script can do either of two things, depending on the (required) first argument passed to it: 
## - provision a new, empty database with the correct schema using the 'create' argument
## - populate the database with dummy data from a static file using the 'populate' argument (and ensure it has at least 500 students)
##
## author: jordan
##

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

SCRIPT_FILENAME=$(basename $0)
USAGE_MSG="Usage: $SCRIPT_FILENAME {create | populate} \e[4musername\e[0m \e[4mpassword\e[0m \e[4mport\e[0m \e[4mhost\e[0m [\e[4menable_ssl\e[0m]\n"

DATABASE_NAME=scholar_warehouse
SET_DATABASE_NAME_COMMAND="set @databasename='$DATABASE_NAME';"

## ## ## BEGIN ARG CHECKING

if [ "$1" ]; then 
    if [[ "$1" = "create" || "$1" = "populate" ]]; then
        ACTION="$1"
    else
        printf "ERROR: Invalid first argument $1 - must be either 'create' or 'populate'\n"
        printf "$USAGE_MSG\n"
        exit 1
    fi
else 
    printf "ERROR: First arg not found\n"
    printf "$USAGE_MSG\n"
    exit 1
fi

## arguments 2, 3, 4 and 5 are required
if [[ -z "$2" || -z "$3" || -z "$4" || -z "$5" ]]; then
    printf "ERROR: Required argument is null!\n"
    printf "$USAGE_MSG\n"
    exit 1
fi

MYSQL_COMMAND="mysql --skip-column-names -u$2 -p$3 --port=$4 --host=$5"
## the sixth optional argument, if present (any value is fine) enables SSL
if [ "$6" ]; then 
    MYSQL_COMMAND="$MYSQL_COMMAND --ssl-ca=$SCRIPT_DIR/../warehouse/src/main/resources/db-public-key.ca-bundle"
fi

## DB_EXISTS=$(echo "select schema_name from information_schema.schemata where schema_name = '$DATABASE_NAME';" | ${MYSQL_COMMAND})

MYSQL_COMMAND_WITH_DB="$MYSQL_COMMAND --database=$DATABASE_NAME"

## ## ## DONE ARG CHECKING

##DATABASE_NAME=scriptTest2
##temp_function() {
##    echo "$(echo "set @databasename='$DATABASE_NAME';")" "$(echo "SET @query = CONCAT('CREATE DATABASE ', @databasename);PREPARE stmt FROM @query;EXECUTE stmt;DEALLOCATE PREPARE stmt;")" | mysql -u root;
##}

## create the DB schema from scratch (via a script that does this)
create_db() { 
    ## the only effective way I found to dynamically specify a DB name to "use" is to specify it as a command-line arg
    ## however it gets lost when we drop and recreate the DB, so do this in its own separate call to the mysql command
    echo "$SET_DATABASE_NAME_COMMAND" "$(cat $SCRIPT_DIR/dropAndRecreateDatabase.sql)" | ${MYSQL_COMMAND}

    echo "$SET_DATABASE_NAME_COMMAND" "$(cat $SCRIPT_DIR/createTableDDL.sql $SCRIPT_DIR/createWebappAdminUser.sql)" | ${MYSQL_COMMAND_WITH_DB}
}

## populate the DB from an EXISTING db SQL file
populate_db() { 

    DUMMY_DB_FILENAME=dev_scholar_warehouse.sql
    
    
    if [ ! -f "$SCRIPT_DIR/$DUMMY_DB_FILENAME" ]; then
        printf "Dummy DB population FAILED - Dummy DB file does not exist at $SCRIPT_DIR/$DUMMY_DB_FILENAME..."
        exit 1
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
    
    NUM_STUDENTS=$(echo "select count(*) from $DATABASE_NAME.student" | ${MYSQL_COMMAND})
    
    if [ $NUM_STUDENTS -gt 499 ]; then
        printf "At least 500 students in sample data set. All is well.\n"
    else 
        printf "ERROR! More than 500 students expected in sample dataset...\n"
    fi

}

if [ "$ACTION" = "create" ]; then 
    create_db
elif [ "$ACTION" = "populate" ]; then
    populate_db
fi