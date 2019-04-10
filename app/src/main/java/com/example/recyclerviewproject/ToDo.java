package com.example.recyclerviewproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

import static java.lang.System.in;

public class ToDo {

    private int id;
    private String task;

    public ToDo(String task){
        this.task=task;
        this.id= new Random().nextInt(99999);
    }


    public ToDo(int id,String task){
       this.id=id;
       this.task=task;
    }


    public String getTask(){
        return task;
    }

    public void setTask(String task){
        this.task= task;
    }

    public int getId(){
        return this.id;
    }

}
