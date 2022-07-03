package com.codzzz.lang.easy.statemachine.annotation;


import com.codzzz.lang.easy.statemachine.constants.StateMachineSystemConstants;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface WithStateMachine {
    /**
     * 关联状态机id
     *
     * @return 状态机id
     */
    String id();

    /**
     * 关联状态机名称 默认stateMachine
     *
     * @return 状态机名称
     */
    String name() default StateMachineSystemConstants.DEFAULT_NAME_STATEMACHINE;
}