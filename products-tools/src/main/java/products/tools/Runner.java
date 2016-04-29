package products.tools;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Injector;

import products.tools.config.ToolsModule;

public class Runner {
	static Logger log = LoggerFactory.getLogger(Runner.class);

	@Parameter(names = { "--log", "--verbose" }, description = "Level of verbosity")
	private Integer verbose = 1;

	//@Parameter(names = { "--tool", "-t" }, required = true, description = "Tool name")
	//private String toolName;

	@Parameter(names = { "--help", "-h" }, description = "Display Help", help = true)
	public Boolean help = false;

	@Inject
	private Map<String, Tool> tools;

	public void run(JCommander cmd,String[] args) {
		tools.forEach((k, v) -> cmd.addCommand(k, v));

		try {
			cmd.parse(args);
		} catch(ParameterException e) {
			log.error(e.getLocalizedMessage());
			abort();
		}
		
		if (help) {
			cmd.usage();
			exit();
		}
		
		if (cmd.getParsedCommand()==null) {
			cmd.usage();
			abort();
		}
		Tool tool = tools.get(cmd.getParsedCommand());
		tool.run();
	}

	private void abort() {
		System.exit(-1);
	}

	private void exit() {
		System.exit(0);
	}
	
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ToolsModule());
		Runner runner = injector.getInstance(Runner.class);
		JCommander cmd = new JCommander(runner);

		runner.run(cmd,args);
	}
}
