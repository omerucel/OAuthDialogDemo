package com.omerucel.oauthdialogdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.omerucel.oauthdialog.AccessToken;
import com.omerucel.oauthdialog.OAuthDialog;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signInWithGoogle(View view) {
        OAuthDialog oAuthDialog = new OAuthDialog(this);
        oAuthDialog
                .setAuthorizeUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .setTokenUrl("https://www.googleapis.com/oauth2/v4/token")
                .setScope("email")
                .setRedirectUri("http://localhost")
                .setClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .setTitle("Google Sign In")
                .setEventListener(new OAuthDialog.EventListener() {
                    @Override
                    public void cancelled() {
                        cancelled("Without any reason!");
                    }

                    @Override
                    public void cancelled(String reason) {
                        showMessage("Cancelled", "Reason:" + reason, null);
                    }

                    @Override
                    public void succeeded(AccessToken accessToken) {
                        try {
                            showMessage("Succeeded", accessToken.getAccessToken(), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    public void signInWithDropbox(View view) {
        OAuthDialog oAuthDialog = new OAuthDialog(this);
        oAuthDialog
                .setAuthorizeUrl("https://www.dropbox.com/oauth2/authorize")
                .setTokenUrl("https://api.dropboxapi.com/oauth2/token")
                .setRedirectUri("http://localhost")
                .setClientId(BuildConfig.DROPBOX_API_KEY)
                .setClientSecret(BuildConfig.DROPBOX_API_SECRET)
                .setTitle("Dropbox Sign In")
                .setEventListener(new OAuthDialog.EventListener() {
                    @Override
                    public void cancelled() {
                        cancelled("Without any reason!");
                    }

                    @Override
                    public void cancelled(String reason) {
                        showMessage("Cancelled", "Reason:" + reason, null);
                    }

                    @Override
                    public void succeeded(AccessToken accessToken) {
                        try {
                            showMessage("Succeeded", accessToken.getAccessToken(), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    public void signInWithOneDrive(View view) {
        OAuthDialog oAuthDialog = new OAuthDialog(this);
        oAuthDialog
                .setAuthorizeUrl("https://login.live.com/oauth20_authorize.srf")
                .setTokenUrl("https://login.live.com/oauth20_token.srf")
                .setRedirectUri("http://localhost")
                .setScope("onedrive.appfolder")
                .setClientId(BuildConfig.ONEDRIVE_CLIENT_ID)
                .setClientSecret(BuildConfig.ONEDRIVE_CLIENT_SECRET)
                .setTitle("OneDrive Sign In")
                .setEventListener(new OAuthDialog.EventListener() {
                    @Override
                    public void cancelled() {
                        cancelled("Without any reason!");
                    }

                    @Override
                    public void cancelled(String reason) {
                        showMessage("Cancelled", "Reason:" + reason, null);
                    }

                    @Override
                    public void succeeded(AccessToken accessToken) {
                        try {
                            showMessage("Succeeded", accessToken.getAccessToken(), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    public void showMessage(String title, String message, DialogInterface.OnCancelListener onCancelListener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setOnCancelListener(onCancelListener)
                .show();
    }
}
