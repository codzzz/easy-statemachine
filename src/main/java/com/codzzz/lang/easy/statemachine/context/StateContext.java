package com.codzzz.lang.easy.statemachine.context;

import com.codzzz.lang.easy.statemachine.message.EventMessage;

import java.io.Serializable;

public interface StateContext<E, S> extends Serializable {
    /**
     * 获取触发事件
     *
     * @return 触发事件
     */
    E getEvent();

    /**
     * 获取消息体
     *
     * @param <T> 消息体类型
     * @return 消息体
     */
    <T> T getPayload();

    /**
     * 消息体
     *
     * @param clazz 消息体类
     * @param <T>   消息体类型
     * @return 消息体
     */
    <T> T getPayload(Class<T> clazz);

    /**
     * 获取消息头
     *
     * @return 消息头
     */
    EventMessage<E, S> getEventMessage();

    /**
     * 获取消息头值
     *
     * @param header 消息头名称
     * @param clazz  消息头类型
     * @param <T>    消息头对象类型
     * @return 消息头值
     */
    <T> T getMessageHeader(String header, Class<T> clazz);
}
