package products.tools.importers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import products.tools.Tool;

@Parameters(commandDescription = "Import price list")
public class ImportPrices extends Tool {
	enum InputType {
		GENERAL, PLASTIC
	};

	public void process(Path input, InputType type) throws IOException {
		try (Stream<String> lines = Files.lines(input)) {
			// Skip Header
			lines.skip(1);
			// Process lineslues
			lines.forEachOrdered(line -> {
				System.out.println(line);
			});
		}
	}

	@Parameter(names={"--input","-i"},required=true)
	public String inputName;

	public void run() {
		Path input = Paths.get(inputName);

		try {
			process(input, InputType.GENERAL);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ImportPrices prog = getInjector().getInstance(ImportPrices.class);
		prog.inputName = "data/prices.txt";
		prog.run();
	}
}
