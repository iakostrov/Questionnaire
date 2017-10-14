package ru.ikostrov.questionnaire;

/**
 * Created by User on 17.09.2017.
 */

import java.util.List;

public class Question {

    private String text;
    private List<String> answers;
    private int right;
    private int num = 0;

    public Question(int num, String text, List<String> answers, int right) {
        this.num = num;
        this.text = text;
        this.answers = answers;
        this.right = right;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getNum() {
        return num;
    }

    void setNum(int num) {
        this.num = num;
    }

    public boolean isRight(String answer) {
        System.out.println("answer:" + answer + " right:" + this.answers.get(this.right) + "isRight:" + this.answers.get(this.right).equals(answer));
        return this.answers.get(this.right).equals(answer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (right != question.right) return false;
        if (num != question.num) return false;
        if (text != null ? !text.equals(question.text) : question.text != null) return false;
        return answers != null ? answers.equals(question.answers) : question.answers == null;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", answers=" + answers +
                ", right=" + right +
                ", num=" + num +
                '}';
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (answers != null ? answers.hashCode() : 0);
        result = 31 * result + right;
        result = 31 * result + num;
        return result;
    }
}