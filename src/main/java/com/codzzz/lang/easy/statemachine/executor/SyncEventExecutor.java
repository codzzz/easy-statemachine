package com.codzzz.lang.easy.statemachine.executor;

public class SyncEventExecutor<T> implements EventExecutor<T> {

    @Override
    public T execute(ExecuteAble<T> task) {
        return task.execute();
    }
}
