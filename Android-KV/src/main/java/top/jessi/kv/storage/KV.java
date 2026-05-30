package top.jessi.kv.storage;

import android.content.Context;

import java.util.Map;

/**
 * Android 安全、简单的键值存储工具类。
 */
public final class KV {

    private KV() {
        // 禁止实例化
    }

    static KvFacade sKvFacade = new KvFacade.EmptyKvFacade();

    /**
     * 初始化 KV（无密码保护）。
     *
     * @param context 用于实例化基于 Context 的对象，将使用 ApplicationContext
     */
    public static KvBuilder init(Context context) {
        KvUtils.checkNull("Context", context);
        sKvFacade = null;
        return new KvBuilder(context);
    }

    static void build(KvBuilder kvBuilder) {
        sKvFacade = new DefaultKvFacade(kvBuilder);
    }

    /**
     * 保存任意类型的数据，包括集合、基本类型和自定义对象。
     *
     * @param key   用于区分数据的键
     * @param value 待加密和持久化的数据
     * @return 操作成功返回 true，任何步骤失败均返回 false
     */
    public static <T> boolean put(String key, T value) {
        return sKvFacade.put(key, value);
    }

    /**
     * 根据给定的键获取原始数据及其原始类型。
     * 注意：由于 KV 使用序列化机制，此操作不保证完全正确。
     * 请求的数据类型发生任何变化都可能影响结果。
     * 基本类型和 String 类型的返回是有保证的。
     *
     * @param key 用于获取持久化数据的键
     * @return 原始对象
     */
    public static <T> T get(String key) {
        return sKvFacade.get(key);
    }

    /**
     * 获取已保存的数据，如果为 null，则返回默认值。
     *
     * @param key          用于获取已保存数据的键
     * @param defaultValue 结果为 null 时返回的默认值
     * @return 已保存的对象
     */
    public static <T> T get(String key, T defaultValue) {
        return sKvFacade.get(key, defaultValue);
    }

    /**
     * 获取存储中的所有数据
     *
     * @param <T> 类型
     * @return 以Map的形式返回
     */
    public static <T> Map<String, T> getAll() {
        return sKvFacade.getAll();
    }

    /**
     * 获取已保存数据的数量，每个键计为 1。
     *
     * @return 数据条数
     */
    public static long count() {
        return sKvFacade.count();
    }

    /**
     * 清空存储。注意：加密相关数据（如盐键等）不会被删除。
     * 如需删除加密信息，请使用 resetCrypto 方法。
     *
     * @return 清空成功返回 true
     */
    public static boolean deleteAll() {
        return sKvFacade.deleteAll();
    }

    /**
     * 从存储中移除指定的键值对。
     *
     * @param key 用于从存储中移除相关数据的键
     * @return 删除成功返回 true
     */
    public static boolean delete(String key) {
        return sKvFacade.delete(key);
    }

    /**
     * 检查给定的键是否存在于存储中。
     *
     * @param key 待检查的键
     * @return 如果键存在于存储中则返回 true
     */
    public static boolean contains(String key) {
        return sKvFacade.contains(key);
    }

    /**
     * 验证 KV 是否已正确初始化并构建完成。
     *
     * @return 如果已正确初始化和构建则返回 true，否则返回 false
     */
    public static boolean isBuilt() {
        return sKvFacade.isBuilt();
    }

    public static void destroy() {
        sKvFacade.destroy();
    }

}
