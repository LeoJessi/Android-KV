# Android-KV 模块 Consumer ProGuard Rules
# 作为 AAR 依赖时，这些规则会自动应用到使用者的混淆配置中

# ==================== 核心公开 API — 必须保留 ====================

# KV 主入口类：所有 public static 方法
-keep class top.jessi.kv.storage.KV {
    public static *;
}

# KvBuilder 构建器：所有 public 方法
-keep class top.jessi.kv.storage.KvBuilder {
    public *;
}

# Spkv SharedPreferences 工具类：所有 public 方法
-keep class top.jessi.kv.Spkv {
    public *;
}

# ==================== 公开接口 — 供外部实现，必须保留 ====================

# 所有存储相关接口
-keep interface top.jessi.kv.storage.Storage { *; }
-keep interface top.jessi.kv.storage.Encryption { *; }
-keep interface top.jessi.kv.storage.Parser { *; }
-keep interface top.jessi.kv.storage.Serializer { *; }
-keep interface top.jessi.kv.storage.Converter { *; }
-keep interface top.jessi.kv.storage.LogInterceptor { *; }
-keep interface top.jessi.kv.storage.KvFacade { *; }

# ==================== 接口实现类 — 通过反射或泛型使用 ====================

# Storage 实现类
-keep class * implements top.jessi.kv.storage.Storage {
    public *;
}

# Encryption 实现类
-keep class * implements top.jessi.kv.storage.Encryption {
    public *;
}

# Parser 实现类
-keep class * implements top.jessi.kv.storage.Parser {
    public *;
}

# Serializer 实现类
-keep class * implements top.jessi.kv.storage.Serializer {
    public *;
}

# Converter 实现类
-keep class * implements top.jessi.kv.storage.Converter {
    public *;
}

# LogInterceptor 实现类
-keep class * implements top.jessi.kv.storage.LogInterceptor {
    public *;
}

# KvFacade 实现类
-keep class * implements top.jessi.kv.storage.KvFacade {
    public *;
}

# ==================== Gson 序列化相关 ====================

# 使用 Gson 进行序列化，保留被序列化类的字段
# 用户通过 KV.put/get 存取的自定义对象，其字段名不能被混淆
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Gson 默认使用 TypeToken，保留其构造器
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# ==================== 内部类 ====================

# KvFacade 内部空实现类
-keep class top.jessi.kv.storage.KvFacade$EmptyKvFacade { *; }

# Spkv 内部 SPCompat 类（通过反射调用 apply 方法）
-keepclassmembers class top.jessi.kv.Spkv$SPCompat {
    private static java.lang.reflect.Method findApplyMethod();
    public static void apply(android.content.SharedPreferences$Editor);
}

# ==================== 保留异常和注解 ====================

-keepattributes Exceptions
-keepattributes Signature
-keepattributes *Annotation*
