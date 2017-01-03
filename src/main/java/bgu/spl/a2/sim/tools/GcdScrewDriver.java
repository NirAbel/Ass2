package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;


public class GcdScrewDriver implements Tool {
    /**returns the type of the tool
     *
     * @return type
     */
    @Override
    public String getType() {
        return "gs-driver";
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
            BigInteger id=BigInteger.valueOf(product.getFinalId());
            BigInteger reverseId=BigInteger.valueOf(reverse(product.getFinalId()));
            BigInteger gcd=id.gcd(reverseId);
            ans=ans+Math.abs(gcd.longValue());
        }
        return ans;
    }

    /**gets a number and reverse it
     *
     * @param n - the number to reverse
     * @return reversed number
     */
    public long reverse(long n){
        long ans=0;
        long tmp;
        while (n!=0){
            tmp=n%10;
            ans=ans*10;
            ans=ans+tmp;
            n=n/10;
        }
        return ans;
    }
}
