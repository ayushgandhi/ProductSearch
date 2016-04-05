package pict.ama.com.beproone;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 4/2/16.
 */
public class StoredData
{
        private static final String PREF_NAME = "Twigme";
        public static final String UDATA = "udata";
        public static final String LOGINTOKEN = "loginToken";
        public static String get(Context context, String key)
        {
            SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            return CrypUtil.decrypt(pref.getString(key, null));
        }
        public static void set(Context context, String key, String value)
        {
            SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(key, CrypUtil.encrypt(value));
            editor.commit();
        }
        public static void remove(Context context, String key)
        {
            SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove(key);
            editor.commit();
        }
}