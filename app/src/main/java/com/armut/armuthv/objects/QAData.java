package com.armut.armuthv.objects;

/**
 * Created by oguzemreozcan on 27/06/16.
 */
public class QAData {

    private final int page;
    private final int controlId;
    private final BasicNameValuePair pair;
    private final String answerToSend;

    public QAData(int page, int controlId, String answerToSend, BasicNameValuePair pair) {
        this.page = page;
        this.controlId = controlId;
        this.answerToSend = answerToSend;
        this.pair = pair;
    }

    public int getPage() {
        return page;
    }

    public String getAnswerToSend(){
        return answerToSend;
    }

    public BasicNameValuePair getPair() {
        return pair;
    }

    public int getControlId() {
        return controlId;
    }

}
