package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinner;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResumeAndSkillsActivity extends AppCompatActivity {

    Spinner spinner;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Bitmap bitmap;
    Button btnConfirmUpload;

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
    public void onBackPressed() {
        // do nothing.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_and_skills);

        btnConfirmUpload = findViewById(R.id.btn_confirm);
        btnConfirmUpload.setVisibility(View.GONE);

        getInitialPhoto();

/***
 * -1 is no by default selection
 * 0 to length will select corresponding values
 */
        final SharedPreferences preferences = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        Map<String,?> marked = preferences.getAll();

        for (Map.Entry<String, ?> entry : marked.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }


        final List<String> list = Arrays.asList(getResources().getStringArray(R.array.sports_array));

        MultiSpinnerSearch searchSpinner = (MultiSpinnerSearch) findViewById(R.id.searchMultiSpinner);

        final List<KeyPairBoolData> listArray = new ArrayList<KeyPairBoolData>();

        KeyPairBoolData h;

        for(int i=0; i<list.size(); i++) {
            h = new KeyPairBoolData();
            h.setId(i+1);
            h.setName(list.get(i));
            if(marked.containsKey(Integer.toString(i))){
                h.setSelected(true);
            }
            else{
                h.setSelected(false);
            }
            listArray.add(h);
        }

//        for (Map.Entry<String, ?> entry : marked.entrySet()) {
//            h = new KeyPairBoolData();
//            h.setId(Long.parseLong(entry.getKey()+1));
//            h.setName(list.get(Integer.parseInt(entry.getKey()+1)));
//            h.setSelected(true);
//            listArray.add(h);
//            Log.d("mapValues", entry.getKey() + ": " + entry.getValue().toString());
//        }

        searchSpinner.setItems(listArray, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                ArrayList<String> skills = new ArrayList<String>();
                for(int i=0; i<items.size(); i++) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    if(items.get(i).isSelected()) {

                        skills.add(items.get(i).getName());
                        Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                        editor.putInt(Integer.toString(i),i);
                        editor.apply();
                    }
                    else{
                        int exist = preferences.getInt(Integer.toString(i),-1);

                        if(exist!=-1) {
                            editor.remove(Integer.toString(i));
                            editor.apply();
                        }
                    }
                }

                ParseUser user = ParseUser.getCurrentUser();
                user.put("skills",skills);

                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Toast.makeText(ResumeAndSkillsActivity.this, "Skills saved.", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ResumeAndSkillsActivity.this, "Profile pic uploaded", Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Toast.makeText(ResumeAndSkillsActivity.this, "Image upload failed, please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        searchSpinner.performClick();
    }

    private void getInitialPhoto() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.orderByDescending("createdAt");
        query.setLimit(1);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        ParseFile file = (ParseFile) objects.get(0).get("image");

                        if(file!=null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {

                                    if (e == null && data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        ImageView ivUploadedImage = findViewById(R.id.iv_uploaded_image);
                                        ivUploadedImage.setImageBitmap(bitmap);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void findCounsellors(View view){
        Intent intent = new Intent(ResumeAndSkillsActivity.this, CounsellorListActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void confirmUpload(View view){

        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
        btnConfirmUpload.setVisibility(View.GONE);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        newbitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);

        byte[] byteArray = stream.toByteArray();

        ParseFile file = new ParseFile("image.png", byteArray);

        ParseUser user = ParseUser.getCurrentUser();
        user.put("image",file);
//        ParseObject object = new ParseObject("Image");
//        object.put("image", file);
//        object.put("username", ParseUser.getCurrentUser().getUsername());

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Toast.makeText(ResumeAndSkillsActivity.this, "Profile pic uploaded", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ResumeAndSkillsActivity.this, "Image upload failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&resultCode==RESULT_OK&&data!=null){
            Uri selectedImage = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                ImageView ivUploadedImage = findViewById(R.id.iv_uploaded_image);
                ivUploadedImage.setImageBitmap(bitmap);
                btnConfirmUpload.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }

    public void uploadPhoto(View view){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else {
                getPhoto();
            }
        }
        else{
            getPhoto();
        }
    }
}
