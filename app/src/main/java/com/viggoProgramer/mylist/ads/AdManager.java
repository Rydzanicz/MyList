package com.viggoProgramer.mylist.ads;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdManager {

    private static InterstitialAd interstitialAd;
    private static boolean isAdLoading = false;

    private AdManager() {
    }

    /**
     * Initializes and loads an interstitial ad.
     *
     * @param context Application context
     */
    public static void loadInterstitialAd(Context context) {
        if (isAdLoading || interstitialAd != null) {
            return;
        }

        isAdLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, "ca-app-pub-5419495578981092/5489470756", adRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd ad) {
                                    interstitialAd = ad;
                                    isAdLoading = false;
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    interstitialAd = null;
                                    isAdLoading = false;
                                }
                            });
    }

    /**
     * Shows the interstitial ad if it is loaded.
     *
     * @param context Application context
     * @param onAdClosed Callback to execute after the ad is closed
     */
    public static void showInterstitialAd(Context context, Runnable onAdClosed) {
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
                    loadInterstitialAd(context); // Preload the next ad
                    if (onAdClosed != null) {
                        onAdClosed.run();
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    interstitialAd = null; // Ad is shown, reset the ad object
                }
            });

            interstitialAd.show((android.app.Activity) context);
        } else {
            // If the ad is not ready, proceed without showing it
            if (onAdClosed != null) {
                onAdClosed.run();
            }
        }
    }
}
