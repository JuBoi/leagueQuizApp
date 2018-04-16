package com.bryanju2.myproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.bryanju2.myproject.R.id.number;


/**
 * A simple {@link Fragment} subclass.
 */
public class EndFragment extends Fragment implements View.OnClickListener{
    private static final String MyDATA = "MyData";

    View view;

    private long time;

    private int numberOfQuestions;

    private double highScore;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public EndFragment() {
        // Required empty public constructor
        time = 0;
        numberOfQuestions = 0;
        highScore = 0;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_end, container, false);
        sharedpreferences = getSharedPreferences();
        editor = sharedpreferences.edit();

        time = getArguments().getLong("time");
        numberOfQuestions = getArguments().getInt("numberOfQuestions");
        int maxScore = getArguments().getInt("maxScore");

        float doubleTime = (float) time;
        doubleTime /= 1000;
        float high = doubleTime;
        TextView timeView = (TextView) view.findViewById(R.id.time);
        timeView.setText("Time elapsed: " + Float.toString(doubleTime) + " seconds");

        float oldHigh = getOldHigh(maxScore);
        if(high > oldHigh){
            high = oldHigh;
        }
        if(maxScore == 10){
            editor.putFloat("high10", high);
        }
        else {
            editor.putFloat("high20", high);
        }
        TextView highScore = (TextView) view.findViewById(R.id.highScore);
        highScore.setText("The high score is: " + Float.toString(high) + " seconds");

        TextView number = (TextView) view.findViewById(R.id.number);
        number.setText("Number of Questions attempted: " + Integer.toString(numberOfQuestions));

        Button newGame = (Button) view.findViewById(R.id.newgame);
        newGame.setOnClickListener(this);


        editor.commit();
        return view;
    }

    public SharedPreferences getSharedPreferences() {
        return this.getContext().getSharedPreferences(MyDATA, Context.MODE_PRIVATE);
    }

    public float getOldHigh(int maxScore){
        if(maxScore == 10){
            return sharedpreferences.getFloat("high10", 1000);
        }
        else {
            return sharedpreferences.getFloat("high20", 1000);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.newgame:
                GameFragment gameFragment = new GameFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, gameFragment).commit();
        }
    }
}
