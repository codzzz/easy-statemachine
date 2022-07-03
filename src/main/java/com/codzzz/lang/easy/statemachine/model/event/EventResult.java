package com.codzzz.lang.easy.statemachine.model.event;


public class EventResult implements Serializable {
    private static final long serialVersionUID = -39356352372138262L;

    /**
     * 是否触发成功
     */
    @Getter
    private final boolean             success;
    /**
     * 事件触发异常
     */
    @Getter
    private       Exception           exception;
    /**
     * 错误信息
     */
    @Getter
    private       String              errorMsg;
    /**
     * 上下文信息 预留字段 用于后续扩展
     */
    private final Map<String, Object> resultContext = new HashMap<>(8);

    private EventResult(boolean success) {
        this.success = success;
    }

    /**
     * 设置事件错误
     *
     * @param exception 异常信息
     */
    public void setEventException(Exception exception) {
        this.exception = exception;
        this.errorMsg = exception.getMessage();
    }

    /**
     * 获取上下文结果
     *
     * @param key        key
     * @param resultType 结果类型
     * @param <T>        value type
     * @return 结果对象
     */
    public <T> T getResult(String key, Class<T> resultType) {
        return resultType.cast(resultContext.get(key));
    }

    /**
     * 获取上下文结果
     *
     * @param key key
     * @param <T> value type
     * @return 结果对象
     */
    public <T> T getResult(String key) {
        return ClassUtil.cast(resultContext.get(key));
    }

    /**
     * 存放结果
     *
     * @param key   key
     * @param value value type
     */
    public void putResult(String key, Object value) {
        resultContext.put(key, value);
    }

    /**
     * 失败结果失败
     *
     * @param exception 异常信息
     * @return 结果对象
     */
    public static EventResult ofFail(Exception exception) {
        EventResult eventResult = new EventResult(false);
        eventResult.setEventException(exception);
        return eventResult;
    }

    /**
     * 成功结果返回
     *
     * @return 结果对象
     */
    public static EventResult ofSuccess() {
        return new EventResult(true);
    }

}