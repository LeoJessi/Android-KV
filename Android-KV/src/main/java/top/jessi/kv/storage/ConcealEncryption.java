package top.jessi.kv.storage;


import top.jessi.jhelper.enigma.Enigma;

/**
 * 默认使用aes进行加密
 * 加密key为 MagicGalaxyX9944
 * 加密iv为 994400LeoJessi10
 * 可重写本类自定义加密解密算法
 */
public class ConcealEncryption implements Encryption {

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String encrypt(String key, String plainText) {
        return Enigma.encryptAes(plainText, "MagicGalaxyX9944", "994400LeoJessi10");
    }

    @Override
    public String decrypt(String key, String cipherText) {
        return Enigma.decryptAes(cipherText, "MagicGalaxyX9944", "994400LeoJessi10");
    }

}
