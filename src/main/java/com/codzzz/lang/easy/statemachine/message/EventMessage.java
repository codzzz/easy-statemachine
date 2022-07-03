package com.codzzz.lang.easy.statemachine.message;

import java.io.Serializable;

public interface EventMessage<E, S> extends Serializable {

    /**
     * 触发事件
     *
     * @return 触发事件
     */
    E getEvent();

    /**
     * 初始状态
     *
     * @return 初始状态
     */
    S getSourceStatus();

    /**
     * 目标状态
     *
     * @return 目标状态
     */
    S getTargetStatus();

    /**
     * 消息头
     *
     * @return 消息头
     */
    EventMessageHeaders getHeaders();
}
