package com.cobub.mongo;

import com.mongodb.*;

/**
 * Created by weifeng on 2015/8/21.
 */
public class MongoTest {

    public static void main(String[] args){
        Mongo mongo = new Mongo("192.168.1.160",27017);
        DB db = mongo.getDB("mydb");

        DBCollection tagCid = db.getCollection("tag_cid");
//        long total = tagCid.count();

//        DBObject query = tagCid.findOne();

        long t1 = System.currentTimeMillis();
        DBCursor cursor = tagCid.find();
        while (cursor.hasNext()){
            System.out.println(cursor.next());
        }
        long t2 = System.currentTimeMillis();

        System.out.println(tagCid.getName() + "=" + cursor.count());

        System.out.println("finding all uses time is:" + ((t2 - t1)));

    }


}
