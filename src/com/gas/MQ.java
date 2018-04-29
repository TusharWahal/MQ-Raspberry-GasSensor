/**
 * 
 */
package com.gas;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 
 *
 */
public class MQ {
	
   /* ######################### Hardware Related Macros #########################*/

	public int RL_VALUE = 20;//For MQ4
	
	public double RO_CLEAN_AIR_FACTOR = 4.4;//FOR MQ4 ~3.65 For MQ135

	/* ######################### Software Related Macros ######################### */
	static long CALIBARAION_SAMPLE_TIMES     = 50 ;      // define how many samples you are going to take in the calibration phase
	static long CALIBRATION_SAMPLE_INTERVAL  = 500 ;     // define the time interal(in milisecond) between each samples in the
	                                            	 // cablibration phase
	static long READ_SAMPLE_INTERVAL         = 50 ;      // define how many samples you are going to take in normal operation
	static long READ_SAMPLE_TIMES            = 5  ;      // define the time interal(in milisecond) between each samples in 
	                                              // normal operation
	

    /* ######################### Application Related Macros ###################### */
    static int GAS_LPG                      = 0;
    static int GAS_CO                       = 1;
    static int GAS_SMOKE                    = 2;
	
    
    public  double[] lpgCurve=	{1,1,1}; 
    public  double[] coCurve =	{1,1,1};
    public  double[] smokeCurve= {1,1,1};
    
	public short adcChannel = 0;
    
    public ADC adc;
    
    public double Ro=0;

	/**
	 * @throws IOException 
	 * @throws InterruptedException 
	 * 
	 */
	public MQ(short adcChannel,double RO_CLEAN_AIR_FACTOR,int RL_VALUE) throws IOException, InterruptedException {
		
		this.adc = new ADC();
		this.adcChannel=adcChannel;
		this.RO_CLEAN_AIR_FACTOR=RO_CLEAN_AIR_FACTOR;
		this.RL_VALUE=RL_VALUE;
		this.Ro=calibration();
		System.out.println("RO value for Sensor at channel "+adcChannel+" ="+this.Ro);
		
	}

	public double calibration() throws InterruptedException, IOException {
		double val = 0.0;
		System.out.println("CALIBRATING... ");

		for (int i = 0; i <= CALIBARAION_SAMPLE_TIMES; i++) {
			val += mqResistanceCalculation(adcRead());
			Thread.sleep(CALIBRATION_SAMPLE_INTERVAL);
		}

		val = val / CALIBARAION_SAMPLE_TIMES; // calculate the average value

		val = val / this.RO_CLEAN_AIR_FACTOR;      // divided by RO_CLEAN_AIR_FACTOR yields the Ro
											  // according to the chart in the datasheet
		return val;
	}
	public double mqResistanceCalculation(double adcReadValue) {

		return (double) (this.RL_VALUE * (1023.0 - adcReadValue) / adcReadValue);

	}	
	
	private double  adcRead() throws IOException
	{
		return (double) this.adc.getConversionValue(this.adcChannel);
	}
	
	public double readRSValue() throws InterruptedException, IOException {
		double rs = 0.0;

		for (int i = 0; i <= READ_SAMPLE_TIMES; i++) {
			rs += mqResistanceCalculation(adcRead());
			Thread.sleep(READ_SAMPLE_INTERVAL);
		}

		rs = rs / READ_SAMPLE_TIMES; // calculate the average value
		return rs;
	}

	public Map<String, Double> getMQValues() throws InterruptedException, IOException {

		Map<String, Double> gasValueMap = new HashMap<String, Double>();

		double rs = readRSValue();

		gasValueMap.put("CO",  getPPMFromStraightLine(rs / this.Ro, this.coCurve)) ;
		gasValueMap.put("LPG", getPPMFromStraightLine(rs / this.Ro, this.lpgCurve));
		
		return gasValueMap;

	}

	public double getPPMFromStraightLine(double rs_ro_ratio, double[] pcurve) {
		return (Math.pow(10, (((Math.log(rs_ro_ratio) - pcurve[1]) / pcurve[2]) + pcurve[0])));
	}
	
	
}
