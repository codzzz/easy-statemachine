package com.codzzz.lang.easy.statemachine.executor;

@FunctionalInterface
public interface EventExecutor<T> {

    /**
     * 具体执行器
     *
     * @param task 可执行任务
     * @return 任务返回
     */
    T execute(ExecuteAble<T> task);
}
