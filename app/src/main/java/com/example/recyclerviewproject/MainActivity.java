package com.example.recyclerviewproject;

import android.database.Cursor;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText todoText;
    private Button addButton;
    private RecyclerView todoList;

    private ArrayList<ToDo> data;
    private MyAdapter mAdapter;


    private DbAdapter mDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addButton= findViewById(R.id.button_add_todo);
        todoText=findViewById(R.id.todo_input);
        todoList=findViewById(R.id.todo_list);




        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,2);
        todoList.setLayoutManager(mLayoutManager);



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task=todoText.getText().toString();
                if(task.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Empty text");
                    builder.create().show();
                }else{
                    ToDo todo= new ToDo(task);
                    mDBAdapter.createTodo(todo);
                    data.add(todo);
                    mAdapter.notifyDataSetChanged();
                    todoText.setText("");
                }
            }
        });

        mDBAdapter = DbAdapter.getInstance(getApplicationContext());
        mDBAdapter.open();
        if(mDBAdapter.isEmpty()){
            mDBAdapter.createTodo(new ToDo("ToDo1") );
            mDBAdapter.createTodo(new ToDo("ToDo2") );
            mDBAdapter.createTodo(new ToDo("ToDo3") );
            mDBAdapter.createTodo(new ToDo("ToDo4") );
        }

        fetchData();

    }

    private void fetchData(){

        Cursor todoData = mDBAdapter.fetchAllTodos();

        this.data=new ArrayList<>();

        for(todoData.moveToFirst();!todoData.isAfterLast();todoData.moveToNext()){
            int id = todoData.getInt(0);
            String task = todoData.getString(1);
            this.data.add(new ToDo(id,task));
        }

        mAdapter= new MyAdapter(data);
        todoList.setAdapter(mAdapter);
    }





}
