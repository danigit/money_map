package com.example.moneymap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public final static String TAG = "money_map";
    public final static String applicationDirectory = "MoneyMap";
    public final static String userImageName = "user_image";
    public final static FirebaseDatabase database = FirebaseDatabase.getInstance("https://moneymap-70e0a-default-rtdb.europe-west1.firebasedatabase.app");
    public final static DatabaseReference databaseReference = database.getReference();


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
