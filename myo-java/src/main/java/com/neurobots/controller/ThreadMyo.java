package com.neurobots.controller;

import java.text.DecimalFormat;

import com.thalmic.myo.example.EMG_Grafico;

public class ThreadMyo extends Thread {
	static EMG_Grafico emg;
	
	public ThreadMyo(EMG_Grafico emg){
		this.emg = emg;
		
	}
	public void run() {
		
		try {
			if (this.emg.myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}
			while (true) {
				this.emg.hub.run(1000 / 20);

				this.emg.dados_String = this.emg.dataCollector.toString();
				this.emg.eletrodo = this.emg.dados_String.split("[\\W][ ]|\\[|\\]");
				this.emg.chaveColunaString = (this.emg.valor++) + "";
				int chaveColunaInt = Integer.parseInt(this.emg.chaveColunaString);
				this.emg.df = new DecimalFormat("##");
				this.emg.controle_porcetagem = this.emg.df.format((chaveColunaInt * this.emg.porcentagem_Limiar));
				int controle_porcetagem_Int = Integer.parseInt(this.emg.controle_porcetagem);

				try {

					this.emg.setDadoEletrodo_valor_Tratado_Integer(Integer.parseInt(this.emg.eletrodo[this.emg.eletrodo_MYO]));

					this.emg.RMS = this.emg.calcular_RMS(this.emg.lista_valores_RMS_x, this.emg.janela_dados_RMS_X, chaveColunaInt,
							this.emg.dado_Eletrodo_valor_Tratado_Integer);

					this.emg.setValorRealGeral(this.emg.dado_Eletrodo_valor_Tratado_Integer);
					this.emg.valor_Real_Positivo = this.emg.pegarApenasPositivos(this.emg.limiar_List_Positivo,
							this.emg.dado_Eletrodo_valor_Tratado_Integer);
					this.emg.valor_Max_Positivo = this.emg.pegarMaiorValor_EMG(this.emg.limiar_List_Positivo);
					this.emg.valor_Maximo_RMS = this.emg.pegarValorMaximoRMS(this.emg.lista_valores_RMS, this.emg.RMS);
					this.emg.valor_Minimo_RMS = this.emg.pegarValorMinimoRMS(this.emg.lista_valores_RMS, this.emg.RMS);

					this.emg.lb_valor_Max.setText("MÁXIMO: " + this.emg.valor_Maximo_RMS);
					this.emg.lb_valor_geral_real.setText("GERAL: " + (int) this.emg.RMS);
					this.emg.lb_valor_limiar.setText("LIMIAR (%): " + this.emg.limiar_Porcentagem);
					this.emg.removerDadosAntigosGrafico(chaveColunaInt, controle_porcetagem_Int);

					this.emg.ds_Dados_RMS.addValue(this.emg.RMS, "maximo", this.emg.chaveColunaString);
					this.emg.ds_Dados_Brutos.addValue(this.emg.dado_Eletrodo_valor_Tratado_Integer, "maximo", this.emg.chaveColunaString);

					this.emg.aplicarLimiarComPortentagem((int) this.emg.RMS, this.emg.limiar_Porcentagem, this.emg.controle_mover);

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
