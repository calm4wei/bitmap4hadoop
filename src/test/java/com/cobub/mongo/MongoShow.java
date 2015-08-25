package com.cobub.mongo;

import com.mongodb.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by weifeng on 2015/8/25.
 */
public class MongoShow {

    Mongo mongo;
    DB db;
    DBCollection tagCid;

    @Before
    public void init(){
        mongo = new Mongo("192.168.1.160",27017);
        db = mongo.getDB("mydb");
        tagCid = db.getCollection("tag_cid");
    }

    @Test
    public void findByTags(){
        BasicDBObject obj = new BasicDBObject("tags",new BasicDBObject(QueryOperators.ALL,new String[]{
                "java","江苏南京","宅男","IT男","淘宝","新闻工作者","单身狗"
                , "桃子" , "双截棍" , "美食" , "时尚" , "美女" , "呵呵" , "法拉利"
                , "演艺圈" , "牛腩" , "大数据工作者", "现代" , "低调" , "消费水平较低"
        }));

        long t1 = System.currentTimeMillis();
        DBCursor cursor = tagCid.find(obj);
        int count = cursor.count();
        long t2 = System.currentTimeMillis();
        while (cursor.hasNext()){
            System.out.println(cursor.next());
        }
        long t3 = System.currentTimeMillis();
        mongo.close();
        System.out.println("t2-t1=" + ((t2-t1) / 1000) + ", t3-t2=" + ((t3-t2) / 1000) + ", count=" + count);
    }
}
