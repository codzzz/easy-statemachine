package com.codzzz.lang.easy.statemachine.message.impl;

import com.codzzz.lang.easy.statemachine.message.EventMessage;
import com.codzzz.lang.easy.statemachine.message.EventMessageHeaders;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Map;

@ToString
@EqualsAndHashCode
public class DefaultEventMessageImpl<T, S> implements EventMessage<T, S> {

    private static final long serialVersionUID = -226976713523934717L;

    /**
     * 触发事件
     */
    @Getter
    private final T                   event;
    /**
     * 消息头
     */
    @Getter
    private final EventMessageHeaders headers;
    /**
     * 起始状态
     */
    @Getter
    private final S                   sourceStatus;
    /**
     * 目标状态
     */
    @Getter
    private final S                   targetStatus;

    public DefaultEventMessageImpl(T eventPayload, S sourceStatus, S targetStatus, EventMessageHeaders headers) {
        Assert.notNull(eventPayload, "Payload must not be null");
        Assert.notNull(headers, "MessageHeaders must not be null");
        this.event = eventPayload;
        this.headers = headers;
        this.sourceStatus = sourceStatus;
        this.targetStatus = targetStatus;
    }

    public DefaultEventMessageImpl(T eventPayload) {
        this(eventPayload, null, null, new EventMessageHeaders(null));
    }

    public DefaultEventMessageImpl(T eventPayload, Map<String, Object> header) {
        this(eventPayload, null, null, new EventMessageHeaders(header));
    }
}
