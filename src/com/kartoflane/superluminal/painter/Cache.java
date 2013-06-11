package com.kartoflane.superluminal.painter;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.kartoflane.superluminal.core.Main;

/**
 * @author Vhati
 */
public class Cache {
	private static HashMap<String, Image> cachedImagesMap = new HashMap<String, Image>();
	private static HashMap<RGB, Color> cachedColorsMap = new HashMap<RGB, Color>();
	private static HashMap<String, ArrayList<Object>> cachedImageCustomersMap = new HashMap<String, ArrayList<Object>>();
	private static HashMap<RGB, ArrayList<Object>> cachedColorCustomersMap = new HashMap<RGB, ArrayList<Object>>();


	/**
	 * Requests an image from the cache.
	 *
	 * The image must be checked in later, rather than directly
	 * disposed in case other objects are sharing it.

	 * @param customer the interested object
	 * @param path a path to an image within the filesystem, a jar, etc.
	 */
	public static Image checkOutImage(Object customer, String path) {
		Image image = null;
		ArrayList<Object> customers = cachedImageCustomersMap.get(path);
		if (customers == null) {
			customers = new ArrayList<Object>();
			cachedImageCustomersMap.put(path, customers);
		}

		if (customer == null) {
			Main.debug("Error - customer is null.");
			throw new NullPointerException("Customer is null.");
		}
		
		// See if it was already loaded.
		image = cachedImagesMap.get(path);
		if (image != null && image.isDisposed()) {
			cachedImagesMap.remove(path); // Purge the image, if dead.
			customers.clear();            // They couldn't be using it anyway.
			image = null;
		}
		if (path != null) {
			if (image == null) {
				try {
					InputStream stream = customer.getClass().getResourceAsStream(path);
					image = new Image(Display.getCurrent(), stream);
					cachedImagesMap.put(path, image);
				} catch (IllegalArgumentException e) {
					//Main.erDialog.add(String.format("%s: Warning - loading \"%s\": resource not found.", customer.getClass().getSimpleName(), path));
					Main.debug(String.format("%s: Warning - loading \"%s\": resource not found.", customer.getClass().getSimpleName(), path));
				} catch (SWTException e) {
					Main.erDialog.add(String.format("%s: Error loading \"%s\": resource contains invalid data.", customer.getClass().getSimpleName(), path));
				}
			}
			customers.add(customer);
		} else {
			Main.debug(String.format("Cache: warning - tried to check out image with null path (customer: %s).", customer.getClass().getSimpleName()));
		}
		return image;
	}
	
	/**
	 * 
	 * @param customer the interested object
	 * @param path a path to an image wi
	 */
	public static Image checkOutImageAbsolute(Object customer, String path) {
		Image image = null;
		if (path != null) {
			ArrayList<Object> customers = cachedImageCustomersMap.get(path);
			if (customers == null) {
				customers = new ArrayList<Object>();
				cachedImageCustomersMap.put(path, customers);
			}
	
			image = cachedImagesMap.get(path);
			if (image != null && image.isDisposed()) {
				cachedImagesMap.remove(path);
				customers.clear();
				image = null;
			}
			if (image == null) {
				try {
					File f = new File(path);
					if (f.exists()) {
						image = new Image(Display.getCurrent(), path);
						cachedImagesMap.put(path, image);
					} else {
						//Main.erDialog.add(String.format("%s: Error loading \"%s\": file not found.", customer.getClass().getSimpleName(), f.getName()));
					}
				} catch (IllegalArgumentException e) {
					Main.erDialog.add(String.format("%s: Warning - loading \"%s\": null argument.", customer.getClass().getSimpleName(), path));
				} catch (SWTException e) {
					Main.erDialog.add(String.format("%s: Error loading \"%s\": file contains invalid data.", customer.getClass().getSimpleName(), path));
				}
			}
			customers.add(customer);
		}
		return image;
	}
	
	public static Color checkOutColor(Object customer, RGB rgb) {
		Color color = null;
		ArrayList<Object> customers = cachedColorCustomersMap.get(rgb);

		if (customers == null) {
			customers = new ArrayList<Object>();
			cachedColorCustomersMap.put(rgb, customers);
		}
		
		color = cachedColorsMap.get(rgb);
		if (color != null && color.isDisposed()) {
			cachedColorsMap.remove(rgb);
			customers.clear();
			color = null;
		}
		
		if (color == null) {
			try {
				color = new Color(Main.shell.getDisplay(), rgb);
				cachedColorsMap.put(rgb, color);
			} catch (IllegalArgumentException e) {
				//Main.erDialog.add(String.format("%s: Error loading color %s: null RGB argument.", customer.getClass().getSimpleName(), rgb));
			}
		}
		customers.add(customer);
		return color;
	}
	

	/**
	 * Signals that an object is done using an image it had checked out.
	 * When the last such object checks in, the image is disposed.
	 */
	public static void checkInImage(Object customer, String path) {
		ArrayList<Object> customers = cachedImageCustomersMap.get(path);

		if (customers != null && customers.size() > 0) {
			// Can't use equals(), so list.remove is also out.
			Iterator<Object> it = customers.iterator();
			while (it.hasNext()) {
				if (it.next() == customer) {
					it.remove();
					break;
				}
			}
		}
		if (customers == null || customers.size() == 0) {
			Image image = cachedImagesMap.get(path);
			if (image != null && !image.isDisposed()) {
				image.dispose();
			}
			cachedImagesMap.remove(path);
		}
	}
	
	public static void checkInImageAbsolute(Object customer, String path) {
		ArrayList<Object> customers = cachedImageCustomersMap.get(path);

		if (customers != null && customers.size() > 0) {
			Iterator<Object> it = customers.iterator();
			while (it.hasNext()) {
				if (it.next() == customer) {
					it.remove();
					break;
				}
			}
		}
		if (customers == null || customers.size() == 0) {
			Image image = cachedImagesMap.get(path);
			if (image != null && !image.isDisposed()) {
				image.dispose();
			}
			cachedImagesMap.remove(path);
		}
	}
	
	public static void checkInColor(Object customer, RGB rgb) {
		ArrayList<Object> customers = cachedColorCustomersMap.get(rgb);
		
		if (customers != null && customers.size() > 0) {
			Iterator<Object> it = customers.iterator();
			while (it.hasNext()) {
				if (it.next() == customer) {
					it.remove();
					break;
				}
			}
		}
		if (customers == null || customers.size() == 0) {
			Color color = cachedColorsMap.get(rgb);
			if (color != null && !color.isDisposed()) {
				color.dispose();
			}
			cachedColorsMap.remove(rgb);
		}
	}

	public static void disposeImages() {
		for (Map.Entry<String, Image> entry : cachedImagesMap.entrySet()) {
			if (entry.getValue() != null && !entry.getValue().isDisposed()) {
				entry.getValue().dispose();
			}
		}
		cachedImagesMap.clear();
		cachedImageCustomersMap.clear();
	}
	
	public static void disposeColors() {
		for (Map.Entry<RGB, Color> entry : cachedColorsMap.entrySet()) {
			if (entry.getValue() != null && !entry.getValue().isDisposed()) {
				entry.getValue().dispose();
			}
		}
		cachedColorsMap.clear();
		cachedColorCustomersMap.clear();
	}

	public static void dispose() {
		disposeImages();
		disposeColors();
	}
}
