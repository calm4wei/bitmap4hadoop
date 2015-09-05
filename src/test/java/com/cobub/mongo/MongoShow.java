package com.cobub.mongo;

import com.wbkit.cobub.json.*;
import com.mongodb.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by weifeng on 2015/8/25.
 */
public class MongoShow {

    MongoClient mongo;
    DB db;
    DBCollection tagCid;

    @Before
    public void init() {
        mongo = new MongoClient("192.168.1.180", 27017);
        db = mongo.getDB("mydb");
        tagCid = db.getCollection("tag_cid");
    }

    @Test
    public void findByTags() {
        BasicDBObject query = new BasicDBObject("tags", new BasicDBObject(QueryOperators.ALL, new String[]{
                "java", "江苏南京", "宅男", "IT男", "淘宝", "新闻工作者"
                , "桃子", "双截棍", "美食", "时尚", "美女", "呵呵"
                , "演艺圈" , "牛腩" , "大数据工作者", "现代" , "低调" , "消费水平较低"
//                "现代舞", "低调深沉有内涵"
        }));

//        BasicDBObject projection = new BasicDBObject("cid",1);

        long t1 = System.currentTimeMillis();
        DBCursor cursor = tagCid.find(query);
        int count = cursor.count();
        long t2 = System.currentTimeMillis();
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
        long t3 = System.currentTimeMillis();
        mongo.close();
        System.out.println("t2-t1=" + ((t2 - t1) / 1000) + ", t3-t2=" + ((t3 - t2) / 1000) + ", count=" + count);
    }

    @Test
    public void modifyByDeviceId() {
//        DBObject updatedValue = new BasicDBObject();
//        updatedValue.put("cid",111158305);
//        updatedValue.put("appkey","1cfdb0601fce11e5bb1c005056ae0c3b");
////        updatedValue.put()
//        DBObject updateSetValue = new BasicDBObject("$set",updatedValue);
//        tagCid.findAndModify(new BasicDBObject("deviceid", "33545433968"), updateSetValue);

        long t1 = System.currentTimeMillis();
        DBCursor cursor = tagCid.find(new BasicDBObject("deviceid", "28303740711"));
        while (cursor.hasNext()) {
//            System.out.println(cursor.next());
            DBObject obj = cursor.next();
            JSONObject jsonObj = new JSONObject(obj.toString());
            System.out.println(jsonObj + "," + jsonObj.getJSONArray("tags").get(0));
        }

        long t2 = System.currentTimeMillis();
        System.out.println("t2-t1=" + (t2 - t1));
    }

    @Test
    public void update() {
        DBObject updateCondition = new BasicDBObject();
        updateCondition.put("deviceid", "33545433968");
        DBObject updatedValue = new BasicDBObject();
//        updatedValue.removeField("headers");
        updatedValue.put("headers", 3);
        updatedValue.put("legs3", new int[]{1, 8, 9, 4});
        updatedValue.put("tags", new String[]{"java", "江苏南京"});
        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
        tagCid.update(updateCondition, updateSetValue, true, true);

        DBCursor cursor = tagCid.find(new BasicDBObject("deviceid", "33545433968"));
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
//            cursor.next();
        }

    }

    @Test
    public void removeByDeviceId() {

        tagCid.remove(new BasicDBObject("deviceid", "33545433968"));
    }


    @Test
    public void insert() {

    }

    @Test
    public void save() {

    }
}
