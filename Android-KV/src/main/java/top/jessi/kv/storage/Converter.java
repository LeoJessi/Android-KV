package top.jessi.kv.storage;

/**
 * 编码/解码中间层，用于处理数据的编码与解码。
 * <p>如需自定义实现，可实现此接口。</p>
 *
 * @see KvConverter
 */
public interface Converter {

    /**
     * 对值进行编码。
     *
     * @param value 待编码的值
     * @return 编码后的字符串
     */
    <T> String toString(T value);

    /**
     * 对值进行解码。
     *
     * @param value 编码后的数据
     * @return 原始值
     */
    <T> T fromString(String value, DataInfo dataInfo) throws Exception;

}
