package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CounsellorListActivity extends AppCompatActivity {

    ListView listCounsellors;

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            startActivity(new Intent(this,MainActivity.class));
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsellor_list);

        listCounsellors = findViewById(R.id.list_counsellors);
        listCounsellors.setAdapter(new CustomAdapter(this));
    }

    public void updateSkills(View view){
        Intent intent = new Intent(this, ResumeAndSkillsActivity.class);
        startActivity(intent);
        this.finish();
    }
}

class SingleCounsellor{
    String name;
    Object skills;
    Object photo;

    SingleCounsellor(String name, Object skills, Object photo){
        this.name = name;
        this.skills = skills;
        this.photo = photo;
    }
}

class CustomAdapter extends BaseAdapter{

    ArrayList<SingleCounsellor> list;
    Context context;
    CustomAdapter(Context c){
        context = c;
        list = new ArrayList<SingleCounsellor>();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("type","Counsellor");
        query.setLimit(10);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(final List<ParseUser> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){

                        final ArrayList<SingleCounsellor> updatedList = new ArrayList<SingleCounsellor>();
                        Log.d("Fetched ", objects.size()+ " Counsellors");

                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
                        query.setLimit(1);

                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> myObjects, ParseException e) {
                                ArrayList<String> mySkills = (ArrayList<String>) myObjects.get(0).get("skills");

                                for(final ParseUser object: objects){

                                    ArrayList<String> counsellorSkills = (ArrayList<String>) object.get("skills");

                                    if(skillsMatchingAlgo(mySkills,counsellorSkills)) {

                                        ParseFile file = (ParseFile) object.get("image");
                                        if (file != null) {
                                            file.getDataInBackground(new GetDataCallback() {
                                                @Override
                                                public void done(byte[] data, ParseException e) {

                                                    if (e == null && data != null) {
                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                        updatedList.add(new SingleCounsellor(object.getUsername(), object.get("skills"), bitmap));
                                                    }
                                                    updateContactList(updatedList);
                                                }
                                            });
                                        } else {
                                            updatedList.add(new SingleCounsellor(object.getUsername(), object.get("skills"), null));
                                            updateContactList(updatedList);
                                        }
                                    }
                                }

                            }
                        });
                    }
                }
            }
        });

    }

    private boolean skillsMatchingAlgo(ArrayList<String> list1, ArrayList<String> list2) {
        int count = 0;
        int required = 2;

        for(String s1:list1){
            for(String s2:list2){
                if(s1.equals(s2)){
                    count++;
                }
            }
        }

        if(count>=required){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public int getCount() {

        Log.d("ListSize ", list.size()+"");
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.counsellor_list_row,viewGroup,false);
        TextView tvName = row.findViewById(R.id.tv_name);
        TextView tvSkills = row.findViewById(R.id.tv_skills);
        ImageView ivProfile = row.findViewById(R.id.iv_profile);

        SingleCounsellor temp = list.get(i);
        tvName.setText(temp.name);
        tvSkills.setText(temp.skills+"");

        if(temp.photo!=null) {
            ivProfile.setImageBitmap((Bitmap) temp.photo);
        }

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("username", list.get(i).name);
                Toast.makeText(context, "" + list.get(i).name, Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });
        return row;
    }

    private void updateContactList(ArrayList<SingleCounsellor> updatedList) {
        list.clear();
        list.addAll(updatedList);
        Log.d("FetchedListSize", String.valueOf(updatedList.size()));
        notifyDataSetChanged();
    }
}
