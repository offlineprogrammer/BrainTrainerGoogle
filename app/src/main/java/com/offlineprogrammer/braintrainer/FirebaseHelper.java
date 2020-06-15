package com.offlineprogrammer.braintrainer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.offlineprogrammer.braintrainer.game.HighScoreGame;
import com.offlineprogrammer.braintrainer.user.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class FirebaseHelper {
    private User m_User;
    FirebaseFirestore m_db;
    FirebaseAuth firebaseAuth;
    FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "FirebaseHelper";
    Context mContext;


    public FirebaseHelper (Context c){
        m_db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mContext = c;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    Observable<String> getDeviceToken() {
        return Observable.create((ObservableEmitter<String> emitter) -> {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String deviceToken = instanceIdResult.getToken();
                    emitter.onNext(deviceToken);
                }
            });

        });
    }

    Observable<User> getUserData(String deviceToken) {
        return Observable.create((ObservableEmitter<User> emitter) -> {
            m_User = new User(deviceToken);
            m_db.collection("users")
                    .whereEqualTo("deviceToken", deviceToken)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        m_User=document.toObject(User.class);
                                        m_User.setFirebaseId(document.getId());
                                    }
                                }
                                emitter.onNext(m_User);
                            } else {
                                emitter.onError(task.getException());
                                Log.d("Got Date", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        });
    }

    Observable<User> saveUser() {
        return Observable.create((ObservableEmitter<User> emitter) -> {
            m_User.setDateCreated(Calendar.getInstance().getTime());
            m_User.setHighScoreGame(new HighScoreGame(Calendar.getInstance().getTime(),
                    0,
                    0,
                    0));
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                m_User.setUserId(currentUser.getEmail());

            } else {
                m_User.setUserId("guest");
            }

            m_db.collection("users")
                    .add(m_User)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            m_User.setFirebaseId(documentReference.getId());
                            emitter.onNext(m_User);
                            Log.d("SavingData", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("SavingData", "Error adding document", e);
                            emitter.onError(e);
                        }
                    });

        });
    }

    public Completable  saveUserGame(HighScoreGame highScoreGame, String userFireStoreId) {

        return Completable.create( emitter -> {
            Map<String, Object> gameValues = highScoreGame.toMap();
            DocumentReference selectedUserRef = m_db.collection("users").document(userFireStoreId);
            selectedUserRef.update("highScoreGame", gameValues)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "DocumentSnapshot successfully updated!");
                            emitter.onComplete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Error updating document", e);
                            emitter.onError(e);
                        }
                    });
        });
    }


    public Completable updateGameOperation(String sOperation, String userFireStoreId) {
        return Completable.create( emitter -> {

            DocumentReference selectedUserRef = m_db.collection("users").document(userFireStoreId);
            selectedUserRef.update("gameOperation", sOperation)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "DocumentSnapshot successfully updated!");
                            emitter.onComplete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Error updating document", e);
                            emitter.onError(e);
                        }
                    });
        });
    }
}
