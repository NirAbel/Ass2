package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;

/**
 * Created by win10 on 31-Dec-16.
 */
public class NextPrimeHammer implements Tool {
    /**returns the type of the tool
     *
     * @return type
     */
    @Override
    public String getType() {
        return "np-hammer";
    }

    /**gets product and uses the tool on the product
     *
     * @param p - Product to use tool on
     * @return sum of product after the use on
     */
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
