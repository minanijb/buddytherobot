package net.ptidej.buddytherobot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

public class MainActivity extends BuddyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public void onSDKReady() {
        enableWheels(true);
        run();
    }

    @Override
    public void onDestroy() {
        enableWheels(false);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        enableWheels(false);
        super.onStop();
    }
    private void enableWheels(final boolean shouldEnable) {
        final int leftWheelStatus, rightWheelStatus;
        if (shouldEnable) {
            leftWheelStatus = 1;
            rightWheelStatus = 1;
        } else {
            leftWheelStatus = 0;
            rightWheelStatus = 0;
        }
        BuddySDK.USB.enableWheels(leftWheelStatus, rightWheelStatus, new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("", "Wheels status is: " + s);
            }
            @Override
            public void onFailed(String s) throws RemoteException {
                Log.i("", "Wheels status is:" + s);
            }
        });
    }
    private void run() {
        final float speed,rotationSpeed, distance;
        rotationSpeed=30;
        speed = 0.5f;
        distance = 0.5f;
        //call of the function to make buddy go forward or backwards
        BuddySDK.USB.moveBuddy(speed, distance, new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(final String s1) throws RemoteException {
                try {
                    Thread.sleep(5000);
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (s1.toUpperCase().contains("_FINISHED")){
                    BuddySDK.USB.moveBuddy(speed * (-1), distance * 0.5f, new IUsbCommadRsp.Stub() {
                        @Override
                        public void onSuccess(final String s2) throws RemoteException {
                            if(s2.toUpperCase().contains("_FINISHED")) {
                                BuddySDK.USB.rotateBuddy(rotationSpeed, 180, new IUsbCommadRsp.Stub() {
                                    @Override
                                    public void onSuccess(final String s3) throws RemoteException {
                                        if(s3.toUpperCase().contains("_FINISHED")) {
                                            BuddySDK.USB.moveBuddy(speed, distance, new IUsbCommadRsp.Stub() {
                                                @Override
                                                public void onSuccess(final String s) throws RemoteException {
                                                    Log.i("","End of fourth action");
                                                    //  Toast.makeText(MainActivity.this, "Next step here", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailed(final String s) throws RemoteException {
                                                    //Toast.makeText(MainActivity.this, "Failed fourth", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } //end 3rd if
                                    }

                                    @Override
                                    public void onFailed(final String s) throws RemoteException {
                                        Toast.makeText(MainActivity.this, "Failed third", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } //end 2nd if
                        }

                        @Override
                        public void onFailed(final String s) throws RemoteException {
                            Toast.makeText(MainActivity.this, "Failed second", Toast.LENGTH_SHORT).show();
                        }
                    });
            } //end 1st if
            }

            @Override
            public void onFailed(final String s) throws RemoteException {
                Toast.makeText(MainActivity.this, "Failed first", Toast.LENGTH_SHORT).show();
            }
        });
    }

}