package com.example.shelfshare.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;

public class RazorpayHelper implements PaymentResultListener {
    private final Activity activity;
    private final PaymentCallback callback;
    private final String razorpayKey;

    public interface PaymentCallback {
        void onPaymentSuccess(String paymentId);
        void onPaymentError(int code, String response);
    }

    public RazorpayHelper(Activity activity, PaymentCallback callback) {
        this.activity = activity;
        this.callback = callback;
        this.razorpayKey = "YOUR_RAZORPAY_KEY"; // Replace with your actual Razorpay key
    }

    public void startPayment(String amount, String currency, String description) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(razorpayKey);

        try {
            JSONObject options = new JSONObject();
            options.put("name", "ShelfShare");
            options.put("description", description);
            options.put("currency", currency);
            options.put("amount", Integer.parseInt(amount) * 100); // Convert to paise
            options.put("prefill.email", "user@example.com");
            options.put("prefill.contact", "9876543210");

            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String paymentId) {
        callback.onPaymentSuccess(paymentId);
    }

    @Override
    public void onPaymentError(int code, String response) {
        callback.onPaymentError(code, response);
    }
} 