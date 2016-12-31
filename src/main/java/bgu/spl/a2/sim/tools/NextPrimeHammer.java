package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;

/**
 * Created by win10 on 31-Dec-16.
 */
public class NextPrimeHammer implements Tool {
    @Override
    public String getType() {
        return "np-hammer";
    }

    @Override
    public long useOn(Product p) {
        long ans=0;
        for(Product product:p.getParts()){
            BigInteger primeNum=BigInteger.valueOf(product.getFinalId());
            primeNum=primeNum.nextProbablePrime();
            ans=ans+Math.abs(primeNum.longValue());
        }
        return ans;
    }
}
