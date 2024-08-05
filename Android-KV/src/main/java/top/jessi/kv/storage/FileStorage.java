package top.jessi.kv.storage;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.jessi.jhelper.file.Files;


/**
 * Created by Jessi on 2023/8/30 10:37
 * Email：17324719944@189.cn
 * Describe：文件存储
 */
public class FileStorage implements Storage {

    private final Map<String, String> mAllMap;
    private final Gson mGson;
    private final String FILE_PATH;
    private final Pattern mKVPattern = Pattern.compile("\\{([^}]+)\\}");

    public FileStorage() {
        FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/JToolA/KVFILE.txt";
        Files.create(FILE_PATH);
        mGson = new Gson();
        mAllMap = getAll();
    }

    public FileStorage(String filePath) {
        FILE_PATH = filePath;
        if (!Files.isExists(FILE_PATH)) {
            if (!Files.create(FILE_PATH)) {
                throw new UnsupportedOperationException("File creation failed. Please check if the file path is valid");
            }
        }
        mGson = new Gson();
        mAllMap = getAll();
    }

    @Override
    public <T> boolean put(String key, T value) {
        KvUtils.checkNull("key", key);
        mAllMap.put(key, (String) value);
        return Files.write(FILE_PATH, mGson.toJson(mAllMap), false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) mAllMap.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getAll() {
        Map<String, T> map = new HashMap<>();
        try {
            String allData = Files.read(FILE_PATH);
            Matcher matcher = mKVPattern.matcher(allData);
            while (matcher.find()) {
                Map<String, String> data = mGson.fromJson(matcher.group(0), new TypeToken<Map<String, String>>() {
                }.getType());
                if (data != null) {
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        map.put(entry.getKey(), (T) entry.getValue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public boolean delete(String key) {
        try {
            mAllMap.remove(key);
            Files.write(FILE_PATH, mGson.toJson(mAllMap), false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAll() {
        try {
            mAllMap.clear();
            Files.write(FILE_PATH, mGson.toJson(mAllMap), false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long count() {
        return mAllMap.size();
    }

    @Override
    public boolean contains(String key) {
        return mAllMap.containsKey(key);
    }
}
