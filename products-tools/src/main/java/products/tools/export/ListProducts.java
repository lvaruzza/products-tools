package products.tools.export;

import java.io.PrintStream;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import products.db.services.DBService;
import products.tools.Tool;

@Parameters(commandDescription = "Export Products List in XML")
public class ListProducts extends Tool {

	@Inject
	private DBService dbs;

	@Parameter(names = { "--output", "-o" })
	public String outputName;


	@Override
	public void run() {
		PrintStream out=System.out;
		
		dbs.listProducts().forEach( product ->{
			out.println(String.format("%s", product.getSku()));
			product.getDescriptions().forEach(d -> 
				out.println(String.format("\t%s (%s)",
						d.getName().substring(0, Math.min(75, d.getName().length())),d.getLang()))); 
		});
	}

}
