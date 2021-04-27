package client;

import compute.Task;

import java.io.Serializable;
import java.math.BigInteger;

public class Fibonacci implements Task<BigInteger>, Serializable {
    private int enteFibonacci;

    public Fibonacci(int nthFibonacci) {
        this.enteFibonacci = nthFibonacci;
    }

    @Override
    public BigInteger execute() {
        try {
            Integer n = enteFibonacci;
            BigInteger prepre = new BigInteger("0");
            BigInteger pre = new BigInteger("1");
            BigInteger out = new BigInteger("1");
            if (n >= 2) {
                for (int i = 3; i <= n; i++) {
                    prepre = pre;
                    pre = out;
                    out = prepre.add(pre);
                }
                return out;
            } else if (n >= 0) {
                return out;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
