package me.wizos.billorcs.utils;

import me.wizos.billorcs.ActivityCollector;

/**
 * 根据资源文件的名字获取id
 * Created by xdsjs on 2015/11/23.
 */
public class ResourceIdUtils {
    /**
     * @param name 资源文件的名字
     * @param type 资源的类型
     * @return
     */
    public static int getIdOfResource(String name, String type) {
        return ActivityCollector.getContext().getResources().getIdentifier(name, type, ActivityCollector.getContext().getPackageName());
    }
}
