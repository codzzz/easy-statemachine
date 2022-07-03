package com.codzzz.lang.easy.statemachine.container;

import com.codzzz.lang.easy.statemachine.annotation.StateMachine;
import com.codzzz.lang.easy.statemachine.annotation.WithStateMachine;
import com.codzzz.lang.easy.statemachine.core.AbstractStateMachine;
import com.codzzz.lang.easy.statemachine.core.impl.BaseCommonStateMachine;
import com.codzzz.lang.easy.statemachine.core.impl.BaseEnumConfigStateMachine;
import com.codzzz.lang.easy.statemachine.exception.StateMachineException;
import com.codzzz.lang.easy.statemachine.model.metadata.StateMachineMetadata;
import com.codzzz.lang.easy.statemachine.transaction.EventTransition;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Component
public class StateMachineContainer implements ApplicationRunListener {

    /**
     * 状态机初始化并发控制
     */
    private final ConcurrentMap<StateMachineMetadata, Boolean> stateMachineInitializedMap = new ConcurrentHashMap<>();

    /**
     * WithStateMachine注解构建状态机元数据
     *
     * @param withStateMachine 状态机绑定注解
     * @return 状态机元数据
     */
    private StateMachineMetadata buildStateMachineMetadata(WithStateMachine withStateMachine) {
        return StateMachineMetadata.of(withStateMachine.id(), withStateMachine.name());
    }

    /**
     * 状态机->对应事件转换分组
     *
     * @param innerContext 容器上下文
     * @return map，key->状态机元数据 value—>状态机对应的事件转换列表
     */
    @SuppressWarnings("unchecked")
    private Map<StateMachineMetadata, List<EventTransition<Object, Object>>> sortTransitionByStateMachine(ApplicationContext innerContext) {
        Collection<Object> transitionBeans = innerContext.getBeansWithAnnotation(WithStateMachine.class).values();

        if (!CollectionUtils.isEmpty(transitionBeans)) {
            return transitionBeans.stream()
                    .filter(bean -> bean instanceof EventTransition)
                    .map(bean -> (EventTransition<Object, Object>) bean)
                    .collect(Collectors.groupingBy(bean -> {
                        WithStateMachine withStateMachine = AopUtils.getTargetClass(bean).getAnnotation(WithStateMachine.class);
                        return buildStateMachineMetadata(withStateMachine);
                    }));
        }
        return Collections.emptyMap();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void started(ConfigurableApplicationContext applicationContext) {
        //遍历所有@StateMachine注解实例
        Collection<Object> stateMachines = applicationContext.getBeansWithAnnotation(StateMachine.class).values();

        //状态机元数据set，用于校验是否有重复的状态机
        Set<StateMachineMetadata> machineMetadata = new HashSet<>();
        //状态机Map，key->状态机元数据，value—>状态机实例
        Map<StateMachineMetadata, AbstractStateMachine> stateMachineMap = new HashMap<>(16);

        stateMachines.forEach(bean -> {
            boolean validStateMachine = bean instanceof BaseEnumConfigStateMachine || bean instanceof BaseCommonStateMachine;
            if (!validStateMachine) {
                throw new StateMachineException(
                        "Illegal StateMachine,StateMachine must inherit from BaseEnumConfigStateMachine or BaseCommonStateMachine");
            }

            AbstractStateMachine stateMachine = (AbstractStateMachine) bean;
            StateMachineMetadata metadata = stateMachine.getStateMachineMetaData();

            if (machineMetadata.contains(metadata)) {
                throw new StateMachineException(String.format("Repeated state machine,id:%s,name:%s", metadata.getMachineId(),
                        metadata.getMachineName()));
            }
            machineMetadata.add(metadata);
            //写入Map
            stateMachineMap.put(metadata, (AbstractStateMachine) bean);
        });

        Map<StateMachineMetadata, List<EventTransition<Object, Object>>> statemachineTransitionMap
                = sortTransitionByStateMachine(applicationContext);

        stateMachineMap.forEach((metadata, stateMachine) -> {
            if (stateMachineInitializedMap.getOrDefault(metadata, false)) {
                return;
            }
            //找到状态机对应的Transition列表
            List<EventTransition<Object, Object>> eventTransitions = statemachineTransitionMap.get(metadata);
            if (eventTransitions != null && !eventTransitions.isEmpty()) {
                //注册状态转换
                stateMachine.registryTransitions(eventTransitions);
            }
            stateMachineInitializedMap.put(metadata, true);
        });
    }
}
