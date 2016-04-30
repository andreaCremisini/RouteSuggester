import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;


public class FlickrCrawler {
	private static String path = "";
    private static Preferences userPrefs = Preferences.userNodeForPackage(FlickrCrawler.class);

    // convert filename to clean filename
    public static String convertToFileSystemChar(String name) {
        String erg = "";
        Matcher m = Pattern.compile("[a-z0-9 _#&@\\[\\(\\)\\]\\-\\.]", Pattern.CASE_INSENSITIVE).matcher(name);
        while (m.find()) {
            erg += name.substring(m.start(), m.end());
        }
        if (erg.length() > 200) {
            erg = erg.substring(0, 200);
            System.out.println("cut filename: " + erg);
        }
        return erg;
    }

    public static boolean saveImage(Flickr f, Photo p) {

        String cleanTitle = convertToFileSystemChar(p.getTitle());

        File orgFile = new File(path + File.separator + cleanTitle + "_" + p.getId() + "_o." + p.getOriginalFormat());
        File largeFile = new File(path + File.separator + cleanTitle + "_" + p.getId() + "_b." + p.getOriginalFormat());

        System.out.println(orgFile.toString());
        System.out.println(largeFile.toString());
        
        
        if (orgFile.exists() || largeFile.exists()) {
            System.out.println(p.getTitle() + "\t" + p.getLargeUrl() + " skipped!");
            return false;
        }

        try {
            Photo nfo = f.getPhotosInterface().getInfo(p.getId(), null);
            if (nfo.getOriginalSecret().isEmpty()) {
                ImageIO.write(p.getLargeImage(), p.getOriginalFormat(), largeFile);
                System.out.println(p.getTitle() + "\t" + p.getLargeUrl() + " was written to " + largeFile.getName());
            } else {
                p.setOriginalSecret(nfo.getOriginalSecret());
                ImageIO.write(p.getOriginalImage(), p.getOriginalFormat(), orgFile);
                System.out.println(p.getTitle() + "\t" + p.getOriginalUrl() + " was written to " + orgFile.getName());
            }
        } catch (FlickrException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Parameter are needed as for searching. Example: FlickrCrawler.java sunset");
            return;
        }

        String apikey = "9c59c8bc62c5a788de428fb57a31e2bd";
        String secret = "f5412e38c5ac7143";

        Flickr flickr = new Flickr(apikey, secret, new REST());
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setAccuracy(1);

        StringBuilder tagsBuilder = new StringBuilder();
        for (String tmp : args) {
            tagsBuilder.append(" " + tmp);
        }
        path = "pics" + File.separator + tagsBuilder.toString().substring(1);

        new File(path).mkdirs();
        searchParameters.setTags(args);
        searchParameters.setHasGeo(true);  //setBBox()
        //cercare foto e catalogarle in base a "foto dello stesso utente" datpo che non si possono cercare album
        
        for (int i = userPrefs.getInt(path, 0); true; i++) {
            userPrefs.putInt( path, i );
            System.out.println("\tcurrent page: " + userPrefs.getInt(path, 0));
            try {
                PhotoList<Photo> list = flickr.getPhotosInterface().search(searchParameters, 500, i);
                if (list.isEmpty())
                    break;
                    
                Iterator itr = list.iterator();
                while (itr.hasNext()) {
                    saveImage(flickr, (Photo) itr.next());
                }
            } catch (FlickrException e) {
                e.printStackTrace();
            }
        }
    }
}
