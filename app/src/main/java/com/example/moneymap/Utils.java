package com.example.moneymap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.method.MetaKeyKeyListener;
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


    /**
     * Method that controls if the password passed as parameter is a valid password
     * @param password - string containing the password
     * @return - true if the password is valid, false otherwise
     */
    public static boolean isPasswordValid(String password){
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

    /**
     * Method that scales the image passed as parameter to the dimensions also passed as parameters
     * @param image - the image to be scaled
     * @param imageWidth - the new image width
     * @param imageHeight - the new image height
     * @return the scaled image
     */
    public static Bitmap scaleImage(Bitmap image, int imageWidth, int imageHeight) {
        Matrix matrix = new Matrix();

        int width = image.getWidth();
        int height = image.getHeight();

        RectF firsRect = new RectF(0, 0, width, height);
        RectF secondRect = new RectF(0, 0, imageWidth, imageHeight);

        matrix .setRectToRect(firsRect, secondRect, Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
    }

    /**
     * Method that shows a toast message
     * @param context - the context in which the toast has to be shown
     * @param message - the message to be shown
     * @param length - the message duration
     */
    public static void showToast(Context context, String message, int length){
        Toast.makeText(context, message, length)
            .show();
    }

    /**
     * Method that shows an error toast message
     * @param context - the context in which the message has to be shown
     * @param message - the message of the message
     * @param length - the duration of the message
     */
    public static void showErrorToast(Context context, String message, int length){
        Toast errorToast = Toast.makeText(context, message, length);
        TextView v = (TextView) errorToast.getView().findViewById(android.R.id.message);
        ((View)v.getParent()).setBackgroundColor(context.getResources().getColor(R.color.red_color));
        v.setBackgroundColor(context.getResources().getColor(R.color.red_color));
        v.setTextColor(context.getResources().getColor(R.color.white));
        errorToast.show();
    }

    /**
     * Method that shows an warning toast message
     * @param context - the context in which the message has to be shown
     * @param message - the message of the message
     * @param length - the duration of the message
     */
    public static void showWarnToast(Context context, String message, int length){
        Toast errorToast = Toast.makeText(context, message, length);
        TextView v = (TextView) errorToast.getView().findViewById(android.R.id.message);
        ((View)v.getParent()).setBackgroundColor(context.getResources().getColor(R.color.yellow_color));
        v.setBackgroundColor(context.getResources().getColor(R.color.yellow_color));
        v.setTextColor(context.getResources().getColor(R.color.ap_black));
        errorToast.show();
    }

    /**
     * Method that controls if the device has an active connection
     * @param context
     * @return true if the connection is active, false otherwise
     */
    public static boolean isConnectionActive(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Method that close the virtual keyboard if opened
     * @param v
     */
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
    public static View.OnClickListener openCloseAccountPanel(final SlidingUpPanelLayout slidingPanel, final ImageView openButton, final String section){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle the panel up and down
                if (slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    Utils.closeKeyboard(v);
                    Utils.closePanel(slidingPanel, openButton);
                } else {
                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    openButton.setImageResource(R.drawable.close_panel_icon);

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

    /**
     * Method that close the SlidingUpPanel passed as parameter
     * @param panel - the panel to be closed
     * @param panelButton - the button to be updated
     */
    public static void closePanel(SlidingUpPanelLayout panel, ImageView panelButton){
        if (panel != null)
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        if (panelButton != null) {
            panelButton.setImageResource(R.drawable.open_panel_icon);
        }
    }
}
