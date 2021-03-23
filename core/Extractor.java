package core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

import utility.Parser;

public class Extractor {

	public void extract(String logfile,String database, ArrayList alIns, ArrayList alUpd,ArrayList alDel)
	{
		if(logfile.isEmpty()) return;
		System.out.println("Log file: "+logfile);
		logfile=logfile.replace("\\", "\\\\");
		Parser parser=new Parser();

		/*
		ArrayList alIns=new ArrayList();
		ArrayList alUpd=new ArrayList();
		ArrayList alDel=new ArrayList();
		*/
		
		try {

			BufferedReader br = new BufferedReader(new FileReader(logfile));
			
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    boolean found=false;
		    while (line != null) {
		        found=parser.checkDB(line, database,found);
		        if(found)
		        {
		        	parser.checkInsert(line, database, alIns);
		        	parser.checkUpdate(line, database, alUpd);
		        	parser.checkDelete(line, database, alDel);
		        }

		    	//sb.append(line);
		        //sb.append(System.lineSeparator());
		        line = br.readLine();
		        
		    }
		    //System.out.println(sb.toString());
		    br.close();
	        //parser.checkDB(sb.toString(), database);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    //System.out.println(alIns.size());
	    //System.out.println(alUpd.size());
	    //System.out.println(alDel.size());

	}
}
