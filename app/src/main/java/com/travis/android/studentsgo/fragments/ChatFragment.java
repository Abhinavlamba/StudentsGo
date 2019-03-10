package com.travis.android.studentsgo.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.travis.android.studentsgo.Activity.CurrentClass;
import com.travis.android.studentsgo.Activity.MainActivity;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.adapter.ChatAdapter;
import com.travis.android.studentsgo.model.Chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment
{
    EditText search;
    List<Chat> chatList,displayArray,displayArray2;
    String name,body,uid,phone;
    ChatAdapter adapter;
    RecyclerView recyclerView;
    EditText editText;
    ImageButton imageButton;
    FirebaseAuth auth;
    FirebaseUser user;
    JsonObject jsonObject;
    Button searchbutton;
    Activity activity;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://quiet-citadel-80241.herokuapp.com");
        } catch (URISyntaxException e) {}
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener(){

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void call(final Object... args) {

            if(getActivity() != null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("hell", "messagereceived");
                        JSONObject data = (JSONObject) args[0];
                        try {
                            name = data.getString("name");
                            uid = data.getString("uid");
                            phone = data.getString("phone");
                            body = data.getString("body");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Chat chat = new Chat(name, phone, uid, body);
                        chatList.add(chat);
                        recyclerView.smoothScrollToPosition(chatList.size());
                        adapter.notifyDataSetChanged();
                    }
                });
        }};
    public ChatFragment()
    {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public ChatFragment (Activity activity)
    {
        this.activity = activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        displayArray = new ArrayList<>();
        mSocket.connect();
        mSocket.on("newMessage", onNewMessage);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, getActivity());
        recyclerView = view.findViewById(R.id.recyclerview);
        editText = view.findViewById(R.id.edittext);
        imageButton = view.findViewById(R.id.imagebut);
        search = view.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Abstract Method of TextWatcher Interface.
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Abstract Method of TextWatcher Interface.
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    displayArray2 = new ArrayList<>();
                    String y = search.getText().toString();
                    for (int i = 0; i<chatList.size(); i ++ )
                    {
                        Chat chat = chatList.get(i);
                        String name = chat.getName();
                        if (name.contains(y))
                            displayArray2.add(chat);
                    }
                    ChatAdapter chatAdapter = new ChatAdapter(displayArray2,getActivity());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(chatAdapter);
                    adapter.notifyDataSetChanged();
                }
                catch (Exception ex)
                {
                    System.out.println("error");
                }
            }
        });
        searchbutton = view.findViewById(R.id.searchbutton);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
            }
        });
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        getData();
        Set<Chat> set = new HashSet<>(chatList);
        chatList.clear();
        chatList.addAll(set);
        adapter.notifyDataSetChanged();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    postData("uid","Name","phone",editText.getText().toString());
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("name","Abhinav");
                    jsonBody.put("phone","8384099318");
                    jsonBody.put("uid","abcd");
                    jsonBody.put("body",editText.getText().toString());
                    jsonBody.put("branch","Csedd");
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                editText.setText("");
                mSocket.emit("newMessage", jsonBody);
                postData(jsonBody);
            }
        });
        return view;
    }
    public void postData(JSONObject jsonBody) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL = "https://quiet-citadel-80241.herokuapp.com/dextroxd/chats";
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("name",name);
//            jsonBody.put("phone","8684889090");
//            jsonBody.put("uid",uid);
//            jsonBody.put("body",body);
//            jsonBody.put("branch","Csedd");
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
    }

    public void getData() {

        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                                displayArray.add(new Chat(name , phone , uid , body));
                                adapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(chatList.size());
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
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }

}
