package top.jessi.kv.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * KvFacade 的默认实现，负责协调加密、序列化、存储等组件完成数据的存取。
 */
public class DefaultKvFacade implements KvFacade {

    private final Storage storage;
    private final Converter converter;
    private final Encryption encryption;
    private final Serializer serializer;
    private final LogInterceptor logInterceptor;

    public DefaultKvFacade(KvBuilder builder) {
        encryption = builder.getEncryption();
        storage = builder.getStorage();
        converter = builder.getConverter();
        serializer = builder.getSerializer();
        logInterceptor = builder.getLogInterceptor();
        logInterceptor.onLog("KV.init -> Encryption : " + encryption.getClass().getSimpleName());
    }

    @Override
    public <T> boolean put(String key, T value) {
        // 参数校验
        KvUtils.checkNull("Key", key);
        log("KV.put -> key: " + key + ", value: " + value);

        // 如果值为 null，则删除该 key 对应的已有数据
        if (value == null) {
            log("KV.put -> 值为 null，将删除该 key 对应的已有数据");
            return delete(key);
        }

        // 1. 转换为文本
        String plainText = converter.toString(value);
        log("KV.put -> 转换为文本: " + plainText);
        if (plainText == null) {
            log("KV.put -> 转换失败");
            return false;
        }

        // 2. 加密文本
        String cipherText = null;
        try {
            cipherText = encryption.encrypt(key, plainText);
            log("KV.put -> 加密为: " + cipherText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cipherText == null) {
            log("KV.put -> 加密失败");
            return false;
        }

        // 3. 将密文与原始对象一起序列化
        String serializedText = serializer.serialize(cipherText, value);
        log("KV.put -> 序列化为: " + serializedText);
        if (serializedText == null) {
            log("KV.put -> 序列化失败");
            return false;
        }

        // 4. 保存到存储
        if (storage.put(key, serializedText)) {
            log("KV.put -> 存储成功");
            return true;
        } else {
            log("KV.put -> 存储操作失败");
            return false;
        }
    }

    @Override
    public <T> T get(String key) {
        log("KV.get -> key: " + key);
        if (key == null) {
            log("KV.get -> null key, returning null value ");
            return null;
        }

        // 1. Get serialized text from the storage
        String serializedText = storage.get(key);
        log("KV.get -> Fetched from storage : " + serializedText);
        if (serializedText == null) {
            log("KV.get -> Fetching from storage failed");
            return null;
        }

        // 2. Deserialize
        DataInfo dataInfo = serializer.deserialize(serializedText);
        log("KV.get -> Deserialized");
        if (dataInfo == null) {
            log("KV.get -> Deserialization failed");
            return null;
        }

        // 3. Decrypt
        String plainText = null;
        try {
            plainText = encryption.decrypt(key, dataInfo.cipherText);
            log("KV.get -> Decrypted to : " + plainText);
        } catch (Exception e) {
            log("KV.get -> Decrypt failed: " + e.getMessage());
        }
        if (plainText == null) {
            log("KV.get -> Decrypt failed");
            return null;
        }

        // 4. Convert the text to original data along with original type
        T result = null;
        try {
            result = converter.fromString(plainText, dataInfo);
            log("KV.get -> Converted to : " + result);
        } catch (Exception e) {
            log("KV.get -> Converter failed");
        }

        return result;
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        T t = get(key);
        if (t == null) return defaultValue;
        return t;
    }

    @Override
    public <T> Map<String, T> getAll() {
        Map<String, T> map = new HashMap<>();
        if (storage.getAll() != null){
            for (String key : storage.getAll().keySet()) {
                map.put(key, get(key));
            }
        }
        return map;
    }

    @Override
    public long count() {
        return storage.count();
    }

    @Override
    public boolean deleteAll() {
        return storage.deleteAll();
    }

    @Override
    public boolean delete(String key) {
        return storage.delete(key);
    }

    @Override
    public boolean contains(String key) {
        return storage.contains(key);
    }

    @Override
    public boolean isBuilt() {
        return true;
    }

    @Override
    public void destroy() {
    }

    private void log(String message) {
        logInterceptor.onLog(message);
    }
}
