package com.example.chatbot;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatbot.databinding.ActivityChatBinding;
import com.example.chatbot.model.MessageData;
import com.example.chatbot.model.RoomData;
import com.example.chatbot.retrofit.Result;
import com.example.chatbot.retrofit.RetrofitClient;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;

    private Socket mSocket;
    private RetrofitClient retrofitClient;

    private String username;
    private String roomNumber;
    private ChatAdapter adapter;

    private Gson gson = new Gson();

    private final int SELECT_IMAGE = 100;

    String str,tranContent;
    String my,you;
    int language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }//end of on creates

    void init() {
        try {
            mSocket = IO.socket("http://10.0.2.2:80");
            Log.d("SOCKET", "Connection success : " + mSocket.id());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }//end of try catch
        retrofitClient = RetrofitClient.getInstance();

        // MainActivity로부터 username과 roomNumber를 넘겨받음
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        roomNumber = intent.getStringExtra("roomNumber");
        my = intent.getStringExtra("my");
        you  = intent.getStringExtra("you");

        adapter = new ChatAdapter(getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);

        // 메시지 전송 버튼
        binding.sendBtn.setOnClickListener(v -> sendMessage());
        // 이미지 전송 버튼
        binding.imageBtn.setOnClickListener(v -> {
            Intent imageIntent = new Intent(Intent.ACTION_PICK);
            imageIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(imageIntent, SELECT_IMAGE);
        });//end of onClick

        binding.tranBtn.setOnClickListener(v -> {
            str = binding.contentEdit.getText().toString();
            Translate translate = new Translate();
            translate.execute();
            Toast.makeText(ChatActivity.this,"번역 완료",Toast.LENGTH_SHORT).show();
        } );

        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, args -> {
            mSocket.emit("enter", gson.toJson(new RoomData(username, roomNumber)));
        });
        mSocket.on("update", args -> {
            MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
            addChat(data);
        });
    }//end of init




    // 리사이클러뷰에 채팅 추가
    private void addChat(MessageData data) {

        runOnUiThread(() -> {
            if (data.getType().equals("ENTER") || data.getType().equals("LEFT")) {

                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.CENTER_MESSAGE,data.getContent()));
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);


            }else if (data.getType().equals("IMAGE")) {
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.LEFT_IMAGE,data.getContent()));
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);


            } else {
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.LEFT_MESSAGE,data.getContent()));
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);

            }//end of else if
        });//end of runOnUI

    }//end of addChat

    //send 버튼 누름
     private void sendMessage() {

        mSocket.emit("newMessage", gson.toJson(new MessageData("MESSAGE",
                username,
                roomNumber,
                binding.contentEdit.getText().toString(),
                System.currentTimeMillis())));
            Log.d("MESSAGE", new MessageData("MESSAGE",
                username,
                roomNumber,
                binding.contentEdit.getText().toString(),
                System.currentTimeMillis()).toString());

                 adapter.addItem(new ChatItem(username, binding.contentEdit.getText().toString(), toDate(System.currentTimeMillis()), ChatType.RIGHT_MESSAGE,tranContent));
                 binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                 binding.contentEdit.setText("");


         }//end of sendMess


    class Translate extends AsyncTask<String ,Void, String > {   //ASYNCTASK를 사용
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override

        protected String doInBackground(String... strings) {

            //////네이버 API

            String clientId = "TRfw0hE248GXpHfdrz5J"; //애플리케이션 클라이언트 아이디값";
            String clientSecret = "iwRSEFFCQk"; //애플리케이션 클라이언트 시크릿값";
            String getMy = my;
            String getYou = you;

            try {
                String text = URLEncoder.encode(str, "UTF-8");  /// 번역할 문장 Edittext  입력

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


    // 이미지 uri로부터 실제 파일 경로를 알아냄
    private String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();

        return result;
    }//end of get Real path

    // Node.js 서버에 이미지를 업로드
    public void uploadImage(Uri imageUri, Context context) {
        File image = new File(getRealPathFromURI(imageUri, context));
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), image);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", image.getName(), requestBody);

        retrofitClient.getApiService().uploadImage(body).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result.getResult() == 1) {
                    Log.d("PHOTO", "Upload success : " + result.getImageUri());
                    sendImage(result.getImageUri());
                } else {
                    Log.d("PHOTO", "Upload failed");
                }//end of else if
            }//end of onRespnse

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("PHOTO", "Upload failed : " + t.getMessage());
            }//end of onFailure
        });//end of retrofitClient.getApiService
    }//end of up

    private void sendImage(String imageUri) {
        mSocket.emit("newImage", gson.toJson(new MessageData("IMAGE",
                username,
                roomNumber,
                imageUri,
                System.currentTimeMillis())));
        Log.d("IMAGE", new MessageData("IMAGE",
                username,
                roomNumber,
                imageUri,
                System.currentTimeMillis()).toString());
    }//ne dof sendImg

    // System.currentTimeMillis를 몇시:몇분 am/pm 형태의 문자열로 반환
    private String toDate(long currentMiliis) {
        return new SimpleDateFormat("hh:mm a").format(new Date(currentMiliis));
    }//end of todate

    // 이미지를 갤러리에서 선택했을 때 이벤트
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri;

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            uploadImage(selectedImageUri, getApplicationContext());
            adapter.addItem(new ChatItem(username, String.valueOf(selectedImageUri), toDate(System.currentTimeMillis()), ChatType.RIGHT_IMAGE,"          "));
            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }//end of if
    }//end of onActivityResult

    // 이전 버튼을 누를 시 방을 나가고 소켓 통신 종료
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.emit("left", gson.toJson(new RoomData(username, roomNumber)));
        mSocket.disconnect();
    }//end of Destory



}//end of chatAct