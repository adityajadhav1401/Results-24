package com.example.aditya.result24;

public class QuestionModel {
    private int id;
    private String question;
    private String optA;
    private String optB;
    private String optC;
    private String optD;
    private String answer;
    public QuestionModel()
    {
        id=0;
        question="";
        optA="";
        optB="";
        optC="";
        optD="";
        answer="";
    }
    public QuestionModel(int id, String question, String optA, String optB, String optC, String optD,
                         String answer) {
        this.id     = id;
        this.question=question;
        this.optA   = optA;
        this.optB   = optB;
        this.optC   = optC;
        this.optD   = optD;
        this.answer = answer;
    }
    public int getId() {
        return id;
    }
    public String getQuestion() {
        return question;
    }
    public String getOptA() {
        return optA;
    }
    public String getOptB() {
        return optB;
    }
    public String getOptC() {
        return optC;
    }
    public String getOptD() {
        return optD;
    }
    public String getAnswer() {
        return answer;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public void setOptA(String optA) {
        this.optA = optA;
    }
    public void setOptB(String optB) {
        this.optB = optB;
    }
    public void setOptC(String optC) {
        this.optC = optC;
    }
    public void setOptD(String optD) {
        this.optD = optD;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

}