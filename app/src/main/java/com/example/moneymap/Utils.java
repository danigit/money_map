package com.example.moneymap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public final static String TAG = "money_map";
    public final static String applicationDirectory = "MoneyMap";
    public final static String userImageName = "user_image";

    public static boolean validatePassword(String password){
        Pattern pattern;
        Matcher matcher;
        // defining the password constraints
        // the password has to be at least 6 characters long with letters (upper and lower case)
        // and numbers.
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public static Bitmap scaleImage(Bitmap image, int imageWidth, int imageHeight) {
        Matrix matrix = new Matrix();

        int width = image.getWidth();
        int height = image.getHeight();

        RectF firsRect = new RectF(0, 0, width, height);
        RectF secondRect = new RectF(0, 0, imageWidth, imageHeight);

        matrix .setRectToRect(firsRect, secondRect, Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
    }

    public static void showToast(Context context, String message, int length){
        Toast.makeText(context, message, length)
            .show();
    }
}
