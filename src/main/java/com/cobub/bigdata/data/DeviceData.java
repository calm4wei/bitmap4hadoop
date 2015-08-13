package com.cobub.bigdata.data;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Iterator;

import com.cobub.bigdata.common.Config;
import com.cobub.bigdata.common.Constant;
import com.wbkit.cobub.json.JSONObject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HDFS to hbase for deviceid
 * Created by weifeng on 15/7/16.
 */
public class DeviceData {

    private static Logger logger = LoggerFactory.getLogger(DeviceData.class);
    //    private static Configuration conf = null;
//    private static HTable expressTable = null;
    private static HTable deviceidTable = null;

    public static void main(String[] args) throws Exception {
//        conf = Config.getConf();
//        HConnection hConn = Config.getHConn();
//        expressTable = new HTable(conf, Constant.TABLE_EXPRESS);
        System.out.println("staring.......");
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "slave02,slave01,master");
        conf.set("hdfs.razor.clientdata.path", "/user/hbase/bitmap/cd");
        conf.set("hdfs.razor.event.path", "/user/hbase/bitmap/event");
        conf.set("hdfs.razor.usinglog.path", "/user/hbase/bitmap/usinglog");
        conf.set("hdfs.razor.error.path", "/user/hbase/bitmap/error");

        conf.set("fs.defaultFS", "hdfs://master:8020");
        Configuration HBASE_CONFIG = HBaseConfiguration.create(conf);

        FileSystem fs = FileSystem.get(conf);
        RemoteIterator<LocatedFileStatus> locatedFiles = fs.listFiles(new Path("/user/hbase/bitmap/cd"), true);
//        fs.setVerifyChecksum(false);
        Path path = null;

        deviceidTable = new HTable(HBASE_CONFIG, Constant.TABLE_DEVICEID);
        System.out.println("deviceTable=" + deviceidTable);

        int num = 1;
        ResultScanner rs = deviceidTable.getScanner(Bytes.toBytes(Constant.F));
        Iterator<Result> iter = rs.iterator();
        while (iter.hasNext()){
            iter.next();
            System.out.println(num);
            num++;
        }
        System.out.println(num);
        rs.close();
        long t1 = System.currentTimeMillis();
        deviceidTable.setAutoFlush(false,true);
        while (locatedFiles.hasNext()) {
            path = locatedFiles.next().getPath();
            System.out.println("path=" + path.toString() + ",num=" + num);
            num = readFromHdfs2Hbase(path, fs, num);
        }
//        deviceidTable.setAutoFlush(true,true);
        long t2 = System.currentTimeMillis();
        deviceidTable.close();
        System.out.println("import deviceid to hbase time-consuming = " + ((t2 - t1) / 1000));
    }

    private static int readFromHdfs2Hbase(Path path, FileSystem fs, int num) throws IOException {
//        String pathString = conf.get("hdfs.razor.clientdata.path");
//        pathString += "/cd.log.2015072211";
//        logger.info("pathString = " + pathString);
//        FileSystem fs = FileSystem.get(conf);
//        Path path = new Path(pathString);
        FSDataInputStream fsData = fs.open(path);
        String line = null;
        int count = 0;
        while ((line = fsData.readLine()) != null) {

            JSONObject jsonObj = new JSONObject(line);
            String deviceid = (String) jsonObj.get("deviceid");
//            System.out.println("line=" + line + ",deviceid=" + deviceid);
            byte[] rowBytes = Bytes.toBytes(deviceid);
            Get get = new Get(rowBytes);
//            System.out.println("get="+ get);
//            System.out.println("deviceidTable.getName()=" + deviceidTable.getEndKeys());
//            System.out.println("get_id=" + deviceid + "deviceidTable.get(get).isEmpty=" + deviceidTable.get(get).isEmpty());
            if (deviceidTable.get(get).isEmpty()) {
                Put put = new Put(rowBytes);
                put.add(Constant.FBytes, Constant.QBytes, Bytes.toBytes(num));
                deviceidTable.put(put);
                num++;
            }
            if (num % 10000 == 0){
                deviceidTable.flushCommits();
            }

        }
        return num;

//        deviceidTable.close();
    }


}
