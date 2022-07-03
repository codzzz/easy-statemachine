package com.codzzz.lang.easy.statemachine.message;


import com.codzzz.lang.easy.statemachine.constants.StateMachineSystemConstants;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

@Log4j2
@EqualsAndHashCode(of = "headers")
@ToString(of = {"headers"})
public class EventMessageHeaders implements Map<String, Object>, Serializable {

    private static final long serialVersionUID = -1539412527735782019L;

    /**
     * 实际存储数据map
     */
    private final Map<String, Object> headers;

    /**
     * 构造器
     *
     * @param headers 消息头map
     */
    public EventMessageHeaders(Map<String, Object> headers) {
        this.headers = (headers != null ? new HashMap<>(headers) : new HashMap<>());
    }

    /**
     * 获取值
     *
     * @param key  key
     * @param type value class
     * @param <T>  value type
     * @return value
     */
    @Nullable
    public <T> T get(String key, Class<T> type) {
        Object value = this.headers.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Incorrect type specified for header '" +
                    key + "'. Expected [" + type + "] but actual type is [" + value.getClass() + "]");
        }
        return type.cast(value);
    }

    /**
     * 获取header
     *
     * @return headers
     */
    @NotNull
    protected Map<String, Object> getRawHeaders() {
        return this.headers;
    }

    /**
     * get payload
     *
     * @return payload
     */
    public Object getPayLoad() {
        return get(StateMachineSystemConstants.CONTEXT_MESSAGE_PAYLOAD);
    }

    /**
     * 是否包含key
     *
     * @param key key
     * @return 是否包含
     */
    @Override
    public boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }

    /**
     * 是否包含具体的值
     *
     * @param value value
     * @return 是否包含
     */
    @Override
    public boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }

    /**
     * entrySet
     *
     * @return entrySet
     */
    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Collections.unmodifiableMap(this.headers).entrySet();
    }

    /**
     * getValue
     *
     * @param key key
     * @return value
     */
    @Override
    public Object get(Object key) {
        return this.headers.get(key);
    }

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    /**
     * keySet
     *
     * @return keySet
     */
    @NotNull
    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.headers.keySet());
    }

    /**
     * size of header
     *
     * @return size
     */
    @Override
    public int size() {
        return this.headers.size();
    }

    /**
     * values
     *
     * @return values collection
     */
    @NotNull
    @Override
    public Collection<Object> values() {
        return Collections.unmodifiableCollection(this.headers.values());
    }

    /**
     * put
     *
     * @param key   key
     * @param value value
     * @return not support
     */
    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    /**
     * putAll
     *
     * @param map putMap
     */
    @Override
    public void putAll(@NotNull Map<? extends String, ?> map) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    /**
     * remove key
     *
     * @param key key
     * @return not support
     */
    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    /**
     * clear
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

}