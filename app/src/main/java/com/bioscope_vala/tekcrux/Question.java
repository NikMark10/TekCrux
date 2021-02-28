package com.bioscope_vala.tekcrux;

import java.util.List;

public class Question {
    private String profilePicUri, usernameCurious, pointsCurious;
    private String dateOfCreation, questionType, questionText;
    private String answer, answerer, answererPoints;

    public Question(String profilePicUri, String usernameCurious, String pointsCurious, String dateOfCreation, String questionType, String questionText, String answer, String answerer, String answererPoints) {
        this.profilePicUri = profilePicUri;
        this.usernameCurious = usernameCurious;
        this.pointsCurious = pointsCurious;
        this.dateOfCreation = dateOfCreation;
        this.questionType = questionType;
        this.questionText = questionText;
        this.answer = answer;
        this.answerer = answerer;
        this.answererPoints = answererPoints;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public String getUsernameCurious() {
        return usernameCurious;
    }

    public String getPointsCurious() {
        return pointsCurious;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAnswerer() {
        return answerer;
    }

    public String getAnswererPoints() {
        return answererPoints;
    }
}
