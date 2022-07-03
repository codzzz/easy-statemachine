package com.codzzz.lang.easy.statemachine.executor;

@FunctionalInterface
public interface ExecuteAble<V> {

    /**
     * The actual execute
     *
     * @return 执行结果
     */
    V execute();
}
