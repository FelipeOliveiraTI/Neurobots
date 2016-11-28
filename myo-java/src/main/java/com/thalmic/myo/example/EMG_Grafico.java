package com.thalmic.myo.example;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.neurobots.controller.ThreadMyo;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;

public class EMG_Grafico extends JFrame {

	private static final long serialVersionUID = 1L;
	public static int controle_mover = 0;
	public static int valor = 0;
	public static int contador = 0;
	public static int cont = 0;

	public static int tamando_janela_RMS = 31;
	public static int eletrodo_MYO = 1;

	public static DefaultCategoryDataset ds_Dados_Brutos = new DefaultCategoryDataset();
	public static DefaultCategoryDataset ds_Dados_RMS = new DefaultCategoryDataset();

	public static ArrayList<Integer> limiar_List_Positivo = new ArrayList<Integer>();
	public static ArrayList<Integer> lista_valores_RMS_x = new ArrayList<Integer>();
	public static ArrayList<Integer> lista_valores_RMS = new ArrayList<Integer>();
	public static ArrayList<Integer> janela_dados_RMS_X = new ArrayList<Integer>();
	public static ArrayList<Integer> mover_exo_list = new ArrayList<Integer>();

	public static int valor_Real_Geral;
	public static int valor_Real_Positivo;
	public static int valor_Max_Positivo;
	public static int valor_Minimo_RMS;
	public static int valor_Maximo_RMS;
	public static int limiar_Porcentagem = 100;
	public static int dado_Eletrodo_valor_Tratado_Integer;
	public static double porcentagem_Limiar = 0.25;
	public static String selected;
	public static double RMS;
	public static int janelaControleGrafico = 100;

	public static JFreeChart grafico_Bruto;
	public static JFreeChart grafico_RMS;
	public static JFrame janela;
	public static JPanel painel_dados_brutos;
	public static JPanel painel_dados_RMS;
	public static JPanel painel_dados;
	public static JTextField tx_limiar;
	public JLabel lb_valor_Max;
	public JLabel lb_valor_geral_real;
	public JLabel lb_valor_limiar;
	public JButton bt_aplicar_limiar;
	public static JButton bt_chave_execucao;
	public JComboBox<String> cbx_eletrodoList;
	public Font f;

	public Dimension tamanhoTela;

	public Hub hub;
	public Myo myo;
	public DeviceListener dataCollector;

	public String dados_String;
	public String[] eletrodo;
	public String chaveColunaString;
	public DecimalFormat df;

	public static String controle_porcetagem;
	public static Boolean chave_execucao = true;

	public ThreadMyo t;

	public EMG_Grafico() {

		grafico_Bruto = ChartFactory.createLineChart("DADOS EMG BRUTO", "Tempo", "Valor", ds_Dados_Brutos,
				PlotOrientation.VERTICAL, true, true, false);
		grafico_RMS = ChartFactory.createLineChart("DADOS EMG RMS", "Tempo", "Valor", ds_Dados_RMS,
				PlotOrientation.VERTICAL, true, true, false);

		janela = new JFrame("NEUROBOTS - PESQUISA E DESENVOLVIMENTO");
		janela.setContentPane(new JLabel());
		janela.setLayout(null);
		painel_dados_brutos = new JPanel();
		painel_dados_RMS = new JPanel();
		painel_dados = new JPanel();
		tx_limiar = new JTextField();
		painel_dados.setLayout(null);

		lb_valor_Max = new JLabel("MÁXIMO: ");
		lb_valor_geral_real = new JLabel("REAL: ");
		lb_valor_limiar = new JLabel("LIMIAR: ");
		bt_aplicar_limiar = new JButton("Aplicar");
		String[] listaEletrodos = { "Eletrodo [ 1 ]", "Eletrodo [ 2 ]", "Eletrodo [ 3 ]", "Eletrodo [ 4 ]",
				"Eletrodo [ 5 ]", "Eletrodo [ 6 ]", "Eletrodo [ 7 ]", "Eletrodo [ 8 ]" };
		cbx_eletrodoList = new JComboBox<>(listaEletrodos);
		f = new Font("SansSerif", Font.BOLD, 20);

		lb_valor_Max.setFont(f);
		lb_valor_geral_real.setFont(f);
		lb_valor_limiar.setFont(f);

		lb_valor_Max.setVisible(true);
		lb_valor_geral_real.setVisible(true);
		lb_valor_limiar.setVisible(true);

		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.add(painel_dados_brutos);
		janela.add(painel_dados_RMS);
		janela.add(painel_dados);
		tamanhoTela = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		bt_chave_execucao = new JButton("[ ON ]");

		painel_dados_brutos.add(new ChartPanel(grafico_Bruto));
		painel_dados_RMS.add(new ChartPanel(grafico_RMS));
		painel_dados.add(lb_valor_Max).setBounds(520, 20, 300, 20);
		painel_dados.add(lb_valor_geral_real).setBounds(650, 20, 300, 20);
		painel_dados.add(lb_valor_limiar).setBounds(770, 20, 300, 20);
		painel_dados.add(tx_limiar).setBounds(920, 20, 30, 20);
		painel_dados.add(bt_aplicar_limiar).setBounds(960, 20, 75, 20);
		painel_dados.add(cbx_eletrodoList).setBounds((int) tamanhoTela.getWidth() / 2, 80, 100, 50);
		painel_dados.add(bt_chave_execucao).setBounds(0, 40, 100, 100);

		painel_dados_brutos.setBounds(0, 0, 680, 430);
		painel_dados_RMS.setBounds(670, 0, 690, 430);
		painel_dados.setBounds(0, painel_dados_brutos.getHeight(), (int) tamanhoTela.getWidth(), 200);
		janela.setSize((int) (tamanhoTela.getWidth()), ((int) tamanhoTela.height));
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setVisible(true);

		selecionar_eletrodoMYO(cbx_eletrodoList);
		selecionar_porcentagemLimiar(tx_limiar, bt_aplicar_limiar);

		hub = new Hub("com.example.emg-data-sample");
		System.out.println("Attempting to find a Myo...");
		myo = hub.waitForMyo(10000);

		System.out.println("Connected to a Myo armband!");
		myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
		dataCollector = new EmgDataCollector();
		hub.addListener(dataCollector);

		this.t = new ThreadMyo(this);
		t.start();
		
		bt_chave_execucao.setVisible(false);

		bt_chave_execucao.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// TODO Auto-generated method stub
				if (chave_execucao == true) {
					System.out.println(chave_execucao);
					chave_execucao = false;
					try {
						t.wait();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					

				} else if (chave_execucao == false) {
					System.out.println(chave_execucao);
					chave_execucao = true;
					try {
						t.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
				
					new EMG_Grafico();
				}
			}
		});
	}

	@SuppressWarnings("unused")
	public static void aplicarLimiarComPortentagem(int valor_RMS_atual, int limiar_Porcentagem, int controle_mover)
			throws InterruptedException {

		if (controle_mover == 0 && valor_RMS_atual > limiar_Porcentagem) {
			System.out.println("MOVER");
			setControle_mover(1);
			System.out.println("Mudou pra " + getControle_mover());

			try {
				Socket soc = new Socket("localhost", 8080);
				DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
				dout.writeUTF("MOVER");
				dout.flush();
				dout.close();
				soc.close();
			} catch (Exception e) {
				System.out.println("SERVIDOR PYTHON DESLIGADO! ");
				e.printStackTrace();
			}
		}
		if (controle_mover == 1 && valor_RMS_atual < limiar_Porcentagem) {
			setControle_mover(0);
			System.out.println("Mudou pra " + getControle_mover());

		}
	}

	private static void selecionar_porcentagemLimiar(final JTextField tx_limiar, JButton bt_aplicar_limiar) {
		bt_aplicar_limiar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				double valor_double = Double.parseDouble(tx_limiar.getText());
				setLimiar_Porcentagem((int) (valor_Maximo_RMS * (valor_double / 100)));
				System.out.println(getLimiar_Porcentagem());
			}
		});
	}

	private static void selecionar_eletrodoMYO(final JComboBox<String> cbx_eletrodoList) {
		cbx_eletrodoList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				int eletrodo_Selecionado = cbx_eletrodoList.getSelectedIndex() + 1;
				switch (eletrodo_Selecionado) {
				case 1:

					setEletrodo_MYO(eletrodo_Selecionado);
					JOptionPane.showMessageDialog(null, "Você selecionou o Eletrodo " + getEletrodo_MYO());
					break;
				case 2:
					setEletrodo_MYO(eletrodo_Selecionado);
					JOptionPane.showMessageDialog(null, "Você selecionou o Eletrodo " + getEletrodo_MYO());
					break;
				case 3:
					setEletrodo_MYO(eletrodo_Selecionado);
					JOptionPane.showMessageDialog(null, "Você selecionou o Eletrodo " + getEletrodo_MYO());
					break;
				case 4:
					setEletrodo_MYO(eletrodo_Selecionado);
					JOptionPane.showMessageDialog(null, "Você selecionou o Eletrodo " + getEletrodo_MYO());
					break;
				case 5:
					setEletrodo_MYO(eletrodo_Selecionado);
					JOptionPane.showMessageDialog(null, "Você selecionou o Eletrodo " + getEletrodo_MYO());
					break;
				case 6:
					setEletrodo_MYO(eletrodo_Selecionado);
					JOptionPane.showMessageDialog(null, "Você selecionou o Eletrodo " + getEletrodo_MYO());
					break;
				case 7:
					setEletrodo_MYO(eletrodo_Selecionado);
					JOptionPane.showMessageDialog(null, "Você selecionou o Eletrodo " + getEletrodo_MYO());
					break;
				case 8:
					setEletrodo_MYO(eletrodo_Selecionado);
					JOptionPane.showMessageDialog(null, "Você selecionou o Eletrodo " + getEletrodo_MYO());
					break;

				default:
					break;
				}
			}
		});
	}

	public static int pegarValorMaximoRMS(ArrayList<Integer> lista_valor_maximo_RMS, double RMS) {
		lista_valor_maximo_RMS.add((int) RMS);

		int valorMax_RMS = lista_valor_maximo_RMS.get(0);
		for (int i = 1; i < lista_valor_maximo_RMS.size(); i++) {
			if (lista_valor_maximo_RMS.get(i) > valorMax_RMS) {
				valorMax_RMS = (int) lista_valor_maximo_RMS.get(i);
			}
			if (lista_valor_maximo_RMS.get(i) == 1) {
				lista_valor_maximo_RMS.remove(i);
			}
		}
		return valorMax_RMS;
	}

	public static int pegarValorMinimoRMS(ArrayList<Integer> lista_valor_maximo_RMS, double RMS) {
		lista_valor_maximo_RMS.add((int) RMS);

		int valorMinimo_RMS = lista_valor_maximo_RMS.get(0);
		for (int i = 1; i < lista_valor_maximo_RMS.size(); i++) {
			if (lista_valor_maximo_RMS.get(i) < valorMinimo_RMS) {
				valorMinimo_RMS = (int) lista_valor_maximo_RMS.get(i);
			}
			if (lista_valor_maximo_RMS.get(i) == 1) {
				lista_valor_maximo_RMS.remove(i);
			}
		}
		return valorMinimo_RMS;
	}

	public static double calcular_RMS(ArrayList<Integer> lista_valores_RMS_x, ArrayList<Integer> janela_dados_RMS_X,
			int chaveColunaInt, int dadoEletrodo_valor_Tratado_Integer) {
		lista_valores_RMS_x.add(dadoEletrodo_valor_Tratado_Integer);
		janela_dados_RMS_X.add(dadoEletrodo_valor_Tratado_Integer);

		if (janela_dados_RMS_X.size() == tamando_janela_RMS) {
			janela_dados_RMS_X.remove(0);
		}

		int somaValor_RMS_X = somatorio_dados_EMG_quadradico(janela_dados_RMS_X);
		double RMS = Math.sqrt((somaValor_RMS_X / janela_dados_RMS_X.size()));
		return (int) RMS;
	}

	public static int somatorio_dados_EMG_quadradico(ArrayList<Integer> lista_valores_RMS_x) {
		int somaValor_RMS_X = 0;
		for (int i = 0; i < lista_valores_RMS_x.size(); i++) {
			somaValor_RMS_X += (Math.pow(lista_valores_RMS_x.get(i), 2));
		}
		return somaValor_RMS_X;
	}

	public static void removerDadosAntigosGrafico(int chaveColunaInt, int controle_porcetagem_Int) {
		int controle_dados = (chaveColunaInt - janelaControleGrafico);
		if (controle_dados > 0) {
			ds_Dados_Brutos.removeValue("maximo", "" + controle_dados);
			ds_Dados_RMS.removeValue("maximo", "" + controle_dados);
		}
	}

	public static int pegarApenasPositivos(ArrayList<Integer> limiarList, int dadoEletrodo_valor_Tratado_Integer) {
		if (dadoEletrodo_valor_Tratado_Integer > 0) {
			limiarList.add(dadoEletrodo_valor_Tratado_Integer);
		} else {
			limiarList.add(1);
		}
		return limiarList.get(limiarList.size() - 1);
	}

	public static int pegarMaiorValor_EMG(ArrayList<Integer> limiarList) {
		int valorMaxPositivo = limiarList.get(0);
		for (int i = 1; i < limiarList.size(); i++) {
			if (limiarList.get(i) > valorMaxPositivo) {
				valorMaxPositivo = (int) limiarList.get(i);
			}
			if (limiarList.get(i) == 1) {
				limiarList.remove(i);
			}
		}
		return valorMaxPositivo;
	}

	public static int getEletrodo_MYO() {
		return eletrodo_MYO;
	}

	public static void setEletrodo_MYO(int eletrodo_MYO) {
		EMG_Grafico.eletrodo_MYO = eletrodo_MYO;
	}

	public static ArrayList<Integer> getLimiarListPositivo() {
		return limiar_List_Positivo;
	}

	public static void setLimiarListPositivo(ArrayList<Integer> limiarListPositivo) {
		EMG_Grafico.limiar_List_Positivo = limiarListPositivo;
	}

	public static int getValorRealGeral() {
		return valor_Real_Geral;
	}

	public static void setValorRealGeral(int valorRealGeral) {
		EMG_Grafico.valor_Real_Geral = valorRealGeral;
	}

	public static int getValorRealPositivo() {
		return valor_Real_Positivo;
	}

	public static void setValorRealPositivo(int valorRealPositivo) {
		EMG_Grafico.valor_Real_Positivo = valorRealPositivo;
	}

	public static int getValorMaxPositivo() {
		return valor_Max_Positivo;
	}

	public static void setValorMaxPositivo(int valorMaxPositivo) {
		EMG_Grafico.valor_Max_Positivo = valorMaxPositivo;
	}

	public static int getValorMinimo_RMS() {
		return valor_Minimo_RMS;
	}

	public static void setValorMinimo_RMS(int valorMinimo_RMS) {
		EMG_Grafico.valor_Minimo_RMS = valorMinimo_RMS;
	}

	public static int getLimiar_Porcentagem() {
		return limiar_Porcentagem;
	}

	public static void setLimiar_Porcentagem(int limiar_Porcentagem) {
		EMG_Grafico.limiar_Porcentagem = limiar_Porcentagem;
	}

	public static int getDadoEletrodo_valor_Tratado_Integer() {
		return dado_Eletrodo_valor_Tratado_Integer;
	}

	public void setDadoEletrodo_valor_Tratado_Integer(int dadoEletrodo_valor_Tratado_Integer) {
		EMG_Grafico.dado_Eletrodo_valor_Tratado_Integer = dadoEletrodo_valor_Tratado_Integer;
	}

	public double getPorcentagemLimiar() {
		return porcentagem_Limiar;
	}

	public void setPorcentagemLimiar(double porcentagemLimiar) {
		EMG_Grafico.porcentagem_Limiar = porcentagemLimiar;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		EMG_Grafico.selected = selected;
	}

	public int getValor() {
		return valor;
	}

	public static void setValor(int valor) {
		EMG_Grafico.valor = valor;
	}

	public static double getRMS() {
		return RMS;
	}

	public static void setRMS(double rMS) {
		RMS = rMS;
	}

	public static int getControle_mover() {
		return controle_mover;
	}

	public static void setControle_mover(int teste) {
		EMG_Grafico.controle_mover = teste;
	}

	public static int getContador() {
		return contador;
	}

	public static void setContador(int contador) {
		EMG_Grafico.contador = contador;
	}

	public static int getCont() {
		return cont;
	}

	public static void setCont(int cont) {
		EMG_Grafico.cont = cont;
	}

	public static void main(String[] args) throws InterruptedException {

		new EMG_Grafico();
		// executar_EMG();
	}

	// public static void executar_EMG() {
	// try {
	// if (myo == null) {
	// throw new RuntimeException("Unable to find a Myo!");
	// }
	// while (true) {
	// hub.run(1000 / 20);
	//
	// dados_String = dataCollector.toString();
	// eletrodo = dados_String.split("[\\W][ ]|\\[|\\]");
	// chaveColunaString = (valor++) + "";
	// int chaveColunaInt = Integer.parseInt(chaveColunaString);
	// df = new DecimalFormat("##");
	// controle_porcetagem = df.format((chaveColunaInt * porcentagem_Limiar));
	// int controle_porcetagem_Int = Integer.parseInt(controle_porcetagem);
	//
	// try {
	//
	// setDadoEletrodo_valor_Tratado_Integer(Integer.parseInt(eletrodo[eletrodo_MYO]));
	//
	// RMS = calcular_RMS(lista_valores_RMS_x, janela_dados_RMS_X,
	// chaveColunaInt,
	// dado_Eletrodo_valor_Tratado_Integer);
	//
	// setValorRealGeral(dado_Eletrodo_valor_Tratado_Integer);
	// valor_Real_Positivo = pegarApenasPositivos(limiar_List_Positivo,
	// dado_Eletrodo_valor_Tratado_Integer);
	// valor_Max_Positivo = pegarMaiorValor_EMG(limiar_List_Positivo);
	// valor_Maximo_RMS = pegarValorMaximoRMS(lista_valores_RMS, RMS);
	// valor_Minimo_RMS = pegarValorMinimoRMS(lista_valores_RMS, RMS);
	//
	// lb_valor_Max.setText("MÁXIMO: " + valor_Maximo_RMS);
	// lb_valor_geral_real.setText("GERAL: " + (int) RMS);
	// lb_valor_limiar.setText("LIMIAR (%): " + limiar_Porcentagem);
	// removerDadosAntigosGrafico(chaveColunaInt, controle_porcetagem_Int);
	//
	// ds_Dados_RMS.addValue(RMS, "maximo", chaveColunaString);
	// ds_Dados_Brutos.addValue(dado_Eletrodo_valor_Tratado_Integer, "maximo",
	// chaveColunaString);
	//
	// aplicarLimiarComPortentagem((int) RMS, limiar_Porcentagem,
	// controle_mover);
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	// } catch (Exception e) {
	// System.err.println("Error: ");
	// e.printStackTrace();
	// System.exit(1);
	// }
	//
	// }
}
