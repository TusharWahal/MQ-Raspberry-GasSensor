package com.gas;

import java.io.IOException;
import java.util.Map;

public class MQApplication {

	public static void main(String[] args) {
	    
		System.out.println("Press CTRL+C to abort.");

		short mq4Channel = 0;
		double mq4RoClean = 4.4;
		int mq4RL = 20;
		MQ mq4;
		
		short mq135Channel = 1;
		double mq135RoClean = 3.65;
		int mq135RL = 20;
		MQ mq135;
		
		try {
			mq4 = new MQ(mq4Channel, mq4RoClean, mq4RL);
			mq135= new MQ(mq135Channel,mq135RoClean,mq135RL);
			
			while (true) {

				Map<String, Double> mq4GasValueMap = mq4.getMQValues();
				Map<String, Double> mq135GasValueMap = mq135.getMQValues();
				System.out.println("\r");
				System.out.println("\033[K");
				System.out.println("CO: " + (mq4GasValueMap.get("CO")) + " ppm");
				System.out.println("LPG: " + (mq135GasValueMap.get("LPG")) + " ppm");

				System.out.flush();
				
				if (mq4GasValueMap.get("CO") > 111) {
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
