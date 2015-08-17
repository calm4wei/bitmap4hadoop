package com.cobub.bigdata.data;

import com.cobub.bigdata.common.Config;
import com.cobub.bigdata.common.Constant;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Created by weifeng on 2015/8/13.
 */
public class LaunchClient {

    public static void main(String[] args) throws IOException {
        Config.setup();

        HTable launchTable = new HTable(Config.getConf(),Constant.TABLE_LAUNCH);

//        HBaseClient.findRowByFilter(launchTable, "os_version");
        getDimensioin(launchTable);
//        getCounter();

        launchTable.close();

    }

    public static void getDimensioin(HTable launchTable) throws IOException {
        String[] points = {
                "20150804_devicename_HTC Aria",
                "20150804_os_version_1.6"
        };
        HBaseClient.dimensionareyAny(launchTable,points);

    }

    public static void getCounter() throws IOException {

        HTable incrementTable = new HTable(Config.getConf(), Constant.TABLE_COUTER);
        long index = incrementTable.incrementColumnValue(Bytes.toBytes(Constant.TABLE_COUNTER_ROWKEY),
                Constant.FBytes,Constant.QBytes,0);
        System.out.println("increment=" + index);
        incrementTable.close();

    }
}
