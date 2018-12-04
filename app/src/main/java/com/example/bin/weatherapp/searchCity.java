package com.example.bin.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class searchCity extends AppCompatActivity {
    private SearchView mSearchView;
    private ListView mListView;
    private List<citys.City> mCities;
    private TextView sinCity;
    private TextView longCity;
    private BaseAdapter mBaseAdapter;
    private Intent mIntent;
    private citys.City resCity;
    private String cityName;
    private static final String TAG = "searchCity";

    class fetchCityTask extends AsyncTask<Void,Void,List<citys.City>>{
        @Override
        protected List<citys.City> doInBackground(Void... voids) {
            List<citys.City> fetchResult = null;
            try{
                if (cityName!=""){
                    fetchResult = weatherSpider.getCity(cityName);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return fetchResult;
        }

        @Override
        protected void onPostExecute(List<citys.City> cities) {
            Log.i(TAG, "onPostExecute: size is "+cities.size());
            mCities = cities;
            mBaseAdapter.notifyDataSetChanged();
            Log.i(TAG, "onPostExecute: in here");
        }
    }

    public boolean isCorrect(String s){
        String reg = "[\\u4e00-\\u9fa5]+";
        return s.matches(reg)&&!s.isEmpty();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        mCities = new ArrayList<>();

        mIntent = new Intent();

        mSearchView = findViewById(R.id.search_text);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (isCorrect(s)){
                    //搜索城市
                    cityName = s;
                    new fetchCityTask().execute();

                }
                return false;
            }
        });

        mBaseAdapter = new BaseAdapter(){
            @Override
            public int getCount() {
                return mCities==null?0:mCities.size();
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                LayoutInflater inflater = searchCity.this.getLayoutInflater();
                View v;
                if (view==null){
                    v = inflater.inflate(R.layout.single_city,null);
                }
                else{
                    v = view;
                }
                sinCity = v.findViewById(R.id.city_name);
                sinCity.setText(mCities.get(i).getLocation());

                longCity = v.findViewById(R.id.long_city_name);
                longCity.setText("\t"+mCities.get(i).getAdmin_area()+" "+mCities.get(i).getParent_city());

                Log.i(TAG, "getView: get view here");

                return v;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public Object getItem(int i) {
                return mCities.get(i);
            }
        };

        mListView = findViewById(R.id.list_view);

        mListView.setAdapter(mBaseAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
                resCity = mCities.get(i);
                Log.i(TAG, "onItemClick: "+resCity.getLocation());
            }
        });

        mListView = findViewById(R.id.list_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIntent.putExtra("resCity", new Gson().toJson(resCity));
        setResult(2,mIntent);
    }
}
