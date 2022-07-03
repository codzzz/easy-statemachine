package com.codzzz.lang.easy.statemachine.message;

import com.codzzz.lang.easy.statemachine.constants.StateMachineSystemConstants;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ToString(of = "headers")
@EqualsAndHashCode(of = "headers")
public class EventHeaderAccessor {

    /**
     * 消息头
     */
    private final MutableEventMessageHeaders headers;
    /**
     *
     */
    private       boolean                    modified = false;

    public EventHeaderAccessor() {
        this(null);
    }

    public EventHeaderAccessor(EventMessage<?, ?> message) {
        this.headers = new MutableEventMessageHeaders(message != null ? message.getHeaders() : null);
    }

    /**
     * 消息头是否有被修改
     *
     * @return
     */
    public boolean isModified() {
        return this.modified;
    }

    /**
     * 转换为MessageHeader
     *
     * @return
     */
    public EventMessageHeaders toMessageHeaders() {
        return new EventMessageHeaders(this.headers);
    }

    /**
     * toMap
     *
     * @return
     */
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(this.headers);
    }

    /**
     * get header value
     *
     * @param headerName
     * @return
     */
    public Object getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    /**
     * set payload value
     *
     * @param payload
     */
    public void setPayload(Object payload) {
        setHeader(StateMachineSystemConstants.CONTEXT_MESSAGE_PAYLOAD, payload);
    }

    /**
     * set header value
     *
     * @param name  -
     * @param value -
     */
    public void setHeader(String name, Object value) {
        if (isReadOnly(name)) {
            throw new IllegalArgumentException("'" + name + "' header is read-only");
        }
        if (value != null) {
            // Modify header if necessary
            if (!ObjectUtils.nullSafeEquals(value, getHeader(name))) {
                this.modified = true;
                this.headers.getRawHeaders().put(name, value);
            }
        } else {
            // Remove header if available
            if (this.headers.containsKey(name)) {
                this.modified = true;
                this.headers.getRawHeaders().remove(name);
            }
        }
    }

    /**
     * Set the value for the given header name only if the header name is not
     * already associated with a value.
     */
    public void setHeaderIfAbsent(String name, Object value) {
        if (getHeader(name) == null) {
            setHeader(name, value);
        }
    }

    /**
     * Remove the value for the given header name.
     */
    public void removeHeader(String headerName) {
        if (StringUtils.hasLength(headerName) && !isReadOnly(headerName)) {
            setHeader(headerName, null);
        }
    }

    /**
     * Removes all headers provided via array of 'headerPatterns'.
     * <p>As the name suggests, array may contain simple matching patterns for header
     * names. Supported pattern styles are: "xxx*", "*xxx", "*xxx*" and "xxx*yyy".
     */
    public void removeHeaders(String... headerPatterns) {
        List<String> headersToRemove  = new ArrayList<>();
        for (String pattern : headerPatterns) {
            if (StringUtils.hasLength(pattern)) {
                if (pattern.contains("*")) {
                    headersToRemove.addAll(getMatchingHeaderNames(pattern, this.headers));
                } else {
                    headersToRemove.add(pattern);
                }
            }
        }
        for (String headerToRemove : headersToRemove) {
            removeHeader(headerToRemove);
        }
    }

    private List<String> getMatchingHeaderNames(String pattern, Map<String, Object> headers) {
        if (headers == null) {
            return Collections.emptyList();
        }
        List<String> matchingHeaderNames = new ArrayList<>();
        for (String key : headers.keySet()) {
            if (PatternMatchUtils.simpleMatch(pattern, key)) {
                matchingHeaderNames.add(key);
            }
        }
        return matchingHeaderNames;
    }

    /**
     * Copy the name-value pairs from the provided Map.
     * <p>This operation will overwrite any existing values. Use
     * {@link #copyHeadersIfAbsent(Map)} to avoid overwriting values.
     */
    public void copyHeaders(Map<String, ?> headersToCopy) {
        if (headersToCopy != null) {
            headersToCopy.forEach((key, value) -> {
                if (!isReadOnly(key)) {
                    setHeader(key, value);
                }
            });
        }
    }

    /**
     * Copy the name-value pairs from the provided Map.
     * <p>This operation will <em>not</em> overwrite any existing values.
     */
    public void copyHeadersIfAbsent(Map<String, ?> headersToCopy) {
        if (headersToCopy != null) {
            headersToCopy.forEach((key, value) -> {
                if (!isReadOnly(key)) {
                    setHeaderIfAbsent(key, value);
                }
            });
        }
    }

    protected boolean isReadOnly(String headerName) {
        return false;
    }

    /**
     * 消息头是否可变
     *
     * @return
     */
    public boolean isMutable() {
        return this.headers.isMutable();
    }

    /**
     * @param message
     * @return
     */
    public static EventHeaderAccessor getMutableAccessor(EventMessage<?, ?> message) {
        if (message.getHeaders() instanceof MutableEventMessageHeaders) {
            MutableEventMessageHeaders mutableHeaders = (MutableEventMessageHeaders) message.getHeaders();
            EventHeaderAccessor accessor = mutableHeaders.getAccessor();
            return (accessor.isMutable() ? accessor : new EventHeaderAccessor(message));
        }
        return new EventHeaderAccessor(message);
    }

    /**
     * 可变的消息头实现
     */
    private class MutableEventMessageHeaders extends EventMessageHeaders {

        private static final long serialVersionUID = -2499181912483048074L;

        private volatile boolean mutable = true;

        public MutableEventMessageHeaders(Map<String, Object> headers) {
            super(headers);
        }

        @NotNull
        @Override
        public Map<String, Object> getRawHeaders() {
            Assert.state(this.mutable, "Already immutable");
            return super.getRawHeaders();
        }

        public void setImmutable() {
            if (!this.mutable) {
                return;
            }
            this.mutable = false;
        }

        public boolean isMutable() {
            return this.mutable;
        }

        public EventHeaderAccessor getAccessor() {
            return EventHeaderAccessor.this;
        }
    }
}

