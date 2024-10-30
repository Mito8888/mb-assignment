package com.example.assignment1;

import androidx.lifecycle.ViewModel;

public class PizzaPrice extends ViewModel {

    private String total_needed;
    private String total_cost;

    public PizzaPrice(){
        this.total_needed = "0";
        this.total_cost = "RM 0.00";
    }

    public PizzaPrice(String total_needed, String total_cost) {
        this.total_needed = total_needed;
        this.total_cost = total_cost;
    }

    public String getTotal_needed() {
        return total_needed;
    }

    public void setTotal_needed(String total_needed) {
        this.total_needed = total_needed;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(String total_cost) {
        this.total_cost = total_cost;
    }

    @Override
    public String toString() {
        return "PizzaPrice{" +
                "total_needed='" + total_needed + '\'' +
                ", total_cost='" + total_cost + '\'' +
                '}';
    }
}
