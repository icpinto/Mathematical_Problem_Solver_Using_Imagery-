package com.example.user_pc.withtabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class Tab3Fragment extends Fragment {
    SendMessage2 SM2;
    @Nullable

    private static ListView list_view;
    private static String[] sample = new String[]{"x+1=2", "a+4=6"};
    private DbHelper dbHelper;
    private SimpleCursorAdapter adapter;
    final String[] from =new String[] {  DbHelper.col_2};

    private ArrayList<String > listItem;
    private ArrayAdapter adpt;
    private ViewPager viewPager;
    private ImageButton deletebtn;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_three, container, false);
        dbHelper= new DbHelper(getContext());
        listItem = new ArrayList<>();
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        deletebtn = (ImageButton)rootView.findViewById(R.id.deltebtn);
        deletebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dbHelper.deleteHistory();
            }
        });
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list_view = (ListView) view.findViewById(R.id.list);
        listView();


    }

    //-------------------------------------get queries from the database and add to listview
    public void listView() {
        Log.v("bundle","I am here tab3 listview");
        Cursor cursor = dbHelper.getAllPersons();
        if(cursor!=null && cursor.getCount() != 0){
            while (cursor.moveToNext()){
                listItem.add(cursor.getString(0));
            }
             adpt = new ArrayAdapter<>(getContext(), R.layout.list_view,listItem);


            Log.v("bundle","after get adapter");
        list_view.setAdapter(adpt);
        list_view.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String)list_view.getItemAtPosition(position);
                        SM2.sendData2(val); //------------------ send selected query data to Tab2fragment
                        viewPager.setCurrentItem(1);
                    }
                }
        );}

    }
    interface SendMessage2 {
        void sendData2(String message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM2 = (SendMessage2) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }
}

