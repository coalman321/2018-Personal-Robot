package frc.robot.subsystems;

import java.util.List;

public class TestSystem1 extends Subsystem {

    private static final TestSystem1 instance = new TestSystem1();

    public static TestSystem1 getInstance() {
        return instance;
    }

    private Test1IO periodicIO = new Test1IO();

    private TestSystem1(){

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
        periodicIO = new Test1IO();
    }

    public void incrementVar1(){
        periodicIO.var1 += 1;
    }

    @Override
    public PeriodicIO getLogger() {
        return periodicIO;
    }

    public class Test1IO extends PeriodicIO{
        public double var1 = 10.9;
        public String var2 = "aaaaa";
        public float var3 = 101;
    }
}
