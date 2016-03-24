package com.lmy.lycommon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lmy on 2015/10/13.
 */
public class ZDb implements IDb {
    private final static String TAG = ZDb.class.getSimpleName();
    private Context context;
    private static ZDb instance;
    private SQLiteDatabase db;
    private DatebaseHelper datebaseHelper;


    public synchronized static ZDb instance(Context context) {
        if (instance == null)
            instance = new ZDb(context);
        return instance;
    }

    private ZDb(Context context) {
        this.context = context;
        this.datebaseHelper = new DatebaseHelper(context);
        init();
    }

    private void init() {
        this.db = datebaseHelper.getWritableDatabase();
    }

    private void checkBeforeSave(Object obj) {
        check(obj.getClass());
        String tableName = DbUtil.getTableName(obj.getClass());
        try {
            Map<String, Object> map = DbUtil.getIdField(obj.getClass());
            Method mSet = (Method) map.get("method_set");
            Method mGet = (Method) map.get("method_get");
            int id = (int) mGet.invoke(obj);
            int seq = datebaseHelper.getMaxId(tableName);
            if (id == 0) mSet.invoke(obj, seq + 1);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void check(Class clazz) {
        try {
            db.execSQL(SqlBuilder.buildCheck(clazz));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public boolean exist(Class clazz) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SqlBuilder.buildExistCheck(clazz), null);
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    @Override
    public void save(Object obj) {
        checkBeforeSave(obj);
        String tableName = DbUtil.getTableName(obj.getClass());
        ContentValues c = DbUtil.getContentValues(obj);
        Log.v(TAG, tableName);
        Cursor cursor = db.rawQuery(SqlBuilder.buildSelectByIdNeedValue(obj.getClass()), new String[]{c.get("id").toString()});
        if (cursor.getCount() < 1)
            db.insert(tableName, null, c);
        else
            db.update(tableName, c, "id=?", new String[]{c.get("id").toString()});
//        try{
//            db.insert(tableName, null, c);
//        }catch (android.database.sqlite.SQLiteConstraintException e){
//            Log.v(TAG, "catch SQLiteConstraintException");
////            db.update(tableName, c, "id=?", new String[]{c.get("id").toString()});
//        } finally {
//            db.update(tableName, c, "id=?", new String[]{c.get("id").toString()});
//            Log.v(TAG, "finally");
//        }
    }

    @Override
    public <T> List<T> queryBySql(Class<T> tClass, String sql) {
        if (!exist(tClass)) return null;
        List<T> list = new ArrayList<>();
        Cursor c = db.rawQuery(sql, new String[]{});
        if (c.getCount() < 1) return list;
        c.moveToFirst();
        do {
            try {
                T t = DbUtil.initObject(tClass, c);
                list.add(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } while (c.moveToNext());
        c.close();
        return list;
    }

    @Override
    public <T> List<T> query(Class<T> tClass) {
        if (!exist(tClass)) return null;
        return queryBySql(tClass, SqlBuilder.buildSelect(tClass));
    }

    @Override
    public <T> List<T> query(Class<T> tClass, String orderBy) {
        if (!exist(tClass)) return null;
        return queryBySql(tClass, SqlBuilder.buildSelect(tClass, orderBy));
    }

    @Override
    public <T> List<T> queryByWhere(Class<T> tClass, String where) {
        if (!exist(tClass)) return null;
        return queryBySql(tClass, SqlBuilder.buildSelectByWhere(tClass, where));
    }

    @Override
    public <T> List<T> queryByWhere(Class<T> tClass, String where, String orderBy) {
        if (!exist(tClass)) return null;
        return queryBySql(tClass, SqlBuilder.buildSelectByWhere(tClass, where, orderBy));
    }

    @Override
    public <T> T queryById(Class<T> tClass, int id) {
        if (!exist(tClass)) return null;
        List<T> list = queryByWhere(tClass, "id=" + id);
        if (list == null || list.size() < 1) return null;
        return list.get(0);
    }

    @Override
    public void delete(Object obj) {
        if (!exist(obj.getClass())) return;
        String sql = SqlBuilder.buildDelete(obj);
        Log.v(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public void deleteByWhere(Class clazz, String where) {
        if (!exist(clazz)) return;
        String sql = SqlBuilder.buildDeleteByWhere(clazz, where);
        Log.v(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public void clear(Class clazz) {
        if (!exist(clazz)) return;
//        db.execSQL(SqlBuilder.buildDropAll(clazz));
        db.execSQL(SqlBuilder.buildDeleteAll(clazz));
    }

    @Override
    public int size(Class tClass) {
        if (!exist(tClass)) return -1;
        Cursor c = db.rawQuery(SqlBuilder.buildSelect(tClass), new String[]{});
        int size = c.getCount();
        c.close();
        return size;
    }
}
