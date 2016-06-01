package products.tools.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Optional;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import products.db.model.Product;
import products.db.services.DBService;
import products.tools.Tool;

@Parameters(commandDescription = "Export Products List in XML")
public class GetJson extends Tool {

	@Inject
	private DBService dbs;

	@Parameter(names = { "--output", "-o" })
	public String outputName;

	@Parameter(names = { "--id", "-i" })
	public long id;

	private void export(OutputStream out) throws Exception {
		Optional<Product> product = dbs.getProduct(id);
		PrintStream p=new PrintStream(out);
		p.println(dbs.json(product.get()));
	}

	@Override
	public void run() {
		OutputStream out = System.out;
		try {
			if (outputName != null) {
				out = new FileOutputStream(outputName);
			}
			export(out);
		} catch (Exception e) {
			exception(e);
		} finally {
			if (out != System.out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
