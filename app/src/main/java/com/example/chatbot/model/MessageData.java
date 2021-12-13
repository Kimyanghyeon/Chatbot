package com.example.chatbot.model;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MessageData {
    //node js 로 메세지 내용 및 정보 전송
    private String type;
    private String from;
    private String to;
    private String content;
    private long sendTime;

    public MessageData(String type, String from, String to, String content, long sendTime) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
        this.sendTime = sendTime;
    }//end of messageDate

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getFrom() { return from; }

    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }

    public void setTo(String to) { this.to = to; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public long getSendTime() { return sendTime; }

    public void setSendTime(long sendTime) { this.sendTime = sendTime; }


}//end of class
