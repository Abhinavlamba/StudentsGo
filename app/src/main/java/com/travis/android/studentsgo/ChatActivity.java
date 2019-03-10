package com.travis.android.studentsgo;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.travis.android.studentsgo.adapter.ChatAdapter;
import com.travis.android.studentsgo.model.Chat;
import com.travis.android.studentsgo.model.Wall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    List<Chat>chatList;
    String name,body,uid,phone;
    ChatAdapter adapter;
    RecyclerView recyclerView;
    EditText editText;
    ImageButton imageButton;
    FirebaseAuth auth;
    FirebaseUser user;
    JsonObject jsonObject;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://quiet-citadel-80241.herokuapp.com");
        } catch (URISyntaxException e) {}
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener(){

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("hell","messagereceived");

                    JSONObject data = (JSONObject) args[0];
                    try {
                        name = data.getString("name");
                        uid = data.getString("uid");
                        phone = data.getString("phone");
                        body = data.getString("body");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Chat chat = new Chat(name,phone,uid,body);
                    chatList.add(chat);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSocket.connect();
        mSocket.on("newMessage", onNewMessage);

//        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList,this);
        recyclerView = findViewById(R.id.recyclerview);
        editText = findViewById(R.id.edittext);
        imageButton = findViewById(R.id.imagebut);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(manager);
//        getData();
        adapter.notifyDataSetChanged();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    postData("uid","Name","phone",editText.getText().toString());
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("name","yoyo");
                    jsonBody.put("phone","8684889090");
                    jsonBody.put("uid","abcd");
                    jsonBody.put("body",editText.getText().toString());
                    jsonBody.put("branch","Csedd");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                editText.setText("");
                mSocket.emit("newMessage", jsonBody);
//                chatList.clear();
//                getData();

            }
        });



    }
    public void postData(String uid,String name,String phone,String body) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://quiet-citadel-80241.herokuapp.com/dextroxd/chats";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name",name);
            jsonBody.put("phone","8684889090");
            jsonBody.put("uid",uid);
            jsonBody.put("body",body);
            jsonBody.put("branch","Csedd");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void getData() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest("https://quiet-citadel-80241.herokuapp.com/dextroxd/chats",
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for(int i=0;i<jsonArray.length();i++){
                            try {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                name = jsonObject.getString("name");
                                uid = jsonObject.getString("uid");
                                phone = jsonObject.getString("phone");
                                body = jsonObject.getString("body");
                                chatList.add(new Chat(name,phone,uid,body));

                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
        requestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
}
