package com.example.moneymap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

    public static void showErrorToast(Context context, String message, int length){
        Toast errorToast = Toast.makeText(context, message, length);
        TextView v = (TextView) errorToast.getView().findViewById(android.R.id.message);
        ((View)v.getParent()).setBackgroundColor(context.getResources().getColor(R.color.red_color));
        v.setBackgroundColor(context.getResources().getColor(R.color.red_color));
        v.setTextColor(context.getResources().getColor(R.color.white));
        errorToast.show();
    }

    public static void showWarnToast(Context context, String message, int length){
        Toast errorToast = Toast.makeText(context, message, length);
        TextView v = (TextView) errorToast.getView().findViewById(android.R.id.message);
        ((View)v.getParent()).setBackgroundColor(context.getResources().getColor(R.color.yellow_color));
        v.setBackgroundColor(context.getResources().getColor(R.color.yellow_color));
        v.setTextColor(context.getResources().getColor(R.color.ap_black));
        errorToast.show();
    }

    public static boolean isConnectionActive(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void closeKeyboard(View v){
        Context context = v.getContext();
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * method that handles the click on the add and close button in the account page
     * @return onClickListener
     */
    public static View.OnClickListener openCloseAccountPanel(final SlidingUpPanelLayout slidingPanel, final TextView openButton, final String section){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle the panel up and down
                if (slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    Utils.closeKeyboard(v);
                    Utils.closePanel(slidingPanel, openButton);
                } else {
                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    openButton.setBackgroundResource(R.drawable.rounded_corners_red);
                    openButton.setText(R.string.close);

                    // cleaning the panels
                    switch (section){
                        case "categories":
                            ((TextView) slidingPanel.findViewById(R.id.category_name)).setText("");
                            ((TextView) slidingPanel.findViewById(R.id.category_icon_name_text_view)).setText("");
                            ((ImageView) slidingPanel.findViewById(R.id.category_icon_image)).setImageDrawable(null);
                            break;
                        case "accounts":
                            ((TextView) slidingPanel.findViewById(R.id.account_title_input)).setText("");
                            ((TextView) slidingPanel.findViewById(R.id.account_description_input)).setText("");
                            ((TextView) slidingPanel.findViewById(R.id.account_amount_input)).setText("");
                            break;
                    }
                }
            }
        };
    }

    public static void closePanel(SlidingUpPanelLayout panel, TextView panelButton){
        panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        panelButton.setBackgroundResource(R.drawable.rounded_corners);
        panelButton.setText(R.string.add);
    }
}
