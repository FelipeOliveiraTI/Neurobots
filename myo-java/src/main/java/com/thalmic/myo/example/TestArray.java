package com.thalmic.myo.example;

import java.util.ArrayList;

public class TestArray {
	   public static void main(String[] args) {
	      
		    ArrayList<Integer> limiarList = new ArrayList<Integer>();
			
		   limiarList.add(1);
		   limiarList.add(2);
		   limiarList.add(3);
		   limiarList.add(9);
		   
		   int max = limiarList.get(0);
	      
//	      double max = myList[0];//aqui a variável max recebe o valor do primeiro item do array
	      for (int i = 1; i < limiarList.size(); i++) {//aqui a iteração irá ocorrer
	         if (limiarList.get(i) > max) //caso o valor da posição i seja maior que o valor de max, max será substituído pelo valor da i-ésima posição.
	        	 max = (int) limiarList.get(i);
	      }
	System.out.println("Maximo elemento é " + max);
	System.out.println(Math.sqrt(max));
	}
	}