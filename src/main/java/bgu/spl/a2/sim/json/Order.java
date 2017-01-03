package bgu.spl.a2.sim.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("product")
    @Expose
    private String product;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("startId")
    @Expose
    private Long startId;

    /**
     * return the Product
     */
    public String getProduct() {
        return product;
    }

    /**
     * set the Product
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * return the number if qty
     */
    public Integer getQty() {
        return qty;
    }

    /**
     * set the qty
     */
    public void setQty(Integer qty) {
        this.qty = qty;
    }

    /**
     * this func return the startId
     */
    public Long getStartId() {
        return startId;
    }
    /**
     * this func set the startId
     */
    public void setStartId(Long startId) {
        this.startId = startId;
    }
}