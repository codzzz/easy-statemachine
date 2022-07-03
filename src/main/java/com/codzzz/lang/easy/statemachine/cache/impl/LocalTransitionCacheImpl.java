package com.codzzz.lang.easy.statemachine.cache.impl;


import com.codzzz.lang.easy.statemachine.cache.TransitionCache;
import com.codzzz.lang.easy.statemachine.exception.StateMachineException;
import com.codzzz.lang.easy.statemachine.model.metadata.TransitionMetadata;
import com.codzzz.lang.easy.statemachine.transaction.EventTransition;
import com.codzzz.lang.easy.statemachine.utils.ObjectUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Transition本地缓存默认实现
 *
 * @author
 * @version : LocalTransitionCacheImpl.java, v 0.1 2021年06月24日 7:33 下午 xiaoxuan.zb Exp $
 */
public class LocalTransitionCacheImpl<E, S> implements TransitionCache<E, S> {
    /**
     * 转换map
     */
    private final ConcurrentMap<TransitionMetadata<E, S>, EventTransition<E, S>> transitionMap = new ConcurrentHashMap<>();

    /**
     * 构建Transition元数据
     *
     * @param eventTransition transition具体实现
     * @return 转换元数据
     */
    private TransitionMetadata<E, S> buildTransitionMetaData(EventTransition<E, S> eventTransition) {
        return TransitionMetadata.of(eventTransition.getEvent(), eventTransition.getSourceStatus(), eventTransition.getTargetStatus());
    }

    /**
     * @see TransitionCache#registryTransitions(List, boolean)
     */
    @Override
    public void registryTransitions(List<EventTransition<E, S>> eventTransitions, boolean throwExceptionIfRepeat) {
        for (EventTransition<E, S> eventTransition : eventTransitions) {
            TransitionMetadata<E, S> metaData = buildTransitionMetaData(eventTransition);

            if (transitionMap.containsKey(metaData)) {
                throw new StateMachineException("存在重复状态转换");
            }
            transitionMap.putIfAbsent(metaData, eventTransition);
        }
    }

    /**
     * 是否是相同的事件对象
     *
     * @param event        事件
     * @param compareEvent 比较事件对象
     * @return -
     */
    private boolean isEqualEventObj(E event, E compareEvent) {
        if (event == null || compareEvent == null) {
            return false;
        }
        //枚举 比较引用是否相同
        if (event instanceof Enum<?> || compareEvent instanceof Enum<?>) {
            return event == compareEvent;
        }
        return ObjectUtils.equalObject(event, compareEvent) || (event.getClass() == compareEvent.getClass());
    }

    /**
     * 是否是相同的状态对象
     *
     * @param state        状态对象
     * @param compareState 比较状态对象
     * @return -
     */
    private boolean isEqualStateObj(S state, S compareState) {
        return ObjectUtils.equalObject(state, compareState);
    }

    /**
     * @see TransitionCache#findTransition(Object, Object, Object)
     */
    @Override
    public EventTransition<E, S> findTransition(E event, S sourceStatus, S targetStatus) {
        if (sourceStatus != null && targetStatus != null) {
            return transitionMap.keySet()
                    .stream()
                    .filter(metaData -> isEqualEventObj(metaData.getEvent(), event)
                            && isEqualStateObj(metaData.getSourceStatus(), sourceStatus)
                            && isEqualStateObj(metaData.getTargetStatus(), targetStatus))
                    .findFirst()
                    .map(transitionMap::get)
                    .orElse(null);
        } else {
            return transitionMap.keySet()
                    .stream()
                    .filter(metaData -> isEqualEventObj(metaData.getEvent(), event))
                    .findFirst()
                    .map(transitionMap::get)
                    .orElse(null);
        }
    }
}