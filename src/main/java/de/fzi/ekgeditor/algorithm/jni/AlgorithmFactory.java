package de.fzi.ekgeditor.algorithm.jni;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AlgorithmFactory {
	@SuppressWarnings("unchecked")
	public static IAlgorithmJNI createAlgorithmJNI(String name, String className){
		try{
			Class algorithmClass = Class.forName(className);
			Constructor readerConstructor = algorithmClass.getConstructor(String.class);
			return (IAlgorithmJNI)readerConstructor.newInstance(name);
		} catch (ClassNotFoundException e) {
            System.out.println("Class (" + className + ") could not be found!");
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.out.println("Class (" + className + ") could not be instantiated!");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("Class (" + className + ") could not be accessed!");
            e.printStackTrace();
        } catch (ClassCastException ec) {
            ec.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
		
		return null;
	}
}
