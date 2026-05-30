package top.jessi.kv.storage;

public class NoEncryption implements Encryption {
    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String encrypt(String key, String value) {
        return value;
    }

    @Override
    public String decrypt(String key, String value) {
        return value;
    }
}
