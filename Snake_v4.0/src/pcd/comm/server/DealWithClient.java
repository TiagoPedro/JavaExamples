package pcd.comm.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import pcd.comm.ClientSideMessage;
import pcd.comm.ConstructorMessage;
import pcd.comm.ServerSideMessage;
import pcd.gameengine.Game;

public class DealWithClient extends Thread implements Observer {
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Socket clientSocket;
	private Game game;
	private int numberOfClients;
	private boolean isSelected;
	private ServerComm server;
	
	public DealWithClient(Socket clientSocket, Game game, int numberOfClients, ServerComm server){
		this.clientSocket = clientSocket;
		this.game = game;
		this.numberOfClients = numberOfClients;
		this.isSelected = false;
		this.server = server;
		try {
			in = new ObjectInputStream(clientSocket.getInputStream());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		game.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == game) {
			ServerSideMessage updateMessage = new ServerSideMessage(
					game.getListOfUnselectedSnakesPos(), 
					game.getListOfSelectedSnakesPos(), 
					game.getListOfJumpsPos(),
					game.getListOfBarriersPos(),
					game.getListOfTurboBoostsPos(),
					game.getListOfFoodsPos());
			try {
				out.writeObject(updateMessage);
				out.reset();
			} catch (IOException e) {
				System.out.println("Error writing update message.");
			}
		}
	}

	@Override
	public synchronized void run() {
		ConstructorMessage constMessage = new ConstructorMessage(numberOfClients, game.getNumberOfCells());
		try {
			out.writeObject(constMessage);
			out.reset();
		} catch (IOException e1) {
			System.out.println("Error sending construction message.");
		}
		if(!game.isStarted()){
			game.setStarted(true);
			game.execute();
		}
		while(!interrupted()){
			ClientSideMessage message = null;
			try {
				message = (ClientSideMessage) in.readObject();
				if(isSelected){
					game.setTargetCell(message.getPos(), message.getSnakeID());
					isSelected = false;
				} else {
					game.addToListOfSelectedSnakes(message.getSnakeID());
					isSelected = true;
				}

			} catch (ClassNotFoundException e) {
				System.out.println("Error reading input message from client. (ClassNotFoundException)");
			} catch (IOException e) {
				System.out.println("Error reading input message from client. (IOException)");
				try {
					clientSocket.close();
					server.setNumberOfClients(server.getNumberOfClients() - 1);
					System.out.println("Client socket closed.");
					interrupt();
				} catch (IOException e1) {
					System.out.println("Error closing client socket.");
				}
			}
		}
		try {
			clientSocket.close();
			out.close();
		} catch (IOException e) {
			System.out.println("Error closing client socket or output stream.");
		}
	}
}
