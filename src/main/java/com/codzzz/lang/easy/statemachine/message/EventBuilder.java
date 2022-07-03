package com.codzzz.lang.easy.statemachine.message;

import com.codzzz.lang.easy.statemachine.message.impl.DefaultEventMessageImpl;
import org.springframework.util.Assert;

import java.util.Map;

public final class EventBuilder<T, S> {

    /**
     * 消息体
     */
    private final T                   payLoad;
    /**
     * provideMessage
     */
    private final EventMessage<T, S>  provideMessage;
    /**
     * headerAccessor
     */
    private       EventHeaderAccessor headerAccessor;
    /**
     * 事件起始状态
     */
    private       S                   sourceStatus;
    /**
     * 事件目标主体
     */
    private       S                   targetStatus;

    private EventBuilder(EventMessage<T, S> providedMessage) {
        Assert.notNull(providedMessage, "Message must not be null");
        this.payLoad = providedMessage.getEvent();
        this.provideMessage = providedMessage;
        this.headerAccessor = EventHeaderAccessor.getMutableAccessor(providedMessage);
    }

    private EventBuilder(T payload, EventHeaderAccessor accessor) {
        Assert.notNull(payload, "Payload must not be null");
        Assert.notNull(accessor, "MessageHeaderAccessor must not be null");
        this.payLoad = payload;
        this.provideMessage = null;
        this.headerAccessor = accessor;
    }

    /**
     * set headers
     *
     * @param headerAccessor headerAccessor
     * @return EventBuilder
     */
    public EventBuilder<T, S> setHeaders(EventHeaderAccessor headerAccessor) {
        this.headerAccessor = headerAccessor;
        return this;
    }

    /**
     * 设置消息头
     *
     * @param headerName  name
     * @param headerValue value
     * @return EventBuilder
     */
    public EventBuilder<T, S> setHeader(String headerName, Object headerValue) {
        this.headerAccessor.setHeader(headerName, headerValue);
        return this;
    }

    /**
     * 设置消息主体
     *
     * @param payload 消息体
     * @return EventBuilder
     */
    public EventBuilder<T, S> setPayload(Object payload) {
        this.headerAccessor.setPayload(payload);
        return this;
    }

    /**
     * set header if not exists
     *
     * @param headerName  name
     * @param headerValue value
     * @return EventBuilder
     */
    public EventBuilder<T, S> setHeaderIfAbsent(String headerName, Object headerValue) {
        this.headerAccessor.setHeaderIfAbsent(headerName, headerValue);
        return this;
    }

    /**
     * 移除header值
     *
     * @param headerPatterns 消息头字符串
     * @return EventBuilder
     */
    public EventBuilder<T, S> removeHeaders(String... headerPatterns) {
        this.headerAccessor.removeHeaders(headerPatterns);
        return this;
    }

    /**
     * 移除header值
     *
     * @param headerName 消息头
     * @return EventBuilder
     */
    public EventBuilder<T, S> removeHeader(String headerName) {
        this.headerAccessor.removeHeader(headerName);
        return this;
    }

    /**
     * Copy the name-value pairs from the provided Map. This operation will <em>not</em>
     * overwrite any existing values.
     */
    public EventBuilder<T, S> copyHeaders(Map<String, ?> headersToCopy) {
        this.headerAccessor.copyHeaders(headersToCopy);
        return this;
    }

    /**
     * Copy the name-value pairs from the provided Map. This operation will <em>not</em>
     * overwrite any existing values.
     */
    public EventBuilder<T, S> copyHeadersIfAbsent(Map<String, ?> headersToCopy) {
        this.headerAccessor.copyHeadersIfAbsent(headersToCopy);
        return this;
    }

    /**
     * 设置初始状态
     *
     * @param sourceStatus 初始状态
     * @return EventBuilder
     */
    public EventBuilder<T, S> sourceStatus(S sourceStatus) {
        this.sourceStatus = sourceStatus;
        return this;
    }

    /**
     * 设置目标状态
     *
     * @param targetStatus 目标状态
     * @return EventBuilder
     */
    public EventBuilder<T, S> targetStatus(S targetStatus) {
        this.targetStatus = targetStatus;
        return this;
    }

    public EventMessage<T, S> build() {
        if (this.provideMessage != null && !this.headerAccessor.isModified()) {
            return this.provideMessage;
        }
        EventMessageHeaders headersToUse = this.headerAccessor.toMessageHeaders();

        return new DefaultEventMessageImpl<>(this.payLoad, sourceStatus, targetStatus, headersToUse);
    }

    public static <T, S> EventBuilder<T, S> fromMessage(EventMessage<T, S> message) {
        return new EventBuilder<>(message);
    }

    /**
     * Create a new builder for a message with the given event.
     *
     * @param event the event payload
     */
    public static <T, S> EventBuilder<T, S> withEvent(T event) {
        return new EventBuilder<>(event, new EventHeaderAccessor());
    }
}
