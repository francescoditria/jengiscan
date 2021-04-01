# jengiscan
Java Engine for Database Scan<br>
jengiscan extracts the profile of MySQL databases and creates an HTML report with fundamental metadata, sample data, and further statistical information (min, max,standard deviation, % null values, % distinct values, ...).<br>

Argument to pass to the main java file:<br>
username.password@host:port/database path

<br>where:
<ul>
<li><i>username</i> is the database user
<li><i>password</i> is the database user's password
<li><i>host</i> is the address of the database (for example, localhost)
<li><i>port</i> is the database port (standard MySQL port is 3306)
<li><i>database</i> is the database name
<li><i>path</i> [optional] is the path to the log file
</ul>

