package JavaUtils.ClassLoader;

import java.io.File;
import java.net.MalformedURLException;
/**
 * A Factory Class for UtilClasses
 * @author Max
 * 
 * @param <Type> The Type of The Object that would be builded
 */
public class UtilClassBuilder<Type> {

	File ord;
	
	/**
	 * Creates a UtilClassBuilder with the Folder
	 * @param folder The Folder where the Files are
	 */
	public UtilClassBuilder(String folder) {
		ord = new File(folder);
	}

	/**
	 * Creates a UtilClassBuilder with the Folder
	 * @param folder The Folder where the Files are
	 */
	public UtilClassBuilder(File folder) {
		ord = folder;
	}
	
	/**
	 * Makes one UtilClass from the given File
	 * @param file The File that would be 
	 * @return The Generated UtilClass
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	public UtilClass<Type> newUtilClass(String file) throws NoSuchMethodException, SecurityException, ClassNotFoundException, MalformedURLException {
		return new UtilClass<Type>(ord, file);
	}

	/**
	 * Makes one UtilClass from the given Jar and Class
	 * @param jar The Jar File
	 * @param clas The Path of the Class seperated with an '.'
	 * @return The Generated UtilClass
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 */
	public UtilClass<Type> newUtilJarClass(File jar,String clas) throws MalformedURLException, ClassNotFoundException{
		return new UtilClass<Type>(jar,clas);
	}
	
	/**
	 * Makes one UtilClass from the given Jar and Class
	 * @param jarName The Name of the Jar in the folder
	 * @param clas The Path of the Class seperated with an '.'
	 * @return The Generated UtilClass
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 */
	public UtilClass<Type> newUtilJarClass(String jarName,String clas) throws MalformedURLException, ClassNotFoundException{
		return new UtilClass<Type>(ord.getAbsolutePath() + "/" + jarName,clas);
	}
	
	/**
	 * Makes an Array of UtilClasses from the given Files (Don't use it with Jars)
	 * @param files The Files that would be loaded
	 * @return An Array of UtilClasses
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	public UtilClass<Type>[] newUtilClasses(String... files) throws NoSuchMethodException, SecurityException, ClassNotFoundException, MalformedURLException {
		UtilClass<Type>[] cl = new UtilClass[files.length];
		int index = 0;
		for (String s : files) {
			cl[index] = new UtilClass<Type>(ord, s);
			index++;
		}
		return cl;
	}
}
