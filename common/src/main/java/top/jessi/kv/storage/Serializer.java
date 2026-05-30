package top.jessi.kv.storage;

/**
 * 密文序列化/反序列化中间层，用于将密文与数据类型一起序列化或反序列化。
 *
 * <p>如果内置实现不满足需求，可使用自定义实现。</p>
 *
 * @see KvSerializer
 */
public interface Serializer {

    /**
     * 将密文与数据类型一起序列化。
     *
     * @param cipherText 密文
     * @param value     原始值
     * @param <T>       值类型
     * @return 序列化后的字符串
     */
    <T> String serialize(String cipherText, T value);

    /**
     * 根据给定的 DataInfo 反序列化文本。
     *
     * @param plainText 序列化文本
     * @return 包含数据类型和密文的信息对象
     */
    DataInfo deserialize(String plainText);
}