package com.cobub.bigdata.data;

import com.cobub.bigdata.common.Constant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by weifeng on 15/7/24.
 */
public class HBaseClient {
//    slave02,slave01,master
//    Configuration HBASE_CONFIG = new Configuration();
//    HBASE_CONFIG.set("hbase.zookeeper.quorum", "192.168.3.206");
//    Configuration configuration = HBaseConfiguration.create(HBASE_CONFIG);

    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "slave02,slave01,master");
        Configuration  HBASE_CONFIG = HBaseConfiguration.create(conf);

        HTable expressTable = new HTable(HBASE_CONFIG, Constant.TABLE_EXPRESS);
        expressTable.getEndKeys();

//        findRowByFilter(expressTable,"phonenumber");

        String[] points = {
                "20150804_version_3.0",
                "20150804_cellid_9438147",
                "20150804_network_CDMA",
                "20150804_os_version_3.2",
                "20150804_phonenumber_18958241528",
                "20150804_platform_Android",
                "20150804_language_ar_SA",
                "20150804_uuid_068A9150E0C54D01A25E36C74CF942F0",
                "20150804_appkey_4400d6bdf205dd1850a2b5a9278cad79",
                "20150804_event_identifier_menu_logout",
                "20150804_ismobiledevice_true",
                "20150804_useridentifier_root"

        };
        dimensionareyAny(expressTable,points);

        expressTable.close();

    }

    public static void findRowByFilter(HTable expressTable,String subRowkey) throws IOException{
        Scan scan = new Scan();
        ResultScanner resultScan = null;
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(subRowkey));
        scan.setFilter(filter);
        scan.setCaching(2000);
        resultScan = expressTable.getScanner(scan);
        for (Result rs : resultScan){
            String str = new String(rs.getRow());
            System.out.println(str);
        }
    }

    public static void dimensionareyAny(HTable expressTable, String[] points) throws IOException {
        Result result = null;
        MutableRoaringBitmap bm1 = new MutableRoaringBitmap();
        ImmutableRoaringBitmap bm2 = null;
//        ImmutableRoaringBitmap bm3 = null;
        ByteBuffer bb = null;
        Get get = null;
        //////////////////

        int i = 1;
        bm1.flip(1, Integer.MAX_VALUE);

        long t1 = System.currentTimeMillis();
        for (String point:points){
            long t3 = 0;
            long t4 = 0;
            long t5 = 0;
            long t6 = 0;
            System.out.println("point=" + point);
            t3 = System.currentTimeMillis();
            get = new Get(Bytes.toBytes(point));
            result = expressTable.get(get);
            t4 = System.currentTimeMillis();

            bb = ByteBuffer.wrap(result.getValue(Constant.FBytes, Bytes.toBytes(Constant.QUALIFER)));
            bm2 = new ImmutableRoaringBitmap(bb);
            t5 = System.currentTimeMillis();
            bm1.and(bm2);
            t6 = System.currentTimeMillis();
            System.out.println("get interval time=" + (t4 - t3));
            System.out.println("bitmap exchange interval =" + (t5-t4));
            System.out.println("bitmap add interval =" + (t6-t5));
            System.out.println("bm2.getCardinality())=" + bm2.getCardinality());
            System.out.println("bm2.getSizeInBytes())=" + bm2.getSizeInBytes());
            System.out.println();
        }

        ////////////////
        long t2 = System.currentTimeMillis();
        expressTable.close();
        System.out.println("t2 - t1 = " + (t2 - t1));
        System.out.println("bm1=" + bm1 + ",bm1.getSizeInBytes()=" + bm1.getSizeInBytes());
        System.out.println("bm1.getCardinality())" + bm1.getCardinality());

//        int[] ins = bm1.toArray();
//        int index = 0;
//        System.out.println("ins.length=" + ins.length);
//        for (int in : ins){
//            System.out.print(in);
//            index++;
//            if (index % 50 == 0){
//                System.out.println();
//            }
//        }
    }

}
