package com.cobub.bigdata.common;

import org.apache.hadoop.hbase.util.Bytes;

public interface Constant {
  
  String prefix = "razor:";
  String F = "f";
  String QUALIFER = "q";
  byte[] FBytes = Bytes.toBytes(Constant.F);
  byte[] QBytes = Bytes.toBytes(Constant.QUALIFER);
  String TABLE_EXPRESS = prefix + "arbitrary_dimensionality";
  String TABLE_DEVICEID = prefix + "device_id";
  String TABLE_LAUNCH = prefix + "launch_dimensioinality";
  String TABLE_COUTER = prefix + "counters";
  String TABLE_COUNTER_ROWKEY = "increment-one-rowkey";
  byte[] counter_Static_Rowkey = Bytes.toBytes(TABLE_COUNTER_ROWKEY);

}
