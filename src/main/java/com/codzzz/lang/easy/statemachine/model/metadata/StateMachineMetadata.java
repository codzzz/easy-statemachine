package com.codzzz.lang.easy.statemachine.model.metadata;

import com.codzzz.lang.easy.statemachine.constants.Constants;
import com.codzzz.lang.easy.statemachine.constants.StateMachineSystemConstants;
import lombok.Data;

@Data(staticConstructor = "of")
public class StateMachineMetadata {

    /**
     * 状态机id
     */
    private final String machineId;
    /**
     * 状态机名称
     */
    private final String machineName;

    public static StateMachineMetadata defaultMetadata() {
        return StateMachineMetadata.of(Constants.EMPTY, StateMachineSystemConstants.DEFAULT_NAME_STATEMACHINE);
    }
}