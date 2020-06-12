package com.offlineprogrammer.braintrainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.offlineprogrammer.braintrainer.answer.Answer;
import com.offlineprogrammer.braintrainer.answer.AnswerAdapter;
import com.offlineprogrammer.braintrainer.answer.AnswerGridItemDecoration;
import com.offlineprogrammer.braintrainer.answer.OnAnswerListener;
import com.offlineprogrammer.braintrainer.game.HighScoreGame;
import com.offlineprogrammer.braintrainer.user.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;

public class MainActivity extends AppCompatActivity implements OnAnswerListener {
    private static final String TAG = "MainActivity";
    RecyclerView mRecyclerView;
    ArrayList<Answer> mAnswerList;
    Answer mAnswer;
    AnswerAdapter myAdapter;
    private TheGame myGame;
    ImageButton goButton;
    TextView timerTextView;
    TextView questionTextView;
    TextView scoreTextView;
    CountDownTimer countDownTimer = null;
    private com.google.android.gms.ads.AdView adView;
    private FirebaseAnalytics mFirebaseAnalytics;
    KonfettiView viewKonfetti;
    FirebaseHelper firebaseHelper;
    User m_User;
    private Disposable disposable;
    ProgressDialog progressBar;
    TextView topScoreTextView;
    MaterialToolbar topAppBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseHelper = new FirebaseHelper(getApplicationContext());
        viewKonfetti = findViewById(R.id.viewKonfetti);
        mRecyclerView = findViewById(R.id.answers_recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.bt_answer_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bt_answer_grid_spacing_small);
        mRecyclerView.addItemDecoration(new AnswerGridItemDecoration(largePadding, smallPadding));
        setupProgressBar();

        goButton = findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myGame == null || !myGame.isActive()) {
                    playTheGame();
                }
            }
        });

        timerTextView = findViewById(R.id.timerTextView);
        questionTextView = findViewById(R.id.questionTextView);
        questionTextView.setText("??");
        scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("??");
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.configure_menu)
                {
                    // do something
                }
                return false;
            }
        });

        topScoreTextView = findViewById(R.id.top_score_text);

        getDeviceToken();
        prepareData();
        setupAds();

    }

    private void getDeviceToken() {
        firebaseHelper.getDeviceToken().observeOn(Schedulers.io())
                //.observeOn(Schedulers.m)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(String deviceToken) {
                        Log.d(TAG, "onNext: " + deviceToken);
                        getUserData(deviceToken);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    private void getUserData(String deviceToken) {
        firebaseHelper.getUserData(deviceToken).observeOn(Schedulers.io())
                //.observeOn(Schedulers.m)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(TAG, "onNext: " + user.getFirebaseId());
                        m_User = user;
                        if (m_User.getFirebaseId()==null) {
                            saveUser();
                        } else {
                            setTopScoreTextView();
                            dismissProgressBar();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

    }

    private void setTopScoreTextView() {
        if (m_User.getHighScoreGame() != null) {
            Log.i(TAG, "onNext: highScoreGame " + m_User.getHighScoreGame().getScore());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    topScoreTextView.setText(m_User.getHighScoreGame().getScore().toString());
                }
            });
        }
    }


    private void saveUser() {
        firebaseHelper.saveUser().observeOn(Schedulers.io())
                //.observeOn(Schedulers.m)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(TAG, "onNext: " + user.getFirebaseId());
                        m_User = user;
                        setTopScoreTextView();
                        dismissProgressBar();


                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }



    private void setupAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void celebratCompletion() {
        viewKonfetti.bringToFront();
        viewKonfetti.setTranslationZ(1);
        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA,Color.RED)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(4000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new nl.dionsegijn.konfetti.models.Size(10, 20))
                .setPosition(viewKonfetti.getX() + viewKonfetti.getWidth()/2, viewKonfetti.getY()+viewKonfetti.getHeight()/2)
                .burst(600);
        //.streamFor(300, 5000L);
    }

    private void playTheGame() {
        mFirebaseAnalytics.logEvent("play_theGame", null);
        myGame = new TheGame("+");
        myGame.setNumberOfQuestions(0);
        myGame.setScore(0);
        timerTextView.setText("30s");
        scoreTextView.setText(Integer.toString(myGame.getScore()));
        newQuestion();
        myGame.setActive(true);
        goButton.setImageResource(R.drawable.question);


        if(countDownTimer  != null){
            countDownTimer.cancel();
        }
        countDownTimer =  new CountDownTimer(30100,1000){

            @Override
            public void onTick(long millisUntilFinished) {

                timerTextView.setText(String.valueOf(millisUntilFinished/1000)+"s");

            }

            @Override
            public void onFinish() {
                timerTextView.setText("0s");
                if (myGame.getScore()>0) {
                    Log.i(TAG, "onFinish: The score is " + myGame.getScorePercentage());
                    celebratCompletion();
                    int numberOfCorrectAnswers = myGame.getScore()/100;
                    int numberOfWrongAnswers = myGame.getNumberOfQuestions() - numberOfCorrectAnswers;

                    String sMsg = String.format(" Score: %d \n Correct Answers: %d \n Wrong Answers: %d \n Your accuracy rate is %d %%", myGame.getScore(), numberOfCorrectAnswers, numberOfWrongAnswers ,  myGame.getScorePercentage());
                    String sTitle = "Well done";

                    HighScoreGame userHighScoreGame = new HighScoreGame(
                            Calendar.getInstance().getTime(),
                            myGame.getScore(),
                            numberOfCorrectAnswers,
                            numberOfWrongAnswers
                    );
                    if(m_User.getHighScoreGame().getScore()<= myGame.getScore()){
                        saveUserGame(userHighScoreGame);
                        m_User.setHighScoreGame(userHighScoreGame);
                        setTopScoreTextView();
                        sTitle = "New High Score!!";
                        mFirebaseAnalytics.logEvent(String.format("high_score_%d",myGame.getScore()) , null);
                    }

                    new MaterialAlertDialogBuilder(MainActivity.this)
                            .setTitle(sTitle)
                            .setMessage(sMsg)
                            .setNeutralButton("Ok",null)
                            .show();

                }

                goButton.setImageResource(R.drawable.playagain);
                myGame.setActive(false);
                mFirebaseAnalytics.logEvent("theGame_Finished", null);


            }
        }.start();

    }

    private void saveUserGame(HighScoreGame userHighScoreGame) {
        firebaseHelper.saveUserGame(userHighScoreGame, m_User.getFirebaseId()).observeOn(Schedulers.io())
                .subscribe(() -> {
                    Log.i(TAG, "updateRewardImage: completed");
                    // handle completion
                }, throwable -> {
                    // handle error
                });
    }

    private void newQuestion() {

        updateAnswers(myGame.setupGame());
        questionTextView.setText(String.format("%s %s %s", Integer.toString(myGame.a), myGame.getOperation() , Integer.toString(myGame.b)));


    }

    private void updateAnswers(ArrayList<Integer> setupGame) {
        mAnswerList.clear();

        Collections.addAll(mAnswerList,
                new Answer(setupGame.get(0)),
                new Answer(setupGame.get(1)),
                new Answer(setupGame.get(2)),
                new Answer(setupGame.get(3)));
        Log.i(TAG, "prepareData: Size " + mAnswerList.size());
        myAdapter.updateData(mAnswerList);

    }

    private void prepareData() {
        mAnswerList = new ArrayList<>(4);

        Collections.addAll(mAnswerList,
                new Answer(0),
                new Answer(0),
                new Answer(0),
                new Answer(0));
        Log.i(TAG, "prepareData: Size " + mAnswerList.size());
        myAdapter = new AnswerAdapter(MainActivity.this, mAnswerList,this);
        mRecyclerView.setAdapter(myAdapter);

    }

    @Override
    public void onAnswerClick(int position) {
        if (!myGame.isActive()){
            return;
        }
        Log.i("Selected button","is " + position);


        if (position == myGame.locationOfCorrectAnswer){
            goButton.setImageResource(R.drawable.correct);
            int score = myGame.getScore();
            myGame.incrementScore();
            Log.i(TAG, "onAnswerClick: myGame " + myGame.getScore());

        } else {
            goButton.setImageResource(R.drawable.wrong);


        }
        myGame.incrementNumberOfQuestions();
        scoreTextView.setText(Integer.toString(myGame.getScore()));
        Log.i(TAG, "onAnswerClick: scoreTextView " + scoreTextView.getText());
        newQuestion();

    }


    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();

    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    private void setupProgressBar() {
        dismissProgressBar();
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Loading data ...");
        progressBar.show();
    }

    private void dismissProgressBar() {
        dismissWithCheck(progressBar);
    }

    public void dismissWithCheck(ProgressDialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {

                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();

                // if the Context used here was an activity AND it hasn't been finished or destroyed
                // then dismiss it
                if (context instanceof Activity) {

                    // Api >=17
                    if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                        dismissWithTryCatch(dialog);
                    }
                } else
                    // if the Context used wasn't an Activity, then dismiss it too
                    dismissWithTryCatch(dialog);
            }
            dialog = null;
        }
    }

    public void dismissWithTryCatch(ProgressDialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        } finally {
            dialog = null;
        }
    }

}
