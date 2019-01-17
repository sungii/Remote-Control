import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static void main(String[]args) throws IOException {
		
		int ID = 0;
		ServerSocket server = new ServerSocket(5656);
		
		while(true) {
			System.out.println("Waiting for Client");
			Socket socket = server.accept();
			System.out.println("Client with ID = " + ID + " is connected");
			ServerThread thread = new ServerThread(socket, ID++);
			thread.start();
		}
		
	}
}
