package products.tools;

import com.google.inject.Guice;
import com.google.inject.Injector;

import products.tools.config.ToolsModule;

public abstract class  Tool {

	public static Injector getInjector() {
		return Guice.createInjector(new ToolsModule());
	}
	
	public abstract void run();
	
	protected void exception(Exception e) {
		e.printStackTrace();
		System.exit(-1);
	}
}
