package de.fzi.ekgdemonstrator.algorithms;

import de.fzi.ekgeditor.algorithm.jni.IAlgorithmJNI;


public class AlgorithmJNI implements IAlgorithmJNI{
	public AlgorithmJNI(String name){
		System.loadLibrary(name);
	}
	public native void initJNI(int[] init);
	public native int[] process(int[] values);
	@Override
	protected void finalize() throws Throwable {
//		System.out.println("Finalize " + name);
//		try {
//			ClassLoader classLoader = AlgorithmJNI.class.getClassLoader();
//			Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
//			field.setAccessible(true);
//			Vector<Object> libs = (Vector<Object>)field.get(classLoader);
//			Object lib = libs.get(libs.size() - 1);	
//			Method finalize = lib.getClass().getDeclaredMethod("finalize", new Class[0]);
//			finalize.setAccessible(true);
//			finalize.invoke(lib, new Object[0]);
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println();
//		System.out.println("Nach finilize");
//		try {
//			ClassLoader classLoader = AlgorithmJNI.class.getClassLoader();
//			Field field = ClassLoader.class.getDeclaredField("loadedLibraryNames");
//			field.setAccessible(true);
//			Vector<String> libs = (Vector<String>)field.get(classLoader);
//			for(String lib : libs)
//				System.out.println(lib);
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		super.finalize();
	}
}
