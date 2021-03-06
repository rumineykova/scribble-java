/*
 * Copyright 2009-11 www.scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides a directory based resource locator.
 *
 */
public class DirectoryResourceLocator implements ResourceLocator {
	
	private static final Logger LOG=Logger.getLogger(DirectoryResourceLocator.class.getName());
	
	private String[] _paths=null;

	/**
	 * This is the constructor for the directory resource
	 * locator, initialised with a ':' separated list
	 * of root directories.
	 * 
	 * @param paths The ':' separated list of directory paths
	 */
	public DirectoryResourceLocator(String paths) {
		_paths = paths.split(":");
	}
	
	/**
	 * This method returns the root location containing the supplied
	 * resource.
	 * 
	 * @return The resource's root location
	 */
	public String getResourceRoot(Resource resource) {
		String ret=null;
		
		for (String path : _paths) {
			String fullPath=path+java.io.File.separator+resource.getPath();
			
			java.io.File f=new java.io.File(fullPath);
			
			if (f.exists()) {
				ret = path;
				break;
			}
		}
		
		return (ret);
	}

	/**
	 * {@inheritDoc}
	 */
	public Resource getResource(String relativePath) {
		Resource ret=null;
		
		// Find module
		for (String path : _paths) {
			String fullPath=path;
			
			if (!fullPath.endsWith(java.io.File.separator)) {
				fullPath += java.io.File.separator;
			}
			
			fullPath += relativePath;
			
			java.io.File f=new java.io.File(fullPath);
			
			if (f.isFile()) {
				try {
					ret = new InputStreamResource(relativePath, new java.io.FileInputStream(f));
					
					break;
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Failed to create file input stream for '"+f+"'", e);
				}
			}
		}
		
		return (ret);
	}
	
}
