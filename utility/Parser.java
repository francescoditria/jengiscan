package utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	public String getDate()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy H:mm:ss"); 
	    return dateFormat.format(new Date()).toString();

	}
	
	public boolean checkInsert(String string,String database, ArrayList list)
	{
		//System.out.println("EVAL "+string);

		boolean x=false;
		
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		String operation="(.+)insert into (.+) value";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(string);
		while(matcher.find()){
			text=matcher.group(2);
			//System.out.println("Insert "+text);
			list.add(text);
	
		}
		return x;

	}

	public boolean checkUpdate(String string,String database, ArrayList list)
	{
		//System.out.println("EVAL "+string);

		boolean x=false;
		
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		String operation="(.+)update (.+) set (.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(string);
		while(matcher.find()){
			text=matcher.group(2);
			//System.out.println("Insert "+text);
			list.add(text);
	
		}
		return x;

	}
	
	public boolean checkDelete(String string,String database, ArrayList list)
	{
		//System.out.println("EVAL "+string);

		boolean x=false;
		
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		String operation="(.+)delete from (.+) (.)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(string);
		while(matcher.find()){
			text=matcher.group(2);
			//System.out.println("Insert "+text);
			list.add(text);
	
		}
		return x;

	}

	public boolean checkDB(String string,String database,boolean oldx)
	{
		//if(string.equals("")) return false;
		//System.out.println("1 "+string);
		boolean x=false;
		
		//string=string.replace("\n", " ");
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		
		//////////////////////////
		String operation2="(.+) Quit";
		pattern=Pattern.compile(operation2);
		matcher=pattern.matcher(string);
		while(matcher.find()){
			//System.out.println("FALSE "+string);
			return false;
		}
		/////////////////////////////////////
		
		String operation="(.+) Init DB\t(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(string);
		while(matcher.find())
		{
			text=matcher.group(2);
			//System.out.println("TEXT "+text);
			//text=text.replace(" ", "");
			if(text.equals(database))
			{
				//System.out.println("TRUE Found "+text);
				//System.out.println("Return true");
				x=true;
				return x;

			}
			else
			{
				//System.out.println("Return false");
				//System.out.println("FALSE Found "+text);
				x=false;
				return x;

			}
		
		}
		x=oldx;
		//System.out.println(string+""+x);
		return x;

	}
	
	
	public String getUsername(String target)
	{
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		String operation="(.+)\\.(.+)@(.+):(.+)/(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(target);
		while(matcher.find()){
			text=matcher.group(1);
		}	
		return text;
	}

	public String getPassword(String target)
	{
		
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		String operation="(.+)\\.(.+)@(.+):(.+)/(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(target);
		while(matcher.find()){
			text=matcher.group(2);
		}	
		return text;
	}
	
	public String getHost(String target)
	{
		
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		String operation="(.+)\\.(.+)@(.+):(.+)/(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(target);
		while(matcher.find()){
			text=matcher.group(3);
		}	
		return text;
	}
	
	public String getPort(String target)
	{
		
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		String operation="(.+)\\.(.+)@(.+):(.+)/(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(target);
		while(matcher.find()){
			text=matcher.group(4);
		}	
		return text;
	}
	
	public String getDatabase(String target)
	{
		
		String text=new String();
		Pattern pattern;
		Matcher matcher;
		String operation="(.+)\\.(.+)@(.+):(.+)/(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(target);
		while(matcher.find()){
			text=matcher.group(5);
		}	
		return text;
	}




}
