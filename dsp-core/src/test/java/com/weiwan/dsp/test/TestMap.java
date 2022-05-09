package com.weiwan.dsp.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/26 18:10
 * @description:
 */
public class TestMap {
    public static void main(String[] args) {
        Map<String, Object> a = new HashMap();
        Map<String, Object> b = new HashMap();
        Map<String, Object> c = new HashMap();
        Map<String, Object> d = new HashMap();

        Map<String, Object> e = new HashMap();
        Map<String, Object> f = new HashMap();
        Map<String, Object> g = new HashMap();
        Map<String, Object> h = new HashMap();

        Map<String, Object> f1 = new HashMap();
        Map<String, Object> g2 = new HashMap();
        Map<String, Object> c3 = new HashMap();
        Map<String, Object> d4 = new HashMap();
        a.put("b", b);
        b.put("c", c);
        c.put("d", d);

        e.put("f", f);
        f.put("g", g);
        g.put("h", h);

        e.put("b", f1);
        f1.put("adb", "abd");
        f1.put("wdadwadwadwa",2);
        c.put("test",new Object());
        b.put("cdadwa",1);
        b.put("a", Arrays.asList("1",2));



        Map<String, Object> merge = merge(a, e);

        System.out.println(merge);

    }

    public static Map<String, Object> merge(Map oldMap, Map newMap) {
        for (Object newKey : newMap.keySet()) {
            Object o = oldMap.get(newKey);
            //不存在,直接放入
            if (o == null) oldMap.put(newKey, newMap.get(newKey));

            if (o instanceof Map) {
                Map<String, Object> merge = merge((Map<String, Object>) o, (Map<String, Object>) newMap.get(newKey));
                oldMap.put(newKey, merge);
            }
        }
        return oldMap;
    }
}
