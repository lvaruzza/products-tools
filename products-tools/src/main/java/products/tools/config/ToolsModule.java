package products.tools.config;


import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

import products.db.config.DBModule;
import products.tools.Runner;
import products.tools.Tool;
import products.tools.export.ExportXML;
import products.tools.export.ListProducts;
import products.tools.importers.ImportFromLegacy;
import products.tools.importers.ImportPrices;

public class ToolsModule extends AbstractModule{

	@Override
	protected void configure() {
        install(new DBModule());
		MapBinder<String,Tool> toolsBinder = MapBinder.newMapBinder(binder(),String.class,Tool.class);

		toolsBinder.addBinding("import-legacy").to(ImportFromLegacy.class);
		toolsBinder.addBinding("import-prices").to(ImportPrices.class);
		toolsBinder.addBinding("export-xml").to(ExportXML.class);
		toolsBinder.addBinding("list").to(ListProducts.class);
		
		binder().bind(Runner.class);
	}

}
