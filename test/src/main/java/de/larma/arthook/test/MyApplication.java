package de.larma.arthook.test;

import android.app.Activity;
import android.app.Application;
import android.hardware.Camera;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.Date;

import de.larma.arthook.$;
import de.larma.arthook.ArtHook;
import de.larma.arthook.BackupIdentifier;
import de.larma.arthook.Hook;
import de.larma.arthook.OriginalMethod;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        ArtHook.hook(MyApplication.class);
        Log.d("MyApplication", "Dying soon...");
        try {
            Log.d("MyApplication", "..." + MyApplication.class.getDeclaredMethod("warGame"));
            Log.d("MyApplication", "..." + MyApplication.class.getDeclaredMethod("endGame"));
        } catch (NoSuchMethodException e) {
            Log.w(TAG, e);
        }
        try {
            Camera.open();
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        try {
            Log.d("MyApplication", "Time:" + System.currentTimeMillis());
            Log.d("MyApplication", "BackupTime:" + OriginalMethod.byOriginal(System.class
                    .getDeclaredMethod("currentTimeMillis")).invokeStatic());
        } catch (NoSuchMethodException e) {
            Log.w(TAG, e);
        }
    }

    public void pieceGame() {
    }

    /**
     * Sample hook of a public member method
     */
    @Hook("android.app.Activity->setContentView")
    public static void Activity_setContentView(Activity activity, int layoutResID) {
        Log.d(TAG, "Now you see me");
        OriginalMethod.by(new $() {}).invoke(activity, layoutResID);
        TextView text = ((TextView) activity.findViewById(R.id.helloWorldText));
        text.append("\n -- I am god");
        text.append("\n " + new Date().toString());
    }

    /**
     * Sample hook of a static method
     */
    @Hook("android.hardware.Camera->open")
    public static Camera Camera_open() {
        Log.d(TAG, "We do not allow Camera access");
        return null;
    }

    /**
     * Sample hook of a static native method
     */
    @Hook("java.lang.System->currentTimeMillis")
    public static long System_currentTimeMillis() {
        Log.d(TAG, "currentTimeMillis is much better in seconds :)");
        return (long) OriginalMethod.by(new $() {}).invokeStatic() / 1000L;
    }

    /**
     * Sample hook of a member method used internally by the system
     * <p/>
     * Note how we use the BackupIdentifier here, because using reflection APIs to access
     * reflection APIs will cause loops...
     */
    @Hook("java.lang.Class->getDeclaredMethod")
    @BackupIdentifier("Class_getDeclaredMethod")
    public static Method Class_getDeclaredMethod(Class cls, String name, Class[] params) {
        Log.d(TAG, "I'm hooked in getDeclaredMethod");
        if (name.contains("War") || name.contains("war")) {
            Log.d(TAG, "make piece not war!"); // This is a political statement!
            name = name.replace("War", "Piece").replace("war", "piece");
        }
        return OriginalMethod.by("Class_getDeclaredMethod").invoke(cls, name, params);
    }
}