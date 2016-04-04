#!/bin/bash

# mac has bsd sed but we need gnu sed -- if on OSX, install gsed
SED_CMD='sed'
if [[ $OSTYPE == *"darwin"* ]]; then
    SED_CMD='gsed'
fi

## will replace anything within quotes, but not empty quotes
PATT='"[^"]\{1,\}"'

MATCH_NUMBER='s/"number":"[[:digit:]]\{3\}-[[:digit:]]\{3\}-[[:digit:]]\{4\}"/"number":"999-999-9999"/g'
MATCH_HOME_PHONE='s/"home_phone":"[[:digit:]]\{3\}-[[:digit:]]\{3\}-[[:digit:]]\{4\}"/"home_phone":"999-999-9999"/g'
MATCH_ADMIN_USERNAME='s/"admin_username":'"$PATT"'/"admin_username":"dummy-username"/g'
MATCH_TEACHER_USERNAME='s/"teacher_username":'"$PATT"'/"teacher_username":"dummy-username"/g'
MATCH_WORK_EMAIL='s/"work_email":'"$PATT"'/"work_email":"dummy-email"/g'

## temporary? these names are needed to match on deanslist, but this should at least let us test powerschool ETL
MATCH_FIRST_NAME='s/"first_name":'"$PATT"'/"first_name":"firsty"/g'
MATCH_MIDDLE_NAME='s/"middle_name":'"$PATT"'/"middle_name":"middle"/g'
MATCH_LAST_NAME='s/"last_name":'"$PATT"'/"last_name":"mclastname"/g'
## seems broad - will this mess anything up?
MATCH_NAME='s/"name":'"$PATT"'/"name":"some-name"/g'

OVERALL_SED_COMMAND="$MATCH_NUMBER;$MATCH_HOME_PHONE;$MATCH_ADMIN_USERNAME;$MATCH_TEACHER_USERNAME;$MATCH_WORK_EMAIL;$MATCH_FIRST_NAME;$MATCH_MIDDLE_NAME;$MATCH_LAST_NAME;$MATCH_NAME"

$SED_CMD -i "$OVERALL_SED_COMMAND" $1
