import core.Controller;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class JengiScan {

	public static void main(String[] args) throws ScriptException {

		
		String target=args[0];
		String logfile=args[1];

		Controller controller=new Controller();
		controller.start(target,logfile);

	}

}
