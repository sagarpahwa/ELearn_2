package com.ithub.mda.elearn;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
public class JoinELearn extends AppCompatActivity implements
        View.OnClickListener,
        ELearnLogin.OnFragmentInteractionListener,
        ELearnSignUp.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{
    //For local database
    private SQLiteDatabase SQLdb;
    private ContentValues cv;
    //for facebook login
    private LoginButton elearnFacebookLoginButton;
    private CallbackManager callbackManager;
    //for google login
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "MainActivity";
    private static final int PROFILE_PIC_SIZE = 100;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private String googlePicPath;
    private Bitmap googlePicBitmap;
    private Button elearnGoogleLoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLocalDB();

            initializeElearnFacebookSDK();
            initializeLayout();
            if( android.os.Build.VERSION.SDK_INT > 9 ) {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy( policy );
                }catch (Exception e){}
            }
            initializeELearnFacebookData();
            initializeELearnGoogleData();
        if(fetchFromLocalDB()) {//start yourelearn activity
            startYourELearn();
        }
    }
    protected void onStart() {
        super.onStart();
        /*try {
            mGoogleApiClient.connect();
        }catch (Exception e){}*/
    }
    protected void onStop() {
        super.onStop();
        /*try {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }catch (Exception e){}*/
    }
    private void initializeELearnGoogleData() {
        elearnGoogleLoginButton = (Button)findViewById(R.id.elearn_google_login_button);
        elearnGoogleLoginButton.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }
    private void startYourELearn() {
        Intent intent = new Intent(this,YourElearn.class);
        startActivity(intent);
    }
    private void initializeLayout() {
        setContentView(R.layout.activity_join_elearn);
        getSupportFragmentManager().beginTransaction().add(R.id.UserDetails, new ELearnLogin()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo: backpress code
    }

    private void initializeElearnFacebookSDK() {
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(getApplication(), "com.ithub.mda.elearn");
    }
    private void initializeLocalDB() {
        SQLdb=new ELearnLocalDB(this).getWritableDatabase();
        cv=new ContentValues();
    }
    private boolean fetchFromLocalDB() {
        SQLiteDatabase SQLreaddb = new ELearnLocalDB(this).getReadableDatabase();
        Cursor cr = SQLreaddb.query("elearnuserdetails",new String[]{"name","email","profilepic","status"},"",null,"","","name");
        if(cr.moveToNext())
            return true;
        else {
            if(mGoogleApiClient.isConnected()){
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status arg0) {
                                Log.e(TAG, "User access revoked!");
                                mGoogleApiClient.connect();
                            }
                        });
            }
            return false;
        }
    }
    private void initializeELearnFacebookData() {
        elearnFacebookLoginButton = (LoginButton)findViewById(R.id.elearn_facebook_login_button);
        elearnFacebookLoginButton.setVisibility(View.VISIBLE);
        elearnFacebookLoginButton.setReadPermissions(Arrays.asList("public_profile,email,user_birthday"));
        callbackManager = CallbackManager.Factory.create();
        elearnFacebookLoginButton.setOnClickListener(this);
        elearnFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getELearnUserFacebookData(loginResult);
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });
    }
    private void getELearnUserFacebookData(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            handleFacebookProfile(object);
                        } catch (JSONException e) {
                            Log.e("Error: ",""+e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString("fields","id,name,email,picture.type(normal)");
        request.setParameters(bundle);
        request.executeAsync();
    }
    private void handleFacebookProfile(JSONObject object) throws Exception {
        String name = object.getString("name");
        String email = object.getString("email");
        String picPath = saveToInternalStorage(getFacebookProfilePicture(object));
        saveToLocalDB(name,email,picPath,"facebook",1);
        Log.e("Success:  ","Facebook Result Json Object: "+object.toString());
        Toast.makeText(this,name+"  "+email,Toast.LENGTH_SHORT).show();
    }
    private void saveToLocalDB(String name, String email, String profilepic, String account, int status) {
        cv.put("name", name);
        cv.put("email", email);
        cv.put("profilepic", profilepic);
        cv.put("account",account);
        cv.put("status",status);
        long l=SQLdb.insert("elearnuserdetails", "", cv);
        if(l>0) {
            startYourELearn();
        }
    }
    public Bitmap getFacebookProfilePicture(JSONObject data/*String userID*/) throws IOException, JSONException {
        String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
        Bitmap bitmap = BitmapFactory.decodeStream(new URL(profilePicUrl).openConnection().getInputStream());
       return bitmap;
    }
    private String saveToInternalStorage(Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,"profile.jpg");
        Log.e("directory: ",directory.getAbsolutePath()+" "+mypath.getName());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode,Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }
    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        getProfileInformation();
    }
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String name = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Log.e(TAG, "Name: " + name + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                new LoadProfileImage().execute(personPhotoUrl);
                try {
                    googlePicPath = saveToInternalStorage(googlePicBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveToLocalDB(name,email,googlePicPath,"googleplus",1);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.elearn_google_login_button:
                signInWithGplus();
                break;
        }
    }
    private void signInWithGplus() {
        if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

        public LoadProfileImage() {}

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            googlePicBitmap = result;
        }
    }
    /*
    * overrided methods of login and signup elearn
    * */
    @Override
    public void createAccount() {
        elearnFacebookLoginButton.setVisibility(View.GONE);
        elearnGoogleLoginButton.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.UserDetails,new ELearnSignUp()).addToBackStack(null).commit();
    }
    @Override
    public void onLogin() {
        startYourELearn();
    }
    @Override
    public void needHelp() {
        /*
        * code for forget passwordar other help.
        *
        * */
        Toast.makeText(this,"no help available",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onSignUp() {
        startYourELearn();
    }
    @Override
    public void haveAccount() {
        elearnFacebookLoginButton.setVisibility(View.VISIBLE);
        elearnGoogleLoginButton.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.UserDetails,new ELearnLogin()).addToBackStack(null).commit();
    }
}
