package frc.lib.statespace;

import Jama.Matrix;

public class StateSpaceController{

    Matrix a, b, c, d;
    double[] x, y, r;

    public StateSpaceController(Matrix a, Matrix b, Matrix c, Matrix d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }


    public double[] update( Matrix x, Matrix y){


        return new double[] {};
    }

}