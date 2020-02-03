package ser321.assign3.esandlin.client;

import java.io.Serializable;
import org.json.JSONObject;
import java.rmi.*;
import java.io.*;
import javax.swing.*;
//import ser321.assign3.esandlin.*;

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
 * Purpose: MediaDescription is a class whose properties describe a single
 * media work -- song or video/clip.
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version January 2020
 */
public class MediaDescription extends Object implements Serializable {

    
   /**
	 * 
	 */
	private static final long serialVersionUID = 7840221892892686434L;
	
	
   public String title;
   public String author;
   public String album;
   public String fileName;

   public MediaDescription(String aTitle, String anAuthor,
                           String anAlbum, String aFileName){
      this.title = aTitle;
      this.author = anAuthor;
      this.album = anAlbum;
      this.fileName = aFileName;
   }

   public MediaDescription(String jsonString){
      this(new JSONObject(jsonString));
      //System.out.println("constructor from json string got: "+jsonString);
      //System.out.println("constructed MD: "+this.toJsonString()+" from json");
   }

   public MediaDescription(JSONObject jsonObj){
      try{
         //System.out.println("constructor from json received: "+
         //                   jsonObj.toString());
         title = jsonObj.getString("title");
         author = jsonObj.getString("author");
         album = jsonObj.getString("album");
         fileName = jsonObj.getString("fileName");
         System.out.println("constructed "+this.toJsonString()+" from json");
      }catch(Exception ex){
         System.out.println("Exception in MediaDescription(JSONObject): "+ex.getMessage());
      }
   }

   public String toJsonString(){
      String ret = "{}";
      try{
         ret = this.toJson().toString(0);
      }catch(Exception ex){
         System.out.println("Exception in toJsonString: "+ex.getMessage());
      }
      return ret;
   }

   public JSONObject toJson(){
      JSONObject obj = new JSONObject();
      try{
         obj.put("title", title);
         obj.put("author", author);
         obj.put("album", album);
         obj.put("fileName", fileName);
      }catch(Exception ex){
         System.out.println("Exception in toJson: "+ex.getMessage());
      }
      return obj;
   }

}
