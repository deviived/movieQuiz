package com.example.deviived.moviequiz.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.example.deviived.moviequiz.R;
import com.example.deviived.moviequiz.model.Question;
import com.example.deviived.moviequiz.model.QuestionBank;

import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTimer;
    private TextView mQuestion;
    private Button mAnswer1;
    private Button mAnswer2;
    private Button mAnswer3;
    private Button mAnswer4;
    private Button mAnswers[];

    private QuestionBank mQuestionBank;
    private Question mCurrentQuestion;

    private int mScore;
    private int mNumberOfQuestions;

    private boolean mEnableTouchEvents;

    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";

    CountDownTimer countDownTimer = null;

    String timeS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mQuestionBank = this.generateQuestions();
        mNumberOfQuestions = 10;
        mEnableTouchEvents = true;

        mTimer = (TextView) findViewById(R.id.activity_timer_text);
        mQuestion = (TextView) findViewById(R.id.activity_game_question_text);
        mAnswer1 = (Button) findViewById(R.id.activity_game_answer1_btn);
        mAnswer2 = (Button) findViewById(R.id.activity_game_answer2_btn);
        mAnswer3 = (Button) findViewById(R.id.activity_game_answer3_btn);
        mAnswer4 = (Button) findViewById(R.id.activity_game_answer4_btn);
        mAnswers = new Button[] {mAnswer1, mAnswer2, mAnswer3, mAnswer4};

        // Use the tag property to 'name' the buttons
        mAnswer1.setTag(0);
        mAnswer2.setTag(1);
        mAnswer3.setTag(2);
        mAnswer4.setTag(3);

        // Use the same listener for the four buttons.
        // The tag value will be used to distinguish the button triggered
        mAnswer1.setOnClickListener(this);
        mAnswer2.setOnClickListener(this);
        mAnswer3.setOnClickListener(this);
        mAnswer4.setOnClickListener(this);

        mCurrentQuestion = mQuestionBank.getQuestion();
        this.displayQuestion(mCurrentQuestion);
    }

    @Override
    public void onClick(View v) {
        countDownTimer.cancel();
        mTimer.setText("");
        int responseIndex = (int) v.getTag();

        if (responseIndex == mCurrentQuestion.getAnswerIndex()) {
            // Good answer
            mAnswers[responseIndex].setBackgroundColor(getResources().getColor(R.color.ans_green));
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
            mScore++;
        } else {
            // Wrong answer
            mAnswers[responseIndex].setBackgroundColor(getResources().getColor(R.color.ans_red));
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEnableTouchEvents = true;

                // If this is the last question, ends the game.
                // Else, display the next question.
                if (--mNumberOfQuestions == 0) {
                    // End the game
                    endGame();
                } else {
                    mCurrentQuestion = mQuestionBank.getQuestion();
                    displayQuestion(mCurrentQuestion);
                }
            }
        }, 1500); // LENGTH_SHORT is usually 2 second long
    }

    private void displayQuestion(final Question question) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        mQuestion.setText(question.getQuestion());
        mAnswer1.setText(question.getChoiceList().get(0));
        mAnswer1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mAnswer2.setText(question.getChoiceList().get(1));
        mAnswer2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mAnswer3.setText(question.getChoiceList().get(2));
        mAnswer3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mAnswer4.setText(question.getChoiceList().get(3));
        mAnswer4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        countDownTimer = new CountDownTimer(15000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                timeS = Long.toString(millisUntilFinished / 1000);
                mTimer.setText(timeS);
            }

            @Override
            public void onFinish() {
                Toast.makeText(GameActivity.this ,"Time's up!", Toast.LENGTH_SHORT).show();

                mEnableTouchEvents = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEnableTouchEvents = true;

                        // If this is the last question, ends the game.
                        // Else, display the next question.
                        if (--mNumberOfQuestions == 0) {
                            // End the game
                            endGame();
                        } else {
                            mCurrentQuestion = mQuestionBank.getQuestion();
                            displayQuestion(mCurrentQuestion);
                        }
                    }
                }, 1000); // LENGTH_SHORT is usually 2 second long

            }


        };
        countDownTimer.start();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    private void endGame() {
        countDownTimer.cancel();
        mTimer.setText("");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Well done!")
                .setMessage("Your score is " + mScore)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .create()
                .show();
    }



    private QuestionBank generateQuestions() {
        Question question1 = new Question("Dans quel Matrix apparaît le personnage du Mérovingien ?",
                Arrays.asList("Matrix", "Matrix Reloaded", "Matrix Revolutions", "Dans aucun, vous êtes dans la matrice"),
                1);

        Question question2 = new Question("Dans quel pays est né Stanley Kubrick ?",
                Arrays.asList("Royaume-Uni", "Etats-Unis", "Australie", "Danemark"),
                1);

        Question question3 = new Question("Stanley Kubrick souhaitait réaliser (projet inabouti) un film sur un personnage historique français, lequel?",
                Arrays.asList("François 1er", "Louis XIV", "Marie-Antoinette", "Napoléon Bonaparte"),
                3);

        Question question4 = new Question("Parmi ces films de Stanley Kubrick, lequel n'a pas été tourné en noir et blanc?",
                Arrays.asList("Spartacus", "Les sentiers de la gloire", "Docteur Folamour", "Lolita"),
                0);

        Question question5 = new Question("Dans quel film de Stanley Kubrick retrouve-t-on la musique du « Beau Danube bleu » de Johann Strauss?",
                Arrays.asList("Orange Mécanique", "Barry Lyndon", "2001, l'odyssée de l'espace", "Eyes Wide Shut"),
                2);

        Question question6 = new Question("Quel est le dernier film de Stanley Kubrick?",
                Arrays.asList("Shining", "Full Metal Jacket", "Eyes Wide Shut", "Orange mécanique"),
                2);

        Question question7 = new Question("Dans quel film le héros dit-il en mourant « Rosebud »",
                Arrays.asList("Citizen Kane", "Casablanca", "Le pont de la rivière Kwai", "Autant en emporte le vent"),
                0);

        Question question8 = new Question("Qui a réalisé le film « Raging Bull » ?",
                Arrays.asList("Wes Craven", "Wim Wender", "Jim Jarmusch", "Martin Scorsese"),
                3);

        Question question9 = new Question("Quel film n'a pas été réalisé par David Fincher ?",
                Arrays.asList("Seven", "Fight Club", "Gone Girl", "Prisoners"),
                3);

        Question question10 = new Question("Quel film n'a pas été réalisé par Sergio Leone ?",
                Arrays.asList("Pour une poignée de dollars", "L'homme qui valait 3 milliard", "Le Bon, la Brute et le Truand", "Il était une fois dans l'Ouest"),
                1);

        Question question11 = new Question("Quel film n'a pas été réalisé par Wes Anderson ?",
                Arrays.asList("La Famille Tenenbaum", "À bord du Darjeeling Limited", "The Grand Budapest Hotel", "Moonwalkers"),
                3);

        Question question12 = new Question("Qui est le réalisateur de «Psychose» ?",
                Arrays.asList("Dario Argento", "George A. Romero", "Alfred Hitchcock", "John Carpenter"),
                2);

        Question question13 = new Question("Qui est le réalisateur de «Jurassic Park» ?",
                Arrays.asList("Steven Spielberg", "George Lucas", "James Cameron", "Peter Jackson"),
                0);

        Question question14 = new Question("Qui est le réalisateur de «Titanic» ?",
                Arrays.asList("Steven Spielberg", "George Lucas", "James Cameron", "Peter Jackson"),
                2);

        Question question15 = new Question("Qui est le réalisateur de «King Kong»(2005) ?",
                Arrays.asList("Steven Spielberg", "George Lucas", "James Cameron", "Peter Jackson"),
                3);

        Question question16 = new Question("Qui est le réalisateur de «Les Dents de la mer» ?",
                Arrays.asList("Steven Spielberg", "George Lucas", "James Cameron", "Peter Jackson"),
                0);

        Question question17 = new Question("Qui est le réalisateur de «Forrest Gump» ?",
                Arrays.asList("Steven Spielberg", "George Lucas", "Robert Zemeckis", "Paul Thomas Anderson"),
                2);

        Question question18 = new Question("Qui est le réalisateur de «Seul au monde» ? ",
                Arrays.asList("Steven Spielberg", "Robert Zemeckis", "Paul Thomas Anderson", "George Lucas"),
                1);

        Question question19 = new Question("Qui est le réalisateur de «American Graffiti» ?",
                Arrays.asList("Steven Spielberg", "Robert Zemeckis", "James Cameron", "George Lucas"),
                3);

        Question question20 = new Question("Qui est le réalisateur de «Resident Evil», le film ?",
                Arrays.asList("Wes Anderson", "Michael Anderson", "Paul W.S. Anderson", "Paul Thomas Anderson"),
                2);

        Question question21 = new Question("Qui est le réalisateur de «Le Bon, la Brute et le Truand» ?",
                Arrays.asList("Clint Eastwood", "Sergio Leone", "John Ford", "Henry Hathaway"),
                1);

        Question question22 = new Question("Quel film n'est pas sur le thème de la «guerre» ?",
                Arrays.asList("La Ligne Verte", "La Ligne Rouge", "Full Metal Jacket", "Platoon"),
                0);

        Question question23 = new Question("Quel film n'est pas de la «science-fiction» ?",
                Arrays.asList("Interstellar", "Retour vers le futur", "Le Prodige", "Idiocraty"),
                2);

        Question question24 = new Question("Quel film n'est pas de la «science-fiction» ?",
                Arrays.asList("Metropolis", "Minority Report", "Premier Contact", "Enemy"),
                3);

        Question question25 = new Question("Quel film est un «drame» ?",
                Arrays.asList("Le dîner de cons", "La chèvre", "La vérité si je mens", "Vol au-dessus d'un nid de coucou"),
                3);

        Question question26 = new Question("Quel film n'est pas un «thriller» ?",
                Arrays.asList("Seven", "Usual Suspects", "Le Silence des agneaux", "Le Fabuleux destin d'Amélie Poulain"),
                3);


        return new QuestionBank(Arrays.asList(question1,
                question2,
                question3,
                question4,
                question5,
                question6,
                question7,
                question8,
                question9,
                question10,
                question11,
                question12,
                question13,
                question14,
                question15,
                question16,
                question17,
                question18,
                question19,
                question20,
                question21,
                question22,
                question23,
                question24,
                question25,
                question26
        ));
    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("GameActivity::onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("GameActivity::onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        System.out.println("GameActivity::onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        System.out.println("GameActivity::onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.out.println("GameActivity::onDestroy()");
    }
}
