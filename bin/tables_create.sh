#!/bin/bash -l
# Create all tables
# Don't edit this file unless you know exactly what you're doing.

BIN_DIR=$(cd $(dirname $0); pwd)
. $BIN_DIR/env.sh

# Namespace
namespace=$HBASE_NAMESPACE

# Tables
result_tables='arbitrary_dimensionality device_id launch_dimensioinality counters'

# Initial script of creating table
tables_create="create_namespace '$namespace'"

# Original tables
for table in $result_tables
do
  table="$namespace:$table"
  tables_create="$tables_create\ncreate '$table', {NAME=>'f', VERSIONS=>10, BLOOMFILTER=>'NONE', COMPRESSION=>'SNAPPY', DATA_BLOCK_ENCODING=>'DIFF'}, NUMREGIONS=>3, SPLITALGO=>'HexStringSplit'"
done

if [ `command -v hbase` ]; then
  echo -e $tables_create | hbase shell
else
  if [ "$HBASE_HOME" == "" ]; then
    echo "Environment variable HBASE_HOME is not set."
    exit 1
  else
    echo -e $tables_create | $HBASE_HOME/bin/hbase shell
  fi
fi

