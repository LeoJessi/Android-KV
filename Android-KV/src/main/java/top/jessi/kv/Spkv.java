package top.jessi.kv;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Jessi on 2022/8/15 14:52
 * Email：17324719944@189.cn
 * Describe：sp工具类
 */
public class Spkv {
    private final SharedPreferences mPreferences;
    private final SharedPreferences.Editor mEditor;

    public Spkv(Context context) {
        mPreferences = context.getSharedPreferences("Spkv", MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public Spkv(Context context, String fileName) {
        mPreferences = context.getSharedPreferences(fileName, MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    /**
     * 向sp存入指定key对应的数据
     * 其中value可以是String、boolean、float、int、long等各种基本类型的值
     *
     * @param key   key
     * @param value value
     */
    public void put(String key, Object value) {
        if (value instanceof String s) {
            mEditor.putString(key, s);
            SPCompat.apply(mEditor);
            return;
        }
        if (value instanceof Boolean bool) {
            mEditor.putBoolean(key, bool);
            SPCompat.apply(mEditor);
            return;
        }
        if (value instanceof Float f) {
            mEditor.putFloat(key, f);
            SPCompat.apply(mEditor);
            return;
        }
        if (value instanceof Integer i) {
            mEditor.putInt(key, i);
            SPCompat.apply(mEditor);
            return;
        }
        if (value instanceof Long l) {
            mEditor.putLong(key, l);
        } else {
            mEditor.putString(key, value.toString());
        }
        SPCompat.apply(mEditor);
    }

    /**
     * 获取sp存入指定key对应的数据
     * 其中value可以是String、boolean、float、int、long等各种基本类型的值
     *
     * @param key      key
     * @param defValue 默认值
     * @return value
     */
    public Object get(String key, Object defValue) {
        if (defValue instanceof String s) {
            return mPreferences.getString(key, s);
        }
        if (defValue instanceof Boolean bool) {
            return mPreferences.getBoolean(key, bool);
        }
        if (defValue instanceof Float f) {
            return mPreferences.getFloat(key, f);
        }
        if (defValue instanceof Integer i) {
            return mPreferences.getInt(key, i);
        }
        if (defValue instanceof Long l) {
            return mPreferences.getLong(key, l);
        } else {
            return mPreferences.getString(key, String.valueOf(defValue));
        }
    }

    /**
     * 清空sp里所有数据
     */
    public void clear() {
        mEditor.clear();
        mEditor.commit();
    }

    /**
     * 删除sp里指定key对应的数据项
     *
     * @param key key
     */
    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    /**
     * 判断sp是否包含特定key的数据
     *
     * @param key key
     * @return 是否包含这个key
     */
    public boolean contains(String key) {
        return mPreferences.contains(key);
    }

    /**
     * 返回所有键值对
     *
     * @return 返回所有键值对
     */
    public Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     * 当一些旧手机版本不支持apply时使用commit
     * commit方法是同步的
     * 使用apply进行替代，apply异步的进行写入
     */
    private static class SPCompat {
        private static final Method APPLY_METHOD = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor 要提交的数据
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (APPLY_METHOD != null) {
                    APPLY_METHOD.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }
}


