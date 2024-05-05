package lol.athena.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

public class PluginClassLoader extends URLClassLoader {

	
	public PluginClassLoader(String name, URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
	
	public PluginClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
		
	}

    static {
       registerAsParallelCapable();
    }

	public void addPluginClasses(URL[] urls) {
		for (URL url : urls) {
			//Athena.getInstance().getLogger().warning(url.getPath());
			addURL(url);
		}
	}
	
		
	@Override
	public URL getResource(String name) {
		return findResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return findResources(name);
	}
}
