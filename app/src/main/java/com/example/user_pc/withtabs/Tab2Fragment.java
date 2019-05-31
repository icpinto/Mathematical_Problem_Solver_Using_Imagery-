package com.example.user_pc.withtabs;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAExamplePage;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WARelatedExample;
import com.wolfram.alpha.WASubpod;

public class Tab2Fragment extends Fragment {

    private TextView resultview;
    private static String appid="9HH8PL-88HVLXW5KE"; // App ID
    private String inputText="";
    private  String res="";
    private ProgressBar pb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_two, container, false);
        pb = (ProgressBar)view.findViewById(R.id.progressBar);

        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultview = (TextView)view.findViewById(R.id.textView);
    }
    //----------------------------------------------------------------------------check network availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //-------------------------------------------------------------background task for get result of the query from wolfram API
     private class WolframFeed extends AsyncTask<Void, Void, String>{
        private WAException exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("bundle","onpreexec");
            pb.setVisibility(View.VISIBLE);//--------------------------------------------------set progress bar

        }

        @Override
        protected String doInBackground(Void... params) {
            String result="";

            try {

                Log.e("TRYing", "wolfram try/");
                WAEngine engine = new WAEngine();

                engine.setAppID(appid);
                engine.addFormat("plaintext");

                WAQuery query = engine.createQuery();
                query.setInput(inputText);


                WAQueryResult queryResult = engine.performQuery(query);


                if (queryResult.isError()) {


                    String err= "Query error" + "  error code: " + queryResult.getErrorCode() + "  error message: " + queryResult.getErrorMessage();
                    Log.e("err: ",err);

                } else if (!queryResult.isSuccess()) {

                    Log.e("err: " ,"Query was not understood; no results available.");

                } else {

                    // ----------------------------------------------------------------------------Got a result.
                    Log.e("err: ","Successful query. Pods follow:\n");

                    for (WAPod pod : queryResult.getPods()) {

                        if (!pod.isError()) {
                            result+="\n";
                            for (WASubpod subpod : pod.getSubpods()) {
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {

                                        if(((WAPlainText) element).getText()!=""){
                                            result+=pod.getTitle()+ ": ";
                                            result+= ((WAPlainText) element).getText();
                                            result+="\n";
                                        }

                                    }
                                }
                            }
                        }

                        else {
                            Toast.makeText(getActivity(), "OOps!! No result Available..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }


            } catch (WAException e) {
                e.printStackTrace();

            }
            return result;
        }




        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v("bundle",result);
            if (pb!=null) {
                pb.setVisibility(View.GONE);//-------------------------------------------remove the progress bar
            }
            resultview.setText(result); //----------------------------------------set the result

        }


    }
    protected void displayReceivedData(String message)
    {
        //-------------------------------------------------------validate recieved data
        if (message!=null) {
            inputText = message;
        }
        if (isNetworkAvailable() ) {

            Log.v("bundle", inputText + " disp");

            new WolframFeed().execute();
        }
        else {
            Toast.makeText(getActivity(),"Network isn't available",Toast.LENGTH_LONG).show();
        }
    }
}
