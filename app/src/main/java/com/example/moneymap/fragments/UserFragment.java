package com.example.moneymap.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymap.R;
import com.example.moneymap.Utils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class UserFragment extends Fragment {

    private static final int CAMERA_PICTURE = 0;
    private String applicationPath;

    private Context applicationContext;
    private ImageView userImage;
    private ImageView modifyUserInformation;
    private ImageView saveUserInformation;
    private EditText profileSavingsGoalInput;
    private EditText profileFixedCostsInput;
    private EditText profileMaxExpensesInput;
    private TextView profileSavingsGoalText;
    private TextView profileFixedCostsText;
    private TextView profileMaxExpensesText;

    private String[] userInfoFields;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applicationContext = getContext();
        applicationPath = applicationContext.getApplicationInfo().dataDir + "/app_" + Utils.applicationDirectory + "/";

        userImage = (ImageView) view.findViewById(R.id.profile_image);
        profileSavingsGoalInput = (EditText) view.findViewById(R.id.user_savings_goal_input_field);
        profileFixedCostsInput = (EditText) view.findViewById(R.id.user_fixed_costs_input_field);
        profileMaxExpensesInput = (EditText) view.findViewById(R.id.user_max_expense_input_field);
        profileSavingsGoalText = (TextView) view.findViewById(R.id.profile_savings_goal_text_view);
        profileFixedCostsText = (TextView) view.findViewById(R.id.profile_fixed_expenses_text_view);
        profileMaxExpensesText = (TextView) view.findViewById(R.id.profile_max_expenses_text_view);
        modifyUserInformation = (ImageView) view.findViewById(R.id.modify_user_data_icon);
        saveUserInformation = (ImageView) view.findViewById(R.id.save_user_data_icon);

        if(!loadUserData()){
            Utils.showToast(applicationContext, "Unable to load user data", Toast.LENGTH_SHORT);
        }

        ImageView changeProfileImage = (ImageView) view.findViewById(R.id.take_profile_image_button);

        if (getUserImage() != null) {
            userImage.setImageBitmap(getUserImage());
        } else{
            Utils.showToast(applicationContext, "Unable to find the user image", Toast.LENGTH_SHORT);
        }

        changeProfileImage.setOnClickListener(handleTakePhoto);
        modifyUserInformation.setOnClickListener(handleModifyUserInformation);
        saveUserInformation.setOnClickListener(handleSaveUserInformation);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);

        if (requestCode == CAMERA_PICTURE){
            if (resultCode == Activity.RESULT_OK){
                final Bundle cameraImage = result.getExtras();
                if (cameraImage != null) {
                    final Bitmap imageBitmap = (Bitmap) cameraImage.get("data");

                    if (imageBitmap != null) {
                        Bitmap newBitmap = Utils.scaleImage(imageBitmap, 512, 512);
                        userImage.setImageBitmap(newBitmap);

                        boolean imageSaved = saveUserImage(newBitmap);

                        if (imageSaved){
                            Utils.showToast(applicationContext, "Image saved to the device", Toast.LENGTH_SHORT);
                        } else {
                            Utils.showToast(applicationContext, "Could not save the image", Toast.LENGTH_SHORT);
                        }
                    } else {
                        Utils.showToast(applicationContext, "Could not take the picture", Toast.LENGTH_SHORT);
                    }
                } else{
                    Utils.showToast(applicationContext, "Could not take the picture!", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    public View.OnClickListener handleTakePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                startActivityForResult(cameraIntent, CAMERA_PICTURE);
            } catch (SecurityException e) {
                requestPermissions(new String[]{
                        Manifest.permission.CAMERA
                    }, 1
                );
            }
        }
    };

    public View.OnClickListener handleModifyUserInformation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            profileSavingsGoalText.setVisibility(View.INVISIBLE);
            profileFixedCostsText.setVisibility(View.INVISIBLE);
            profileMaxExpensesText.setVisibility(View.INVISIBLE);
            modifyUserInformation.setVisibility(View.INVISIBLE);

            Map<String, String> userData = getUserData();
            if (userData != null){
                profileSavingsGoalInput.setText(userData.get("savings"));
                profileFixedCostsInput.setText(userData.get("fixed"));
                profileMaxExpensesInput.setText(userData.get("maximum"));
            }

            profileSavingsGoalInput.setVisibility(View.VISIBLE);
            profileFixedCostsInput.setVisibility(View.VISIBLE);
            profileMaxExpensesInput.setVisibility(View.VISIBLE);
            saveUserInformation.setVisibility(View.VISIBLE);
        }
    };

    public View.OnClickListener handleSaveUserInformation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            profileSavingsGoalInput.setVisibility(View.INVISIBLE);
            profileFixedCostsInput.setVisibility(View.INVISIBLE);
            profileMaxExpensesInput.setVisibility(View.INVISIBLE);
            saveUserInformation.setVisibility(View.INVISIBLE);

            String [] userData = {
                profileSavingsGoalInput.getText().toString(),
                profileFixedCostsInput.getText().toString(),
                profileMaxExpensesInput.getText().toString()
            };

            userInfoFields = new String[]{"savings", "fixed", "maximum"};

            if (!saveDataToJson(userData, userInfoFields)){
                Utils.showToast(applicationContext, "Unable to save data permanently", Toast.LENGTH_SHORT);
            }

            loadUserData();

            profileSavingsGoalText.setVisibility(View.VISIBLE);
            profileFixedCostsText.setVisibility(View.VISIBLE);
            profileMaxExpensesText.setVisibility(View.VISIBLE);
            modifyUserInformation.setVisibility(View.VISIBLE);

        }
    };

    public boolean saveUserImage(Bitmap image){
        File fileDirectory = applicationContext.getDir(Utils.applicationDirectory, Context.MODE_PRIVATE);
        File imageFile = new File(fileDirectory, Utils.userImageName + ".jpeg");

        if(imageFile.exists()) {
            if(!imageFile.delete())
                return false;
        }

        FileOutputStream imageStream = null;
        try {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                imageStream = new FileOutputStream(imageFile);
                if (image != null) {
                    image.compress(Bitmap.CompressFormat.JPEG, 100, imageStream);
                    imageStream.flush();
                    return true;
                }
            } else {
                requestPermissions(new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1
                );
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (imageStream != null)
                    imageStream.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    public Bitmap getUserImage(){
        Bitmap image = null;

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            image = BitmapFactory.decodeFile(applicationPath + Utils.userImageName + ".jpeg");
        } else {
            requestPermissions(new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE},
            1
            );
        }
        return image;
    }

    public boolean saveDataToJson(String[] userData, String[] fields){
        Map<String, String> jsonData = new HashMap<>();

        for (int i = 0; i < userData.length; i++){
            jsonData.put(fields[i], userData[i]);
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonData);

        FileOutputStream outputStream;
        try {
            outputStream = applicationContext.openFileOutput( Utils.jsonUserDataFile + ".json", Context.MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> getUserData(){
        try {
            FileInputStream fileInputStream = applicationContext.openFileInput(Utils.jsonUserDataFile + ".json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            String jsonString = stringBuilder.toString();

            Gson gson = new Gson();
            return gson.fromJson(jsonString, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean loadUserData(){
        Map<String, String> userData = getUserData();
        if (userData != null){
            profileSavingsGoalText.setText(userData.get("savings"));
            profileFixedCostsText.setText(userData.get("fixed"));
            profileMaxExpensesText.setText(userData.get("maximum"));
            return true;
        }
        return false;
    }
}