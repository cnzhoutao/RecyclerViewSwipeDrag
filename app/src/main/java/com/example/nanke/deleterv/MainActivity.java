package com.example.nanke.deleterv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private List<String> mData=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        rv= (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        RvAdapter adapter=new RvAdapter(mData);
        ItemTouchHelper helper=new ItemTouchHelper(new DragItem(adapter));

        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        helper.attachToRecyclerView(rv);

        Log.d("list",mData.size()+"");
    }
    private void init(){
        for(int i=0;i<30;i++){
            mData.add("这是第"+i+"条数据");
        }
    }
}
