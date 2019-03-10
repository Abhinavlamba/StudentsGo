package com.travis.android.studentsgo.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.assistant.v1.Assistant;
import com.ibm.watson.developer_cloud.assistant.v1.model.InputData;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.adapter.ChatAdapter;
import com.travis.android.studentsgo.adapter.ChatAdapter1;
import com.travis.android.studentsgo.model.Message;

import java.util.ArrayList;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class BotFragment extends Fragment {
    Activity activity;
    com.ibm.watson.developer_cloud.conversation.v1.model.Context context = null;
    ProgressBar asd;
    private RecyclerView recyclerView;
    private ChatAdapter1 mAdapter;
    private ArrayList<Message> messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private boolean initialRequest;
    public BotFragment(){}
    @SuppressLint("ValidFragment")
    public BotFragment(Activity activity) {
    this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_bot, container, false);
        inputMessage =  view.findViewById(R.id.message);
        btnSend =  view.findViewById(R.id.btn_send);
        asd = view.findViewById(R.id.load1);
        final RelativeLayout as = view.findViewById(R.id.chat);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter1(messageArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;
        sendMessage();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                asd.setVisibility(GONE);
                as.setVisibility(View.VISIBLE);
            }
        }, 2000);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    sendMessage();
                }
            }
        });



        return view;}
    private boolean checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(activity, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void sendMessage() {
        final String inputmessage = this.inputMessage.getText().toString().trim();
        if (!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
        } else {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
        }

        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    IamOptions iamOptions = new IamOptions.Builder().apiKey("1OsSHKCGmTRHhpL4F5baRhkWV37izOEgJ4g-VLVu_u_o").build();
                    Assistant assistant = new Assistant("2019-03-09",iamOptions);
                    assistant.setUsernameAndPassword("apikey","1OsSHKCGmTRHhpL4F5baRhkWV37izOEgJ4g-VLVu_u_o\"");
                    assistant.setEndPoint("https://gateway-lon.watsonplatform.net/assistant/api");

                    String workspaceId = "14df49e0-30b9-4a33-88e0-128b2c5e153d";

                    InputData input = new InputData.Builder(inputmessage).build();

                    MessageOptions options = new MessageOptions.Builder(workspaceId)
                            .input(input)
                            .build();

                    MessageResponse response = assistant.message(options).execute();
                    Message outMessage = new Message();
                    if (response != null) {
                        if (response.getOutput() != null && response.getOutput().containsKey("text")) {
                            ArrayList responseList = (ArrayList) response.getOutput().get("text");
                            if (null != responseList && responseList.size() > 0) {
                                outMessage.setMessage((String) responseList.get(0));
                                outMessage.setId("2");
                            }
                            messageArrayList.add(outMessage);
                        }

                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {
                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
