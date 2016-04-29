package products.tools.importers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import products.db.model.Description;
import products.db.model.Price;
import products.db.model.Product;
import products.db.services.DBService;
import products.tools.Tool;

@Parameters(commandDescription = "Import XML file generated by products 1")
public class ImportFromLegacy extends Tool {
	@Inject
	private DBService dbs; 
	
	private XPath xpath;
	private XPathExpression skuXE;
	private XPathExpression descXE;
	private XPathExpression nameXE;
	private XPathExpression transNameXE;
	private XPathExpression transDescXE;
	private XPathExpression priceXE;
	private XPathExpression priceBRLXE;
	private XPathExpression priceUSDXE;
	private XPathExpression priceUpdateXE;
	private SimpleDateFormat formatter = new SimpleDateFormat();
	
	public ImportFromLegacy() throws XPathExpressionException {
		XPathFactory xpf = XPathFactory.newInstance();
		xpath = xpf.newXPath();
	
		skuXE = xpath.compile("./sku");
		descXE = xpath.compile("./description");
		nameXE = xpath.compile("./name");
		transNameXE = xpath.compile("./translation/name");
		transDescXE = xpath.compile("./translation/description");
		priceXE = xpath.compile("./prices/price");
		priceBRLXE = xpath.compile("./price_BRL");
		priceUSDXE = xpath.compile("./price_USD");
		priceUpdateXE = xpath.compile("./updated_on");
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S ZZZ");
	}
	
	public List<Price> parsePrices(Node priceNode) throws XPathExpressionException, ParseException {
		List<Price> res = new java.util.LinkedList<Price>();
		while (priceNode != null) {
			String updatedOn = (String) priceUpdateXE.evaluate(priceNode,XPathConstants.STRING);			
			
			String textUSD = (String) priceUSDXE.evaluate(priceNode,XPathConstants.STRING);
			if (!textUSD.equals("")) {
				Price priceUSD = new Price(new BigDecimal(textUSD),"USD");				
				priceUSD.setValidAfter(new Timestamp(formatter.parse(updatedOn).getTime()));
				res.add(priceUSD);
			}
			String textBRL = (String) priceBRLXE.evaluate(priceNode,XPathConstants.STRING);
			if (!textBRL.equals("")) {
				Price priceBRL = new Price(new BigDecimal(textBRL),"BRL");
				priceBRL.setValidAfter(new Timestamp(formatter.parse(updatedOn).getTime()));
				res.add(priceBRL);
			}
			priceNode=priceNode.getNextSibling();
		}
		return  res;
	}
	
	public Product parseProduct(Node productNode) throws XPathExpressionException, ParseException {
		// SKU
		String sku = (String) skuXE.evaluate(productNode,XPathConstants.STRING);

		System.out.println(sku);
		Product product = new Product(sku);
		
		// Description EN
		String name = (String) nameXE.evaluate(productNode,XPathConstants.STRING);
		String desc = (String) descXE.evaluate(productNode,XPathConstants.STRING);
		
		Description desc_en = new Description();
		desc_en.setLang("en_US");
		desc_en.setName(name);
		desc_en.setDescription(desc);
		product.addDescription(desc_en);

		// Description PT
		String transName = (String) transNameXE.evaluate(productNode,XPathConstants.STRING);
		String transDesc = (String) transDescXE.evaluate(productNode,XPathConstants.STRING);

		Description desc_pt = new Description();
		desc_pt.setLang("pt_BR");
		desc_pt.setName(transName);
		desc_pt.setDescription(transDesc);
		product.addDescription(desc_pt);
		
		// Prices
		Node pricesNode= (Node) priceXE.evaluate(productNode,XPathConstants.NODE);
		List<Price> prices = parsePrices(pricesNode);
		product.addPrices(prices);
		return product;
	}

	public void parseFile(File input) throws FileNotFoundException, XPathExpressionException, ParseException {
		InputSource xml = new InputSource(new FileReader(input));

		Node root = (Node) xpath.evaluate("/list", xml, XPathConstants.NODE);
		Node productNode = root.getFirstChild();
		while (productNode != null) {
			dbs.save(parseProduct(productNode));
			productNode = productNode.getNextSibling();
		}
	}

	@Parameter(names={"--input","-i"},required=true)
	public String inputName;
	
	@Override
	public void run()  {
		File input = new File(inputName);
		try {
			parseFile(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
			ImportFromLegacy prog = getInjector().getInstance(ImportFromLegacy.class);
			prog.inputName="data/products.xml";
			prog.run();
	}
}