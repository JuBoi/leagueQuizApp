package com.bryanju2.myproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment implements View.OnClickListener{
    private static final String MyDATA = "MyData";
    private static final int COUNT = 137;

    //A random int from 0 to 3 inclusive, denoting the correct buttont to press
    private int correct;

    //The number of the random champion
    private int rando;

    //An int from 0 to 3 inclusive, denoting whether its Q,W,E,R.
    private int abilityNumber;

    //The keypress of the ability
    private String key;

    //An array to hold previous random integers so I don't get the same "random" choice two questions in a row
    private int[] previous;

    //The current score
    private int score;

    //The number of questions already answered
    private int numberOfQuestions;

    //The start time of when the first question was asked
    private long startTime;

    MediaPlayer mp;

    private int maxScore;

    private int musicOn;

    View view;

    //The variables responsible for handling local storage
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    /**
     * required public constructor
     */
    public QuestionFragment() {
        previous = new int[4];
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_question, container, false);

        //Gets the number of questions already answered
        numberOfQuestions = getArguments().getInt("numberOfQuestions");
        musicOn = getArguments().getInt("musicOn");

        if(numberOfQuestions == 0){
            score = 0;
            if(musicOn == 1) {
                mp = MediaPlayer.create(getActivity(), R.raw.theme);
                mp.start();
            }
        }
        else {
            score = getArguments().getInt("score");
        }
        maxScore = getArguments().getInt("maxScore");
        startTime = getArguments().getLong("startTime");

        sharedpreferences = getSharedPreferences();
        editor = sharedpreferences.edit();

        //Setting the score
        TextView scoreView = (TextView) view.findViewById(R.id.score);
        scoreView.setText("Score: " + Integer.toString(score));

        //Sets the onclick listener for buttons
        Button next = (Button) view.findViewById(R.id.next);
        Button restart = (Button) view.findViewById(R.id.button);
        restart.setOnClickListener(this);
        next.setOnClickListener(this);

        //Loads up a random question
        getQuestion();

        return view;
    }

    public SharedPreferences getSharedPreferences() {
        return this.getContext().getSharedPreferences(MyDATA, Context.MODE_PRIVATE);
    }

    /**
     * Sets up the question with a random champion and random ability.
     */
    public void getQuestion(){
        rando = (int)(Math.random() * COUNT);

        TextView question = (TextView) view.findViewById(R.id.question);

        abilityNumber = (int) (Math.random() * 4);
        key = getKey(abilityNumber);

        question.setText("What is " + sharedpreferences.getString(Integer.toString(rando), "null") + "'s " + key + " Ability?");
        correct = (int)(Math.random() * 4);
        Button[] choices = new Button[4];
        populateChoices(choices, correct, rando);

    }

    /**
     * Returns the ability key corresponding to the number.
     * @param number A random number between 0 and 3 inclusive.
     * @return A string that is the ability key
     */
    public String getKey(int number){
        if(number == 0){
            return "Q";
        }
        if(number == 1){
            return "W";
        }
        if(number == 2){
            return "E";
        }
        if(number == 3){
            return "R";
        }
        return "null";
    }

    /**
     * Sets up all of the information in the buttons.
     * @param choices The array of buttons
     * @param correct The correct button
     * @param rando The random champion number
     */
    public void populateChoices(Button[] choices, int correct, int rando){
        choices[0] = (Button) view.findViewById(R.id.choice1);
        choices[1] = (Button) view.findViewById(R.id.choice2);
        choices[2] = (Button) view.findViewById(R.id.choice3);
        choices[3] = (Button) view.findViewById(R.id.choice4);
        choices[correct].setText(sharedpreferences.getString(key.toLowerCase() + rando, "null"));
        for(int i = 0; i < 4; i++){
            int randomIndex = (int)(Math.random() * COUNT);
            while(randomIndex == previous[0] || randomIndex == previous[1] || randomIndex == previous[2] || randomIndex == previous[3] || randomIndex == rando){
                randomIndex = (int)(Math.random() * COUNT);
            }
            if(i != correct) {
                previous[i] = randomIndex;
                choices[i].setText(sharedpreferences.getString(key.toLowerCase() + randomIndex, "null"));
            }
            else {
                previous[i] = correct;
            }
            choices[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.choice1:
                checkCorrectness(0);
                break;
            case R.id.choice2:
                checkCorrectness(1);
                break;
            case R.id.choice3:
                checkCorrectness(2);
                break;
            case R.id.choice4:
                checkCorrectness(3);
                break;
            case R.id.next:
                Bundle bundle = new Bundle();
                bundle.putInt("score", score);
                bundle.putLong("startTime", startTime);
                numberOfQuestions++;
                bundle.putInt("numberOfQuestions", numberOfQuestions);
                bundle.putInt("maxScore", maxScore);
                if(score != maxScore) {
                    QuestionFragment questionFragment = new QuestionFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    questionFragment.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.fragment, questionFragment).commit();
                }
                else {
                    showEnd();
                }
                break;
            case R.id.button:
                GameFragment gameFragment = new GameFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, gameFragment).commit();
        }
    }

    /**
     * Checks to see if the player picked the correct choice.
     * @param choice Which button the player pressed.
     */
    public void checkCorrectness(int choice){
        Button choices[] = new Button[4];
        choices[0] = (Button) view.findViewById(R.id.choice1);
        choices[1] = (Button) view.findViewById(R.id.choice2);
        choices[2] = (Button) view.findViewById(R.id.choice3);
        choices[3] = (Button) view.findViewById(R.id.choice4);
        TextView answer = (TextView) view.findViewById(R.id.answer);
        Button next = (Button) view.findViewById(R.id.next);
        if(choice == correct){
            answer.setText("Correct!");
            score = score + 1;
        }
        else {
            choices[choice].setBackgroundColor(Color.RED);
            answer.setText("Incorrect.");
        }
        showCorrect();
        answer.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        if(score == maxScore){
            next.setText("End");
        }
    }

    /**
     * Marks the correct choice as green and makes all buttons unclickable.
     */
    public void showCorrect(){
        Button choices[] = new Button[4];
        choices[0] = (Button) view.findViewById(R.id.choice1);
        choices[1] = (Button) view.findViewById(R.id.choice2);
        choices[2] = (Button) view.findViewById(R.id.choice3);
        choices[3] = (Button) view.findViewById(R.id.choice4);
        for(int i = 0; i < 4; i++){
            if(i == correct) {
                choices[i].setBackgroundColor(Color.GREEN);
            }
            choices[i].setOnClickListener(null);
        }
    }

    /**
     * This runs when the score reaches 10 and the game ends.
     */
    public void showEnd(){
        Bundle bundle = new Bundle();
        bundle.putInt("numberOfQuestions", numberOfQuestions);
        long totalTime = SystemClock.elapsedRealtime() - startTime;
        bundle.putLong("time", totalTime);
        bundle.putInt("maxScore", maxScore);
        EndFragment endFragment = new EndFragment();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        endFragment.setArguments(bundle);
        manager.beginTransaction().replace(R.id.fragment, endFragment).commit();
    }
}
