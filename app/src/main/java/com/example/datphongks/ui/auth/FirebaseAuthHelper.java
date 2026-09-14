package com.example.datphongks.ui.auth;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * HƯỚNG DẪN NGẮN GỌN: Đăng nhập bằng Google qua Firebase Auth.
 *
 * Các bước cần làm THỦ CÔNG trên Firebase Console (bắt buộc, code không thay được):
 *  1. Vào https://console.firebase.google.com -> tạo project -> Add app Android,
 *     package name phải đúng "com.example.datphongks".
 *  2. Bật Authentication -> Sign-in method -> bật "Google".
 *  3. Tải file google-services.json, copy vào thư mục app/ của project.
 *  4. Lấy "Web client ID" (SHA-1 + Web client ID) tại Project settings -> General,
 *     hoặc trong Authentication -> Sign-in method -> Google -> Web SDK configuration.
 *     Dán chuỗi đó vào chỗ WEB_CLIENT_ID bên dưới.
 *  5. Thêm SHA-1 debug key của máy bạn vào Firebase Console (Project settings ->
 *     Your apps -> Add fingerprint) - lấy SHA-1 bằng lệnh:
 *     ./gradlew signingReport
 *
 * CÁCH DÙNG trong LoginFragment (ví dụ):
 *   FirebaseAuthHelper authHelper = new FirebaseAuthHelper(this);
 *   ActivityResultLauncher<Intent> launcher = registerForActivityResult(
 *           new ActivityResultContracts.StartActivityForResult(),
 *           result -> authHelper.handleSignInResult(result.getData(), user -> {
 *               // đăng nhập Firebase thành công, "user" là FirebaseUser
 *               // TODO: đồng bộ user này vào bảng User (SQLite) nếu cần, rồi điều hướng
 *           }));
 *   binding.btnGoogle.setOnClickListener(v -> launcher.launch(authHelper.getSignInIntent()));
 *
 * VỀ FACEBOOK LOGIN: cách làm tương tự nhưng cần thêm Facebook SDK riêng
 * (com.facebook.android:facebook-login) + khai báo App ID trong strings.xml +
 * meta-data trong AndroidManifest.xml. Vì đây là phần tích hợp bên thứ 3 khá dài,
 * nếu bạn muốn mình sẽ viết chi tiết FacebookAuthHelper.java riêng ở bước sau.
 */
public class FirebaseAuthHelper {

    // TODO: Thay bằng Web Client ID thật lấy từ Firebase Console
    private static final String WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com";

    public interface OnGoogleSignInListener {
        void onSuccess(com.google.firebase.auth.FirebaseUser firebaseUser);
        void onFailure(Exception e);
    }

    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInClient googleSignInClient;
    private OnGoogleSignInListener listener;

    public FirebaseAuthHelper(Activity activity) {
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    /** Trả về Intent để mở màn hình chọn tài khoản Google. */
    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    /** Gọi trong callback của ActivityResultLauncher sau khi user chọn xong tài khoản Google. */
    public void handleSignInResult(Intent data, OnGoogleSignInListener listener) {
        this.listener = listener;
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            listener.onFailure(e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onSuccess(firebaseAuth.getCurrentUser());
            } else {
                listener.onFailure(task.getException());
            }
        });
    }
}
