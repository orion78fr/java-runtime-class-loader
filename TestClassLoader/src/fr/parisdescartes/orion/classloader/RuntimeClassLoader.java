package fr.parisdescartes.orion.classloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.ProtectionDomain;

/**
 * This class is a tool to load class files located with a File object during runtime.<br />
 * This adds urls to the SystemClassLoader which can be dangerous.<br />
 * This class is provided as-is, so use it at your own risk!
 * 
 * @author Orion
 * @version 1.0
 * 
 */
public class RuntimeClassLoader {
	private static RuntimeClassLoader rcl = null;

	private URLClassLoader sysloader;
	private Method addUrlMethod;
	private Method defineClassMethod;

	public static RuntimeClassLoader getInstance() throws RuntimeClassLoaderException {
		if (rcl == null)
			rcl = new RuntimeClassLoader();
		return rcl;
	}

	private RuntimeClassLoader() throws RuntimeClassLoaderException {
		sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

		try {
			addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeClassLoaderException("Unable to load the addURL method for the SystemClassLoader");
		}
		addUrlMethod.setAccessible(true);

		try {
			defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, ByteBuffer.class, ProtectionDomain.class });
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeClassLoaderException("Unable to load the defineClass method for the SystemClassLoader");
		}
		defineClassMethod.setAccessible(true);
	}

	/**
	 * Load a class giving its File object
	 * 
	 * @param file
	 *            The .class file to load
	 * @return The class object of the .class file
	 * @throws RuntimeClassLoaderException
	 */
	public Class<?> load(File file) throws RuntimeClassLoaderException {
		Class<?> c = null;
		if (file != null) {
			if (file.getAbsolutePath().endsWith(".class")) {
				Class<?> tempClass = null;
				try {
					FileChannel roChannel = new RandomAccessFile(file, "r").getChannel();
					ByteBuffer bb = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size());
					tempClass = (Class<?>) defineClassMethod.invoke(sysloader, new Object[] { (String) null, bb, (ProtectionDomain) null });
				} catch (FileNotFoundException e) {
					throw new RuntimeClassLoaderException("The file can't be found");
				} catch (IOException e) {
					throw new RuntimeClassLoaderException("The file can't be read");
				} catch (Exception e) {
					throw new RuntimeClassLoaderException("The class can't be instancied");
				}

				String classFullName = tempClass.getCanonicalName();
				String pathToClass = classFullName.replace('.', File.separatorChar).concat(".class");
				String fullPath = file.getAbsolutePath();

				if (fullPath.endsWith(pathToClass)) {
					String folderPath = fullPath.substring(0, fullPath.length() - pathToClass.length());
					try {
						addUrlMethod.invoke(sysloader, new Object[] { new URL("file:/" + folderPath) });
					} catch (Exception e) {
						throw new RuntimeClassLoaderException("The class path can't be added to the SystemClassLoader");
					}
					try {
						c = sysloader.loadClass(classFullName);
					} catch (ClassNotFoundException e) {
						throw new RuntimeClassLoaderException("The class can't be found after adding its path, should not happen because of the verifications");
					}
				} else {
					throw new RuntimeClassLoaderException("The class is not in its package folder representation");
				}
			} else {
				throw new RuntimeClassLoaderException("This file is not a Class File");
			}
		} else {
			throw new RuntimeClassLoaderException("The file is null");
		}
		return c;
	}

}
