package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;


public class GcdScrewDriver implements Tool {
    @Override
    public String getType() {
        return "gs-driver";
    }

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
