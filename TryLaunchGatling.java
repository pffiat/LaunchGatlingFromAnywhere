
import java.io.*;
import java.awt.Desktop;
import java.net.*;
import java.util.Date;

public class TryLaunchGatling{

	public static void main(String... args){
		
		String path = "/home/pif/gatling4";
		String arg = "simuSansNom";
		String simuLine = "";
		String simuName = "";
		if(args.length > 0) {
			arg = args[0];
		}
		
		try {
			InputStream inputStream = TryLaunchGatling.getScript("402", "test@liferay.com", "test");
			String fileName = arg + new Date().getTime();

			/*write the script in a file*/
			BufferedReader in = new BufferedReader( new InputStreamReader(inputStream));
			File script = new File(path+"/user-files/simulations/" + fileName + ".scala");
			script.createNewFile();
			script.setWritable(true);
			FileWriter fileWriter = new FileWriter(script);
        	   	String line = "";  
			String totalLine = "";

			while ((line = in.readLine()) != null) {
				totalLine += line;
        	   	}
			totalLine = totalLine.substring(1,totalLine.length()-1).replace("&#10;", "\n").replace("&#13;", "\t").replace("\\\"", "\"");

			simuLine = TryLaunchGatling.getSimuLine(totalLine);
			simuName = simuLine.split(" ")[1];
			TryLaunchGatling.removeSameSimu(path+"/user-files/simulations/", arg, simuLine);

			fileWriter.write(totalLine);
			fileWriter.flush();
			fileWriter.close();

			/*execute gatling*/
			Process shell = Runtime.getRuntime().exec(path+"/bin/gatling.sh");            	
			in = new BufferedReader( new InputStreamReader(shell.getInputStream()));  

           		while ((line = in.readLine())!=null ) {
    				System.out.println(line); 
           			if(line.contains("Choose a simulation number:")){
					break;      				
           			}			
           		}  
			int i = 0;
           		while ((line = in.readLine())!=null ) {
    				System.out.println(line);   
           			if(line.contains(simuName)){    
					break;      				
           			}
				i++;			
           		}  

			try {	Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();} 
			
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(shell.getOutputStream()));
			bufferedWriter.write(i+"\n");
			bufferedWriter.flush();
			System.out.println("-write and flush-");

           		while ((line = in.readLine()) != null) {
    				System.out.println(line); 
				if(line.equals("Select simulation id (default is 'simulationpifsimu'). Accepted characters are a-z, A-Z, 0-9, - and _")) {
					bufferedWriter.write("\n");
					bufferedWriter.flush();
					break;
				}				
           		}  

			bufferedWriter.write("\n");
			bufferedWriter.flush();	

           		while ((line = in.readLine()) != null && !line.contains("Please open the following file:")) {
				System.out.println(line);
           		}  
			
			try {	Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();} 
						 
			String urlString = line.substring(32); 
			urlString = urlString.substring(0, urlString.length()-11);
			File htmlFile = null;
			File dir = new File(urlString);
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

			System.out.println(urlString);
			/*open the results of the simulation*/
			Desktop.getDesktop().open(htmlFile);



		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("FINALLY is reach, the end is close");
		}
	}

	public static void removeSameSimu(String path, String name, String simuLine) throws IOException {
		File dir = new File(path);
		File[] list = dir.listFiles();
		System.out.println("removeFile  "+simuLine);
		for (File file : list) {
			if(file.getName().contains(".scala")){
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line = bufferedReader.readLine();
				while((line= bufferedReader.readLine()) !=null) { 
					if(simuLine.equals(line)){
						System.out.println("removeFile succed? " +file.delete());
						break;
					}
				}
			}
				
		}

	}

	public static InputStream getScript(String id, String login, String password) throws IOException {			

		/*get the script from the json webservice*/
		URL url = new URL("http://localhost:8080/api/jsonws/gatling-liferay-portlet.websimu/get-simulation/simu-id/" + id + "/login/" + login + "/password/" + password );

		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
	        String userpassword = login + ":" + password;
	        String encodedAuthorization = enc.encode( userpassword.getBytes() );
	        connection.setRequestProperty("Authorization", "Basic "+ encodedAuthorization);
		connection.connect();   

		return connection.getInputStream();

	}

	public static String getSimuLine(String totalLine) throws IOException{ 
		String simuLine = "";
		
		File tempo = new File("tempo.scala");
		tempo.createNewFile();
		tempo.setWritable(true);
		FileWriter fileWriter = new FileWriter(tempo);
		fileWriter.write(totalLine);
		fileWriter.flush();
		fileWriter.close();

		BufferedReader bufferedReader = new BufferedReader(new FileReader(tempo));
		String line = bufferedReader.readLine();

		while( (line = bufferedReader.readLine()) != null) { 
			if(line.contains("extends Simulation")) {
				simuLine = line;
				break;				
			}
		}

		tempo.delete();
		return simuLine;
		
	}

}
