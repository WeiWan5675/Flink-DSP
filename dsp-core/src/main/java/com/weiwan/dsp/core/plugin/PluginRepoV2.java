package com.weiwan.dsp.core.plugin;

import cn.hutool.core.util.ObjectUtil;
import com.weiwan.dsp.api.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: xiaozhennan
 * @description: 插件仓库, 管理插件元信息
 */
public class PluginRepoV2 {


    //jarFileUrl -> PackageMetaDatas{className -> MetaData} 索引
    private Map<String, Map<String, PluginMetaData>> pluginMetaInfoMapping = new ConcurrentHashMap<>();

    //className -> jarFileUrl 的索引
    private Map<String, String> classNameMapping = new ConcurrentHashMap<>();

    //注意这个锁不支持锁升级,支持锁降级  读中不能有写 , 写中可以有读
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    //写写互斥(写读可升级)
    private static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    //读读不互斥(读写不可升级)
    private static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    public void save(Map<String, PluginMetaData> metaInfos) {
        writeLock.lock();
        try {
            if (metaInfos != null) {
                for (String key : metaInfos.keySet()) {
                    PluginMetaData pluginMetaData = metaInfos.get(key);
                    update(pluginMetaData);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void save(PluginMetaData metaInfo) {
        writeLock.lock();
        try {
            if (metaInfo != null) {
                update(metaInfo);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void updateLoadTime(String className, PluginMetaData pluginMetaData) {
        PluginMetaData search = search(className);
        if (search != null) {
            pluginMetaData.setLoadTime(search.getLoadTime());
        }
    }


    public Map<String, PluginMetaData> delete(String urlKey) {
        writeLock.lock();
        try {
            Map<String, PluginMetaData> infos = null;
            if (StringUtils.isNotBlank(urlKey)) {
                infos = pluginMetaInfoMapping.remove(urlKey);
            }
            if (infos != null) {
                infos.forEach((k, v) -> classNameMapping.remove(k));
            }
            return copy(infos);
        } finally {
            writeLock.unlock();
        }
    }


    public boolean disable(String className) {
        writeLock.lock();
        try {
            if (StringUtils.isNotBlank(className)) {
                PluginMetaData metaData = findMetaData(className);
                metaData.setDisabled(true);
                update(metaData);
            }
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    public boolean block(String className) {
        writeLock.lock();
        try {
            if (StringUtils.isNotBlank(className)) {
                PluginMetaData metaData = findMetaData(className);
                metaData.setBlocked(true);
                update(metaData);
            }
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    private PluginMetaData update(PluginMetaData metaData) {
        if (metaData != null) {
            //这里更新的话,主要是针对重新加载了jar的情况,所以metaData的数据都是完全更新过的
            //直接put就可以了
            //更新有可能 3 -> 2 需要删除然后重新put
            String urlKey = metaData.getPluginUrl().toString();
            //获取url对应的package
            //如果之前就已经存在的插件,设置第一次加载的时间
            updateLoadTime(metaData.getPluginClass(), metaData);
            //设置lastReloadTime
            metaData.setLastReloadTime(System.currentTimeMillis());

            Map<String, PluginMetaData> packageMetaData = pluginMetaInfoMapping.get(urlKey);
            if (packageMetaData == null) {
                packageMetaData = new HashMap<>();
                pluginMetaInfoMapping.put(urlKey, packageMetaData);
            }
            //重新生成package内的索引
            packageMetaData.put(metaData.getPluginClass(), metaData);
            //重新生成className到packageUrl的索引
            classNameMapping.put(metaData.getPluginClass(), urlKey);
        }
        return metaData;
    }

    /**
     * 不允许又改的,查找到的元数据都是库里元数据的copy
     *
     * @param className
     * @return
     */
    public PluginMetaData search(String className) {
        PluginMetaData metaData = findMetaData(className);
        return copy(metaData);
    }

    /**
     * 不允许又改的,查找到的元数据都是库里元数据的copy
     *
     * @param urlKey
     * @return
     */
    public Map<String, PluginMetaData> search(URL urlKey) {
        readLock.lock();
        try {
            Map<String, PluginMetaData> map = pluginMetaInfoMapping.get(urlKey);
            return copy(map);
        } finally {
            readLock.unlock();
        }
    }

    private PluginMetaData findMetaData(String className) {
        readLock.lock();
        try {
            //根据className查找metaData
            String urlKey = this.classNameMapping.get(className);
            if (urlKey != null) {
                return get(urlKey, className);
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }


    public <T> T copy(T pluginMetaData) {
        return ObjectUtil.clone(pluginMetaData);
    }

    private PluginMetaData get(String urlKey, String className) {
        if (urlKey != null) {
            Map<String, PluginMetaData> infos = pluginMetaInfoMapping.get(urlKey);
            if (infos != null) {
                return infos.get(className);
            }
        }
        return pluginMetaInfoMapping.get(urlKey).get(className);
    }
}
