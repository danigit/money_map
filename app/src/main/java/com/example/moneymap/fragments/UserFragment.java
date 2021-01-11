package com.example.moneymap.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymap.R;
import com.example.moneymap.Utils;


public class UserFragment extends Fragment {

    public static final int CAMERA_PICTURE = 0;

    private ImageView userImage;
    private ImageView modifyUserInformation;
    private ImageView saveUserInformation;
    private EditText profileNameInput;
    private EditText profileSurnameInput;
    private EditText profilePhoneInput;
    private TextView profileNameText;
    private TextView profileSurnameText;
    private TextView profilePhoneText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userImage = (ImageView) view.findViewById(R.id.profile_image);
        profileNameInput = (EditText) view.findViewById(R.id.user_name_input_field);
        profileSurnameInput = (EditText) view.findViewById(R.id.user_surname_input_field);
        profilePhoneInput = (EditText) view.findViewById(R.id.user_phone_input_field);
        profileNameText = (TextView) view.findViewById(R.id.profile_name_text_view);
        profileSurnameText = (TextView) view.findViewById(R.id.profile_surname_text_view);
        profilePhoneText = (TextView) view.findViewById(R.id.profile_phone_text_view);
        modifyUserInformation = (ImageView) view.findViewById(R.id.modify_user_data_icon);
        saveUserInformation = (ImageView) view.findViewById(R.id.save_user_data_icon);

        Button changeProfileImage = (Button) view.findViewById(R.id.take_profile_image_button);

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
                    } else {
                        Toast.makeText(getContext(), "Could not take the picture!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else{
                    Toast.makeText(getContext(), "Could not take the picture!", Toast.LENGTH_LONG)
                            .show();
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
                    }, 1);
            }
        }
    };

    public View.OnClickListener handleModifyUserInformation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            profileNameText.setVisibility(View.INVISIBLE);
            profileSurnameText.setVisibility(View.INVISIBLE);
            profilePhoneText.setVisibility(View.INVISIBLE);
            modifyUserInformation.setVisibility(View.INVISIBLE);

            profileNameInput.setVisibility(View.VISIBLE);
            profileSurnameInput.setVisibility(View.VISIBLE);
            profilePhoneInput.setVisibility(View.VISIBLE);
            saveUserInformation.setVisibility(View.VISIBLE);
        }
    };

    public View.OnClickListener handleSaveUserInformation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            profileNameInput.setVisibility(View.INVISIBLE);
            profileSurnameInput.setVisibility(View.INVISIBLE);
            profilePhoneInput.setVisibility(View.INVISIBLE);
            saveUserInformation.setVisibility(View.INVISIBLE);

            profileNameText.setVisibility(View.VISIBLE);
            profileSurnameText.setVisibility(View.VISIBLE);
            profilePhoneText.setVisibility(View.VISIBLE);
            modifyUserInformation.setVisibility(View.VISIBLE);
        }
    };
}