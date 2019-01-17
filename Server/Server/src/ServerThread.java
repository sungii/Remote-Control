import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.imageio.ImageIO;

public class ServerThread extends Thread{

	Socket socket;
	DataOutputStream data_out;
	DataInputStream data_in;
	
	int ID;

	public ServerThread(Socket socket, int id) throws IOException {
		this.socket = socket;
		this.data_out = new DataOutputStream(socket.getOutputStream());
		this.data_in = new DataInputStream(socket.getInputStream());
		this.ID = id;
		
	}

	
	@Override
	public void run(){

		Robot robot;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		BufferedImage img;
		File file = new File("C:\\Users\\vietd\\Desktop\\ScreenSharing\\screen.jpg");
		FileInputStream file_in = null;
		Encoder encoder = Base64.getEncoder();

		try {
			//Create Screenshot
			robot = new Robot();
			
			Thread.sleep(1000);
			int datatype;
			while((datatype = data_in.readByte()) != -1) {
				/*switch(datatype) {
				case 1: 
					int move = data_in.readInt();
					img  = robot.createScreenCapture(new Rectangle(move, 0,1280, 720)); 
					break;
				case 2: break;
				default: break;
				}*/
				img  = robot.createScreenCapture(new Rectangle(screenSize));

				//Save Screenshot
				ImageIO.write(img, "png" , file);

				//Create image bytes
				file_in = new FileInputStream(file);
				byte [] imgBytes = new byte[(int) file.length()];
				file_in.read(imgBytes);

				//Encode
				imgBytes = encoder.encode(imgBytes);

				//Send Bytes of Image
				data_out.writeInt(imgBytes.length);
				data_out.write(imgBytes);
				
			}
		} catch (AWTException | IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				if(data_in.read() == -1)
					socket.close();
				data_out.close();
				if(file_in != null)
					file_in.close();
				System.out.println("Client with ID = " + ID + " is disconnected \nAll Streams successfully closed! ");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}







	}

}
