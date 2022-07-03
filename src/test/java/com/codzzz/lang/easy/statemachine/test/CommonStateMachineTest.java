package com.codzzz.lang.easy.statemachine.test;

import com.codzzz.lang.easy.statemachine.context.StateContext;
import com.codzzz.lang.easy.statemachine.core.impl.BaseEnumConfigStateMachine;
import com.codzzz.lang.easy.statemachine.message.EventBuilder;
import com.codzzz.lang.easy.statemachine.message.EventMessage;
import com.codzzz.lang.easy.statemachine.transaction.EventTransition;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CommonStateMachineTest {

    @Test
    public void testCommonStateMachine() {
        CommonStateMachine commonStateMachine = new CommonStateMachine();

        //状态转换列表
        List<EventTransition<Event, State>> transitionList = new ArrayList<>();
        transitionList.add(new AToBTransition());
        transitionList.add(new BToCTransition());
        transitionList.add(new CToDTransition());

        //状态转换注册至状态机
        commonStateMachine.registryTransitions(transitionList);

        //发送A->B转换事件
        commonStateMachine.sendEvent(Event.EVENT_A_TO_B, new Payload().setId("A->B"));

        //发送B->C转换事件 构造消息
        EventMessage<Event, State> eventMessage = EventBuilder
                //事件
                .<Event, State>withEvent(Event.EVENT_B_TO_C)
                //初始状态
                .sourceStatus(State.B)
                //模板状态
                .targetStatus(State.C)
                //设置消息头
                .setHeader("header1", "headerValue1")
                //设置消息头(如果key不存在)
                .setHeaderIfAbsent("header2", "headerValue2")
                //设置消息体
                .setPayload(new Payload().setId("A->B")).build();
        commonStateMachine.sendEvent(eventMessage);

        //发送C->D转换事件
        commonStateMachine.sendEvent(Event.EVENT_C_TO_D, new Payload().setId("C->D"));
    }

    public static class CommonStateMachine extends BaseEnumConfigStateMachine<Event, State> {

        @Override
        protected String configId() {
            return "CommonStateMachineId";
        }

        @Override
        protected String configName() {
            return "CommonStateMachineName";
        }
    }

    /**
     * 状态机状态
     */
    public enum State {
        A,
        B,
        C,
        D
    }

    /**
     * 状态机事件
     */
    public enum Event {

        /**
         * 状态A->B触发事件
         */
        EVENT_A_TO_B,
        /**
         * 状态B->C触发事件
         */
        EVENT_B_TO_C,
        /**
         * 状态C->D触发事件
         */
        EVENT_C_TO_D
    }

    /**
     * 状态机事件消息主体
     */
    @Data
    @Accessors(chain = true)
    public static class Payload {
        /**
         * Id
         */
        private String id;
    }

    /**
     * A->B状态转换
     */
    public static class AToBTransition implements EventTransition<Event, State> {

        @Override
        public boolean evaluateTransition(StateContext<Event, State> stateContext) {
            return true;
        }

        @Override
        public void preTransition(StateContext<Event, State> stateContext) {
            System.out.println("[PreTransition]A->B,context:" + stateContext);
        }

        @Override
        public void onTransition(StateContext<Event, State> stateContext) {
            System.out.println("[OnTransition]A->B,context:" + stateContext);
            Assert.assertNotNull(stateContext.getPayload());
        }

        @Override
        public void postTransition(StateContext<Event, State> stateContext) {
            Sy stem.out.println("[PostTransition]A->B,context:" + stateContext);
        }

        /**
         * 配置触发事件
         *
         * @return
         */
        @Override
        public Event getEvent() {
            return Event.EVENT_A_TO_B;
        }

        /**
         * 配置起始状态
         *
         * @return
         */
        @Override
        public State getSourceStatus() {
            return State.A;
        }

        /**
         * 配置目标状态
         *
         * @return
         */
        @Override
        public State getTargetStatus() {
            return State.B;
        }
    }

    /**
     * B->C状态转换
     */
    public static class BToCTransition implements EventTransition<Event, State> {

        @Override
        public boolean evaluateTransition(StateContext<Event, State> stateContext) {
            return true;
        }

        @Override
        public void preTransition(StateContext<Event, State> stateContext) {
            System.out.println("[PreTransition] B->C");
        }

        @Override
        public void onTransition(StateContext<Event, State> stateContext) {
            System.out.println("[OnTransition] B->C");
            Assert.assertNotNull(stateContext.getPayload());
            Assert.assertEquals(stateContext.getMessageHeader("header1", String.class), "headerValue1");
            Assert.assertEquals(stateContext.getMessageHeader("header2", String.class), "headerValue2");
        }

        @Override
        public void postTransition(StateContext<Event, State> stateContext) {
            System.out.println("[PostTransition] B->C");
        }

        @Override
        public Event getEvent() {
            return Event.EVENT_B_TO_C;
        }

        @Override
        public State getSourceStatus() {
            return State.B;
        }

        @Override
        public State getTargetStatus() {
            return State.C;
        }
    }

    /**
     * C->D状态转换
     */
    public static class CToDTransition implements EventTransition<Event, State> {

        @Override
        public boolean evaluateTransition(StateContext<Event, State> stateContext) {
            return true;
        }

        @Override
        public void preTransition(StateContext<Event, State> stateContext) {
            System.out.println("[PreTransition]C->D,context:" + stateContext);
        }

        @Override
        public void onTransition(StateContext<Event, State> stateContext) {
            System.out.println("[OnTransition]C->D,context:" + stateContext);
        }

        @Override
        public void postTransition(StateContext<Event, State> stateContext) {
            System.out.println("[PostTransition]C->D,context:" + stateContext);
        }

        @Override
        public Event getEvent() {
            return Event.EVENT_C_TO_D;
        }

        @Override
        public State getSourceStatus() {
            return State.C;
        }

        @Override
        public State getTargetStatus() {
            return State.D;
        }
    }

}
