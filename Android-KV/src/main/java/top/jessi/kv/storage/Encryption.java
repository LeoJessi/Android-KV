package top.jessi.kv.storage;

/**
 * 加密解密中间层，用于对存储的值进行加密和解明。
 *
 * <p>如果内置实现不满足需求，可使用自定义实现。</p>
 *
 * @see ConcealEncryption
 */
public interface Encryption {

    /**
     * 初始化加密算法。如果设备不支持所需的加密方式，则返回 false。
     *
     * @return 如果支持加密则返回 true
     */
    boolean init();

    /**
     * 加密给定的字符串，返回密文。
     *
     * @param key   加密密钥
     * @param value 明文
     * @return 密文字符串
     */
    String encrypt(String key, String value) throws Exception;

    /**
     * 解密给定的密文，返回明文。
     *
     * @param key   解密密钥
     * @param value 密文
     * @return 明文
     */
    String decrypt(String key, String value) throws Exception;

}
