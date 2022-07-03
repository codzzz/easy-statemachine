package com.codzzz.lang.easy.statemachine.context.impl;

import com.codzzz.lang.easy.statemachine.context.StateContext;
import com.codzzz.lang.easy.statemachine.message.EventMessage;
import com.codzzz.lang.easy.statemachine.message.EventMessageHeaders;
import com.codzzz.lang.easy.statemachine.utils.ClassUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@ToString
public class DefaultStateContext<E, S> implements StateContext<E, S> {

    private static final long serialVersionUID = 6528588555931662013L;

    /**
     * 事件消息
     */
    @Getter
    private final EventMessage<E, S> eventMessage;

    public DefaultStateContext(EventMessage<E, S> eventMessage) {
        this.eventMessage = eventMessage;
    }

    /**
     * 获取消息头
     *
     * @return 消息头
     */
    private EventMessageHeaders getMessageHeaders() {
        return Optional.ofNullable(eventMessage).map(EventMessage::getHeaders).orElse(null);
    }

    /**
     * 获取触发事件
     *
     * @return 触发事件
     */
    @Override
    public E getEvent() {
        return Optional.ofNullable(eventMessage).map(EventMessage::getEvent).orElse(null);
    }

    /**
     * 获取消息体
     *
     * @param <T> payLoad对象类型
     * @return payload
     */
    @Override
    public <T> T getPayload() {
        return ClassUtil.cast(Optional.ofNullable(getMessageHeaders()).map(EventMessageHeaders::getPayLoad).orElse(null));
    }

    /**
     * 获取消息体
     *
     * @param clazz payload clazz
     * @param <T>   payLoad对象类型
     * @return payload
     */
    @Override
    public <T> T getPayload(Class<T> clazz) {
        return clazz.cast(Optional.ofNullable(getMessageHeaders()).map(EventMessageHeaders::getPayLoad).orElse(null));
    }

    /**
     * 获取消息头值
     *
     * @param header header key
     * @param clazz  clazz
     * @param <T>    消息头对象类型
     * @return 消息头
     */
    @Override
    public <T> T getMessageHeader(String header, Class<T> clazz) {
        return Optional.ofNullable(getMessageHeaders()).map(msgHeader -> msgHeader.get(header, clazz)).orElse(null);
    }
}
