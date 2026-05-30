package top.jessi.kv.storage;

/**
 * 数据信息封装类，用于存储序列化后的数据类型和密文。
 */
final class DataInfo {

    /** 普通对象类型 */
    static final char TYPE_OBJECT = '0';
    /** List 类型 */
    static final char TYPE_LIST = '1';
    /** Map 类型 */
    static final char TYPE_MAP = '2';
    /** Set 类型 */
    static final char TYPE_SET = '3';

    final char dataType;
    final String cipherText;
    final Class<?> keyClazz;
    final Class<?> valueClazz;

    DataInfo(char dataType, String cipherText, Class<?> keyClazz, Class<?> valueClazz) {
        this.cipherText = cipherText;
        this.keyClazz = keyClazz;
        this.valueClazz = valueClazz;
        this.dataType = dataType;
    }
}
