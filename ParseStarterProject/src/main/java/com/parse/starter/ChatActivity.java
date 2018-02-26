package com.parse.starter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String activeCounsellor;
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    ListView lvMessages;

    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new Handler();

    public static final String PREFS_NAME = "PrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Runnable mRefreshMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                refreshMessages();
                myHandler.postDelayed(this, POLL_INTERVAL);
            }
        };

        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);

        Intent intent = getIntent();
        activeCounsellor = intent.getStringExtra("username");
        setTitle("Chat with "+ activeCounsellor);
        Log.d("ChatActivityUser", activeCounsellor);

        lvMessages = findViewById(R.id.lv_messages);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,messages);
        lvMessages.setAdapter(arrayAdapter);

//        Parse.initialize(this);

//        ParseLiveQueryClient parseLiveQueryClient = null;
//        try {
//            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(""));
//            //"https://parseapi.back4app.com/"
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
////        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Message");
//
//        SubscriptionHandling<ParseObject> subscriptionHandling = null;
//        if (parseLiveQueryClient != null) {
//            subscriptionHandling = parseLiveQueryClient.subscribe(query);
//        }
//
//        subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
//
//            @Override
//            public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
//                // HANDLING all events
//                Log.d("EventOccured", " true");
//                Toast.makeText(ChatActivity.this, "Event occured", Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    private void refreshMessages() {
        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");

        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient", activeCounsellor);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

        query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeCounsellor);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e==null){
                    if(objects.size()>0){

                        messages.clear();

                        for(ParseObject message : objects){

                            String messageContent = message.getString("message");
                            if(!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())){
                                messageContent = "> " + messageContent;
                            }

                            messages.add(messageContent);
                        }

                        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
                        int oldSize = preferences.getInt("messageSize",0);
                        int currSize = messages.size();

                        arrayAdapter.notifyDataSetChanged();

                        if(oldSize!=currSize) {
                            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putInt("messageSize",currSize);
                            editor.apply();
                            lvMessages.smoothScrollToPosition(messages.size() - 1);
                        }
                    }
                }
            }
        });
    }

    public void sendMessage(View view){

        final EditText etMessage = findViewById(R.id.et_message);
        ParseObject message = new ParseObject("Message");
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient",activeCounsellor);

        final String messageContent = etMessage.getText().toString();
        message.put("message",messageContent);
        etMessage.setText("");

        messages.add(messageContent+"");
        arrayAdapter.notifyDataSetChanged();

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Log.d("ChatSaved", messageContent+"");
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
