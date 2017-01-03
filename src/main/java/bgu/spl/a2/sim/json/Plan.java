package bgu.spl.a2.sim.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plan {
    @SerializedName("product")
    @Expose
    private String product;
    @SerializedName("tools")
    @Expose
    private String[] tools = null;
    @SerializedName("parts")
    @Expose
    private String[] parts = null;

    /**
     * @return the Product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @set the Product
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @set the tools
     */
    public void setTools(String[] tools) {
        this.tools = tools;
    }

    /**
     *
     * @return the tools
     */
    public String[] getTools() {
        return tools;
    }

    /**
     * @set the parts
     */
    public void setParts(String[] parts) {
        this.parts = parts;
    }

    /**
     * return the parts
     */
    public String[] getParts() {
        return parts;
    }



}

