package top.jessi.kv.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Jessi on 2023/8/18 11:57
 * Email：17324719944@189.cn
 * Describe：SP存储
 */
public final class SharedPreferencesStorage implements Storage {

    private final SharedPreferences preferences;

    public SharedPreferencesStorage(Context context) {
        /**
         * NEVER ever change STORAGE_TAG_DO_NOT_CHANGE and TAG_INFO.
         * It will break backward compatibility in terms of keeping previous data
         */
        String STORAGE_TAG_DO_NOT_CHANGE = "KVSP";
        preferences = context.getSharedPreferences(STORAGE_TAG_DO_NOT_CHANGE, Context.MODE_PRIVATE);
    }

    public SharedPreferencesStorage(Context context, String tag) {
        preferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
    }

    @Override
    public <T> boolean put(String key, T value) {
        KvUtils.checkNull("key", key);
        return SPCompat.apply(getEditor().putString(key, String.valueOf(value)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) preferences.getString(key, null);
    }

    @Override
    public boolean delete(String key) {
        return SPCompat.apply(getEditor().remove(key));
    }

    @Override
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getAll() {
        return (Map<String, T>) preferences.getAll();
    }

    @Override
    public boolean deleteAll() {
        return SPCompat.apply(getEditor().clear());
    }

    @Override
    public long count() {
        return preferences.getAll().size();
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     * 当一些旧手机版本不支持apply时使用commit
     * <p>
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
        public static boolean apply(SharedPreferences.Editor editor) {
            try {
                if (APPLY_METHOD != null) {
                    APPLY_METHOD.invoke(editor);
                    return true;
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
            return editor.commit();
        }
    }

}
