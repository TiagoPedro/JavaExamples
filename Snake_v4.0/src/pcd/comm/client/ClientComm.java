package pcd.comm.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;

import javax.swing.JOptionPane;

import pcd.comm.ClientSideMessage;
import pcd.comm.ConstructorMessage;
import pcd.comm.ServerSideMessage;
import pcd.gamegui.GameGUI;

public class ClientComm extends Observable {
	private final static int PORT = 8080;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Socket clientSocket;
	private ConstructorMessage constructorMessage;
	private ServerSideMessage updateMessage;
	private String ipAddress = "127.0.0.1";
	
	public ClientComm() {
		ipAddress = JOptionPane.showInputDialog("Qual o endereco de IP pretendido?");
	}

	public void connectToServer() throws IOException {
		System.out.println("Client connecting...");
		InetAddress serverAddress = InetAddress.getByName(ipAddress);
		System.out.println(serverAddress.getHostAddress());
		clientSocket = new Socket(serverAddress, PORT);
		System.out.println("Client connected to server.");
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		in = new ObjectInputStream(clientSocket.getInputStream());
	}
	
	public ConstructorMessage getConstructorMessage() {
		return constructorMessage;
	}

	public ServerSideMessage getUpdateMessage() {
		return updateMessage;
	}

	public ConstructorMessage getConstructionInfoFromServer(){
		try {
			constructorMessage = (ConstructorMessage) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Error reading construction info.");
		}
		return constructorMessage;
	}

	public void sendSelectedIdToServer(int snakeID) {
		ClientSideMessage clientMessage = new ClientSideMessage(snakeID, false);
		try {
			out.writeObject(clientMessage);
			out.reset();
		} catch (IOException e) {
			System.out.println("Error on sending message with snakeID to add.");
		}
	}

	public void removeIdFromListOfSelectedSnakes(int snakeID) {
		ClientSideMessage clientMessage = new ClientSideMessage(snakeID, true);
		try {
			out.writeObject(clientMessage);
			out.reset();
		} catch (IOException e) {
			System.out.println("Error on sending message with SnakeID to remove");
		}
	}

	public void sendTargetCellInfo(int[] pos, int snakeID) {
		ClientSideMessage clientMessage = new ClientSideMessage(pos, snakeID, false);
		try {
			out.writeObject(clientMessage);
			out.reset();
		} catch (IOException e) {
			System.out.println("Error on sending message with target cell.");
		}
	}

	public void init() {
		while(true) {
			try {
//				System.out.println("Client waiting for server message...");
				updateMessage = (ServerSideMessage) in.readObject();
				setChanged();
				notifyObservers();
//				System.out.println("Message read from server.");
			} catch (ClassNotFoundException e) {
				System.out.println("Error reading input message from server. (ClassNotFoundException)");
			} catch (IOException e) {
				System.out.println("Error reading input message from server. (IOException)");
				try {
					clientSocket.close();
				} catch (IOException e1) {
					System.out.println("Couldn't get message from server and exited.");
				}
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		ClientComm client = new ClientComm();
//		System.out.println("Client created.");
		try {
			client.connectToServer();
		} catch (IOException e) {
			System.out.println("Couldn't connect to server.");
		}
		GameGUI gameGui = new GameGUI(client);
		client.addObserver(gameGui);
		gameGui.execute();
		client.init();
		System.out.println("Client main thread ended.");
	}
}
