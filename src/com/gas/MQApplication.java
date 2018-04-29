package com.gas;

import java.io.IOException;
import java.util.Map;

public class MQApplication {

	public static void main(String[] args) {
	    
		System.out.println("Press CTRL+C to abort.");

		short mq2Channel = 0;
		double mq2RoClean = 4.4;
		int mq2RL = 20;
		MQ mq2;
		try {
			mq2 = new MQ(mq2Channel, mq2RoClean, mq2RL);

			while (true) {

				Map<String, Double> mq2GasValueMap = mq2.getMQValues();
				System.out.println("\r");
				System.out.println("\033[K");
				System.out.println("CO: " + (mq2GasValueMap.get("CO")) + " ppm");
				System.out.flush();
				
				if (mq2GasValueMap.get("CO") > 111) {
					//TODO alert();
					System.out.println("Alert Function");
				}
				Thread.sleep(5000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
