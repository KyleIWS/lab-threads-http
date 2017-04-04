import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	/*
	Takes in a string representing a movie to be searched in the omdbapi database.
	Assuming a connection could be established, will return a list of JSON
	formatted movie details for up to 10 most relevant matches.
	*/
	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		// Attempt connection to the database and in case of an exception involving
		// unsupported encoding, return null (thus exiting method)
		try {
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){
			return null;
		}

		// Object representing the Http connection used to make
		// requests.
		HttpURLConnection urlConnection = null;
		
		// Object representing a stream of character data.
		BufferedReader reader = null;

		String[] movies = null;

		try {

			// Stores URL that is going to be accessed (the database)
			URL url = new URL(urlString);

			// Opens http connection to database
			urlConnection = (HttpURLConnection) url.openConnection();

			// Initiates GET request
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			// Represents stream of bytes. In this case, from the HTTP connection
			// that has just been formed.
			InputStream inputStream = urlConnection.getInputStream();
			// Reserving some space for a string.
			StringBuffer buffer = new StringBuffer();
			// If the data from the http connection is empty (null)
			// then return null right away.
			if (inputStream == null) {
				return null;
			}

			// Create a tool capable of reading character data from the more
			// primitive byte reader (BufferedReader is eating InputStream)
			reader = new BufferedReader(new InputStreamReader(inputStream));

			// While there are lines of character data
			// append them to a memory space meant for a string.
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}

			// Another check for data that is empty.
			if (buffer.length() == 0) {
				return null;
			}

			// Print results that have accumulated for one stream
			// in a nice way.
			String results = buffer.toString();
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			// Separate all the results based off the location of a newline character
			// so that we can separate the different movies that result from the query.
			movies = results.split("\n");
		} 
		// Capture instances IOExceptions (which are numerous I imagine)
		catch (IOException e) {
			return null;
		} 
		// Only occurs when the try block has exited.
		finally {
			// Disconnect from the database
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			// Close off the character reader
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
				}
			}
		}

		return movies;
	}


	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
