package core;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import utility.Manager;
import utility.Parser;

public class Engine {

    Connection connection;
    PrintWriter out;
    private String version="JengiScan 1.1";
    private String path;
    private String dirCount="count";
    private String dirSample="sample";
    
	public void scan(String username,String password, String host, String port, String database, String logfile)
	{
	    Parser parser=new Parser();
	    String currentDate=parser.getDate();
		
		String jdbc="jdbc:mysql://"+host+":"+port+"/"+database;
		path=System.getProperty("user.dir")+File.separator +database;
	    String filename=path+File.separator+"index.html";
	    
	    Manager manager=new Manager();
	    String subDir=path+File.separator+dirCount;
	    boolean result=manager.createDir(subDir);
	    subDir=path+File.separator+dirSample;
	    result=manager.createDir(subDir);
			    
	    
        try {
			Class.forName("com.mysql.jdbc.Driver");
	        connection = (Connection) DriverManager.getConnection(jdbc, username, password);

	        FileWriter fw = new FileWriter(filename, false);
		    BufferedWriter bw = new BufferedWriter(fw);
		    out = new PrintWriter(bw);
		    
		    this.printIntro(database,host,currentDate);
	        this.showDatabases(database);
	        this.showTables(database,logfile);
	        this.showColumns(database);

		    out.close();
		    System.out.println();
		    System.out.println("Saved in "+path);
		    System.out.println("Main file is "+filename);
		    
		    //System.out.println(out.getAbsolutePath());
		    
	        
		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
		}
        
	}

	private void printIntro(String database, String host, String currentDate)
	{
		//SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy H:mm:ss"); 
	    out.println(version); 
	    out.println("<br>"); 
	    //String intro="Connection to " + database + " at "+ host + " on "+ dateFormat.format(new Date()).toString();
	    String intro="Connection to " + database + " at "+ host + " on "+ currentDate;
	    System.out.println(intro);
	    out.println(intro);

	    out.println("<p>"); 
	    out.println("<a name=index><b>Index</b></a>"); 
	    out.println("<table>"); 
	    out.println("<tr><td><a href='#databases'>Databases</a></td></tr>"); 
	    out.println("<tr><td><a href='#tables'>Tables</a></td></tr>"); 
	    out.println("<tr><td><a href='#columns'>Columns</a></td></tr>"); 
	    out.println("</table>"); 
	    out.println("</p>"); 
	    out.println("<hr>"); 
		
	}
	
	private void showDatabases(String database)
	{
        int i=0;
		ResultSet rs;
		String dbName=new String();

		System.out.println("Extracting databases");
	    out.println("<p>"); 
		out.println("<a name=databases><b>Databases</b></a>");
	    out.println("</p>"); 
	    out.println("<p>"); 
	    out.println("List of databases");
		
		try {
			rs = connection.getMetaData().getCatalogs();
			out.println("<table border=1>");
		    out.println("<tr><th colspan=2>Database</th></tr>"); 
	        while (rs.next()) {
	        	dbName=rs.getString("TABLE_CAT");
	        	if(dbName.equals(database)) dbName="<a href='#tables'>"+dbName+"</a>";
	            //stampa il nome del database
	            //System.out.println(rs.getString("TABLE_CAT"));
	        	i++;
			    out.println("<tr><td>"+i+"</td><td>"+dbName+"</td></tr>"); 

	        }
			out.println("</table>");
		    out.println("<a href='#' onclick='window.history.back();'>Back</a> <a href='#index'>Index</a>"); 
		    out.println("</p>"); 
		    out.println("<hr>"); 


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void showTables(String database,String logfile)
	{
        String[] types={"TABLE"};
        int i=0;
		ResultSet rs;
		ResultSet ds;
		String query=new String();
		String tableName=new String();

		/////////////LOG Analyzer
		ArrayList alIns=new ArrayList();
		ArrayList alUpd=new ArrayList();
		ArrayList alDel=new ArrayList();
		Extractor extractor=new Extractor();
		extractor.extract(logfile,database, alIns, alUpd, alDel);
		////////////////////////////////////
		
		System.out.println("Extracting tables of "+database);
	    out.println("<p>"); 
		out.println("<a name=tables><b>Tables</b></a>");
	    out.println("</p>"); 
	    out.println("<p>"); 
	    out.println("List of tables of "+database);
		Statement statement;
		
		try {
			statement=connection.createStatement();
			rs = connection.getMetaData().getTables(database, null, "%", types);
			out.println("<table border=1>");
		    out.println("<tr><th colspan=2>Table</th><th>#rows</th><th>#cols</th><th>#insert</th><th>#update</th><th>#delete</th></tr>"); 
            while (rs.next()) {
            	
            	tableName=rs.getString("TABLE_NAME");
            	query="select * from "+tableName;
            	ds = statement.executeQuery(query);
                ds.last();
                long nrows=ds.getRow();
                int ncols=ds.getMetaData().getColumnCount();
                
                long numIns=this.countListSize(tableName,alIns);
                long numUpd=this.countListSize(tableName,alUpd);
                long numDel=this.countListSize(tableName,alDel);
                
                tableName="<a href='#"+tableName+"'>"+tableName+"</a>";
                
              //stampa i nomi delle tabelle
              //System.out.println(rs.getString("TABLE_NAME"));
            	i++;
            	
			    out.println("<tr><td>"+i+"</td><td>"+tableName+"</td><td>"+nrows+"</td><td>"+ncols+"</td><td>"+numIns+"</td><td>"+numUpd+"</td><td>"+numDel+"</td></tr>"); 
			    
            }
			out.println("</table>");
		    out.println("<a href='#' onclick='window.history.back();'>Back</a> <a href='#index'>Index</a>"); 
		    out.println("</p>"); 
		    out.println("<hr>"); 
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	
	private long countListSize(String tableName,ArrayList list)
	{
		long x = 0;
		String tb=new String();
		long n=list.size();
		long i;
		
		for(i=0;i<n;i++)
		{
			tb=(String) list.get((int) i);
			if(tb.equals(tableName))
				x++;
		}
		
		return x;
	}


	private void showColumns(String database)
	{
        int i=0;
        int j;
		ResultSet rs;
		ResultSet ds;
        String[] types={"TABLE"};
		String tableName=new String();
		
		String min=new String();
		String max=new String();
		String avg=new String();
		String stddev=new String();
		
		ArrayList<String> pkList=new ArrayList<String>();
		ArrayList<String> fkList=new ArrayList<String>();


		try {
			Statement statement=connection.createStatement();
			rs = connection.getMetaData().getTables(database, null, "%", types);

    	    out.println("<p>"); 
    		out.println("<a name=columns><b>Columns</b></a>");
    	    out.println("</p>"); 
    	    
			while (rs.next()) {
            	i=0;
            	tableName=rs.getString("TABLE_NAME");
            	long nrows=this.countRows(tableName);

            	//********Create SAMPLE
			    this.sample(tableName, nrows);
            	String aSample=path+File.separator+dirSample+File.separator+tableName+".html";
            	String linkSample="<a href="+aSample+">"+tableName+"</a>";
            	//************
            	
        		System.out.println("Extracting columns of "+tableName);
        	    out.println("<p>"); 
        		out.println("List of columns of <a name="+tableName+">"+linkSample+"</a>");

            	pkList =this.getPrimayKeys(tableName);
        		int npk=pkList.size();

            	fkList =this.getForeignKeys(tableName);
        		int nfk=fkList.size();

            	
        		ds = connection.getMetaData().getColumns(database, null,tableName, null);
     			out.println("<table border=1>");
    		    out.println("<tr><th colspan=2>Column</th><th>PK</th><th>FK</th><th>Type</th><th>%Null</th><th>%Distinct</th><th>Min</th><th>Max</th><th>Avg</th><th>Std Dev</th></tr>"); 
    		    while (ds.next()) {
                    //System.out.println(ds.getString("COLUMN_NAME")+"\t"+ds.getString("TYPE_NAME"));
                 	i++;
                 	String columnName=ds.getString("COLUMN_NAME");
                 	String columnType=ds.getString("TYPE_NAME");
                 	float numbNull=this.countNull(tableName, columnName,nrows);
                 	float numbDistinct=this.countDistinct(tableName, columnName,nrows);
                 	
                 	
                 	
                 	//System.out.println(columnType + " "+this.isSafeType(columnType));
            		if(this.isSafeType(columnType))
            		{
                		String aggregate[] = new String[4];
                		aggregate=this.getMinMax(tableName, columnName);
                		min=aggregate[0];
                		max=aggregate[1];
                		avg=aggregate[2];
                		stddev=aggregate[3];
                		
            		}
            		else
            		{
            			min="";
            			max="";
            			avg="";
            			stddev="";
            		}
            		
            		String pkString=new String();
            		pkString="";
            		for(j=0;j<npk;j++)
            		{
            			if(columnName.equals(pkList.get(j)))
            					pkString="PK";
            					
            		}
            		
            		String fkString=new String();
            		fkString="";
            		for(j=0;j<nfk;j++)
            		{
            			String tempfk=fkList.get(j);
            			String[] r = tempfk.split("@");
            			if(columnName.equals(r[0]))
            					fkString=r[1];
            					
            		}
            		
            		
    			    out.println("<tr><td>"+i+"</td><td>");
    			    if(numbDistinct<50)
    			    {
    			    	this.countFreq(tableName, columnName, nrows);
    					String filetarget=path+File.separator+dirCount+File.separator+tableName+"."+columnName+".html";
    			    	out.println("<a href="+filetarget+">"+columnName+"</a>");
    			    }
    			    else
    			    out.println(columnName);

    			    out.println("</td><td>"+pkString+"</td><td>"+fkString+"</td><td>"+columnType+"</td><td>"+numbNull+"</td><td>"+numbDistinct+"</td><td>"+min+"</td><td>"+max+"</td><td>"+avg+"</td><td>"+stddev+"</td></tr>"); 

                 }
    			out.println("</table>");
    		    out.println("<p><a href='#' onclick='window.history.back();'>Back</a> <a href='#index'>Index</a>"); 
    		    out.println("</p>");
    		    
            }
		    out.println("<hr>");


		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	

	private long countRows(String tableName)
	{
    	String query="select * from "+tableName;
    	long nrows=0;
        
		 try {
	            Statement statement=connection.createStatement();
	            ResultSet rs = statement.executeQuery(query);
	            rs.last();
	            nrows=rs.getRow();
	            
		 } catch (SQLException e) {
	            e.printStackTrace();
	        }
		 return nrows;

	}
	
	private float countNull(String tableName,String columnName, long nrows)
	{
		long n=0;
		float x=0;
		String query="select count(*) as n from "+tableName +" where "+columnName +" is null";
		//System.out.println(query);
		 try {
	            Statement statement=connection.createStatement();
	            ResultSet rs = statement.executeQuery(query);
	            rs.next();
	            //System.out.println(rs.getString("n"));
	            n=rs.getLong("n");
	            
	            
		 } catch (SQLException e) {
	            e.printStackTrace();
	        }
		
		 //x=(float) Math.round((float) n/nrows*100);
		 x=(float) n*100/ (float) nrows;
		 //float temp = (float) Math.pow(10, 2);
		 //x=Math.round(x*temp)/temp;
		 //System.out.println(x);
		 x=this.roundDecimal(x);
		 return x;
	}
	
	private float roundDecimal(float num)
	{
		 float temp = (float) Math.pow(10, 2);
		 float x=Math.round(num*temp)/temp;

		return x;
	}

	private float countDistinct(String tableName,String columnName, long nrows)
	{
		long n=0;
		float x=0;
		String query="select count(distinct "+columnName+") as n from "+tableName;
		//System.out.println(query);
		 try {
	            Statement statement=connection.createStatement();
	            ResultSet rs = statement.executeQuery(query);
	            rs.next();
	            //System.out.println(rs.getString("n"));
	            n=rs.getLong("n");
	            
	            
		 } catch (SQLException e) {
	            e.printStackTrace();
	        }
		
		 //x=(float) Math.round((float) n/nrows*100);
		 x=(float) n*100/ (float) nrows;
		 //float temp = (float) Math.pow(10, 2);
		 //x=Math.round(x*temp)/temp;
		 x=this.roundDecimal(x);
		 return x;
	}

	private void countFreq(String tableName,String columnName, long nrows)
	{
		String filename=path+File.separator+dirCount+File.separator+tableName+"."+columnName+".html";
        FileWriter fw;
		System.out.println("Counting frequency of "+tableName+"."+columnName);

		String query="SELECT "+columnName+",count(*)/"+nrows+"*100 as freq from "+tableName+" group by "+columnName+" order by freq desc";
		//System.out.println(query);
		 try {
	            Statement statement=connection.createStatement();
	            ResultSet rs = statement.executeQuery(query);
				fw = new FileWriter(filename, false);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out2 = new PrintWriter(bw);
	            
    		    out2.println("<a href='#' onclick='window.history.back();'>Back</a>"); 
    		    out2.println("<br>");
    		    //out2.println(query);
    		    out2.println("<p><table border=1>");
			    out2.println("<tr><th>"+tableName+"."+columnName+"</th><th>Frequency</th></tr>"); 

	            while (rs.next()) {
	            	
	            	String field=rs.getString(columnName);
	            	Float freq=rs.getFloat("freq");
				    out2.println("<tr><td>"+field+"</td><td>"+freq+"</td></tr>"); 
	        	    
	            }
	    	    out2.println("</table>"); 
			    out2.close();

	            
		 } catch (SQLException | IOException e) {
	            e.printStackTrace();
	        }
		
	}

	
	private void sample(String tableName,long nrows)
	{
		String filename=path+File.separator+dirSample+File.separator+tableName+".html";

        FileWriter fw;
		System.out.println("Sample of "+tableName);
		int i;
		long selN;
		if(nrows>10)
			selN=10;
		else
			selN=nrows;
		
		String query="select *,rand() as n from "+tableName+" order by n asc limit 0,"+selN;
		System.out.println(query);
		 try {
	            Statement statement=connection.createStatement();
	            ResultSet rs = statement.executeQuery(query);
				fw = new FileWriter(filename, false);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out2 = new PrintWriter(bw);
	            
                int ncols=rs.getMetaData().getColumnCount();
        		//System.out.println("Num col "+ncols+" di "+tableName);
			    
    		    out2.println("<a href='#' onclick='window.history.back();'>Back</a>"); 
    		    out2.println("<br>");
    		    //out2.println(query);
    		    out2.println("<p><table border=1>");

			    out2.println("<tr>"); 			    	
			    for(i=1;i<ncols;i++)
			    {
				    out2.println("<th>"+rs.getMetaData().getColumnName(i)+"</th>"); 			    	
			    }
			    out2.println("</tr>"); 			    	

			    while (rs.next()) {
	            	
				    out2.println("<tr>"); 			    	
				    for(i=1;i<ncols;i++)
				    {
					    out2.println("<td>"+rs.getString(i)+"</th>"); 			    	
				    }
				    out2.println("</tr>"); 			    	
	        	    
	            }
	    	    out2.println("</table>"); 
			    out2.close();

	            
		 } catch (SQLException | IOException e) {
	            e.printStackTrace();
	        }
		
	}

	
	private String[] getMinMax(String tableName,String columnName)
	{
		//float x[] = new float[4];
		String y[] = new String[4];
		//int i;
		String query="select min("+columnName+") as min,max("+columnName+") as max,round(avg("+columnName+"),2) as avg,round(stddev("+columnName+"),2) as stddev from "+tableName;
		//System.out.println(query);
		 try {
	            Statement statement=connection.createStatement();
	            ResultSet rs = statement.executeQuery(query);
	            rs.next();
	            //System.out.println(rs.getString("n"));
	            y[0]=rs.getString("min");
	            y[1]=rs.getString("max");
	            y[2]=rs.getString("avg");
	            y[3]=rs.getString("stddev");
	          
	            
	            //Float.parseFloat(
	            //x[0]=Float.parseFloat(y[0]);
	            //x[1]=max;
	            //x[2=avg;
	            //x[3]=stddev;
	            	            
		 } catch (SQLException e) {
	            e.printStackTrace();
	        }
	
		 return y;
	}
	
	private boolean isSafeType(String type)
	{
		type=type.toUpperCase();
		int i;
		String[] safe={"INTEGER", "INT", "SMALLINT", "TINYINT", "MEDIUMINT", "BIGINT","DECIMAL", "NUMERIC","FLOAT","DOUBLE","BIT"};
		
		int n=safe.length;
		
		for(i=0;i<n;i++)
		{
			if(type.equals(safe[i]))
					return true;
		}
		
		return false;
	}
	
	private ArrayList<String> getPrimayKeys(String tableName)
	{
		DatabaseMetaData meta;
		ArrayList<String> list=new ArrayList<String>();
		
		System.out.println("Extracting primary key of "+tableName);
		
		try {
			 	meta = (DatabaseMetaData) connection.getMetaData();
			 	//ResultSet rs= meta.getTables(null, null, tableName, new String[]{"TABLE"});
			 	ResultSet rs=meta.getPrimaryKeys(null, null, tableName);
			 	while(rs.next())
			 	{
			 		list.add(rs.getString(4));
			 		//System.out.println("Primary Key: "+rs.getString(4));
			 	}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}

	
	private ArrayList<String> getForeignKeys(String tableName)
	{
		DatabaseMetaData meta;
		ArrayList<String> list=new ArrayList<String>();
		
		System.out.println("Extracting foreign keys of "+tableName);
		
		try {
			 	meta = (DatabaseMetaData) connection.getMetaData();
			 	//ResultSet rs= meta.getTables(null, null, tableName, new String[]{"TABLE"});
			 	ResultSet rs=meta.getImportedKeys(null, null, tableName);
			 	
			 	while(rs.next())
			 	{
			 		String fk=rs.getString(8)+"@"+rs.getString(3)+"."+rs.getString(4);
			 		list.add(fk);
			 		//System.out.println("Foreign Key: "+rs.getString(1));
			 		//System.out.println("Foreign Key: "+rs.getString(2));
			 		//System.out.println("Foreign Key: "+rs.getString(3));
			 		//System.out.println("Foreign Key: "+rs.getString(4));
			 		//System.out.println("Foreign Key: "+rs.getString(5));
			 		//System.out.println("Foreign Key: "+rs.getString(6));
			 		//System.out.println("Foreign Key: "+rs.getString(7));
			 		//System.out.println("Foreign Key: "+rs.getString(8));
			 	}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
