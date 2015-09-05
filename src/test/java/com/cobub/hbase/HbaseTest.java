package com.cobub.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by weifeng on 2015/9/4.
 */
public class HbaseTest {

    HTable errorTable;
    byte[] fBytes;

    @Before
    public void init() throws IOException {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "192.168.1.160");
        errorTable = new HTable(conf,"wf:error");
        fBytes = Bytes.toBytes("f");
    }

    @After
    public void close() throws IOException {
        if (errorTable != null){

            errorTable.close();
        }
    }

    @Test
    public void insert_rowkey_prefix_date() throws IOException {

        System.out.println(errorTable);
        errorTable.setAutoFlushTo(false);
        List<Put> puts = new ArrayList<Put>();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++){
            String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            Put put = new Put(Bytes.toBytes("20150705" + "_" + uuid));
            put.add(fBytes, Bytes.toBytes("stacktrace"), Bytes.toBytes("java.io.IOException:file not found" + UUID.randomUUID().toString()));
//            puts.add(put);
            errorTable.put(put);
            if ( i % 10000 == 0){
                errorTable.flushCommits();
            }
        }
        errorTable.flushCommits();
        long t2 = System.currentTimeMillis();
        System.out.println("count=" + puts.size() + ",t2-t1=" + (t2 - t1));
//        errorTable.close();
    }

    @Test
    public void insert_multithread() throws InterruptedException {


        for (int k = 1; k <= 10; k++){
            final int j = k;
            new Thread(new Runnable() {
                public void run() {
                    try {

//                    List<Put> puts = new ArrayList<Put>();
                        Configuration conf = new Configuration();
                        conf.set("hbase.zookeeper.quorum", "192.168.1.160");
                        HTable table = new HTable(conf,"wf:error");
                        table.setAutoFlushTo(false);
                        long t1 = System.currentTimeMillis();
                        for (int i = 0; i < 1000000; i++){
                            String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
                            Put put = new Put(Bytes.toBytes( uuid + "_" +"2015070" + j ));
                            put.add(fBytes,Bytes.toBytes("stacktrace"),Bytes.toBytes("java.io.IOException:file not found" + UUID.randomUUID().toString()));
//                        puts.add(put);
                            table.put(put);
                            if (i % 10000 == 0) {
                                table.flushCommits();
                            }

                        }
                        table.close();
                        long t2 = System.currentTimeMillis();
                        System.out.println(Thread.currentThread() + ",t2-t1=" + (t2 - t1));
                    }catch (IOException e){

                    }
                }
            }).start();
        }

        System.out.println("waiting.....");
        Thread.sleep(1000 * 60 * 10);
        System.out.println("completing.......");
    }

    @Test
    public void scan_by_prefix_date() throws IOException {
        FilterList fl = new FilterList();
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL,new SubstringComparator("20150903"));
        fl.addFilter(filter);
        Scan scan = new Scan();
        scan.setFilter(fl);

        long t1 = System.currentTimeMillis();
        ResultScanner rs = errorTable.getScanner(scan);
        Result result;
        int count = 0;
        while ((result = rs.next()) != null){
            System.out.println("rowkey=" + new String(result.getRow()));
            System.out.println("value=" + new String(result.getValue(fBytes, Bytes.toBytes("stacktrace"))));
            System.out.println();
            count++;
        }
        long t2 = System.currentTimeMillis();
        System.out.println("count=" + count + ",t2 - t1=" + ((t2 - t1) / 1000));

    }




}
