package JavaUtils.ClassLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * An Tool to initialise Classes to an Object
 * @author Max
 * 
 * @param <Type> The Type of the Object
 */
public class UtilClass<Type> {

	Constructor<?>[] c;
	Class<Type> cw;
	/**
	 * Generates a UtilClass with a Class
	 * @param cw The Class That would be initialised
	 */
	UtilClass(Class<Object> cw){
		c = cw.getConstructors();
	}
	
	/**
	 * Generates a UtilClass with a Class
	 * @param folder The Folder where the Class is in
	 * @param clas The Name of the Class
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 */
	public UtilClass(String folder,String clas) throws MalformedURLException, ClassNotFoundException{
		URL u = new File(folder).toURL();

		ClassLoader cl = URLClassLoader.newInstance(new URL[] { u });

		cw = (Class<Type>) cl.loadClass(clas);
		
		c = cw.getConstructors();
	}
	
	/**
	 * Generates a UtilClass with a Class in a Jar
	 * @param jar The Jar
	 * @param clas The Class Path seperated with an '.'
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 */
	public UtilClass(File jar, String klasse) throws MalformedURLException, ClassNotFoundException{
		URL u = jar.toURL();
		
		ClassLoader cl = URLClassLoader.newInstance(new URL[] { u });
		
		cw = (Class<Type>) cl.loadClass(klasse);
		
		c = cw.getConstructors();
	}
	
	/**
	 * Initialising the Constructor with undefined Numbers of Arguments
	 * @param initArgs The Arguments for This Object
	 * @return The Created Object, Returns Null if the Class has no Constructor with this number of Arguments
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Type initialise(Object... initArgs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Constructor<?> co = null;
		for(Constructor<?> con : c){
			if(con.getParameterTypes().length == initArgs.length){
				co = con;
			}
		}
		if(co==null){
			return null;
		}
		if (initArgs != null) {
			return (Type) co.newInstance(initArgs);
		} else {
			return (Type) co.newInstance();
		}
	}
	
	public Class getUtilizedClass(){
		return cw;
	}
	
}
