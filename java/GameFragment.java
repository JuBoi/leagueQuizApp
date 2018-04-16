package com.bryanju2.myproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment implements View.OnClickListener{
    final private String patch_number = "7.23.1";
    final private String champion_url = "http://ddragon.leagueoflegends.com/cdn/" + patch_number + "/data/en_US/champion.json";
    final private String individual_url = "http://ddragon.leagueoflegends.com/cdn/" + patch_number + "/data/en_US/champion/";
    private static final String MyDATA = "MyData";

    //The json that holds the id and name of all champions
    String championJson;

    //Lets the main thread know when all of the data has been pulled from the json
    boolean initialReady;

    View view;

    //The json object created from championJson
    JSONObject obj = null;

    //ArrayLists for champions names and their q, w, e, and r abilities.
    ArrayList<String> champions = new ArrayList<String>();
    ArrayList<String> q = new ArrayList<String>();
    ArrayList<String> w = new ArrayList<String>();
    ArrayList<String> e = new ArrayList<String>();
    ArrayList<String> r = new ArrayList<String>();

    //The count of champions in the game
    final double COUNT = 137.00;
    int count;

    int maxScore;
    int musicOn;

    //The variables responsible for handling the local storage
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    /**
     * required public constructor
     */
    public GameFragment() {
        championJson = "";
        initialReady = false;
        count = 1;
        maxScore = 10;
        musicOn = 1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_game, container, false);

        //Getting/Setting up the variables from the bundle

        //Initializing the sharedpreferences
        sharedpreferences = getSharedPreferences();
        editor = sharedpreferences.edit();
        String patch = sharedpreferences.getString("patch", "nope");
        maxScore = sharedpreferences.getInt("maxScore", 10);
        musicOn = sharedpreferences.getInt("musicOn", 1);

        //Getting buttons
        Button getJson = (Button) view.findViewById(R.id.jsonButton);
        Button test = (Button) view.findViewById(R.id.test);
        Button actualTest = (Button) view.findViewById(R.id.actualTest);
        Button options = (Button) view.findViewById(R.id.options);

        //Grabbing the data from online
        if(!patch.equals(patch_number)){
            new jsonRetriever().execute(champion_url);
            while (!initialReady) {
            }
            try {
                grabChampionData();
                while(count < COUNT){
                }
                storeData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Grabbing the data from local storage
        else {
            loadData();
        }

        //Setting the button's onClickListeners
        test.setOnClickListener(this);
        getJson.setOnClickListener(this);
        actualTest.setOnClickListener(this);
        options.setOnClickListener(this);

        return view;
    }

    /**
     * Getting the sharedPreferences
     * @return A sharedPreferences object
     */
    public SharedPreferences getSharedPreferences() {
        return this.getContext().getSharedPreferences(MyDATA, Context.MODE_PRIVATE);
    }

    /**
     * Grabs the name and id of the champion from the initial json file, then runs an
     * Async Thread to go to the new url, grab that json, and gets the champion's
     * abilites.
     * @throws JSONException
     */
    public void grabChampionData() throws JSONException {
        obj = new JSONObject(championJson);
        JSONObject data = obj.getJSONObject("data");
        Iterator keys = data.keys();
        while (keys.hasNext()) {
            Object key = keys.next();
            String name = data.getJSONObject((String) key).getString("name");
            champions.add(name);
            String id = data.getJSONObject((String) key).getString("id");
            new abilityRetriever().execute(individual_url + id + ".json", id);
        }
        editor.putString("patch", patch_number);
        editor.commit();
    }


    @Override
    /**
     * The onClick Listener for buttons.
     */
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.jsonButton:
                while(count < COUNT) {

                }
                int rando = (int)(Math.random() * (COUNT - 1));
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                text1.setText(champions.get(rando));

                TextView textQ = (TextView) view.findViewById(R.id.textQ);
                TextView textW = (TextView) view.findViewById(R.id.textW);
                TextView textE = (TextView) view.findViewById(R.id.textE);
                TextView textR = (TextView) view.findViewById(R.id.textR);
                textQ.setText(q.get(rando));
                textW.setText(w.get(rando));
                textE.setText(e.get(rando));
                textR.setText(r.get(rando));
                break;
            case R.id.test: //Start a new game with a score limit of 10
                Bundle bundle = new Bundle();
                bundle.putInt("numberOfQuestions", 0);
                bundle.putInt("maxScore", maxScore);
                bundle.putInt("musicOn", musicOn);
                long time = SystemClock.elapsedRealtime();
                bundle.putLong("startTime", time);
                QuestionFragment questionFragment = new QuestionFragment();
                questionFragment.setArguments(bundle);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, questionFragment).commit();
                break;
            case R.id.actualTest: //Start a quick test with a score limit of 10, but the score is already set to 7 to allow a quick ending
                Bundle bundle1 = new Bundle();
                bundle1.putInt("numberOfQuestions", 7);
                bundle1.putInt("score", 7);
                bundle1.putInt("maxScore", maxScore);
                bundle1.putInt("musicOn", musicOn);
                long time1 = SystemClock.elapsedRealtime();
                bundle1.putLong("startTime", time1);
                QuestionFragment questionFragment1 = new QuestionFragment();
                questionFragment1.setArguments(bundle1);
                FragmentManager manager1 = getActivity().getSupportFragmentManager();
                manager1.beginTransaction().replace(R.id.fragment, questionFragment1).commit();
                break;
            case R.id.options:
                Bundle bundle2 = new Bundle();
                bundle2.putInt("maxScore", maxScore);
                bundle2.putInt("musicOn", musicOn);
                OptionFragment optionFragment = new OptionFragment();
                optionFragment.setArguments(bundle2);
                FragmentManager manager2 = getActivity().getSupportFragmentManager();
                manager2.beginTransaction().replace(R.id.fragment, optionFragment).commit();
                break;
        }
    }

    /**
     * Grabs the initial json
     */
    public class jsonRetriever extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            JsonParser getJson = new JsonParser(params[0]);
            try {
                championJson = getJson.parseJson();
                initialReady = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return championJson;
        }


    }

    /**
     * Async Thread that grabs the abilities of one champion
     */
    public class abilityRetriever extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            JsonParser getJson = new JsonParser(params[0]);
            String id = params[1];
            try {
                String jsonString = getJson.parseJson();
                JSONObject champion = new JSONObject(jsonString);
                JSONArray abilities = champion.getJSONObject("data").getJSONObject(id).getJSONArray("spells");
                q.add(abilities.getJSONObject(0).getString("name"));
                w.add(abilities.getJSONObject(1).getString("name"));
                e.add(abilities.getJSONObject(2).getString("name"));
                r.add(abilities.getJSONObject(3).getString("name"));
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return championJson;
        }
    }

    /**
     * Stores the data into local storage.
     */
    public void storeData(){
        int size = (int) COUNT - 1;
        for(int i = 0; i < size; i++){
            editor.putString("q" + i, q.get(i));
            editor.putString("w" + i, w.get(i));
            editor.putString("e" + i, e.get(i));
            editor.putString("r" + i, r.get(i));
            editor.putString(Integer.toString(i), champions.get(i));
        }
        editor.commit();
    }

    /**
     * Loads the data from local storage.
     */
    public void loadData(){
        int size = (int) COUNT - 1;
        Button getJson = (Button) view.findViewById(R.id.jsonButton);
        for(int i = 0; i < size; i++){
            champions.add(sharedpreferences.getString(Integer.toString(i), "null"));
            q.add(sharedpreferences.getString("q" + i, "null"));
            w.add(sharedpreferences.getString("w" + i, "null"));
            e.add(sharedpreferences.getString("e" + i, "null"));
            r.add(sharedpreferences.getString("r" + i, "null"));
            count++;
        }
        initialReady = true;
    }
}
