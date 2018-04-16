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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class OptionFragment extends Fragment implements View.OnClickListener{
    private static final String MyDATA = "MyData";

    ToggleButton mus;

    View view;

    int music;

    int changeNum;

    int maxScore;

    TextView max;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public OptionFragment() {
        // Required empty public constructor
        music = 1;
        changeNum = 0;
        maxScore = 10;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_option, container, false);

        sharedpreferences = getSharedPreferences();
        editor = sharedpreferences.edit();

        setUpToggle();

        max = (TextView) view.findViewById(R.id.max);
        Button change = (Button) view.findViewById(R.id.change);
        Button back = (Button) view.findViewById(R.id.back);

        change.setOnClickListener(this);
        back.setOnClickListener(this);


        return view;
    }

    public SharedPreferences getSharedPreferences() {
        return this.getContext().getSharedPreferences(MyDATA, Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.change:
                changeNum++;
                if(changeNum % 2 == 0){
                    maxScore = 10;
                }
                else {
                    maxScore = 20;
                }
                max.setText(Integer.toString(maxScore) + " questions");
                break;
            case R.id.back:
                editor.putInt("maxScore", maxScore);
                editor.putInt("musicOn", music);
                editor.commit();
                GameFragment gameFragment = new GameFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, gameFragment).commit();
        }
    }

    public void setUpToggle(){
        mus = (ToggleButton) view.findViewById(R.id.toggleButton);
        mus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    music = 1;
                }else{
                    music = 0;
                }
            }
        });
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        if(on){
            music = 1;
        }
        else {
            music = 0;
        }
    }
}
