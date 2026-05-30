package top.jessi.kv.storage;

/**
 * 日志拦截器，用于拦截库中产生的所有日志。
 */
public interface LogInterceptor {

    /**
     * 每次写入日志时触发。
     *
     * @param message 日志消息
     */
    void onLog(String message);
}
