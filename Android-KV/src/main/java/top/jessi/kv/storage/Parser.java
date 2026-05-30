package top.jessi.kv.storage;

import java.lang.reflect.Type;

/**
 * 序列化/反序列化中间层，负责最终结果的序列化与反序列化。
 * 注意：此接口与 {@link Serializer} 不同，仅用于将中间值转换为字符串，
 * 或将字符串转换回中间值，供 {@link Storage} 使用。
 *
 * <p>如果内置实现不满足需求，可使用自定义实现。</p>
 *
 * @see GsonParser
 */
public interface Parser {

    /**
     * 将给定文本反序列化为指定类型的对象。
     *
     * @param content 待反序列化的文本
     * @param type    目标对象类型
     * @param <T>     期望的类型
     * @return 反序列化后的对象
     * @throws Exception 如果操作失败则抛出异常
     */
    <T> T fromJson(String content, Type type) throws Exception;

    /**
     * 将给定对象序列化为字符串。
     *
     * @param body 待序列化的对象
     * @return 序列化后的字符串
     */
    String toJson(Object body);

}
