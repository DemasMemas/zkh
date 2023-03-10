package com.practice.zkh.dbo;

public class Offer {
    private String name;
    private String count;
    private String summary;

    public Offer(String name, String count, String summary) {
        this.name = name;
        this.count = count;
        this.summary = summary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
