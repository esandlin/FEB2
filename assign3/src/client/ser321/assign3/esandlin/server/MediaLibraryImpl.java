package ser321.assign3.esandlin.server;


import java.io.*;
import java.util.*;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import java.rmi.server.*;
import java.rmi.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Set;

import ser321.assign3.esandlin.client.MediaDescription;
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
public class MediaLibraryImpl extends UnicastRemoteObject implements MediaLibrary{

   private Hashtable<String,MediaDescription> aLib;
   private static final String fileName="music.json";
   
   public MediaLibraryImpl () throws RemoteException {
      this.aLib = new Hashtable<String,MediaDescription>();
     try{
         InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.fileName);
         if(is==null){
            is = new FileInputStream(new File(this.fileName));
         }
         JSONObject media = new JSONObject(new JSONTokener(is));
         Iterator<String> it = media.keys();
         while (it.hasNext()){
            String mediaTitle = it.next();
            JSONObject aMed = media.optJSONObject(mediaTitle);
            if (aMed != null){
               MediaDescription md = new MediaDescription(aMed);
               aLib.put(mediaTitle, md);
            }
         }
      }catch (Exception ex){
         System.out.println("Exception reading "+fileName+": "+ex.getMessage());
      }
   }

   public boolean add(MediaDescription md){
      boolean result = false;
      System.out.println("Adding: "+md.title);
      try{
         aLib.put(md.title,md);
         result = true;
      }catch(Exception ex){
         System.out.println("exception in add: "+ex.getMessage());
      }
      return result;
   }

   public boolean remove(String mediaTitle){
      boolean result = false;
      System.out.println("Removing "+mediaTitle);
      try{
         aLib.remove(mediaTitle);
         result = true;
      }catch(Exception ex){
         System.out.println("exception in remove: "+ex.getMessage());
      }
      return result;
   }

   public MediaDescription get(String mediaTitle){
      MediaDescription result = null;
      try{
         result = aLib.get(mediaTitle);
      }catch(Exception ex){
         System.out.println("exception in get: "+ex.getMessage());
      }
      return result;
   }

   public String[] getTitles(){
      String[] result = null;
      try{
         Set<String> vec = aLib.keySet();
         result = vec.toArray(new String[]{});
      }catch(Exception ex){
         System.out.println("exception in getTitles: "+ex.getMessage());
      }
      return result;
   }
   
   public static void main(String args[]) {
       try {
          String hostId="localhost";
          String regPort="1099";
          if (args.length >= 2){
         hostId=args[0];
             regPort=args[1];
          }
          //System.setSecurityManager(new RMISecurityManager()); // rmisecmgr deprecated
          MediaLibrary obj = new MediaLibraryImpl();
          Naming.rebind("rmi://"+hostId+":"+regPort+"/EmployeeServer", obj);
          System.out.println("Server bound in registry as: "+
                             "rmi://"+hostId+":"+regPort+"/EmployeeServer");
       }catch (Exception e) {
          e.printStackTrace();
       }
    }

}
