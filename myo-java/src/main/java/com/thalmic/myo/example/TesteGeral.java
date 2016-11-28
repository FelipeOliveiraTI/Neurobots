package com.thalmic.myo.example;

import javax.swing.JFrame;

import org.jfree.data.category.DefaultCategoryDataset;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;

public class TesteGeral extends JFrame {

	int teste = 0;
	static int valor = 0;
	static int tamando_janela_RMS = 31;

	static DefaultCategoryDataset ds_Dados_Brutos = new DefaultCategoryDataset();
	static DefaultCategoryDataset ds_Dados_RMS = new DefaultCategoryDataset();

	public static void main(String[] args) throws InterruptedException {

		try {
			Hub hub = new Hub("com.example.emg-data-sample");

			System.out.println("Attempting to find a Myo...");
			Myo myo = hub.waitForMyo(10000);

			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}

			System.out.println("Connected to a Myo armband!");
			myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
			DeviceListener dataCollector = new EmgDataCollector();
			hub.addListener(dataCollector);

			while (true) {
				hub.run(1000 / 20);
				// System.out.println(dataCollector.toString());
				
			
				String dadosString = dataCollector.toString();
				String[] eletrodo = dadosString.split("[\\W][ ]|\\[|\\]| ");
				
				try {
					int dado = Integer.parseInt(eletrodo[6]);
//					System.out.println(" "+eletrodo[0]+" "+eletrodo[1]+" "+eletrodo[2]+" "+eletrodo[3]+" "+eletrodo[4]+
//							" "+eletrodo[5]+" "+eletrodo[6]+" "+eletrodo[7]);
					
					System.out.println(dado);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}

	}
}
