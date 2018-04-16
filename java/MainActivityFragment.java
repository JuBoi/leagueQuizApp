package com.bryanju2.myproject;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener{

    View view;
    MediaPlayer mp;
    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_main, container, false);
        mp = MediaPlayer.create(getActivity(), R.raw.wow);

        Button play = (Button) view.findViewById(R.id.playButton);
        play.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.playButton:
                mp.start();
                StartGameFragment();
                break;
        }
    }

    /**
     * Starts a game fragment
     */
    public void StartGameFragment(){
        GameFragment gameFragment = new GameFragment();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment, gameFragment).commit();
    }
}
