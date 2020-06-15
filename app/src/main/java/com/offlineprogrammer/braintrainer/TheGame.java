package com.offlineprogrammer.braintrainer;

import java.util.ArrayList;
import java.util.Random;

public class TheGame {

    private String mOperation;
    private String mathOp;
    private boolean isActive = false;
    public int locationOfCorrectAnswer;
    public  int a;
    public  int b;
    private int score;
    private int scorePercentage;
    private int numberOfQuestions;
    ArrayList<Integer> answers = new ArrayList<Integer>();

    public TheGame(String sOperation) {
        mOperation=sOperation;
    }

    public  int doMath(int a, int b, String sOperation){
        int result = 0;
        if (sOperation.equals("+")) {
            result = a+b;
        } else if (sOperation.equals("-")) {
            result = a-b;
        } else if (sOperation.equals("*")) {
            result = a*b;
        } else  if (sOperation.equals("/")) {
            result = a/b;
        }
        return result;
    }


    public ArrayList<Integer> setupGame(){
        int i11;
        int i12;
        mathOp = mOperation;
        if (mOperation.equals("Random")) {
            String[] list = {"+", "-", "*", "/"};
            Random r = new Random();
            mathOp = list[r.nextInt(list.length)];
            mathOp = "-";
        }
        Random rand = new Random();
        if (mathOp.equals("/")) {
            i11 = rand.nextInt(14) + 1;
            i12 = rand.nextInt(6);
            b = i12 + 1;
            a = i11 * b;
        } else if (mathOp.equals("-")) {
            i11 = rand.nextInt(21) + 1;
            i12 = rand.nextInt(21);
            int i20 = i12 + 1;
            if (i11 > i20) {
                a = i11;
                b = i20;

            } else {
                a = i20;
                b = i11;
            }
        } else {
            a = rand.nextInt(21);
            b = rand.nextInt(21);
        }
        locationOfCorrectAnswer = rand.nextInt(4);
        // sumTextView.setText(String.format("%s %s %s", Integer.toString(a), myGame.getOperation() , Integer.toString(b)));
        answers.clear();
        for (int i = 0; i < 4; i++) {
            if (i == locationOfCorrectAnswer) {
                answers.add(doMath(a, b, mathOp));
            } else {
                int wrongAnswer = getRandom(a, b, mathOp);
                while (wrongAnswer == doMath(a, b, mathOp)) {
                    wrongAnswer = getRandom(a, b, mathOp);
                }
                answers.add(wrongAnswer);
            }
        }
        return answers ;
    }








    public  int getRandom(int a, int b, String sOperation){
        int result = 41;



        Random rand = new Random();
        if (sOperation.equals("+")) {
            result =rand.nextInt(41);
        } else if (sOperation.equals("-")) {
            result = rand.nextInt(41 + 20) - 20;
        } else if (sOperation.equals("*")) {
            if (a == 0) {
                a = 1;
            }
            if (b == 0) {
                b =1;
            }
            result =rand.nextInt(2*a*b);
        } else  if (sOperation.equals("/")) {
            result =rand.nextInt(41);
        }
        return result;
    }


    public String getOperation() {
        return mathOp;// mOperation;
    }

    public void setOperation(String mOperation) {
        this.mOperation = mOperation;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getScorePercentage(){
        scorePercentage = (score / numberOfQuestions);
        return scorePercentage;
    }

    public void incrementScore() {

        this.score+=100;
    }

    public void incrementNumberOfQuestions() {
        this.numberOfQuestions++;
    }
}
