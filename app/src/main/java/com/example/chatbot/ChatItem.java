package com.example.chatbot;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ChatItem {
    private String name;
    private String content;
    private String sendTime;
    private int viewType;    // 0일 시 왼쪽(상대가 보낸 메세지), 1일 시 중앙(~가 입장하셨습니다), 2일 시 오른쪽(내가 보낸 메세지)자
    private String tranContent;




    public ChatItem(String name, String content, String sendTime, int viewType,String tranContent) {
        this.name = name;
        this.content = content;
        this.sendTime = sendTime;
        this.viewType = viewType;
        Translate translate = new Translate();
        translate.execute();
        this.tranContent = tranContent;

    }//생성


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getSendTime() { return sendTime; }

    public void setSendTime(String sendTime) { this.sendTime = sendTime; }

    public int getViewType() { return viewType; }

    public void setViewType(int viewType) { this.viewType = viewType; }

    public String getTranContent() {
        Translate translate = new Translate();
        translate.execute();
        return tranContent;
    }

    public void setTranContent(String tranContent) {
        this.tranContent = tranContent;
    }

    //파파고
    class Translate extends AsyncTask<String ,Void, String > {   //ASYNCTASK를 사용
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override

        protected String doInBackground(String... strings) {

            //////네이버 API

            String clientId = "TRfw0hE248GXpHfdrz5J"; //애플리케이션 클라이언트 아이디값";
            String clientSecret = "iwRSEFFCQk"; //애플리케이션 클라이언트 시크릿값";

            try {
                String text = URLEncoder.encode(content, "UTF-8");  /// 번역할 문장 Edittext  입력

                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=ko&target=zh-CN&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                //        textView.setText(response.toString());
                tranContent = response.toString();

                tranContent = tranContent.split("\"")[27];   //스플릿으로 번역된 결과값만 가져오기
                return tranContent;


            } catch (Exception e) {
                System.out.println(e);
            }//end of try catch
            return null;
        }//end of dobackground
    }//end of class

    class Translate_you extends AsyncTask<String ,Void, String > {   //ASYNCTASK를 사용
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override

        protected String doInBackground(String... strings) {

            //////네이버 API

            String clientId = "TRfw0hE248GXpHfdrz5J"; //애플리케이션 클라이언트 아이디값";
            String clientSecret = "iwRSEFFCQk"; //애플리케이션 클라이언트 시크릿값";

            try {
                String text = URLEncoder.encode(content, "UTF-8");  /// 번역할 문장 Edittext  입력

                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=ko&target=zh-CN&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                //        textView.setText(response.toString());
                tranContent = response.toString();

                tranContent = tranContent.split("\"")[27];   //스플릿으로 번역된 결과값만 가져오기
                return tranContent;


            } catch (Exception e) {
                System.out.println(e);
            }//end of try catch
            return null;
        }//end of dobackground
    }//end of class


}//end of class
