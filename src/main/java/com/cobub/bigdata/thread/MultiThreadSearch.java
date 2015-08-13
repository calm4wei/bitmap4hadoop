
package com.cobub.bigdata.thread;


import com.cobub.bigdata.common.Config;
import com.cobub.bigdata.common.Constant;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

/**
 * Created by weifeng on 2015/8/13.
 *  * 为了快速多维度查询：
 * 对于一些常用指标，可以预先计算出来，并且持久化；
 * 对于活跃用户，可以将每一天的计算结果进行合并，只合并相同 key-value的bitmap，来做and。与其他不同任意维度进行交叉时，只需做and。
 *      如果按月为一个周期，可以将合并为1、2、3、5、8、13、21
 *
 *
 * 数据的意义：单纯的追逐任意维度查询有什么意思？对用户能有什么实质性的帮助？
 *
 * 怎样的数据才是用户需要的，才是真正有价值的？
 * 除了发现应用的整体用户趋势、运营趋势外，对每个用户的深入挖掘才是最有意义的，比如消费水平、兴趣爱好、购买趋势等。
 *
 */
public class MultiThreadSearch {

    public static void main(String[] args){

    }
    /**
     * 一个月中维度交叉查询
     *  ·某个设备型号出现的次数
     *  ·某个版本出现的次数
     */
    public static void findMultiDeminality() throws IOException, InterruptedException, ExecutionException {

        // 指定某几天
        String[] days = {"20150812","20150812"};

        // 需要查询的指标
        final String[] indexs = {"devicename_huawei","os_version_1.0"};

        // 可以根据天数实例化线程池
        ExecutorService threadTool = Executors.newFixedThreadPool(days.length);
        CompletionService<MutableRoaringBitmap> completionService =
                new ExecutorCompletionService<MutableRoaringBitmap>(threadTool);

        long t1 = System.currentTimeMillis();
        for (final String day:days){
            final HTable table = new HTable(Config.getConf(), Constant.TABLE_EXPRESS);
            final MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
            completionService.submit(new Callable<MutableRoaringBitmap>() {
                public MutableRoaringBitmap call() throws Exception {
                    ImmutableRoaringBitmap imbm;
                    for (String index : indexs){
                        Get get = new Get(Bytes.toBytes(day + "_" + index));
                        Result result = table.get(get);
                        ByteBuffer bb =
                                ByteBuffer.wrap(result.getValue(Constant.FBytes, Bytes.toBytes(Constant.QUALIFER)));
                        imbm = new ImmutableRoaringBitmap(bb);
                        bitmap.and(imbm);
                    }
                    return bitmap;
                }
            });
            table.close();
        }

        // 最终合并的bitmap
        MutableRoaringBitmap bm1 = new MutableRoaringBitmap();
        for (String day:days){
            MutableRoaringBitmap bm2 = completionService.take().get();
            System.out.println("getCardinality=" + bm2.getCardinality() + "getSizeInBytes=" + bm2.getSizeInBytes());
            bm1.and(bm2);
        }

        long t2 = System.currentTimeMillis();
        System.out.println("t2 - t1=" + ((t2 - t1) / 1000));
        System.out.println("getCardinality" + bm1.getCardinality() + "getSizeInBytes" + bm1.getSizeInBytes());

    }

}
