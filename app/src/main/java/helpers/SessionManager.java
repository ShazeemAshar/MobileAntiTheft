package helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    Context context;

    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "MobileAntiTheftLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String INTRO_WATCHED = "isIntroWatched";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setIntroWatched(boolean isIntroWatched){
        editor.putBoolean(INTRO_WATCHED,isIntroWatched);
        editor.commit();
    }
    public boolean isIntroWatched(){
        return pref.getBoolean(INTRO_WATCHED, false);
    }
}