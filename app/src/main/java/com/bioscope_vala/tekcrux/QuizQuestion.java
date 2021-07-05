package com.bioscope_vala.tekcrux;

public class QuizQuestion {
    private String question, optA, optB, optC, optD, ra;

        public QuizQuestion(String question, String optA, String optB, String optC, String optD, String ra) {
        this.question = question;
        this.optA = optA;
        this.optB = optB;
        this.optC = optC;
        this.optD = optD;
        this.ra = ra;
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
    public String getRa() {
            return ra;
    }

}
