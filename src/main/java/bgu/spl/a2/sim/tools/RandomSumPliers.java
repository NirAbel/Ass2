package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.util.Random;

public class RandomSumPliers implements Tool {
    @Override
    public String getType() {
        return "rs-pliers";
    }

    @Override
    public long useOn(Product p) {
        long ans=0;
        for(Product product:p.getParts()){
            ans=ans+Math.abs(useRandom(product.getFinalId()));
        }
        return ans;
    }

    private long useRandom(long id){
        Random rnd = new Random(id);
        long sum=0;
        for(int i=0;i<id%10000;i++){
            sum=sum+rnd.nextInt();
        }
        return sum;
    }
}
