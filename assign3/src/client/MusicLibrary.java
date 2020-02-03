package ser321.assign3.esandlin.client;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import org.json.JSONObject;
import org.json.JSONString;

/**
 * @class: SER 321
 * @author: Eric Sandlin
 */
/**
 * Copyright 2020 Tim Lindquist,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Purpose: MediaLibraryImpl is the implementing class for library interface
 *
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version January 2020
 */
public class MusicLibrary implements JSONString, Serializable{
    
    private String album; // the name of this group
    private Vector<Album> albums = new Vector<Album>(); // holds albums
    private Vector<Track> tracks = new Vector<Track>(); // holds albums


    
    /**
     * auto generated 
     */
    private static final long serialVersionUID = 814680781750144597L;

    /*
     * Constructor
     */
    public MusicLibrary() {
        
    }
  
 	 /*
 	  * @param
 	  * @return 
 	  * - a method to get the titles of albums stored in the library. It could return an array of string titles. 
 	  */
 	public String getAlbumNames(String []titles) {
        
 	    return null;
 	}
 	
 	/*
     * @param
     * @return 
 	 * - a method to get an Album object based on its title. You can assume that all albums in the library will have a unique title. 
 	 */
	public Album getAlbum() {
        
	    return null;
	}
	
	/*
	 * @param  
	 * - a method to add an album to the library. Used for example when the user wants to add the result of a Last.fm search to the library.
	 */ 
	public void addAlbum(){
	    
	}
	/*
     * @param 
	 * - a method to remove an album from the library.
	 */
	public void removeAlbum() {
	    
	}
	
	/*
	 * 
	 * - a method to serialize the library to a json file.
	 */
	public void saveLibraryToFile() {
	    
	}
	
	/*
	 * 
	 * - a method to (re)initialize the library by de-serializing from a json file.
	 */
	public void restoreLibraryFromFile(){
	    
	}

    @Override
    public String toJSONString() {
//        String ret;
//        JSONObject obj = new JSONObject();
//        obj.put("name",name);
//        for (Enumeration<User> e = users.elements(); e.hasMoreElements();){
//           User usr = (User)e.nextElement();
//           obj.put(usr.getId(),usr.toJSONObject());
//        }
//        ret = obj.toString();
//        //System.out.println("group tojsonstring returning string: "+ret);
//        return ret;
        return null;
    }
 }
 
 
 