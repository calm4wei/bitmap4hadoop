#!/bin/bash -l
# Recreate all tables
# Don't edit this file unless you know exactly what you're doing.

BIN_DIR=$(cd $(dirname $0); pwd)
$BIN_DIR/tables_drop.sh && $BIN_DIR/tables_create.sh

