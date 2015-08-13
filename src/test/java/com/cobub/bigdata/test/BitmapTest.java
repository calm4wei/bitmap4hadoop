//package com.cobub.bigdata.test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//
//import org.apache.hadoop.hbase.util.Bytes;
//import org.junit.Test;
//import org.roaringbitmap.IntIterator;
//import org.roaringbitmap.RoaringBitmap;
//import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
//import org.roaringbitmap.buffer.MutableRoaringBitmap;
//
//public class BitmapTest {
//  
//  @Test
//  public void test1(){
//    RoaringBitmap bitmap = new RoaringBitmap();
//    for (int i = 0; i < 100; i++){
//      bitmap.add(i);
//    }
//    IntIterator intIter = bitmap.getIntIterator();
//    while (intIter.hasNext()){
//      System.out.println(intIter.next());
//    }
//  }
//  
//  @Test
//  public void test2() throws IOException{
//
//    MutableRoaringBitmap rr1 = MutableRoaringBitmap.bitmapOf(1, 2, 3, 1000);
//    MutableRoaringBitmap rr2 = MutableRoaringBitmap.bitmapOf( 2, 3, 1010);
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//    DataOutputStream dos = new DataOutputStream(bos);
//    rr1.serialize(dos);
//    rr2.serialize(dos);
//    dos.close();
//    ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
//    ImmutableRoaringBitmap rrback1 = new ImmutableRoaringBitmap(bb);
//    bb.position(bb.position() + rrback1.serializedSizeInBytes());
//    ImmutableRoaringBitmap rrback2 = new ImmutableRoaringBitmap(bb);
//    System.out.println(rrback1);
//    System.out.println(rrback2);
//
//  }
//  
//  @Test
//  public void test3(){
//    RoaringBitmap bitmap = new RoaringBitmap();
//    RoaringBitmap bitmap2 = new RoaringBitmap();
//    for (int i = 0; i < 100; i = i + 50){
//      bitmap.add(i);
//    }
//    bitmap2.add(2);
//    bitmap.or(bitmap2);
//    IntIterator intIter = bitmap.getIntIterator();
//    while (intIter.hasNext()){
//      System.out.println(intIter.next());
//    }
//  }
//  
//
//}
