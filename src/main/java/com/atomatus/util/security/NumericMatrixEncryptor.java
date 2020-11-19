package com.atomatus.util.security;

import java.util.Objects;

/**
 * Matrix numeric encryptor.
 * @author Carlos Matos
 */
@SuppressWarnings("unused")
final class NumericMatrixEncryptor extends NumberEncryptor {

	private final int[][] keyMatrix;

	/**
	 * Default constructor with default private key.
	 */
	NumericMatrixEncryptor() {
		this("3121"); //never change it.
	}

	/**
	 * Constructor with private key.
	 * @param privateKey - numeric private key.
	 */
	NumericMatrixEncryptor(String privateKey) {
		super(privateKey);
		this.keyMatrix = convertToMatrix(privateKey);
	}
	
	private int[] toIntArray(String input) {
		char[] aux = Objects.requireNonNull(input).toCharArray();
		int[] out = new int[aux.length];
		for(int i =0; i < aux.length; i++){
			char c = aux[i];
			if(Character.isDigit(c)){
				out[i] = Character.getNumericValue(c);
			} else {
				throw new IllegalArgumentException("Use only digits!");
			}
		}
		return out;
	}
	
	private int[][] convertToMatrix(String input) {
		int[] var = this.toIntArray(input);
		
		if((var.length % 2) != 0){
			throw new IllegalArgumentException("Input data is not even!!");
		}
		
		int columnLength 	= var.length / 2;		
		int[][] matrix		= new int[2][columnLength];
		for(int i=0, r=0, c=0; i < var.length; i++){
			if(i == columnLength){
				c++;
				r = 0;
			}
			matrix[c][r++] = var[i];
		}
		return matrix;
	}
	
	/*
	 * Funciona apenas para esse contexto, pois sempre sera uma matrix (2x2) por outra (2xN).
	 */
	private int[][] multMatrix(int[][] a, int[][] b){
		int[][] result = new int[b.length][b[0].length];
		for(int i=0; i < a.length; i++){
			for(int c=0; c < b[0].length; c++){
				int v = 0;
				for(int r =0; r < b.length; r++){
					v += a[i][r] * b[r][c];					
				}
				result[i][c] = v;
			}
		}
		return result;
	}
	
	/*
	 * Cada valor salvo no resultado tem o seu tamanho adicionado a esquerda. 
	 * @param m
	 * @return
	 */
	private String toEncryptedStringMatrix(int[][] m){
		StringBuilder sb = new StringBuilder();
		for(int[] row : m) {
			for(int data : row) {
				sb.append(String.valueOf(data).length()).append(data);
			}
		}
		return sb.toString();
	}
	
	private String toDecryptedStringMatrix(int[][] m){
		StringBuilder sb = new StringBuilder();
		for(int[] row : m){
			for(int data : row){
				sb.append(data);
			}
		}
		return sb.toString();
	}
	
	private int[][] inverseKey(){
		return Inverse.invert(keyMatrix);
	}
	
	/**
	 * Recupera os valores para matrix.
	 */
	private int[][] split(String str){
		char[] aux = Objects.requireNonNull(str).toCharArray();
		
		String[] var = new String[0];
		for(int i=0, toRead=0; i < aux.length; i++){
			if(toRead > 0){
				var[var.length-1] += aux[i];				
				toRead--;
			}
			else {
				char c = aux[i];
				if(Character.isDigit(c)){
					toRead = Character.getNumericValue(c);
				} else {
					throw new IllegalArgumentException("Use only digits!");
				}

				String[] vAux = var;
				var = new String[vAux.length + 1];
				System.arraycopy(vAux, 0, var, 0, vAux.length);
				var[vAux.length] = "";
			}
		}
		
		int columnLength 	= var.length / 2;		
		int[][] matrix		= new int[2][columnLength];
		
		for(int i=0, r=0, c=0; i < var.length; i++){
			if(i == columnLength) {
				c++;
				r = 0;
			}
			matrix[c][r++] = Integer.parseInt(var[i]);
		}
		
		return matrix;
	}
	
	@Override
	public String encrypt(String original) {
		int[][] result = multMatrix(keyMatrix, convertToMatrix(original));		
		return toEncryptedStringMatrix(result);
	}

	@Override
	public String decrypt(String encrypted) {
		int[][] result = multMatrix(inverseKey(), split(encrypted));		
		return toDecryptedStringMatrix(result);
	}
}