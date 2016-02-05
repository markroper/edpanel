#!/bin/bash
DATABASE_NAME="scholar_warehouse"
MYSQL_LOGIN_COMMAND="mysql --skip-column-names -u root $DATABASE_NAME"

## read a specific line from a specified file
## arg 1 - file to read from | arg 2 - line number to read
function read_file_line () { 
    RESULT=$(sed "$2q;d" $1)
    TRIMMED_RESULT="$(echo -e "${RESULT}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
    printf "$TRIMMED_RESULT"
}

function random_person_name () {
    printf "$(read_file_line 'person.txt' $1)"
}

function random_school_name() { 
    printf "$(read_file_line 'school.txt' $1)"
}

OUTPUT_BUFFER=''

OUTPUT_BUFFER="${OUTPUT_BUFFER}USE ${DATABASE_NAME};\n"
OUTPUT_BUFFER="${OUTPUT_BUFFER}START TRANSACTION;\n"

STUDENT_FKS_TO_UPDATE=$($MYSQL_LOGIN_COMMAND -se "select student_user_fk from student;")
STAFF_FKS_TO_UPDATE=$($MYSQL_LOGIN_COMMAND -se "select staff_user_fk from staff;")
SCHOOLS_TO_UPDATE=$($MYSQL_LOGIN_COMMAND -se "select school_id from school;")

PERSON_INDEX=1
SCHOOL_INDEX=1

for STUDENT_TO_UPDATE in $STUDENT_FKS_TO_UPDATE; do
    STUDENT_NEW_NAME=$(random_person_name $PERSON_INDEX);
    if [[ -z "${STUDENT_NEW_NAME// }" ]]; then
        printf "ERROR - insufficient # of person names in list\n"
        exit 1
    else 
        OUTPUT_BUFFER="${OUTPUT_BUFFER}update student set student_name='$STUDENT_NEW_NAME' where student_user_fk='$STUDENT_TO_UPDATE';\n"
        ((PERSON_INDEX++))
    fi
done

for STAFF_TO_UPDATE in $STAFF_FKS_TO_UPDATE; do
    STAFF_NEW_NAME=$(random_person_name $PERSON_INDEX);
    if [[ -z "${STAFF_NEW_NAME// }" ]]; then
        printf "ERROR - insufficient # of person names in list\n"
        exit 1
    else 
        OUTPUT_BUFFER="${OUTPUT_BUFFER}update staff set staff_name='$STAFF_NEW_NAME' where staff_user_fk='$STAFF_TO_UPDATE';\n"
        ((PERSON_INDEX++))
    fi
done

for SCHOOL_TO_UPDATE in $SCHOOLS_TO_UPDATE; do
    SCHOOL_NEW_NAME=$(random_school_name $SCHOOL_INDEX);
    if [[ -z "${SCHOOL_NEW_NAME// }" ]]; then
        printf "ERROR - insufficient # of school names in list\n"
        exit 1
    else 
        OUTPUT_BUFFER="${OUTPUT_BUFFER}update school set school_name='$SCHOOL_NEW_NAME' where school_id='$SCHOOL_TO_UPDATE';\n"
        ((SCHOOL_INDEX++))
    fi

done

OUTPUT_BUFFER="${OUTPUT_BUFFER}delete from address;\n"

UPDATE_USER_STRING="update user set username='mattg',password='"'$2a$12$QEJJY2BIGzObt/qXynvaHOYmbvvdlcJzUV7PlmDgs0St1C.m4bkrK'"', enabled=1 where user_id = 1;\n"

OUTPUT_BUFFER="${OUTPUT_BUFFER}${UPDATE_USER_STRING}\n"

OUTPUT_BUFFER="${OUTPUT_BUFFER}COMMIT;\n"

# printf "$OUTPUT_BUFFER"
RESULTS=$($MYSQL_LOGIN_COMMAND -se "$OUTPUT_BUFFER")

exit 0;
