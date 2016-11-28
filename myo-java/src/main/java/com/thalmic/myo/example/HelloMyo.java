package com.thalmic.myo.example;

import java.io.DataOutputStream;
import java.net.Socket;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.enums.PoseType;;


/*
 * JVM Arguments to help debug.
 -Xcheck:jni
 -XX:+ShowMessageBoxOnError
 -XX:+UseOSErrorReporting
 */
public class HelloMyo {
	public static void main(String[] args) {
		try {
			Hub hub = new Hub("com.example.hello-myo");

			System.out.println("Attempting to find a Myo...");
			Myo myo = hub.waitForMyo(10000);
			
		//	Process p = Runtime.getRuntime().exec("cmd.exe");
			
			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}

			System.out.println("Connected to a Myo armband!");

			hub.addListener(new AbstractDeviceListener() {
				@Override
				public void onPose(Myo myo, long timestamp, Pose pose) {
					// System.out.println(String.format("Myo switched to pose
					// %s.", pose.getType()));
					PoseType tipo = pose.getType();

					if (tipo != pose.getType().REST || tipo != pose.getType().UNKNOWN) {

						switch (tipo) {
						case DOUBLE_TAP:
							System.out.println("DOUBLE_TAP");
							break;
						case FINGERS_SPREAD:
							System.out.println("FINGERS_SPREAD");
							mover();
							break;
						case FIST:
							System.out.println("FIST");
							mover();
							break;
						case WAVE_IN:
							System.out.println("WAVE_IN");
							mover();
							break;
						case WAVE_OUT:
							System.out.println("WAVE_OUT");
							mover();
							break;

						default:
							break;
						}
					}

				}
			});

			while (true) {
				hub.run(1000 / 20);
			}
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void mover(){
		System.out.println("FIST");
		try {
			Socket soc = new Socket("localhost", 8080);
			DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
			dout.writeUTF("MOVER");
			dout.flush();
			dout.close();
			soc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}