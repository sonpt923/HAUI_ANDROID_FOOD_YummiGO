package com.example.btl_android.utils;

import android.util.Log;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PayOSHelper {
    private static final String CLIENT_ID = "2cfb9e5e-5c45-40cd-b4d5-01fd6004ad91";
    private static final String API_KEY = "f12e17b4-1ee6-4280-a044-51e97178b6c5";
    private static final String CHECKSUM_KEY = "3dae4252bb8463b177bf145fc7c5c3a5e960d72e7483352287cba46770439c5f";
    private static final String BASE_URL = "https://api-merchant.payos.vn/v2/payment-requests";

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public void createPaymentLink(JSONObject paymentRequest, Callback callback) {
        try {
            String signature = generateSignature(paymentRequest);
            paymentRequest.put("signature", signature);

            RequestBody body = RequestBody.create(paymentRequest.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .addHeader("x-client-id", CLIENT_ID)
                    .addHeader("x-api-key", API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.e("PayOSHelper", "Error creating payment link", e);
        }
    }

    public void checkPayment(long orderCode, Callback callback) {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + orderCode)
                    .addHeader("x-client-id", CLIENT_ID)
                    .addHeader("x-api-key", API_KEY)
                    .get()
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.e("PayOSHelper", "Error checking payment", e);
        }
    }

    private String generateSignature(JSONObject jsonObject) {
        try {
            List<String> keys = new ArrayList<>();
            Iterator<String> it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                if (!key.equals("signature")) {
                    keys.add(key);
                }
            }
            Collections.sort(keys);

            StringBuilder transactionStr = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = jsonObject.get(key).toString();
                transactionStr.append(key).append("=").append(value);
                if (i < keys.size() - 1) {
                    transactionStr.append("&");
                }
            }

            return hmacSha256(transactionStr.toString(), CHECKSUM_KEY);
        } catch (Exception e) {
            Log.e("PayOSHelper", "Signature generation failed", e);
            return "";
        }
    }

    private String hmacSha256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
