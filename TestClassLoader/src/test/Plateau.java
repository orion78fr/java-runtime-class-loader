package test;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.ProtectionDomain;

import javax.swing.JFileChooser;

import fr.parisdescartes.orion.classloader.RuntimeClassLoader;
import fr.parisdescartes.orion.classloader.RuntimeClassLoaderException;

public class Plateau implements InterfacePlateau {

	/**
	 * 0 si vide 1 si j1 2 si j2
	 */
	private int[][] plateau_interne;

	public Plateau() throws MalformedURLException, InstantiationException, IllegalAccessException, RuntimeClassLoaderException {
		plateau_interne = new int[10][];
		for (int i = 0; i < 10; i++) {
			plateau_interne[i] = new int[10];
			for(int j = 0; j<10; j++) plateau_interne[i][j] = 1;
		}
		RuntimeClassLoader rcl2 = RuntimeClassLoader.getInstance();
		JFileChooser choose = new JFileChooser(new File("").getAbsolutePath());
		int returnVal = choose.showOpenDialog(null);
		File file = null;
		if(returnVal == JFileChooser.APPROVE_OPTION){
			file = choose.getSelectedFile();
		}
	    /*URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new URL("file:///" + file.getParent() + "/")}, getClass().getClassLoader());
	    Class c = null;
	    try {
	      c = classLoader.loadClass("test2." + file.getName().substring(0, file.getName().lastIndexOf(".")));
	    }
	    catch (ClassNotFoundException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }*/
		
		Class<?> c = null;
	    /*  try{
	        FileChannel roChannel = new RandomAccessFile(file, "r").getChannel();
	        ByteBuffer bb = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int)roChannel.size());
	        String folder = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\bin\\") + 5);
	        System.out.println(folder);
	        PersoClassLoader pcl = new PersoClassLoader(new URL("file:/" + folder));
	        //c = pcl.loadClass("test2.MonIA2");
	        c = pcl.load(bb);
	        }
	      catch(Exception e){
	    	  e.printStackTrace();
	      }*/
		
		try
		{
			RuntimeClassLoader rcl = RuntimeClassLoader.getInstance();
			c = rcl.load(file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	    
		InterfaceIA ia1 = (InterfaceIA) c.newInstance();
		ia1.setPlateau(this);
		
		InterfaceIA ia2 = (InterfaceIA) c.newInstance();
		ia2.setPlateau(this);
		
		String coup;
		for(int i=0; i<50; i++){
			if(i%2 == 0)
				coup = ia1.getCoup();
			else
				coup = ia2.getCoup();
			
			System.out.println(coup);
		}
	}

	@Override
	public int[][] getPlateau() {
		int[][] plateauTemp = new int[10][];
		for (int i = 0; i < 10; i++) {
			plateauTemp[i] = new int[10];
		}
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				plateauTemp[i][j] = plateau_interne[i][j];
			}
		}
		return plateauTemp;
	}
	
	public static void main(String[] args) throws MalformedURLException, InstantiationException, IllegalAccessException, RuntimeClassLoaderException{
		new Plateau();
	}
	private class PersoClassLoader extends URLClassLoader{
		
        public PersoClassLoader(URL u)
		{
        	super(new URL[]{u});
			try
			{
				URLClassLoader sysloader = (URLClassLoader) ClassLoader
	                    .getSystemClassLoader();
	            Class<URLClassLoader> sysclass = URLClassLoader.class;
				Method method = sysclass.getDeclaredMethod("addURL", u.getClass());
	            method.setAccessible(true);
	            method.invoke(sysloader, new Object[] { u });
			}
			catch (Exception e){ e.printStackTrace(); }
		}

		public Class<?> load(ByteBuffer bb){
        	return defineClass((String)null, bb, (ProtectionDomain)null);
        }
	}
}
