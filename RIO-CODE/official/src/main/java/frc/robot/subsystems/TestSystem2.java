package frc.robot.subsystems;

import java.util.List;

public class TestSystem2 extends Subsystem{

    private static final TestSystem2 instance = new TestSystem2();

    public static TestSystem2 getInstance() {
        return instance;
    }

    private Test2IO periodicIO = new Test2IO();

    private TestSystem2(){

    }

    @Override
    public void readPeriodicInputs() {

    }

    @Override
    public void writePeriodicOutputs() {

    }

    @Override
    public void outputTelemetry() {

    }

    @Override
    public void reset() {

    }

    public void toggleVar2(){
        if(periodicIO.var2) periodicIO.var2 = false;
        else periodicIO.var2 = true;
    }

    @Override
    public PeriodicIO getLogger() {
        return periodicIO;
    }

    public class Test2IO extends PeriodicIO{
        public int var1 = 10;
        public boolean var2 = true;
    }


}
