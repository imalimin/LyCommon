package com.lmy.lycommon.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.lmy.zutil.annotation.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李明艺 on 2015/10/13.
 */
public class DbUtil {
    private final static String TAG = DbUtil.class.getSimpleName();

    public static String getTableName(Class cls) {
        return cls.getName().replace(".", "_");
    }

    public static Map<String, Object> getIdField(Class cls) throws NoSuchFieldException {
        Field idField = cls.getDeclaredField("id");
        if (idField == null) throw new NoSuchFieldException("Field id not found!");
        if (!isInt(idField) || !idField.isAnnotationPresent(Id.class) || !idField.getName().equals("id"))
            throw new NoSuchFieldException("Field id not found!");

        Method mSet = null;
        Method mGet = null;
        try {
            mSet = cls.getDeclaredMethod(getMethodNameOfSet(idField), int.class);
            mGet = cls.getDeclaredMethod(getMethodNameOfGet(idField));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("field", idField);
        hashMap.put("method_set", mSet);
        hashMap.put("method_get", mGet);
        return hashMap;
    }

    public static ContentValues getContentValues(Object obj) {
        ContentValues c = new ContentValues();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Method m = obj.getClass().getDeclaredMethod(getMethodNameOfGet(f));
                Object rObj = m.invoke(obj);
                contentValuesPut(c, f.getName(), rObj);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    public static <T> T initObject(Class<T> tClass, Cursor c) throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        T o = null;
        try {
            o = tClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        String[] names = c.getColumnNames();
        for (String n : names) {
            int index = c.getColumnIndex(n);
            if (c.isNull(index)) continue;//为空的字段直接跳过
            Field f = tClass.getDeclaredField(n);
            String type = f.getType().getCanonicalName();
            Method m = tClass.getDeclaredMethod(getMethodNameOfSet(n), f.getType());
            if (SqlBuilder.TYPE_SHORT.equals(type) || SqlBuilder.TYPE_SHORT_CLASS.equals(type))
                m.invoke(o, c.getShort(index));
            if (SqlBuilder.TYPE_INT.equals(type) || SqlBuilder.TYPE_INT_CLASS.equals(type))
                m.invoke(o, c.getInt(index));
            if (SqlBuilder.TYPE_LONG.equals(type) || SqlBuilder.TYPE_LONG_CLASS.equals(type))
                m.invoke(o, c.getLong(index));

            if (SqlBuilder.TYPE_CHAR.equals(type) || SqlBuilder.TYPE_CHAR_CLASS.equals(type)) {
                String s = c.getString(index);
                if (s == null || s.length() == 0) m.invoke(o, (char) 0);
                else
                    m.invoke(o, c.getString(index).charAt(0));
            }
            if (SqlBuilder.TYPE_STRING.equals(type))
                m.invoke(o, c.getString(index));

            if (SqlBuilder.TYPE_FLOAT.equals(type) || SqlBuilder.TYPE_FLOAT_CLASS.equals(type))
                m.invoke(o, c.getFloat(index));
            if (SqlBuilder.TYPE_DOUBLE.equals(type) || SqlBuilder.TYPE_DOUBLE_CLASS.equals(type))
                m.invoke(o, c.getDouble(index));
            if (SqlBuilder.TYPE_BOOLEAN.equals(type) || SqlBuilder.TYPE_BOOLEAN_CLASS.equals(type))
                m.invoke(o, c.getInt(index) > 0 ? true : false);
            if (SqlBuilder.TYPE_BYTE.equals(type) || SqlBuilder.TYPE_BYTE_CLASS.equals(type)) {
                m.invoke(o, (byte) c.getInt(index));
            }
            if (SqlBuilder.TYPE_DATE.equals(type)) {
                m.invoke(o, new Date(c.getLong(index)));
            }
        }
        return o;
    }

    public static ContentValues contentValuesPut(ContentValues c, String key, Object obj) {
//        Log.v(TAG, "contentValuesPut " + key);
        if (obj == null) return c;
        String type = obj.getClass().getCanonicalName();
        if (SqlBuilder.TYPE_SHORT.equals(type) || SqlBuilder.TYPE_SHORT_CLASS.equals(type))
            c.put(key, (short) obj);
        if (SqlBuilder.TYPE_INT.equals(type) || SqlBuilder.TYPE_INT_CLASS.equals(type))
            c.put(key, (int) obj);
        if (SqlBuilder.TYPE_LONG.equals(type) || SqlBuilder.TYPE_LONG_CLASS.equals(type))
            c.put(key, (long) obj);

        if (SqlBuilder.TYPE_CHAR.equals(type) || SqlBuilder.TYPE_CHAR_CLASS.equals(type))
            c.put(key, (char) obj + "");
        if (SqlBuilder.TYPE_STRING.equals(type))
            c.put(key, (String) obj);

        if (SqlBuilder.TYPE_FLOAT.equals(type) || SqlBuilder.TYPE_FLOAT_CLASS.equals(type))
            c.put(key, (float) obj);
        if (SqlBuilder.TYPE_DOUBLE.equals(type) || SqlBuilder.TYPE_DOUBLE_CLASS.equals(type))
            c.put(key, (double) obj);
        if (SqlBuilder.TYPE_BOOLEAN.equals(type) || SqlBuilder.TYPE_BOOLEAN_CLASS.equals(type))
            c.put(key, (boolean) obj);
        if (SqlBuilder.TYPE_BYTE.equals(type) || SqlBuilder.TYPE_BYTE_CLASS.equals(type))
            c.put(key, (byte) obj);
        if (SqlBuilder.TYPE_DATE.equals(type))
            c.put(key, ((Date) obj).getTime());
        return c;
    }

    public static boolean isInt(Field f) {
        return SqlBuilder.TYPE_INT.equals(f.getType().getCanonicalName());
    }

    public static String getMethodNameOfGet(Field f) {
        String type = f.getType().getCanonicalName();
        if (SqlBuilder.TYPE_BOOLEAN.equals(type)) return "is" + toUpCaseFirstCharacter(f.getName());
        return "get" + toUpCaseFirstCharacter(f.getName());
    }

    public static String getMethodNameOfSet(Field f) {
        return "set" + toUpCaseFirstCharacter(f.getName());
    }

    public static String getMethodNameOfSet(String name) {
        return "set" + toUpCaseFirstCharacter(name);
    }

    public static String toUpCaseFirstCharacter(String str) {
        if (isUpCase(str.charAt(1)) || isUpCase(str.charAt(0))) return str;
        return (char) (str.charAt(0) - 32) + str.substring(1);
    }

    private static boolean isUpCase(char c) {
        if (c >= 'A' && c <= 'Z') return true;
        return false;
    }


}
