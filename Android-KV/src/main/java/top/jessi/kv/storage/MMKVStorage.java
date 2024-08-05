package top.jessi.kv.storage;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jessi on 2023/8/19 0:33
 * Email：17324719944@189.cn
 * Describe：MMKV存储
 */
public class MMKVStorage implements Storage {

    private final MMKV mMMKV;

    public MMKVStorage(Context context) {
        String STORAGE_TAG_DO_NOT_CHANGE = "KVMM";
        MMKV.initialize(context,context.getFilesDir().getAbsolutePath() + "/" + STORAGE_TAG_DO_NOT_CHANGE);
        mMMKV = MMKV.defaultMMKV();
    }

    public MMKVStorage(Context context, String fileName) {
        MMKV.initialize(context,context.getFilesDir().getAbsolutePath() + "/" + fileName);
        mMMKV = MMKV.defaultMMKV();
    }

    @Override
    public <T> boolean put(String key, T value) {
        KvUtils.checkNull("key", key);
        return mMMKV.encode(key, String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) mMMKV.getString(key, null);
    }

    @Override
    public <T> Map<String, T> getAll() {
        Map<String, T> map = new HashMap<>();
        String[] keyArr = mMMKV.allKeys();
        if (keyArr == null) return map;
        for (String s : keyArr) {
            map.put(s, get(s));
        }
        return map;
    }

    @Override
    public boolean delete(String key) {
        try {
            mMMKV.removeValueForKey(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAll() {
        try {
            mMMKV.clearAll();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long count() {
        return mMMKV.count();
    }

    @Override
    public boolean contains(String key) {
        return mMMKV.containsKey(key);
    }
}
