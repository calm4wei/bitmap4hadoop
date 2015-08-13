package com.cobub.bigdata.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by weifeng on 15/7/23.
 */
public class TestMao {

    public static void main(String args[]) throws Exception{

        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "slave02,slave01,master");
        conf.addResource("/conf/customer-mr.xml");
        Job job = new Job(conf);
        job.setJarByClass(TestMao.class);
        job.setMapperClass(TestMap.class);
        job.setMapOutputKeyClass(TestMap.class);
        job.setMapOutputValueClass(TestReduce.class);

        job.setReducerClass(TestReduce.class);
        System.out.println("wait for completion.");

        job.waitForCompletion(true);
        System.out.println("waiting........");

    }

    public static class TestMap extends Mapper<LongWritable, Text, Text, Text>{
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//            super.map(key, value, context);
            System.out.println("map starting...");context.getInputSplit().getLength();
        }
    }

    public static class TestReduce extends Reducer<Text,Text,Text,Text>{
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
//            super.reduce(key, values, context);
            System.out.println("reduce starting...");
        }
    }

//    public static void main(String[] args){
//       System.out.println(1000111 % 10000);
//    }
}
