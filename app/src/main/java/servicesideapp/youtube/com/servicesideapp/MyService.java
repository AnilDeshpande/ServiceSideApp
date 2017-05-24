package servicesideapp.youtube.com.servicesideapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by anildeshpande on 19/08/16.
 */
public class MyService extends Service {

    private static final String TAG=MyService.class.getSimpleName();

    private int mRandomNumber;
    private boolean mIsRandomGeneratorOn;

    private final int MIN=0;
    private final int MAX=100;

    public static final int GET_RANDOM_NUMBER_FLAG=0;

    private class RandomNumberRequestHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case GET_RANDOM_NUMBER_FLAG:
                    Message  messageSendRandomNumber=Message.obtain(null, GET_RANDOM_NUMBER_FLAG);
                    messageSendRandomNumber.arg1=getRandomNumber();
                    try{
                        msg.replyTo.send(messageSendRandomNumber);
                    }catch (RemoteException e){
                        Log.i(TAG,""+e.getMessage());
                    }
            }
            super.handleMessage(msg);
        }
    }

    private Messenger randomNumberMessenger=new Messenger(new RandomNumberRequestHandler());


    @Override
    public IBinder onBind(Intent intent) {
        return randomNumberMessenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRandomNumberGenerator();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIsRandomGeneratorOn =true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRandomNumberGenerator();
            }
        }).start();
        return START_STICKY;
    }

    private void startRandomNumberGenerator(){
        while (mIsRandomGeneratorOn){
            try{
                Thread.sleep(1000);
                if(mIsRandomGeneratorOn){
                    mRandomNumber =new Random().nextInt(MAX)+MIN;
                    Log.i(TAG,"Random Number: "+mRandomNumber);
                }
            }catch (InterruptedException e){
                Log.i(TAG,"Thread Interrupted");
            }

        }
    }

    private void stopRandomNumberGenerator(){
        mIsRandomGeneratorOn =false;
        Toast.makeText(getApplicationContext(),"Service Stopped",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    public int getRandomNumber(){
        return mRandomNumber;
    }


}
