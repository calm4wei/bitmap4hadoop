#!/bin/bash -l
# Don't edit this file unless you know exactly what you're doing.

export JAVA_HOME=${JAVA_HOME}
export RAZOR_BITMAP_HOME=$(dirname $(cd $(dirname $0); pwd))
export HADOOP_CONF=/etc/hadoop/conf.cloudera.yarn
export HBASE_CONF=/etc/hbase/conf
export HADOOP_CLASSPATH=/opt/cloudera/parcels/CDH/lib/hadoop/client/*
export LD_LIBRARY_PATH=/opt/cloudera/parcels/CDH/lib/hadoop/lib/native
export HBASE_CLASSPATH=/opt/cloudera/parcels/CDH/lib/hbase/lib/*
export HBASE_NAMESPACE='razor'

echo "JAVA_HOME=$JAVA_HOME"
echo "RAZOR_BITMAP_HOME=$RAZOR_BITMAP_HOME"
echo "HADOOP_CONF=$HADOOP_CONF"
echo "HBASE_CONF=$HBASE_CONF"
echo "HADOOP_CLASSPATH=$HADOOP_CLASSPATH"
echo "HBASE_CLASSPATH=$HBASE_CLASSPATH"
echo "HBASE_NAMESPACE=$HBASE_NAMESPACE"

