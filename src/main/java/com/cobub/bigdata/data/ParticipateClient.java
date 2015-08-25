package com.cobub.bigdata.data;

import com.cobub.bigdata.common.Config;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;

/**
 * Created by weifeng on 2015/8/19.
 */
public class ParticipateClient {

    public static void main(String[] args) throws IOException, DeserializationException {
        Config.setup();

        HTable table = new HTable(Config.getConf(),"razor:participate_degree");

//        Scan scan = new Scan();
//        FilterList filters = new FilterList();
//        FamilyFilter ff = new FamilyFilter(CompareFilter.CompareOp.EQUAL,
//                ByteArrayComparable.parseFrom(Bytes.toBytes("RV")));
//        filters.addFilter(ff);

        getRV(table,"RV");
        getRO(table, "RO");
        getBF(table, "BF");

        table.close();

    }

    private static void getBF(HTable table, String family) throws IOException {
        ResultScanner rs = table.getScanner(Bytes.toBytes(family));
        Result result;
        while ((result = rs.next()) != null){
            String row = new String(result.getRow());
            System.out.print("BF row=" + row);

            List<Cell> cells = result.getColumnCells(Bytes.toBytes(family), Bytes.toBytes("D7"));
            cells.addAll(result.getColumnCells(Bytes.toBytes(family), Bytes.toBytes("D14")));
            cells.addAll(result.getColumnCells(Bytes.toBytes(family), Bytes.toBytes("D30")));
            if (cells != null && cells.size() > 0){
                for (Cell cell : cells){
                    String f = new String(CellUtil.cloneFamily(cell));
                    String q = new String(CellUtil.cloneQualifier(cell));
                    long l = Bytes.toInt(CellUtil.cloneValue(cell));
                    System.out.print(",f=" + f + ",q=" + q + ",value=" +l);
                }
                System.out.println();
            }
        }
    }

    private static void getRO(HTable table, String family) throws IOException {
        ResultScanner rs = table.getScanner(Bytes.toBytes(family));
        Result result;
        while ((result = rs.next()) != null){
            String row = new String(result.getRow());
            System.out.print("RO row=" + row);

            List<Cell> cells = result.getColumnCells(Bytes.toBytes(family), Bytes.toBytes("D7"));
            cells.addAll(result.getColumnCells(Bytes.toBytes(family), Bytes.toBytes("D14")));
            cells.addAll(result.getColumnCells(Bytes.toBytes(family), Bytes.toBytes("D30")));
            if (cells != null && cells.size() > 0){
                for (Cell cell : cells){
                    String f = new String(CellUtil.cloneFamily(cell));
                    String q = new String(CellUtil.cloneQualifier(cell));
                    long l = Bytes.toInt(CellUtil.cloneValue(cell));
                    System.out.print(",f=" + f + ",q=" + q + ",value=" +l);
                }
                System.out.println();
            }
        }
    }

    private static void getRV(HTable table,String family) throws IOException {
        ResultScanner rs = table.getScanner(Bytes.toBytes(family));
        Result result;
        while ((result = rs.next()) != null){
            String row = new String(result.getRow());
            System.out.print("RV row=" + row);
            List<Cell> cells = result.getColumnCells(Bytes.toBytes(family), Bytes.toBytes("D7"));
            if (cells != null && cells.size() > 0){
                for (Cell cell : cells){
                    String f = new String(CellUtil.cloneFamily(cell));
                    String q = new String(CellUtil.cloneQualifier(cell));
                    long l = Bytes.toLong(CellUtil.cloneValue(cell));
                    System.out.print(",f=" + f + ",q=" + q + ",value=" +l);
                }
                System.out.println();
            }

        }
    }
}
