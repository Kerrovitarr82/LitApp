package com.example.litapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ReadBooksFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor bookCursor;
    private SimpleCursorAdapter bookAdapter;
    private ListView lvBooks;

    public ReadBooksFragment() {
        super(R.layout.fragment_read_books);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(MainActivity.getContextOfApplication());
        lvBooks = view.findViewById(R.id.lvBooks);

        lvBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ReaderActivity.class);
                db = databaseHelper.getReadableDatabase();
                bookCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                        DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(l)});
                bookCursor.moveToFirst();
                String fileName = bookCursor.getString(2);
                intent.putExtra(ReaderActivity.EXTRA_NAME, fileName);
                startActivity(intent);
            }
        });
        lvBooks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.getContextOfApplication(), view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_popup_read, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete:
                                db = databaseHelper.getWritableDatabase();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DatabaseHelper.COLUMN_READ, 0);
                                db.update(DatabaseHelper.TABLE, contentValues, "_id = ?", new String[]{String.valueOf(l)});
                                onResume();
                                return true;
                            case R.id.addPlan:
                                db = databaseHelper.getWritableDatabase();
                                ContentValues contentValues2 = new ContentValues();
                                contentValues2.put(DatabaseHelper.COLUMN_READ, 0);
                                contentValues2.put(DatabaseHelper.COLUMN_PLANNED, 1);
                                db.update(DatabaseHelper.TABLE, contentValues2, "_id = ?", new String[]{String.valueOf(l)});
                                onResume();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        db = databaseHelper.getReadableDatabase();
        bookCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                DatabaseHelper.COLUMN_READ + " = 1", null);
        String[] headers = new String[]{DatabaseHelper.COLUMN_NAME};
        bookAdapter = new SimpleCursorAdapter(MainActivity.getContextOfApplication(), android.R.layout.two_line_list_item,
                bookCursor, headers, new int[]{android.R.id.text1}, 0);
        lvBooks.setAdapter(bookAdapter);

    }

    public String getFileName(Uri uri) {
        Context applicationContext = MainActivity.getContextOfApplication();
        Cursor returnCursor = applicationContext.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        return returnCursor.getString(nameIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        bookCursor.close();
    }
}
