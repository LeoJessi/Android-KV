package top.jessi.kv.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jessi on 2023/8/18 11:57
 * Email：17324719944@189.cn
 * Describe：数据库存储
 */
public class SQLiteStorage extends SQLiteOpenHelper implements Storage {

    private final SQLiteDatabase mSQLiteDatabase;
    private static final int VERSION = 1;
    private static final String DATABASE = "KVDB";
    private static final String TABLE = "kvdb_table";
    private static final String PRIMARY_KEY = "key_id";
    private static final String KEY = "kvdb_key";
    private static final String VALUE = "kvdb_value";

    public SQLiteStorage(Context context) {
        super(context, DATABASE, null, VERSION);
        mSQLiteDatabase = this.getWritableDatabase();
    }

    public SQLiteStorage(@Nullable Context context, @Nullable String name,
                         @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mSQLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * 创建一张数据表table 带有三个字段
         * PRIMARY_KEY --> 主键 自增
         * KEY --> 存放数据key 唯一 不能为空
         * VALUE --> 存放数据value
         * */
        db.execSQL("CREATE TABLE " + TABLE + " ( " + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY + " " +
                "VARCHAR UNIQUE NOT NULL, " + VALUE + " VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /*增*/
    @Override
    public <T> boolean put(String key, T value) {
        try {
            KvUtils.checkNull("key", key);
            if (contains(key)) {
                return update(key, value);
            }
            // 开启事务
            mSQLiteDatabase.beginTransaction();
            // 往数据表插入数据

            // 1.直接使用sql语句
            // mSQLiteDatabase.execSQL("INSERT INTO " + TABLE + " ( " + KEY + " , " + VALUE + " ) VALUES ( '" + key +
            // "' , '" + value + "' )");
            // 2.使用execSQL方法中带有占位符的方法，不会导致当插入的值中带有特殊字符 如” ' “ 导致的数据库插入异常
            mSQLiteDatabase.execSQL("INSERT INTO " + TABLE + " ( " + KEY + " , " + VALUE + " ) VALUES (?,?)",
                    new Object[]{key, value});

            // 事务执行成功
            mSQLiteDatabase.setTransactionSuccessful();
            // 结束事务
            mSQLiteDatabase.endTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*查*/
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        T value = null;
        try {
            mSQLiteDatabase.beginTransaction();
            // 查询key的第一条数据
            // Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM " + TABLE + " WHERE " + KEY + " = '" + key +
            // "'" + " LIMIT 1", null);
            Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM " + TABLE + " WHERE " + KEY + " = ?" + " LIMIT 1",
                    new String[]{key});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    value = (T) cursor.getString(cursor.getColumnIndexOrThrow(VALUE));
                }
                cursor.close();
            }
            mSQLiteDatabase.setTransactionSuccessful();
            mSQLiteDatabase.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getAll() {
        Map<String, T> map = new HashMap<>();
        try {
            mSQLiteDatabase.beginTransaction();
            // 查询整个数据表的数据   DESC 降序  ASC 升序
            Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM " + TABLE + " ORDER BY " + PRIMARY_KEY + " ASC",
                    null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    map.put(cursor.getString(cursor.getColumnIndexOrThrow(KEY)),
                            (T) cursor.getString(cursor.getColumnIndexOrThrow(VALUE)));
                }
                cursor.close();
            }
            mSQLiteDatabase.setTransactionSuccessful();
            mSQLiteDatabase.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /*删*/
    @Override
    public boolean delete(String key) {
        try {
            mSQLiteDatabase.beginTransaction();
            // 根据key删除指定数据
            // mSQLiteDatabase.execSQL("DELETE FROM " + TABLE + " WHERE " + KEY + " = '" + key + "'");
            mSQLiteDatabase.execSQL("DELETE FROM " + TABLE + " WHERE " + KEY + " = ?", new Object[]{key});
            mSQLiteDatabase.setTransactionSuccessful();
            mSQLiteDatabase.endTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAll() {
        try {
            mSQLiteDatabase.beginTransaction();
            // 删除一整张数据表
            mSQLiteDatabase.execSQL("DELETE FROM " + TABLE);
            mSQLiteDatabase.setTransactionSuccessful();
            mSQLiteDatabase.endTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long count() {
        // 使用Android自带的方法统计数据库中有多少条数据
        return DatabaseUtils.longForQuery(mSQLiteDatabase, "SELECT COUNT(*) FROM " + TABLE, null);
    }

    @Override
    public boolean contains(String key) {
        try {
            mSQLiteDatabase.beginTransaction();
            // 查询是否有一条key的数据
            // Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM " + TABLE + " WHERE " + KEY + " = '" + key +
            // "'" + " LIMIT 1", null);
            Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * FROM " + TABLE + " WHERE " + KEY + " = ?" + " LIMIT 1",
                    new String[]{key});
            if (cursor != null) {
                // 如果游标能指向下一个 则代表有数据
                if (cursor.moveToFirst()) {
                    cursor.close();
                    return true;
                }
                cursor.close();
            }
            mSQLiteDatabase.setTransactionSuccessful();
            mSQLiteDatabase.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*改*/
    private <T> boolean update(String key, T value) {
        try {
            if (contains(key)) {
                KvUtils.checkNull("key", key);
                mSQLiteDatabase.beginTransaction();
                // mSQLiteDatabase.execSQL("UPDATE " + TABLE + " SET " + VALUE + " = '" + value + "' WHERE " + KEY +
                // " = '" + key + "'");
                mSQLiteDatabase.execSQL("UPDATE " + TABLE + " SET " + VALUE + " = ? WHERE " + KEY + " = ?",
                        new Object[]{key, value});
                mSQLiteDatabase.setTransactionSuccessful();
                mSQLiteDatabase.endTransaction();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
