package com.cobub.bigdata.mr;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import com.wbkit.cobub.utils.DateUtil;
import com.wbkit.cobub.utils.RegexFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.serializer.Serialization;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobub.bigdata.common.Config;
import com.cobub.bigdata.common.Constant;
import com.wbkit.cobub.json.JSONObject;

public class BitmapStatistics {

    private static Logger logger = LoggerFactory.getLogger(BitmapStatistics.class);

    public static void main(String[] args) throws URISyntaxException {
        Config.setup();
        Configuration conf = Config.getConf();
        Job job = null;
        String JOB_NAME = null;
        boolean isSucc = false;
        String dateTime = null;
        try {
            System.out.print("BitmapStatistics starting");
//            dateTime = args[0];
            JOB_NAME = "MR-BitmapStatistics";
            String path = conf.get("hdfs.razor.clientdata.path");
//            Path inputPath = new Path(path);
            job = new Job(conf, JOB_NAME);
//            DistributedCache.setCacheArchives(new URI[]{new URI("hdfs://master:9000/user/hbase/RoaringBitmap-0.4.10.jar")},conf);
//            DistributedCache.addFileToClassPath(new Path("/user/hbase/RoaringBitmap-0.4.10.jar"),conf);

            job.setJarByClass(BitmapStatistics.class);
            job.setMapperClass(BitMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
//            job.setMapOutputValueClass(MutableRoaringBitmap.class);

            FileInputFormat.setInputPathFilter(job, RegexFilter.class);
            FileInputFormat.addInputPath(job, new Path(path + "/20150805/12"));
            FileInputFormat.addInputPath(job, new Path(path + "/20150805/13"));
//            FileInputFormat.setInputPaths(job,);
            logger.info("MR-BitmapStatistics....");
            TableMapReduceUtil.initTableReducerJob(
                    Constant.TABLE_EXPRESS,
                    BitReducer.class,
                    job);
            TableMapReduceUtil.addDependencyJars(job);
            isSucc = job.waitForCompletion(true);
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
//MutableRoaringBitmap
    public static class BitMapper extends Mapper<LongWritable, Text, Text, Text> {
        Configuration conf = null;
//        HTable expressTable = null;
        HTable deviceTable = null;

        @Override
        protected void setup(Context context)
                throws IOException, InterruptedException {
            conf = context.getConfiguration();
//            expressTable = new HTable(conf, Constant.TABLE_EXPRESS);
            deviceTable = new HTable(conf,Constant.TABLE_DEVICEID);

        }

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString();
            JSONObject jsonObj = new JSONObject(line);
            String deviceid = jsonObj.getString("deviceid");
            String localTime = jsonObj.getString("localtime");
            String dateTime = localTime.substring(0, 10).replaceAll("-", "");
            Iterator<String> iter = jsonObj.keys();
//      List<Put> putList = new ArrayList<Put>();
            Get get = new Get(Bytes.toBytes(deviceid));
            Result result = deviceTable.get(get);
            if (result.isEmpty()){
                System.out.println("deviceid=" + deviceid + " is empty");
                return;
            }
            int count = Bytes.toInt(result.getValue(Constant.FBytes, Constant.QBytes));
            MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(count);
            System.out.println("deviceid=" + deviceid + ", count=" + count);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.serialize(new DataOutputStream(bos));
            ByteBuffer buffer = ByteBuffer.wrap(bos.toByteArray());
            System.out.println("bos.size="  + bos.size());
            bos.close();
            byte[] bytes = buffer.array();
            System.out.println("map.bytes.length=" + bytes.length);
            while (iter.hasNext()) {
                String k = iter.next();
                String kValue = "";
                if (!k.equals(deviceid)) {
                    kValue = jsonObj.getString(k);
                    String rowkey = dateTime + "_" + k + "_" + kValue;
                    context.write(new Text(rowkey),new Text(bytes)) ;
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            deviceTable.close();
        }
    }

    public static class BitReducer extends TableReducer<Text, Text, Text> {
        Configuration conf = null;
        HTable expressTable = null;
        HTable deviceTable = null;

        @Override
        protected void setup(Context context)
                throws IOException, InterruptedException {
            conf = Config.getConf();
            expressTable = new HTable(conf, Constant.TABLE_EXPRESS);
            deviceTable = new HTable(conf, Constant.TABLE_DEVICEID);
            expressTable.setAutoFlush(false,true);

        }

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
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
        protected void cleanup(Context context)
                throws IOException, InterruptedException {
            expressTable.setAutoFlush(true,true);
            deviceTable.close();
            expressTable.close();
        }
    }


}
