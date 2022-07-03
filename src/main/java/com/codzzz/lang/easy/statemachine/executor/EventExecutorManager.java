package com.codzzz.lang.easy.statemachine.executor;

import com.codzzz.lang.easy.statemachine.model.event.EventResult;
import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
public class EventExecutorManager {

    private static final AtomicReference<EventExecutor<EventResult>> DEFAULT_SYN_EVENT_EXECUTOR_REF = new AtomicReference<>();

    public EventExecutor<EventResult> getDefaultSyncEventExecutor() {

        for (; ; ) {
            if (DEFAULT_SYN_EVENT_EXECUTOR_REF.get() == null) {
                EventExecutor<EventResult> syncEventExecutor = new SyncEventExecutor<>();
                DEFAULT_SYN_EVENT_EXECUTOR_REF.compareAndSet(null, syncEventExecutor);
            }
            return DEFAULT_SYN_EVENT_EXECUTOR_REF.get();
        }
    }
}
