package com.codzzz.lang.easy.statemachine.core;

import com.codzzz.lang.easy.statemachine.executor.EventExecutor;
import com.codzzz.lang.easy.statemachine.message.EventMessage;
import com.codzzz.lang.easy.statemachine.model.event.EventResult;
import com.codzzz.lang.easy.statemachine.model.metadata.StateMachineMetadata;
import com.codzzz.lang.easy.statemachine.transaction.EventTransition;

import java.util.List;

public interface StateMachine<E, S> {

    /**
     * 发送消息事件
     *
     * @param event   事件
     * @param payload 传输数据对象
     * @return 事件执行结果
     */
    EventResult sendEvent(E event, Object payload);

    /**
     * 发送消息事件，触发Transition
     *
     * @param eventMessage 事件消息
     * @return 事件执行结果
     * @see com.codzzz.lang.easy.statemachine.message.EventBuilder
     */
    EventResult sendEvent(EventMessage<E, S> eventMessage);

    /**
     * 发送不带消息体的事件
     *
     * @param event 事件
     * @return 事件执行结果
     */
    EventResult sendEvent(E event);

    /**
     * 设置任务执行器
     *
     * @param eventExecutor 事件执行器
     */
    void setEventExecutor(EventExecutor<EventResult> eventExecutor);

    /**
     * 注册状态转换Transition
     *
     * @param eventTransitions -
     */
    void registryTransitions(List<EventTransition<E, S>> eventTransitions);

    /**
     * 获取状态机元数据
     *
     * @return 状态机元数据
     */
    StateMachineMetadata getStateMachineMetaData();

}
