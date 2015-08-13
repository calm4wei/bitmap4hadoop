package com.cobub.bigdata.mr;

import com.cobub.bigdata.common.Config;
import com.cobub.bigdata.common.Constant;
import com.wbkit.cobub.json.JSONObject;
import com.wbkit.cobub.utils.RegexFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.log4j.Logger;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Created by weifeng on 2015/8/12.
 */
public class LaunchStatistics {

    private final static Logger logger = Logger.getLogger(LaunchStatistics.class);

    public static void main(String[] args){
        Config.setup();
        Configuration conf = Config.getConf();
        boolean isSucc = false;
        String dateTime = null;
        HTable counter = null;
        try {
            System.out.print("LaunchStatistics starting");
//            dateTime = args[0];
            String JOB_NAME = "MR-LaunchStatistics";
            String path = conf.get("hdfs.razor.clientdata.path");
//            Path inputPath = new Path(path);

            //TODO 清零counters
            counter = new HTable(conf,Constant.TABLE_COUTER);
            int index = (int) counter.incrementColumnValue(
                    Constant.counter_Static_Rowkey,
                    Constant.FBytes,
                    Constant.QBytes,
                    0
            );
            index = (int) counter.incrementColumnValue(
                    Constant.counter_Static_Rowkey,
                    Constant.FBytes,
                    Constant.QBytes,
                    -index
            );
            counter.close();
            logger.info("counter index = " + index);
            Job job = new Job(conf, JOB_NAME);

            job.setJarByClass(LaunchStatistics.class);
            job.setMapperClass(MapLanuch.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
//            job.setMapOutputValueClass(MutableRoaringBitmap.class);

            FileInputFormat.setInputPathFilter(job, RegexFilter.class);
            FileInputFormat.addInputPath(job, new Path(path + "/20150805/12"));
//            FileInputFormat.addInputPath(job, new Path(path + "/20150805/13"));

            logger.info("MR-LaunchStatistics....");
            TableMapReduceUtil.initTableReducerJob(
                    Constant.TABLE_LAUNCH,
                    ReduceLaunch.class,
                    job);
            TableMapReduceUtil.addDependencyJars(job);
//            logger.info("job=" + job);
            isSucc = job.waitForCompletion(true);
//            logger.info("job=" + job.getJobName() + ",counters=" + job.getCounters() + ",jar=" + job.getJar());
        } catch (IOException e) {
            logger.error("An error has caught.", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.error("An error has caught.", e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            logger.error("An error has caught.", e);
            e.printStackTrace();
        }

    }


    public static class MapLanuch extends Mapper<LongWritable, Text, Text, Text>{

        HTable counter = null;
//        HTable launchTable = null;

//        Increment increment = null;
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            counter = new HTable(conf,Constant.TABLE_COUTER);
            counter.setAutoFlush(false,true);
//            launchTable = new HTable(conf, Constant.TABLE_LAUNCH);
//            byte[] staticRowkey = Bytes.toBytes("increment-one-rowkey");
//            increment = new Increment(staticRowkey);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//            Result result = counter.increment(increment);
            int count = (int) counter.incrementColumnValue(
                    Constant.counter_Static_Rowkey,
                    Constant.FBytes,
                    Constant.QBytes,
                    1
            );
            // TODO hadoop 系统计数器
//            Counter counter = context.getCounter(Constant.TABLE_COUNTER_ROWKEY,context.getJobName());
//            counter.increment(1);
//            int count = (int) counter.getValue();
            String line = value.toString();
            JSONObject json = new JSONObject(line);
            String localTime = json.getString("localtime");
            String dateTime = localTime.substring(0, 10).replaceAll("-", "");
            Iterator<String> keys = json.keys();
            while (keys.hasNext()){
                String k = keys.next();
                String v = json.getString(k);
                if (k.equals("time") || k.equals("localtime")){
                    continue;
                }

                if ("".equals(v)){
                    v = "UNKNOW";
                }
                String rowkey = dateTime + "_" + k +"_" +v;
                MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(count);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.serialize(new DataOutputStream(bos));
                ByteBuffer buffer = ByteBuffer.wrap(bos.toByteArray());
                bos.close();
                byte[] bytes = buffer.array();
                System.out.println("map.bytes.length=" + bytes.length);

                context.write(new Text(rowkey),new Text(bytes));
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            if (counter != null){
                counter.close();
            }
        }
    }

    public static class ReduceLaunch extends TableReducer<Text, Text, Text> {

        HTable launchTable = null;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {

            launchTable = new HTable(context.getConfiguration(), Constant.TABLE_LAUNCH);
            launchTable.setAutoFlush(false,true);
        }

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String keyStr = key.toString();
            System.out.println("keyStr=" + keyStr);
            MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
            ByteBuffer byteBuffer = null;
            ImmutableRoaringBitmap imbitmap = null;
            byte[] bytes = null;
            for (Text value : values) {
                bytes = value.getBytes();
                byteBuffer = ByteBuffer.wrap(bytes);
                imbitmap = new ImmutableRoaringBitmap(byteBuffer);
                bitmap.or(imbitmap);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            bitmap.serialize(dos);
            byteBuffer = ByteBuffer.wrap(bos.toByteArray());
            dos.close();
            bos.close();
            byte[] keyBytes = Bytes.toBytes(keyStr);
            Put put = new Put(keyBytes);
            put.add(Constant.FBytes, Bytes.toBytes(Constant.QUALIFER), Bytes.toBytes(byteBuffer));
            context.write(new Text(keyBytes), put);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            launchTable.close();
        }
    }

}
