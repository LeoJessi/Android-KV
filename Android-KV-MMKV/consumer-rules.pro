# Android-KV-MMKV 模块 Consumer ProGuard Rules
# 作为 AAR 依赖时，这些规则会自动应用到使用者的混淆配置中

# ==================== 核心公开 API — 必须保留 ====================

# Mmkv 工具类：所有 public 方法
-keep class top.jessi.kv.mmkv.Mmkv {
    public *;
}

# MMKVStorage 类：实现 Storage 接口，public 方法
-keep class top.jessi.kv.mmkv.MMKVStorage {
    public *;
}

# ==================== 依赖 Android-KV 模块的规则 ====================

# 保留 Android-KV 模块的公开 API（防止传递依赖被混淆）
-keep class top.jessi.kv.storage.KV {
    public static *;
}
-keep class top.jessi.kv.storage.KvBuilder {
    public *;
}
-keep class top.jessi.kv.Spkv {
    public *;
}

# 保留 Android-KV 模块的所有公开接口
-keep interface top.jessi.kv.storage.Storage { *; }
-keep interface top.jessi.kv.storage.Encryption { *; }
-keep interface top.jessi.kv.storage.Parser { *; }
-keep interface top.jessi.kv.storage.Serializer { *; }
-keep interface top.jessi.kv.storage.Converter { *; }
-keep interface top.jessi.kv.storage.LogInterceptor { *; }
-keep interface top.jessi.kv.storage.KvFacade { *; }

# 保留接口实现类
-keep class * implements top.jessi.kv.storage.Storage { public *; }
-keep class * implements top.jessi.kv.storage.Encryption { public *; }
-keep class * implements top.jessi.kv.storage.Parser { public *; }
-keep class * implements top.jessi.kv.storage.Serializer { public *; }
-keep class * implements top.jessi.kv.storage.Converter { public *; }
-keep class * implements top.jessi.kv.storage.LogInterceptor { public *; }
-keep class * implements top.jessi.kv.storage.KvFacade { public *; }

# ==================== MMKV 原生库相关 ====================

# 保留 MMKV 原生类（JNI 调用）
-keep class com.tencent.mmkv.MMKV { *; }
-keep class com.tencent.mmkv.MMKV$LibLoader { *; }

# ==================== Gson 序列化相关 ====================

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# ==================== 保留异常和注解 ====================

-keepattributes Exceptions
-keepattributes Signature
-keepattributes *Annotation*
