package com.example.uidesignlab;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class UserProvider extends ContentProvider {

    public static final String AUTHORITY =
            "com.example.uidesignlab.provider";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/users");

    private static final int USERS = 1;
    private static final int USER_ID = 2;

    private static final UriMatcher URI_MATCHER =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, "users", USERS);
        URI_MATCHER.addURI(AUTHORITY, "users/#", USER_ID);
    }

    private UserDao userDao;

    @Override
    public boolean onCreate() {

        userDao = AppDatabase.getInstance(getContext()).userDao();

        return true;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        Cursor cursor;

        switch (URI_MATCHER.match(uri)) {

            case USERS:
                cursor = userDao.getAllCursor();
                break;

            case USER_ID:
                long id = ContentUris.parseId(uri);
                cursor = userDao.getByIdCursor(id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri
        );

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (URI_MATCHER.match(uri)) {

            case USERS:
                return "vnd.android.cursor.dir/vnd."
                        + AUTHORITY + ".users";

            case USER_ID:
                return "vnd.android.cursor.item/vnd."
                        + AUTHORITY + ".users";

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (URI_MATCHER.match(uri) != USERS) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        String name = values == null
                ? null
                : values.getAsString("name");

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        long id = userDao.insert(new UserRecord(name));

        getContext().getContentResolver().notifyChange(
                CONTENT_URI, null
        );

        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {

        if (URI_MATCHER.match(uri) != USER_ID) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        String name = values == null
                ? null
                : values.getAsString("name");

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        long id = ContentUris.parseId(uri);

        int rows = userDao.updateName(id, name);

        getContext().getContentResolver().notifyChange(
                CONTENT_URI, null
        );

        return rows;
    }

    @Override
    public int delete(
            Uri uri,
            String selection,
            String[] selectionArgs) {

        if (URI_MATCHER.match(uri) != USER_ID) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        long id = ContentUris.parseId(uri);

        int rows = userDao.deleteById(id);

        getContext().getContentResolver().notifyChange(
                CONTENT_URI, null
        );

        return rows;
    }
}