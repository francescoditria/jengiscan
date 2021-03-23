package utility;

import java.io.File;

public class Manager {

	public boolean createDir(String name)
	{
		boolean result=false;
		/*
		File f = new File(name);
		boolean control = f.isDirectory();
		if(control==true)
		{
			f.delete();
		}
		*/
		result = (new File(name)).mkdir();
		return result;
	}
	
}
