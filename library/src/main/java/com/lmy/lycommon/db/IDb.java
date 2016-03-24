package com.lmy.lycommon.db;

import java.util.List;

/**
 * Created by 李明艺 on 2015/10/16.
 */
public interface IDb {
    void save(Object obj);

    <T> List<T> queryBySql(Class<T> tClass, String sql);

    <T> List<T> query(Class<T> tClass);

    <T> List<T> query(Class<T> tClass, String orderBy);

    <T> List<T> queryByWhere(Class<T> tClass, String where);

    <T> List<T> queryByWhere(Class<T> tClass, String where, String orderBy);

    <T> T queryById(Class<T> tClass, int id);

    void delete(Object obj);
    void deleteByWhere(Class clazz, String where);
    void clear(Class clazz);

    int size(Class tClass);
}
