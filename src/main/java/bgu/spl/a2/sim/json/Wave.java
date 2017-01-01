package bgu.spl.a2.sim.json;

import java.util.List;

public class Wave {

    private List<Order> orders = null;

    public Wave(List<Order> orders) {
        this.orders = orders;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}