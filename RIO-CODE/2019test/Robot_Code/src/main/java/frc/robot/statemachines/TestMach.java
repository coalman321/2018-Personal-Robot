package frc.robot.statemachines;

import frc.lib.statemachine.StateMachineDescriptor;
import frc.robot.actions.DelayAction;

public class TestMach extends StateMachineDescriptor{

    public TestMach(){
        addSequential(new DelayAction(11.000), 10000);
        addSequential(new DelayAction(11.000), 10000);
        addSequential(new DelayAction(10.000), 11000);
        addSequential(new DelayAction(10.000), 11000);
    }

}