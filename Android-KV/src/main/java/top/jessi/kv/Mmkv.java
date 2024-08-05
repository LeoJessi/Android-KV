package top.jessi.kv;

import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jessi on 2022/8/17 17:48
 * Email：17324719944@189.cn
 * Describe：MMKV工具类(可以看做高效率SP)
 */
public class Mmkv {
    private final MMKV sMMKV;

    /*
    * 默认保存路径
    * */
    public Mmkv(Context context) {
        MMKV.initialize(context,context.getFilesDir().getAbsolutePath() + "/mmkv");
        sMMKV = MMKV.defaultMMKV();
    }

    /*
    * 保存在本地路径
    * */
    public Mmkv(Context context , String rootDir){
        MMKV.initialize(context,context.getFilesDir().getAbsolutePath() + "/" + rootDir);
        sMMKV = MMKV.defaultMMKV();
    }

    public Mmkv(Context context , String rootDir, MMKV.LibLoader loader){
        MMKV.initialize(context,context.getFilesDir().getAbsolutePath() + "/" + rootDir, loader);
        sMMKV = MMKV.defaultMMKV();
    }

    /*
     * 保存数据的方法，需要拿到保存数据的具体类型
     * 然后根据类型调用不同的保存方法
     * */
    public boolean put(String key, Object objcet) {
        if (objcet instanceof String s) {
            return sMMKV.encode(key, s);
        }
        if (objcet instanceof Integer i) {
            return sMMKV.encode(key, i);
        }
        if (objcet instanceof Boolean bool) {
            return sMMKV.encode(key, bool);
        }
        if (objcet instanceof Float f) {
            return sMMKV.encode(key, f);
        }
        if (objcet instanceof Long l) {
            return sMMKV.encode(key, l);
        }
        if (objcet instanceof Double d) {
            return sMMKV.encode(key, d);
        } else {
            return sMMKV.encode(key, objcet.toString());
        }
    }

    public void putSet(String key, Set<String> sets) {
        sMMKV.encode(key, sets);
    }

    public void putParcelable(String key, Parcelable obj) {
        sMMKV.encode(key, obj);
    }

    /*
     * 根据传入的key取出对应的值
     * */
    public Object get(String key, Object defValue) {
        if (defValue instanceof String s) {
            return sMMKV.decodeString(key, s);
        }
        if (defValue instanceof Integer i) {
            return sMMKV.decodeInt(key, i);
        }
        if (defValue instanceof Boolean bool) {
            return sMMKV.decodeBool(key, bool);
        }
        if (defValue instanceof Float f) {
            return sMMKV.decodeFloat(key, f);
        }
        if (defValue instanceof Long l) {
            return sMMKV.decodeLong(key, l);
        }
        if (defValue instanceof Double d) {
            return sMMKV.decodeDouble(key, d);
        }
        return null;
    }

    public Set<String> getSet(String key, Set<String> defValue) {
        return sMMKV.decodeStringSet(key, defValue);
    }

    public Parcelable getParcelable(String key) {
        return sMMKV.decodeParcelable(key, null);
    }

    /*
     * 根据key移除
     * */
    public void remove(String key) {
        sMMKV.removeValueForKey(key);
    }

    /*
     * 清除所有key
     * */
    public void clearAll() {
        sMMKV.clearAll();
    }

    /*
     * 判断sp是否包含特定key的数据
     * */
    public boolean contains(String key) {
        return sMMKV.containsKey(key);
    }


    /*
     * 返回所有的键
     * */
    public String[] getAllKeys() {
        return sMMKV.allKeys();
    }


    /**------后面自己写的  不知有没有异常------*/
    /*
    * 返回所有的值
    * */
    public Object[] getAllValues(){
        String[] keyArr = this.getAllKeys();
        Object[] valueArr = new Object[keyArr.length];
        for (int i = 0,len = keyArr.length; i < len; i++) {
            Object obj = getObjectValue(keyArr[i]);
            valueArr[i] = obj;
        }
        return valueArr;
    }

    /*
     * 返回所有键值对
     * 如果value里面有包含(0d < value <= 1.0569021313E-314)的小数(会取整变成0或1)
     * 或者有Boolean类型的value不建议使用(true变成1，false变成0)
     * */
    public Map<String, ?> getAll(){
        Map<String,Object> map = new HashMap<>();
        try{
            String[] keyArr = this.getAllKeys();
            Object[] valueArr = this.getAllValues();
            for (int i = 0,len = keyArr.length; i < len; i++) {
                map.put(keyArr[i], valueArr[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    private Object getObjectValue(String key) {
        /*因为其他基础类型value会读成空字符串,所以不是空字符串即为string or string-set类型*/
        String value = sMMKV.decodeString(key);
        if (!TextUtils.isEmpty(value)) {
            // 判断 string or string-set
            if (value.charAt(0) == 0x01) {
                return sMMKV.decodeStringSet(key);
            } else {
                return value;
            }
        }
        /*
        * float double类型可通过string-set配合判断
        * 通过数据分析可以看到类型为float或double时string类型为空字符串且string-set类型读出空数组
        * 最后判断float为0或NAN的时候可以直接读成double类型,否则读float类型
        * 该判断方法对于非常小的double类型数据 (0d < value <= 1.0569021313E-314) 不生效
        */
        Set<String> set = sMMKV.decodeStringSet(key);
        if (set != null && set.size() == 0) {
            float valueFloat = sMMKV.decodeFloat(key);
            Double valueDouble = sMMKV.decodeDouble(key);
            if (Float.compare(valueFloat, 0f) == 0 || Float.compare(valueFloat, Float.NaN) == 0) {
                return valueDouble;
            } else {
                return valueFloat;
            }
        }

        /*
        * int long bool 类型的处理放在一起, int类型1和0等价于bool类型true和false
        * 判断long或int类型时, 如果数据长度超出int的最大长度, 则long与int读出的数据不等, 可确定为long类型
        */
        int valueInt = sMMKV.decodeInt(key);
        long valueLong = sMMKV.decodeLong(key);
        if (valueInt != valueLong) {
            return valueLong;
        } else {
            return valueInt;
        }
    }

}
