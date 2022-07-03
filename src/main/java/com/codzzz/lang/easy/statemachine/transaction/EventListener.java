package com.codzzz.lang.easy.statemachine.transaction;

public interface EventListener<E, S> {

    /**
     * 事件所属名称
     *
     * @return 事件名称
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
}
