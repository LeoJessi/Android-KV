package top.jessi.kv.storage;

import java.util.Map;

/**
 * 存储中间层，用于持久化数据。
 *
 * <p>如果内置实现不满足需求，可自行实现此接口。</p>
 *
 * @see SharedPreferencesStorage
 */
public interface Storage {

    /**
     * 存入一条数据
     *
     * @param key   键名
     * @param value 值
     * @param <T>   值的类型
     * @return 存入成功返回 true，否则返回 false
     */
    <T> boolean put(String key, T value);

    /**
     * 获取一条数据
     *
     * @param key 键名
     * @param <T> 值的类型
     * @return 与键名关联的对象
     */
    <T> T get(String key);

    /**
     * 获取存储中的所有条目
     *
     * @return 以 Map 形式返回
     */
    <T> Map<String, T> getAll();

    /**
     * 删除一条数据
     *
     * @param key 键名
     * @return 删除成功返回 true，否则返回 false
     */
    boolean delete(String key);

    /**
     * 清空所有数据
     *
     * @return 清空成功返回 true，否则返回 false
     */
    boolean deleteAll();

    /**
     * 获取存储中的条目数量
     *
     * @return 条目数量
     */
    long count();

    /**
     * 检查存储中是否包含指定键名的数据
     *
     * @param key 键名
     * @return 存在返回 true，否则返回 false
     */
    boolean contains(String key);
}
