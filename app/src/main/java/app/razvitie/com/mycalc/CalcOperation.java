package app.razvitie.com.mycalc;

public class CalcOperation {
    public static double addition(double a, double b){
        return a + b;
    }

    public static double subtraction(double a, double b){
        return a - b;
    }

    public static double multiplication(double a, double b){
        return a * b;
    }

    public static double division(double a, double b){
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return a / b;
    }
}