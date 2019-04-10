package com.example.recyclerviewproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 */
public class DbAdapter {

    public static abstract class Todo implements BaseColumns {
        public static final String KEY_TITLE = "title";
        public static final String KEY_ROWID = "_id";
        private static final String TABLE_NAME = "todo";

        private static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + "( " +
                        _ID + " INTEGER PRIMARY KEY, "
                        + KEY_TITLE + " TEXT NOT NULL)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String GET_COUNT = "SELECT count(*) FROM " + TABLE_NAME + ";";
    }

    private static DbAdapter instance = null;

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper = null;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 4;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(Todo.CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(Todo.DELETE_TABLE);
            onCreate(db);
        }
    }

    //implements the abstract factory pattern
    public static DbAdapter getInstance(Context ctx) {
        if (instance == null) {
            instance = new DbAdapter(ctx);
        }
        return instance;
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    private DbAdapter(Context ctx) {
        this.mCtx = ctx;
        mDbHelper = null;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(mCtx);
            mDb = mDbHelper.getWritableDatabase();
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
        mDbHelper = null;
    }

    public void upgrade() {
        mDbHelper.onUpgrade(mDb, 1, 2);
    }


    public long createTodo(com.example.recyclerviewproject.ToDo current) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Todo.KEY_ROWID, current.getId());
        initialValues.put(Todo.KEY_TITLE, current.getTask());

        return mDb.insert(Todo.TABLE_NAME, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTodo(long rowId) {

        return mDb.delete(Todo.TABLE_NAME, Todo.KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllTodos() {
        return mDb.query(Todo.TABLE_NAME, new String[]{Todo.KEY_ROWID, Todo.KEY_TITLE}, null, null, null, null, null);

    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchTodo(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, Todo.TABLE_NAME, new String[]{Todo.KEY_ROWID, Todo.KEY_TITLE}, Todo.KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //Aquest metode s'usa perque si esta buit, el que es fa es afegir dades de proba desde una altre classe
    public boolean isEmpty() {
        Cursor cursor = mDb.rawQuery(Todo.GET_COUNT, null);
        cursor.moveToFirst();

        int i = cursor.getInt(0);
        Log.w("hola", "cursor num raws: " + i);
        return i <= 0;
    }
}
