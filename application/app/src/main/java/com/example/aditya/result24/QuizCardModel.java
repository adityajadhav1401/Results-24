package com.example.aditya.result24;

import java.util.Arrays;

public class QuizCardModel {
    Integer pk;
    private String[] quizAttr = new String[5];
    private String[] quizAttrCopy = new String[5];


    public QuizCardModel(Integer pk, String[] attr) {
        this.pk = pk;
        quizAttr = Arrays.copyOf(attr, attr.length);
        quizAttrCopy = Arrays.copyOf(attr, attr.length);
    }

    public String[] getQuizAttrFinal(){
        return quizAttr;
    }
    public String[] getQuizAttr(){
        return quizAttrCopy;
    }
    public String getQuizAttr(int pos){ return quizAttrCopy[pos]; }
    public void setQuizAttr(String[] val){ quizAttr = Arrays.copyOf(val, val.length); }
    public void setQuizAttr(int pos, String val){ quizAttr[pos] = val; }
}
