package com.neurobots.view;

import javax.swing.JFrame;

import com.neurobots.model.Eletromiograma;

public class TelaNeurobots {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Eletromiograma eletromiograma = new Eletromiograma();
	
		while (true) {
			System.out.println(eletromiograma.dadosString);

		}

		
	}

}
