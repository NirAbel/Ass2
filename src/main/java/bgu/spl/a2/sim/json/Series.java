package bgu.spl.a2.sim.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Series {

    @SerializedName("threads")
    @Expose
    private Integer threads;
    @SerializedName("tools")
    @Expose
    private List<ToolJson> tools = null;
    @SerializedName("plans")
    @Expose
    private List<Plan> plans = null;
    @SerializedName("waves")
    @Expose
    private List<List<Order>> waves = null;

    /**
     *
     * @return the threads
     */
    public Integer getThreads() {
        return threads;
    }

    /**
     *
     * @param threads
     */
    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    /**
     *
     * @return the waves
     */
    public List<List<Order>> getWaves() {
        return waves;
    }

    /**
     *
     * @return the tools
     */
    public List<ToolJson> getTools() {
        return tools;
    }

    /**
     *
     * @param tools
     */
    public void setTools(List<ToolJson> tools) {
        this.tools = tools;
    }

    /**
     *
     * @param plans
     */
    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    /**
     *
     * @return plans
     */
    public List<Plan> getPlans() {
        return plans;
    }




}