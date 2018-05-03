package com.gas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MQApplication {

	public static void main(String[] args) {

		System.out.println("Press CTRL+C to abort.");

		short mq4Channel = 0;

		double mq4RoClean = 4.4;

		int mq4RL = 20;

		MQ mq4;

		// short mq135Channel = 1;

		// double mq135RoClean = 3.65;

		// int mq135RL = 10;

		// MQ mq135;

		try {

			mq4 = new MQ(mq4Channel, mq4RoClean, mq4RL);

			// mq135= new MQ(mq135Channel,mq135RoClean,mq135RL);
			File file = new File("/home/pi/Desktop/test.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			while (true) {

				Map<String, Double> mq4GasValueMap = mq4.getMQValues();

				// Map<String, Double> mq135GasValueMap = mq135.getMQValues();

				// Print MQ 4

				System.out.println("MQ - 4 \n");

				System.out.print("CO: " + (mq4GasValueMap.get("CO")) + " ppm \t");

				System.out.print("LPG: " + (mq4GasValueMap.get("LPG")) + " ppm \t");

				System.out.print("CH4: " + (mq4GasValueMap.get("CH4")) + " ppm \t");

				System.out.print("Smoke: " + (mq4GasValueMap.get("Smoke")) + " ppm \t");

				// Print MQ 135

				// System.out.println("MQ - 135");

				// System.out.print("CO2: " + (mq135GasValueMap.get("CO2")) + " ppm \t");

				// System.out.print("NH4: " + (mq135GasValueMap.get("NH4")) + " ppm \t");

				// System.out.print("MQ-135 CO: " + (mq135GasValueMap.get("MQ135CO")) + " ppm
				// \t");

				if (!file.exists()) {
					file.createNewFile();
				}
				Writer w = new BufferedWriter(new FileWriter(file, true));
				w.write("LPG :" + mq4GasValueMap.get("LPG").toString() + "\n");
				w.close();

				StringBuffer jsonString = new StringBuffer();
				URL url = new URL("https://gasstation-fd98.restdb.io/rest/gasstation/5adf8158b8552b5200003704");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("x-apikey", "54cd27243fafb6350f8eccdb39447a9fb4573");
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					jsonString.append(line);
				}
				br.close();
				connection.disconnect();
				//convert to json element using GSON
				System.out.println(jsonString.toString());

				if (mq4GasValueMap.get("LPG") > 1) {

					System.out.println("\n****ALERT****\n");
					Runtime runTime = Runtime.getRuntime();
					runTime.exec("gpio mode 1 out");
					runTime.exec("gpio write 1 1");
					Thread.sleep(2000);
					runTime.exec("gpio write 1 0");
				}

				Thread.sleep(1000);
			}

		} catch (IOException e) {

			e.printStackTrace();

		} catch (InterruptedException e) {
			e.printStackTrace();

		}

	}

	public static String sendPostRequest(String requestUrl, String payload) {

		StringBuffer jsonString = new StringBuffer();

		try {

			URL url = new URL(requestUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);

			connection.setDoOutput(true);

			connection.setRequestMethod("POST");

			connection.setRequestProperty("Accept", "application/json");

			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");

			writer.write(payload);

			writer.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line;

			while ((line = br.readLine()) != null) {

				jsonString.append(line);

			}

			br.close();

			connection.disconnect();

		} catch (Exception e) {

			throw new RuntimeException(e.getMessage());

		}

		return jsonString.toString();

	}

}
