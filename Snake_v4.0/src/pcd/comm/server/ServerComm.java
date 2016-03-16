package pcd.comm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import pcd.gameengine.Game;

public class ServerComm {
	private static final int PORT = 8080;
	private ServerSocket serverSocket;
	private Game game;
	private int numberOfClients;
	
	public ServerComm() {
		game = new Game(40, 2, 30);
		numberOfClients = -1;
	}
	
	public int getNumberOfClients() {
		return numberOfClients;
	}

	public void setNumberOfClients(int numberOfClients) {
		this.numberOfClients = numberOfClients;
	}

	private void openServerSocket(){
		try{
			serverSocket = new ServerSocket(PORT);
		} catch(IOException e) {
			System.out.println("IO error.");
		}
	}
	
	public void init() {
		openServerSocket();
		Socket clientSocket = null;
		try {
			while(true) {
				if(numberOfClients < 3) {
					System.out.println("Waiting connections...");
					clientSocket = serverSocket.accept();
					numberOfClients++;
					System.out.println("Connection accepted.");
					
					DealWithClient dealWithClient = new DealWithClient(clientSocket, game, numberOfClients, this);
					dealWithClient.start();
				} else
					System.out.println("Not receiving more connections!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]){
		new ServerComm().init();
		System.out.println("Server main thread ended.");
	}
}
