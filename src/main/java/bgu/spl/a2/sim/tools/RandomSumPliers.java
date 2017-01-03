package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.util.Random;

public class RandomSumPliers implements Tool {
    /**returns the type of the tool
     *
     * @return type
     */
    @Override
    public String getType() {
        return "rs-pliers";
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
            ans=ans+Math.abs(useRandom(product.getFinalId()));
        }
        return ans;
    }

    /**gets number and returns the sum of (number%10000) random numbers in the range of the number
     *
     * @param id - the number
     * @return sum of (id%10000) random numbers in the range of id
     */
    private long useRandom(long id){
        Random rnd = new Random(id);
        long sum=0;
        for(int i=0;i<id%10000;i++){
            sum=sum+rnd.nextInt();
        }
        return sum;
    }
}
