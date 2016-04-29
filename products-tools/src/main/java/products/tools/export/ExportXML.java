package products.tools.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import products.db.model.Product;
import products.db.services.DBService;
import products.tools.Tool;

@Parameters(commandDescription = "Export Products List in XML")
public class ExportXML extends Tool {

	@Inject
	private DBService dbs;

	@Parameter(names = { "--output", "-o" })
	public String outputName;

	private void export(OutputStream out) throws Exception {
		/*
		 * XStream xstream = new XStream(new StaxDriver());
		 * xstream.alias("product", Product.class); xstream.alias("description",
		 * Description.class); xstream.alias("price", Price.class);
		 * xstream.alias("products", List.class);
		 */

		
		 XMLStreamWriter xmlStreamWriter =
		           XMLOutputFactory.newInstance().createXMLStreamWriter( out );
		 
		 xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
		 xmlStreamWriter.writeStartElement("products");
		JAXBContext jaxbContext = JAXBContext.newInstance(Product.class);

		Marshaller marshaller = jaxbContext.createMarshaller();
		//jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        
		List<Product> lst = dbs.listProducts();

		lst.forEach(product ->{
			try {
				marshaller.marshal(product,xmlStreamWriter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		});
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndDocument();
		// xstream.toXML(lst,out);
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
