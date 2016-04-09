package com.lmy.lycommon.db;

import com.lmy.lycommon.annotation.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by 李明艺 on 2015/10/16.
 */
public class SqlBuilder {
    public final static String ORDER_DESC = "DESC";//降序
    public final static String ORDER_ASC = "ASC";//升序
    private final static String TAG = SqlBuilder.class.getSimpleName();
    public final static String TYPE_SHORT = "short";
    public final static String TYPE_SHORT_CLASS = "java.lang.Short";
    public final static String TYPE_INT = "int";
    public final static String TYPE_INT_CLASS = "java.lang.Integer";
    public final static String TYPE_LONG = "long";
    public final static String TYPE_LONG_CLASS = "java.lang.Long";
    public final static String TYPE_CHAR = "char";
    public final static String TYPE_CHAR_CLASS = "java.lang.Character";
    public final static String TYPE_FLOAT = "float";
    public final static String TYPE_FLOAT_CLASS = "java.lang.Float";
    public final static String TYPE_DOUBLE = "double";
    public final static String TYPE_DOUBLE_CLASS = "java.lang.Double";
    public final static String TYPE_BOOLEAN = "boolean";
    public final static String TYPE_BOOLEAN_CLASS = "java.lang.Boolean";
    public final static String TYPE_BYTE = "byte";
    public final static String TYPE_BYTE_CLASS = "java.lang.Byte";
    public final static String TYPE_DATE = "java.util.Date";
    public final static String TYPE_STRING = "java.lang.String";
    public final static String TYPE_BYTES = "byte[]";

    public static String buildCheck(Class clazz) throws NoSuchFieldException {
        return "CREATE TABLE IF NOT EXISTS " + DBUtil.getTableName(clazz) + " (" + getTableParameter(clazz) + ");";
    }

    public static String buildExistCheck(Class clazz) {
        return "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + DBUtil.getTableName(clazz) + "' ";
    }

    public static String buildSelectByIdNeedValue(Class clazz) {
        return "SELECT * FROM " + DBUtil.getTableName(clazz) + " WHERE id=?";
    }

    public static String buildSelect(Class clazz) {
        return "SELECT * FROM " + DBUtil.getTableName(clazz);
    }

    public static String buildSelect(Class clazz, String orderBy) {
        return "SELECT * FROM " + DBUtil.getTableName(clazz) + " ORDER BY " + orderBy;
    }

    public static String buildSelectByWhere(Class clazz, String where) {
        return "SELECT * FROM " + DBUtil.getTableName(clazz) + " WHERE " + where;
    }

    public static String buildDelete(Object obj) {
        try {
            return "DELETE FROM " + DBUtil.getTableName(obj.getClass()) + " WHERE " + getTableParameter(obj);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String buildDeleteByWhere(Class clazz, String where) {
        return "DELETE FROM " + DBUtil.getTableName(clazz) + " WHERE " + where;
    }

    public static String buildSelectByWhere(Class clazz, String where, String orderBy) {
        String sql = "SELECT * FROM " + DBUtil.getTableName(clazz);
        if (where != null && !where.equals("")) sql += " WHERE " + where;
        if (orderBy != null || !orderBy.equals("")) sql += " ORDER BY " + orderBy;
        return sql;
    }

    public static String buildDropAll(Class clazz) {
        return "DROP TABLE IF EXISTS " + DBUtil.getTableName(clazz);
    }

    public static String buildDeleteAll(Class clazz) {
        return "DELETE FROM " + DBUtil.getTableName(clazz);
    }

    public static String getFieldType(Field f) {
//        Log.v(TAG, "getFieldType " + f.getType().getCanonicalName());
        String type = f.getType().getCanonicalName();
        if (TYPE_SHORT.equals(type) || TYPE_INT.equals(type) || TYPE_LONG.equals(type) || TYPE_SHORT_CLASS.equals(type) || TYPE_INT_CLASS.equals(type) || TYPE_LONG_CLASS.equals(type))
            return "INTEGER";
        if (TYPE_CHAR.equals(type) || TYPE_STRING.equals(type) || TYPE_CHAR_CLASS.equals(type))
            return "TEXT";
        if (TYPE_FLOAT.equals(type) || TYPE_DOUBLE.equals(type) || TYPE_FLOAT_CLASS.equals(type) || TYPE_DOUBLE_CLASS.equals(type))
            return "REAL";
        if (TYPE_BOOLEAN.equals(type) || TYPE_BOOLEAN_CLASS.equals(type))
            return "INTEGER";
        if (TYPE_BYTE.equals(type) || TYPE_BYTE_CLASS.equals(type))
            return "BLOB";
        if (TYPE_DATE.equals(type))
            return "INTEGER";
        return null;
    }

    public static String getTableParameter(Class cls) throws NoSuchFieldException {
        boolean hasId = false;
        String sql = "";
        String idSql = "";
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            if (DBUtil.isStatic(f.getModifiers()))
                continue;
            f.setAccessible(true);
            if (DBUtil.isInt(f) && f.isAnnotationPresent(Id.class) && f.getName().equals("id")) {
                if (hasId) throw new NoSuchFieldException("Field id must be one!");
                hasId = true;
                idSql = f.getName() + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
                continue;
            }
            String type = SqlBuilder.getFieldType(f);
            if (type == null) continue;
            sql += f.getName() + " " + type + ",";
        }
        if (!hasId) throw new NoSuchFieldException("Field id not found!");
        sql = idSql + sql.substring(0, sql.length() - 1);
        return sql;
    }

    public static String getTableParameter(Object obj) throws NoSuchFieldException {
        String sql = "";
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            String type = f.getType().getCanonicalName();
            try {
                Method m = obj.getClass().getDeclaredMethod(DBUtil.getMethodNameOfGet(f));
                if (m != null) {
                    Object o = m.invoke(obj);
                    if (o == null) {
                        sql += f.getName() + " IS NULL" + " AND ";
                        continue;
                    }
                    String t = f.getName() + "=" + o;
                    if (TYPE_CHAR.equals(type) || TYPE_CHAR_CLASS.equals(type))
                        t = f.getName() + "='" + o + "'";
                    if (TYPE_STRING.equals(type))
                        t = f.getName() + "=\"" + o + "\"";
                    if (TYPE_BOOLEAN.equals(type) || TYPE_BOOLEAN_CLASS.equals(type))
                        t = f.getName() + "=" + ((boolean) o ? 1 : 0);
                    if (TYPE_DATE.equals(type))
                        t = f.getName() + "=" + ((Date) o).getTime();
                    if (TYPE_BYTE.equals(type) || TYPE_BYTE_CLASS.equals(type))
                        t = f.getName() + "=" + o;
                    sql += t + " AND ";
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (sql.length() > 5)
            return sql.substring(0, sql.length() - 5);
        return sql;
    }
}
