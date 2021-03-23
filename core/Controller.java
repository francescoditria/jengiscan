package core;

import utility.Manager;
import utility.Parser;

public class Controller {

	public void start(String target,String logfile)
	{
		//return null;
		Parser parser=new Parser();
		Manager manager=new Manager();
		Engine engine=new Engine();
		
		String username=parser.getUsername(target);
		String password=parser.getPassword(target);
		String host=parser.getHost(target);
		String port=parser.getPort(target);
		String database=parser.getDatabase(target);
		//System.out.println("Host " + host + ":" + port);
		//System.out.println("User " + username);
		
		boolean result=manager.createDir(database);
		engine.scan(username, password, host, port, database, logfile);
		
		
		
		
	}
}
