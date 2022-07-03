package com.codzzz.lang.easy.statemachine.model.metadata;

@Data(staticConstructor = "of")
public class TransitionMetadata<E, S> {

    /**
     * 触发转换的事件
     */
    private final E event;
    /**
     * 初始状态
     */
    private final S sourceStatus;
    /**
     * 目标状态
     */
    private final S targetStatus;
}
