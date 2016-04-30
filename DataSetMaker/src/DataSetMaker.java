import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import model.PHoto;
import model.User;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;


public class DataSetMaker {

	private final static String apikey = "9c59c8bc62c5a788de428fb57a31e2bd";
	private final static String secret = "f5412e38c5ac7143";


	public static void main(String[] args) throws FlickrException, IOException, ParseException{ 


		for (int i = 1; i < 6; i++){
			String minDateInString1 = "01-01-201" + i;
			String maxDateInString1 = "01-03-201" + (i);
			System.out.println(minDateInString1);		
			System.out.println(maxDateInString1);

			String minDateInString2 = "02-03-201" + i;
			String maxDateInString2 = "01-05-201" + (i);
			System.out.println(minDateInString2);		
			System.out.println(maxDateInString2);

			String minDateInString3 = "02-05-201" + i;
			String maxDateInString3= "01-07-201" + (i);
			System.out.println(minDateInString3);		
			System.out.println(maxDateInString3);

			String minDateInString4 = "02-07-201" + i;
			String maxDateInString4= "01-09-201" + (i);
			System.out.println(minDateInString4);		
			System.out.println(maxDateInString4);
			
			String minDateInString5 = "02-09-201" + i;
			String maxDateInString5= "01-11-201" + (i);
			System.out.println(minDateInString5);		
			System.out.println(maxDateInString5);

			String minDateInString6 = "02-11-201" + i;
			String maxDateInString6= "31-12-201" + (i);
			System.out.println(minDateInString6);		
			System.out.println(maxDateInString6);

			search(minDateInString1, maxDateInString1);
			search(minDateInString2, maxDateInString2);
			search(minDateInString3, maxDateInString3);
			search(minDateInString4, maxDateInString4);
			search(minDateInString5, maxDateInString5);
			search(minDateInString6, maxDateInString6);
		}

	}

	public static void search(String minDateInString, String maxDateInString) throws FlickrException, IOException, ParseException{

		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		Date minTakenDate = sdf.parse(minDateInString);
		Date maxTakenDate = sdf.parse(maxDateInString);

		Flickr flickr = new Flickr(apikey, secret, new REST());
		// Set the wanted search parameters (I'm not using real variables in the example)
		SearchParameters searchParameters = new SearchParameters();
		//searchParameters.setAccuracy(11);
		searchParameters.setWoeId("721943");
		//searchParameters.setTags(tags);
		searchParameters.setHasGeo(true);
		searchParameters.setMinTakenDate(minTakenDate);
		searchParameters.setMaxTakenDate(maxTakenDate);
		//searchParameters.setBBox(minimum_longitude, minimum_latitude, maximum_longitude, maximum_latitude);


		PhotoList<Photo> list = flickr.getPhotosInterface().search(searchParameters,0, 10);
		Iterator<Photo> iterator = list.iterator();
		int i = 0;

		List<User> users = new ArrayList<User>();

		while(iterator.hasNext()){
			Photo photo = iterator.next();
			System.out.println(photo.getTitle() + "--------------"+ i);
			//		photo.getOwner().getId()+ "___________" + photo.getSecret() 
			//	+ "+++++++++++" + photo.getId() + "-------" + photo.getUrl());
			i++;
			PhotosInterface inter = new PhotosInterface(apikey, secret, new REST() );
			//System.out.println("taken at time: "+inter.getInfo(photo.getId(), photo.getSecret()).getDateTaken().toString());
			//System.out.println("location: "+inter.getInfo(photo.getId(), photo.getSecret()).getGeoData().toString());

			float latitude = inter.getInfo(photo.getId(), photo.getSecret()).getGeoData().getLatitude();
			float longitude = inter.getInfo(photo.getId(), photo.getSecret()).getGeoData().getLongitude();
			Date date = inter.getInfo(photo.getId(), photo.getSecret()).getDateTaken();
			String url = inter.getInfo(photo.getId(), photo.getSecret()).getUrl();
			String owner = photo.getOwner().getId();


			PHoto pHoto = new PHoto(latitude, longitude, date, owner);
			pHoto.setUrl(url);

			if (!userAlreadyExists(owner, users)){
				User user = new User(owner, new ArrayList<PHoto>());
				user.getPhotos().add(pHoto);
				users.add(user);
			}
			else {
				users.get(getUsersIndex(users, owner)).getPhotos().add(pHoto);
			}
		}
		System.out.println("liste user--->photos create. utenti: " + users.size());

		//costruisco il dataset persistente organizzato in cartelle e file di testo	
		//createDir("/home/matteo/Scrivania/Routes");

		Iterator<User> it = users.iterator();
		while (it.hasNext()){
			User us = it.next();
			createDir("Routes4/" + us.getId());
			List<PHoto> photos = us.getPhotos();
			Collections.sort(photos);
			Iterator<PHoto> ite = photos.iterator();
			while (ite.hasNext()){
				PHoto foto = ite.next();

				String name = "";
				if (foto.getDate().toString().length() == 29){
					name = foto.getDate().toString().substring(0, 11) + foto.getDate().toString().substring(25, 29);
				}
				else {
					name = foto.getDate().toString().substring(0, 11) + foto.getDate().toString().substring(24, 28);
				}
				String path1 = "Routes4/" + us.getId() + "/" + name + ".txt";
				//foto.tostring
				//File file = new File(path1);
				FileWriter fstream = new FileWriter(path1, true);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write(foto.getLatitude() + "\t" + foto.getLongitude() + "\t" + foto.getDate().toString());
				out.newLine();

				//close buffer writer
				out.close();

				System.out.println("stringa: "+ foto.getDate().toString() + "\t" + foto.getUser());

			}

		}
		
		/*elimino coordinate simili*/

		//per ogni cartella in Routes
		String myDirectoryPath = "Routes4";
		System.out.println("str");
		float[] coordinates = {0,0};
		File dir = new File(myDirectoryPath);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {  
			for (File child : directoryListing) {
				//per ogni file
				System.out.println("file: "+ child.getName());
				String myDirectoryPath1 = child.getPath();
				File dir1 = new File(myDirectoryPath1);
				File[] directoryListing1 = dir1.listFiles();

				if (directoryListing1 != null) {
					for (File child1 : directoryListing1) {
						System.out.println("	file: "+child1.getName());
						i++;
						System.out.println(i);

						//analizza
						//imposto DELTA = 0.0015
						double DELTA = 0.0015;

						coordinates[0] = 0;
						coordinates[1] = 0;
						try(BufferedReader br = new BufferedReader(new FileReader(child1))) {
							for(String line; (line = br.readLine()) != null; ) {
								// process the line.
								String[] tokenizer = line.split("\t");
								float lat = Float.parseFloat(tokenizer[0]);
								float longi = Float.parseFloat(tokenizer[1]);
								if ((coordinates[0] == 0) && (coordinates[1] == 0)){
									coordinates[0] = lat;
									coordinates[1] = longi;
								}
								else {
									if ((lat>(coordinates[0]-DELTA))&&(lat<(coordinates[0]+DELTA))
											&&(longi>(coordinates[1]-DELTA))&&(longi<(coordinates[1]+DELTA))){
										//deleteline
										System.out.println("lat = "+ lat + "\t longi = " + longi);
										System.out.println("vecchie lat = "+ coordinates[0] + "\t vecchie longi = " + coordinates[1]);
										System.out.println("elimino riga");
										removeLineFromFile(child1.getPath(), line);
									}
									else {
										coordinates[0] = lat;
										coordinates[1] = longi;
									}
								}
							}
						}
					}
				} 
			}
		} 
		System.out.println("pulito");
	






		/*pulizia foto */

		//per ogni utente
		Iterator<User> it1 = users.iterator();
		while (it1.hasNext()){
			//per ogni file
			User us = it1.next();
			String pathDir = "Routes4/" + us.getId();
			File dir1 = new File(pathDir);
			File[] directoryListing1 = dir1.listFiles();
			if (directoryListing1 != null) {
				for (File child : directoryListing1) {
					//se numeroRighe<X e se numCoordDiv<4
					//elimina file
					if(countLines(child.getPath()) < 4 || numCoordDiv(child.getPath()) < 4){
						//delete
						child.delete();
						//se cartella è vuota
						//elimina cartella
						if (dir1.list().length <= 0){
							//deletefolder
							dir1.delete();
						}
					}


				}
			} else {
				System.err.println("directory vuota");
			}

		}
	}



	private static int numCoordDiv(String path) throws FileNotFoundException, IOException {
		List<String> coordList = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokenizer = line.split("\t");
				String coord = tokenizer[0] + "\t" + tokenizer[1];
				if (!coordList.contains(coord)){
					coordList.add(coord);
				}    	   
			}
		}
		return coordList.size();
	}





	private static void createDir(String path) {
		File theDir = new File(path);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + theDir.getName());
			boolean result = false;

			try{
				theDir.mkdir();
				result = true;
			} 
			catch(SecurityException se){
				//handle it
			}        
			if(result) {    
				System.out.println("DIR created: " + path);  
			}
		}

	}







	private static int getUsersIndex(List<User> users, String owner) {
		Iterator<User> it = users.iterator();
		int i = 0;
		while (it.hasNext()){
			if (it.next().getId().equals(owner))
				return i;
			i++;
		}
		return i;
	}


	private static boolean userAlreadyExists(String owner, List<User> users) {
		Iterator<User> it = users.iterator();
		while (it.hasNext()){
			if (it.next().getId().equals(owner))
				return true;
		}
		return false;
	}


	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}



	public static void removeLineFromFile(String file, String lineToRemove) {

		try {

			File inFile = new File(file);

			if (!inFile.isFile()) {
				System.out.println("Parameter is not an existing file");
				return;
			}

			//Construct the new file that will later be renamed to the original filename.
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			//Read from the original file and write to the new
			//unless content matches data to be removed.
			while ((line = br.readLine()) != null) {

				if (!line.trim().equals(lineToRemove)) {

					pw.println(line);
					pw.flush();
				}
			}
			pw.close();
			br.close();

			//Delete the original file
			if (!inFile.delete()) {
				System.out.println("Could not delete file");
				return;
			}

			//Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile))
				System.out.println("Could not rename file");

		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
