package com.armut.armuthv.components;

/**
 * Created by oguzemreozcan on 30/06/16.
 */
public interface QuestionView {

    void setOrder(int order);
    int getOrder();
    String getSelectedValue();
    String getAnswerToSend();
    void setRequired(boolean answerRequired);
    boolean getRequired();
    boolean isAnswered();
    int getControlId();
    void setControlId(int controlId);
}
