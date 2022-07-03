package com.codzzz.lang.easy.statemachine.cache;

import com.codzzz.lang.easy.statemachine.transaction.EventTransition;

import java.util.List;

/**
 * Transition缓存
 *
 * @author xiaoxuan.zb
 * @version : TransitionCache.java, v 0.1 2021年06月24日 7:28 下午 xiaoxuan.zb Exp $
 */
public interface TransitionCache<E, S> {

    /**
     * 注册Transition
     *
     * @param eventTransitions       -
     * @param throwExceptionIfRepeat 存在重复Transition时是否抛出异常
     */
    void registryTransitions(List<EventTransition<E, S>> eventTransitions, boolean throwExceptionIfRepeat);

    /**
     * 获取Transition
     *
     * @param event        事件
     * @param sourceStatus 起始状态
     * @param targetStatus 目标状态
     * @return EventTransition
     */
    EventTransition<E, S> findTransition(E event, S sourceStatus, S targetStatus);
}
