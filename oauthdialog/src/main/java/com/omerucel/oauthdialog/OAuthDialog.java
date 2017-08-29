package com.omerucel.oauthdialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OAuthDialog {
    Context context;
    String authorizeUrl;
    String scope = "";
    String clientId;
    String clientSecret;
    String redirectUri;
    String tokenUrl;
    Dialog dialog;
    WebView webView;
    Toolbar toolbar;
    ProgressBar loading;
    EventListener eventListener;
    OkHttpClient okHttpClient;

    @SuppressLint("SetJavaScriptEnabled")
    public OAuthDialog(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient();
        dialog = new Dialog(context, android.R.style.Theme_NoTitleBar);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(false);
        loading = (ProgressBar) dialog.findViewById(R.id.loading);
        toolbar = (Toolbar) dialog.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        toolbar.getMenu().getItem(0)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        dialog.dismiss();
                        if (eventListener != null) {
                            eventListener.cancelled();
                        }
                        return false;
                    }
                });
        webView = (WebView) dialog.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 Google");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loading.setVisibility(View.VISIBLE);
                view.setVisibility(View.INVISIBLE);
                Uri uri = Uri.parse(url);
                uri.getFragment();
                if (uri.getQueryParameter("code") != null && uri.getQueryParameter("code").length() > 0) {
                    view.stopLoading();
                    handleAccessCode(uri.getQueryParameter("code"));
                } else if (uri.getQueryParameter("error") != null) {
                    view.stopLoading();
                    dialog.dismiss();
                    if (eventListener != null) {
                        eventListener.cancelled(uri.getQueryParameter("error"));
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Uri uri = Uri.parse(url);
                if (uri.getQueryParameter("code") == null) {
                    loading.setVisibility(View.INVISIBLE);
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void show() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(authorizeUrl).newBuilder();
        urlBuilder.addQueryParameter("redirect_uri", redirectUri);
        urlBuilder.addQueryParameter("response_type", "code");
        urlBuilder.addQueryParameter("client_id", clientId);
        if (scope != null && scope.length() > 0) {
            urlBuilder.addQueryParameter("scope", scope);
        }
        webView.loadUrl(urlBuilder.build().toString());
        dialog.show();
    }

    public OAuthDialog setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
        return this;
    }

    public OAuthDialog setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public OAuthDialog setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public OAuthDialog setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public OAuthDialog setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public OAuthDialog setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
        return this;
    }

    public OAuthDialog setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public OAuthDialog setTitle(String title) {
        toolbar.setTitle(title);
        return this;
    }

    public interface EventListener {
        void cancelled();
        void cancelled(String reason);
        void succeeded(AccessToken accessToken);
    }

    public void handleAccessCode(final String accessCode) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(tokenUrl).newBuilder();
        urlBuilder.addQueryParameter("code", accessCode);
        urlBuilder.addQueryParameter("redirect_uri", redirectUri);
        urlBuilder.addQueryParameter("grant_type", "authorization_code");
        urlBuilder.addQueryParameter("client_id", clientId);
        if (clientSecret != null) {
            urlBuilder.addQueryParameter("client_secret", clientSecret);
        }
        String url = urlBuilder.build().toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), "");
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                final String message = e.getMessage();
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eventListener.cancelled(message);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dialog.dismiss();
                final String body = response.body().string();
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            if (jsonObject.has("error")) {
                                eventListener.cancelled(jsonObject.getString("error"));
                            } else {
                                AccessToken accessToken = new AccessToken(jsonObject);
                                eventListener.succeeded(accessToken);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            eventListener.cancelled(e.getMessage());
                        }
                    }
                });
            }
        });
    }
}
