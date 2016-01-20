#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [ $5 ]
then 
    cat $SCRIPT_DIR/createTableDDL.sql $SCRIPT_DIR/createUser.sql | mysql -u$1 -p$2 --port=$3 --host=$4 --ssl-ca=$SCRIPT_DIR/mysql-server-public-key.ca-bundle
else
    cat $SCRIPT_DIR/createTableDDL.sql $SCRIPT_DIR/createUser.sql | mysql -u$1 -p$2 --port=$3 --host=$4
fi
