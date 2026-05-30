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
         * 切勿修改 STORAGE_TAG_DO_NOT_CHANGE 和 TAG_INFO 的值，
         * 否则将破坏向后兼容性，导致无法读取之前存储的数据。
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
     * SharedPreferences.apply 方法的兼容类。
     * 旧版本 Android 不支持 apply 时，自动降级为 commit。
     * <p>
     * commit 是同步写入，apply 是异步写入，优先使用 apply。
     */
    private static class SPCompat {
        private static final Method APPLY_METHOD = findApplyMethod();

        /**
         * 通过反射查找 apply 方法
         *
         * @return apply 方法，若不存在则返回 null
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
         * 优先使用 apply 提交，若不可用则降级为 commit
         *
         * @param editor 编辑器
         * @return true 表示提交成功
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
