package com.viggoProgramer.mylist.ads;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdManager {

    private static InterstitialAd interstitialAd;
    private static boolean isAdLoading = false;
    private static final String PREFERENCES_NAME = "app_preferences";
    private static final String USER_CODE_KEY = "user_code";
    private static final String VALID_CODE = "VIGGO999";

    private AdManager() {
    }

    /**
     * Initializes and loads an interstitial ad if the user has not entered a valid code.
     *
     * @param context Application context
     */
    public static void loadInterstitialAd(final Context context) {
        if (isAdLoading || interstitialAd != null || isCodeValid(context)) {
            return;
        }

        isAdLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, "ca-app-pub-7538847532019776/8984032634", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(final @NonNull InterstitialAd ad) {
                interstitialAd = ad;
                isAdLoading    = false;
            }

            @Override
            public void onAdFailedToLoad(final @NonNull LoadAdError loadAdError) {
                interstitialAd = null;
                isAdLoading    = false;
            }
        });
    }

    /**
     * Shows the interstitial ad if it is loaded and the user has not entered a valid code.
     *
     * @param context    Application context
     * @param onAdClosed Callback to execute after the ad is closed
     */
    public static void showInterstitialAd(final Context context,
                                          final Runnable onAdClosed) {
        if (isCodeValid(context)) {
            if (onAdClosed != null) {
                onAdClosed.run();
            }
            return;
        }

        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    interstitialAd = null;
                    loadInterstitialAd(context);
                    if (onAdClosed != null) {
                        onAdClosed.run();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                    interstitialAd = null;
                    loadInterstitialAd(context);
                    if (onAdClosed != null) {
                        onAdClosed.run();
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    interstitialAd = null;
                }
            });

            interstitialAd.show((android.app.Activity) context);
        } else {
            if (onAdClosed != null) {
                onAdClosed.run();
            }
        }
    }

    /**
     * Checks if the user has entered a valid code.
     *
     * @param context Application context
     * @return true if the valid code is saved in SharedPreferences, false otherwise
     */
    private static boolean isCodeValid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String savedCode = preferences.getString(USER_CODE_KEY, "");
        return VALID_CODE.equals(savedCode);
    }
}
