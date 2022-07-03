package com.codzzz.lang.easy.statemachine.core;


import com.codzzz.lang.easy.statemachine.cache.TransitionCache;
import com.codzzz.lang.easy.statemachine.cache.impl.LocalTransitionCacheImpl;
import com.codzzz.lang.easy.statemachine.constants.Constants;
import com.codzzz.lang.easy.statemachine.context.StateContext;
import com.codzzz.lang.easy.statemachine.context.impl.DefaultStateContext;
import com.codzzz.lang.easy.statemachine.executor.EventExecutor;
import com.codzzz.lang.easy.statemachine.executor.EventExecutorManager;
import com.codzzz.lang.easy.statemachine.message.EventBuilder;
import com.codzzz.lang.easy.statemachine.message.EventMessage;
import com.codzzz.lang.easy.statemachine.model.event.EventResult;
import com.codzzz.lang.easy.statemachine.model.metadata.StateMachineMetadata;
import com.codzzz.lang.easy.statemachine.transaction.EventTransition;
import com.codzzz.lang.easy.statemachine.utils.Asserts;
import com.codzzz.lang.easy.statemachine.utils.DateUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
public abstract class AbstractStateMachine<E, S> implements StateMachine<E, S> {

    /**
     * 状态机元数据
     */
    @Getter
    private final StateMachineMetadata stateMachineMetaData;

    /**
     * Transition本地缓存
     */
    private final TransitionCache<E, S> transitionCache = new LocalTransitionCacheImpl<>();

    /**
     * 事件转换执行器
     */
    private volatile EventExecutor<EventResult> eventExecutor;

    public AbstractStateMachine() {
        this.stateMachineMetaData = StateMachineMetadata.of(configId(), configName());
    }

    /**
     * 状态机日志标识
     *
     * @return -
     */
    private String getStateMachineLogIdentifier() {
        return Objects.toString(stateMachineMetaData.getMachineId(), Constants.EMPTY)
                + Constants.UNDERSCORE
                + Objects.toString(stateMachineMetaData.getMachineName(), Constants.EMPTY);
    }

    /**
     * 事件日志标识
     *
     * @param event 事件
     * @return -
     */
    private String getEventLogIdentifier(E event) {
        return eventToString(event);
    }

    /**
     * 根据事件消息找到对应的Transition
     *
     * @param eventMessage 事件消息
     * @return 具体转换实现
     */
    private EventTransition<E, S> findTransition(EventMessage<E, S> eventMessage) {
        return transitionCache.findTransition(eventMessage.getEvent(), eventMessage.getSourceStatus(), eventMessage.getTargetStatus());
    }

    /**
     * 具体Transition执行
     *
     * @param stateContext 状态机上下文
     * @param transition   执行Transition
     * @return 事件执行结果
     */
    private EventResult processTransitionEvent(final StateContext<E, S> stateContext, final EventTransition<E, S> transition) {
        long startMills = DateUtils.nowTimeStamp();
        boolean evaluated;
        //状态机日志标识
        String stateMachineLogIdentifier = this.getStateMachineLogIdentifier();
        //事件日志标识
        String eventLogIdentifier = this.getEventLogIdentifier(stateContext.getEvent());
        //上下文日志信息
        String logMessage = transition.transitionLogMessage(stateContext);
        try {
            log.info("[Transition][{}][{}] evaluate start,Message:[{}]", stateMachineLogIdentifier, eventLogIdentifier, logMessage);
            evaluated = transition.evaluateTransition(stateContext);
        } catch (Exception e) {
            log.error("[TRANSITION_ERROR][{}][{}] Transition evaluated with error,Message:[{}]",
                    stateMachineLogIdentifier, eventLogIdentifier, logMessage, e);
            return EventResult.ofFail(e);
        }
        if (evaluated) {
            try {
                //PreTransition
                transition.preTransition(stateContext);
                log.info("[Transition][{}][{}] preTransition finished", stateMachineLogIdentifier, eventLogIdentifier);

                //OnTransition
                transition.onTransition(stateContext);
                log.info("[Transition][{}][{}] onTransition finished", stateMachineLogIdentifier, eventLogIdentifier);
            } catch (Exception  e) {
                log.error("[TRANSITION_ERROR][{}][{}] Transition execute with error,Message:[{}]",
                        stateMachineLogIdentifier, eventLogIdentifier, logMessage, e);
                return EventResult.ofFail(e);
            } finally {
                //PostTransition
                transition.postTransition(stateContext);
                log.info("[Transition][{}][{}] postTransition finished", stateMachineLogIdentifier, eventLogIdentifier);
            }
        } else {
            log.warn("[Transition][{}][{}] evaluate to false,Message:[{}]", stateMachineLogIdentifier, eventLogIdentifier, logMessage);
        }
        //记录耗时
        log.info("[Transition][{}][{}] finished,execMills:[{}]", stateMachineLogIdentifier, eventLogIdentifier,
                DateUtils.nowTimeStamp() - startMills);

        return EventResult.ofSuccess();
    }

    /**
     * @see StateMachine#sendEvent(Object, Object)
     */
    @Override
    public EventResult sendEvent(E event, Object payload) {
        EventMessage<E, S> eventMessage = EventBuilder.<E, S>withEvent(event).setPayload(payload).build();

        return this.sendEvent(eventMessage);
    }

    /**
     * @see StateMachine#sendEvent(EventMessage)
     */
    @Override
    public EventResult sendEvent(EventMessage<E, S> eventMessage) {
        final EventTransition<E, S> transition = findTransition(eventMessage);
        Asserts.notNull(transition, "未找到消息对应转换");

        final StateContext<E, S> stateContext = new DefaultStateContext<>(eventMessage);

        EventExecutor<EventResult> eventExecutor = getEventExecutor();
        try {
            return eventExecutor.execute(() -> processTransitionEvent(stateContext, transition));
        } catch (Exception e) {
            return EventResult.ofFail(e);
        }
    }

    /**
     * @see StateMachine#sendEvent(Object)
     */
    @Override
    public EventResult sendEvent(E event) {
        EventMessage<E, S> message = EventBuilder.<E, S>withEvent(event).build();

        return sendEvent(message);
    }

    /**
     * @see StateMachine#setEventExecutor(EventExecutor)
     */
    @Override
    public void setEventExecutor(EventExecutor<EventResult> eventExecutor) {
        Asserts.notNull(eventExecutor, "事件执行器不能为空");
        this.eventExecutor = eventExecutor;
    }

    /**
     * 注册状态转换至当前状态机
     *
     * @param eventTransitions 状态机具体实现bean列表
     */
    @Override
    public void registryTransitions(List<EventTransition<E, S>> eventTransitions) {
        transitionCache.registryTransitions(eventTransitions, true);
    }

    /**
     * event转换成String，用于打印事件日志信息
     *
     * @param event 转换事件
     * @return 事件字符串
     */
    protected String eventToString(E event) {
        return Optional.ofNullable(event).map(String::valueOf).orElse(Constants.EMPTY);
    }

    /**
     * 获取事件执行器
     *
     * @return -
     */
    protected EventExecutor<EventResult> getEventExecutor() {
        return Optional.ofNullable(eventExecutor).orElseGet(() -> {
            //默认使用同步执行器
            this.eventExecutor = EventExecutorManager.getDefaultSyncEventExecutor();
            return eventExecutor;
        });
    }

    /**
     * 配置状态机id
     *
     * @return 状态机id
     */
    protected abstract String configId();

    /**
     * 配置状态机名称
     *
     * @return 状态机名称
     */
    protected abstract String configName();

}