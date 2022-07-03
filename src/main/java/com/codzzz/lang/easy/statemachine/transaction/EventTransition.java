package com.codzzz.lang.easy.statemachine.transaction;

import com.codzzz.lang.easy.statemachine.constants.Constants;
import com.codzzz.lang.easy.statemachine.context.StateContext;

import java.util.Optional;

public interface EventTransition<E, S> extends EventListener<E, S> {
    /**
     * 是否执行转换
     *
     * @param stateContext 状态机上下文
     * @return 是否执行转换
     */
    boolean evaluateTransition(StateContext<E, S> stateContext);

    /**
     * 前置转换
     *
     * @param stateContext 状态机上下文
     */
    void preTransition(StateContext<E, S> stateContext);

    /**
     * 前置执行
     *
     * @param stateContext 状态机上下文
     */
    void onTransition(StateContext<E, S> stateContext);

    /**
     * 后置转换
     *
     * @param stateContext 状态机上下文
     */
    void postTransition(StateContext<E, S> stateContext);

    /**
     * 转换日志信息(需要每个transition去自行实现，防止某些字段过大(比如TaskNode.extInfo,Task.extInfo)导致日志量过大)
     *
     * @param stateContext 状态机上下文
     * @return 日志信息
     */
    default String transitionLogMessage(StateContext<E, S> stateContext) {
        return Optional.ofNullable(stateContext.getEventMessage().toString()).orElse(Constants.EMPTY);
    }

}
