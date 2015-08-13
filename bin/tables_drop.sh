#!/bin/bash -l
# Drop all tables
# Don't edit this file unless you know exactly what you're doing.

BIN_DIR=$(cd $(dirname $0); pwd)
. $BIN_DIR/env.sh

# Namespace
namespace=$HBASE_NAMESPACE

# Tables
result_tables='arbitrary_dimensionality device_id'
tables="$result_tables"

# Generate script of dropping table
# Original tables
tables_drop=''
for table in $tables
do
  table="$namespace:$table"
  tables_drop="$tables_drop\ndisable '$table'\ndrop '$table'"
done

if [ `command -v hbase` ]; then
  echo -e $tables_drop | hbase shell
else 
  if [ "$HBASE_HOME" == "" ]; then
    echo "Environment variable HBASE_HOME is not set."
    exit 1
  else 
    echo -e $tables_drop | $HBASE_HOME/bin/hbase shell
  fi  
fi

