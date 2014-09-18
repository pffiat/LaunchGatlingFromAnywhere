
import java.io.*;
import java.awt.Desktop;
import java.net.*;

public class TryLaunchGatling{

	public static void main(String... args){
		
		String path = "/home/pif/gatling4";
		if(args.length > 0) {
			String arg = args[0];
		}
		
		try {
		
			URL url = new URL("http://127.0.0.1:5984/_all_dbs");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();           	
				
			BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));  
			File script = new File(path+"/results/test.txt");
			script.createNewFile();
			script.setWritable(true);
			FileWriter fileWriter = new FileWriter(script);
        	   	String line = null;  
        	   	while ((line = in.readLine()) != null) {
    				fileWriter.write(line);
        	   	}  
			fileWriter.flush();
			fileWriter.close();

			/*Process shell = Runtime.getRuntime().exec(path+"/bin/gatling.sh");            	
			BufferedReader in = new BufferedReader( new InputStreamReader(shell.getInputStream()));  

           		String line = null;  
           		while ((line = in.readLine()) != null && !line.equals("Choose a simulation number:") ) {
    				System.out.println(line);
           		}  

			try {	Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();} 
			
			
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(shell.getOutputStream()));
			bufferedWriter.write("0\n");
			bufferedWriter.flush();

           		while ((line = in.readLine()) != null && !line.equals("Select simulation id (default is 'simulationpifsimu'). Accepted characters are a-z, A-Z, 0-9, - and _")) {
           		}  

			bufferedWriter.write("\n");
			bufferedWriter.flush();	

			bufferedWriter.write("\n");
			bufferedWriter.flush();	

           		while ((line = in.readLine()) != null && !line.contains("Please open the following file:")) {
           		}  
			
			try {	Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();} 
						 
			String url = line.substring(32); 
			url = url.substring(0, url.length()-11);
			File htmlFile = null;
			File dir = new File(url);
			System.out.println("dir "+dir.exists());

			File[] list = dir.listFiles();
			for (File file : list) {
				if(file.getAbsolutePath().contains("index")) {
					System.out.println("winner");
					htmlFile = file;
					break;
				}
				System.out.println(file.getAbsolutePath());

			}

			System.out.println(url);

			Desktop.getDesktop().open(htmlFile);*/



		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("FINALLY is reach, the end is close");
		}
	}

}

