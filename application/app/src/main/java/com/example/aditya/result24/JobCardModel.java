package com.example.aditya.result24;

import java.util.Arrays;

public class JobCardModel {
    Integer pk;
    private String[] jobAttr = new String[7];
    private String[] jobAttrCopy = new String[7];


    public JobCardModel(Integer pk, String[] attr) {
        this.pk = pk;
        jobAttr = Arrays.copyOf(attr, attr.length);
        jobAttrCopy = Arrays.copyOf(attr, attr.length);
    }

    public String[] getJobAttrFinal(){
        return jobAttr;
    }
    public String[] getJobAttr(){
        return jobAttrCopy;
    }
    public String getJobAttr(int pos){ return jobAttrCopy[pos]; }
    public void setJobAttr(String[] val){ jobAttr = Arrays.copyOf(val, val.length); }
    public void setJobAttr(int pos, String val){ jobAttr[pos] = val; }
}
