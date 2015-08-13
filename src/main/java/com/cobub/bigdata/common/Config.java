package com.cobub.bigdata.common;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by weifeng on 15/7/15.
 */
public class Config {

    private static Logger logger = LoggerFactory.getLogger(Config.class);
    private static Configuration conf = HBaseConfiguration.create();
    private static HConnection hBaseConn = null;
    private static int reduceTaskNum = 4;

    public static void setup() {
        InputStream in = null;
        try {
            in = ClassLoader.getSystemResourceAsStream("customer-mr.xml");
            if (null != in){
                conf.addResource(in);
            }else {
                throw new FileNotFoundException("customer-mr.xml is not found.");
            }
            logger.info("conf is init:" + conf);
            String nameSpace = System.getenv("HBASE_NAMESPACE");
            if (nameSpace == null || nameSpace.isEmpty()) {
                logger.warn("HBASE_NAMESPACE is not set, use default value: razor");
                nameSpace = "razor";
            }

            conf.set("hbase.razor.namespace", nameSpace);
            reduceTaskNum = conf.getInt("hbase.reduce.tasknum", reduceTaskNum);
            //////////
            conf.set("hbase.zookeeper.quorum", "slave02,slave01,master");
            conf.set("hdfs.razor.clientdata.path", "/user/hbase/bitmap/cd");
            conf.set("hdfs.razor.event.path", "/user/hbase/bitmap/event");
            conf.set("hdfs.razor.usinglog.path", "/user/hbase/bitmap/usinglog");
            conf.set("hdfs.razor.error.path", "/user/hbase/bitmap/error");

            conf.set("fs.defaultFS", "hdfs://master:8020");
            Configuration HBASE_CONFIG = HBaseConfiguration.create(conf);
            //////
            hBaseConn = HConnectionManager.createConnection(conf);
            DistributedCache.addFileToClassPath(new Path(conf.get("roaring.bitmap")),conf);
            logger.info("hBaseConn = " + hBaseConn);
        } catch (IOException e) {
            logger.error("An error has caught",e);
            e.printStackTrace();
        }
    }

    public static Configuration getConf(){
        return conf;
    }

    public static HConnection getHConn(){
        return hBaseConn;
    }

    public static int getReduceTaskNum(){
        return reduceTaskNum;
    }
}
