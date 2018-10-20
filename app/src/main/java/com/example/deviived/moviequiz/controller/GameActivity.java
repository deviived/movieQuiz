package com.example.deviived.moviequiz.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.example.deviived.moviequiz.R;
import com.example.deviived.moviequiz.model.Question;
import com.example.deviived.moviequiz.model.QuestionBank;

import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

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
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
    public static final String BUNDLE_STATE_SCORE = "currentScore";
    public static final String BUNDLE_STATE_QUESTION = "currentQuestion";
    public double increment = 0;

    CountDownTimer countDownTimer = null;
    public String timeS;
    MediaPlayer good;
    MediaPlayer wrong;
    ProgressBar progressBar;

    private boolean mEnableTouchEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GameActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mQuestionBank = this.generateQuestions();

        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mNumberOfQuestions = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
        } else {
            mScore = 0;
            mNumberOfQuestions = 3;
        }

        mEnableTouchEvents = true;

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
        progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorTextSecondary)));
        GifImageView gifImageView = (GifImageView) findViewById(R.id.activity_gif_perso);
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
        int responseIndex = (int) v.getTag();

        if (responseIndex == mCurrentQuestion.getAnswerIndex()) {
            // Good answer
            good.start();
            mAnswers[responseIndex].setBackgroundColor(getResources().getColor(R.color.ans_green));
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
            mScore++;
        } else {
            wrong.start();
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
        good = MediaPlayer.create(GameActivity.this, R.raw.good);
        wrong = MediaPlayer.create(GameActivity.this, R.raw.wrong);
        good.setLooping(false);
        wrong.setLooping(false);

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

        increment = 0;

        countDownTimer = new CountDownTimer(16000, 20){

            @Override
            public void onTick(long millisUntilFinished) {
                timeS = Long.toString(millisUntilFinished / 1000);
                increment+=20;
                double total = 16000 - increment;
                progressBar.setProgress((int) (total-20));
                System.out.println("increment = "+increment+" ...millisUntilFinished = "+millisUntilFinished+" ....int TOTAL = "+total);
            }

            @Override
            public void onFinish() {
                Toast.makeText(GameActivity.this ,"Time's up!", Toast.LENGTH_SHORT).show();
                progressBar.setProgress(0);
                wrong.start();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(BUNDLE_STATE_SCORE, mScore);
        outState.putInt(BUNDLE_STATE_QUESTION, mNumberOfQuestions);

        super.onSaveInstanceState(outState);
    }

    private void endGame() {
        countDownTimer.cancel();
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

       /* Question question27 = new Question("Quel film n'a pas été réalisé par Sergio Leone ?",
                Arrays.asList("Pour une poignée de dollars", "L'homme qui valait 3 milliard", "Le Bon, la Brute et le Truand", "Il était une fois dans l'Ouest"),
                1);
*/
        Question question28 = new Question("Qui a réalisé «Ghost Dog» ?",
                Arrays.asList("Jim Jarmusch", "Jim Donovan", "Jim Cummings", "Jim Powers"),
                0);

        Question question29 = new Question("Qui a réalisé «Only Lovers Left Alive» ?",
                Arrays.asList("Claire Denis", "Denis Villeneuve", "Jim Donovan", "Jim Jarmusch"),
                3);

        Question question30 = new Question("Lequel de ces films n'est pas réalisé par Quentin Tarantino ?",
                Arrays.asList("Pulp Fiction", "Les 12 salopards", "Réservoir Dogs", "Les 8 salopards"),
                1);

        Question question31 = new Question("Lequel de ces films est réalisé par Quentin Tarantino ?",
                Arrays.asList("12 hommes en colère", "Les 8 salopards", "Les 12 salopards", "Les 7 mercenaires"),
                1);

        Question question32 = new Question("Lequel de ces films n'est pas réalisé par Guy Ritchie ?",
                Arrays.asList("Snatch : Tu braques ou tu raques", "Arnaques, Crimes et Botanique", "Hyper Tension", "Revolver"),
                2);

        Question question33 = new Question("Qui interprète le rôle de «Pierrot» dans «Pierrot le Fou» de Jean-Luc Godard ?",
                Arrays.asList("Jean-Paul Belmondo", "Simon Belmont", "Jean-Paul Rouve", "Jean-Pierre Belmondo"),
                0);

        Question question34 = new Question("Qui interprète le rôle principal dans «Zatoichi» de Takeshi Kitano ?",
                Arrays.asList("Takeshi Kitano", "Saburo Ishikura", "Guadalcanal Taka", "Takeshi Miike"),
                0);

        Question question35 = new Question("Quel acteur n'a jamais interprété «François Pignon», personnage récurrent du réalisateur Francis Veber ?",
                Arrays.asList("Jacques Villeret", "Thierry Lhermitte", "Pierre Richard", "Daniel Auteuil"),
                1);

        Question question36 = new Question("Dans quel film ne retrouve-t-on pas le personnage de «François Perrin», personnage récurrent du réalisateur Francis Veber ?",
                Arrays.asList("Le Grand Blond avec une chaussure noire", "Le Jouet", "Les Fugitifs", "La Chèvre"),
                2);

        Question question37 = new Question("Qui a réalisé «Suspiria» ?",
                Arrays.asList("Mario Bava", "Dario Argento", "Wes Craven", "Lucio Fulci"),
                1);

        Question question38 = new Question("Qui a réalisé «Les évadés» ?",
                Arrays.asList("Frank Darabont", "Alan Parker", "Don Siegel", "Ric Roman Waugh"),
                0);

        Question question39 = new Question("Qui a réalisé «Midnight Express» ?",
                Arrays.asList("Don Siegel", "Alan Parker", "Ric Roman Waugh", "Frank Darabont"),
                1);

        Question question40 = new Question("Qui a réalisé «Les Affranchis» ?",
                Arrays.asList("Francis Ford Coppola", "Brian De Palma", "Martin Scorsese", "Mike Newell"),
                2);

        Question question41 = new Question("Qui a réalisé «Le Parrain» ?",
                Arrays.asList("Mike Newell", "Martin Scorsese", "Brian De Palma", "Francis Ford Coppola"),
                3);

        Question question42 = new Question("Qui a réalisé «Les Incorruptibles» ?",
                Arrays.asList("Brian De Palma", "Mike Newell", "Francis Ford Coppola", "Martin Scorsese"),
                0);

        Question question43 = new Question("Quel film n'a pas été réalisé par Brad Bird ?",
                Arrays.asList("Ratatouille", "Chicken Run", "Les Indestructibles", "Le Géant de fer"),
                1);


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
                question26,
                //question27,
                question28,
                question29,
                question30,
                question31,
                question32,
                question33,
                question34,
                question35,
                question36,
                question37,
                question38,
                question39,
                question40,
                question41,
                question42,
                question43
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
        if(!good.equals(null)) {
            good.release();
        }
        if(!wrong.equals(null)) {
            wrong.release();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
        System.out.println("GameActivity::onStop()");
        if(!good.equals(null)) {
            good.release();
        }
        if(!wrong.equals(null)) {
            wrong.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();

        if(!good.equals(null)) {
            good.release();
        }
        if(!wrong.equals(null)) {
            wrong.release();
        }
        System.out.println("GameActivity::onDestroy()");
    }
}
