package me.grishka.vezdekod.podcasts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class Utils{
	public static String getFileNameForContentUri(Context context, Uri uri){
		try(Cursor cursor=context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)){
			if(cursor!=null && cursor.moveToFirst()){
				return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
			}
		}
		return uri.getLastPathSegment();
	}
}
