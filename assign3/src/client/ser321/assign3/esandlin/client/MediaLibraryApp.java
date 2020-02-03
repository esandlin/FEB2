package ser321.assign3.esandlin.client;



import javax.swing.*;
import java.io.*;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.nio.charset.Charset;
import javax.sound.sampled.*;
import java.beans.*;
import java.net.*;
import javax.swing.tree.*;

import ser321.assign2.lindquis.MediaLibraryGui;
import ser321.assign3.esandlin.client.MediaDescription;
import ser321.assign3.esandlin.server.MediaLibrary;
import ser321.assign3.esandlin.server.MediaLibraryImpl;

import javax.swing.event.*;
import javax.swing.text.html.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.lang.Runtime;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLConnection;
import java.time.Duration;

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
 * Purpose: MediaLibraryApp instructor sample for solving Spring 2020 ser321 assignments.
 * This problem provides for browsing and managing information about
 * music albums. It uses a Swing JTree, and JTextField controls to 
 * realize a GUI with a split pane. The left pane contains an expandable
 * JTree of the media library.
 * This program is a sample controller for the non-distributed version of
 * the system.
 * The right pane contains components that allow viewing, modifying and adding
 * albums, tracks, and corresponding files.
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu),
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version January 2020
 */
public class MediaLibraryApp extends MediaLibraryGui implements
                                     TreeWillExpandListener,
     					             ActionListener,
					               	 TreeSelectionListener {

   private static final boolean debugOn = true;
   private static final String searchImg = "https://lastfm.freetls.fastly.net/i/u/300x300/3b54885952161aaea4ce2965b2db1638.png";
   private static final String pre = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&artist=";
   private String url;
   private boolean stopPlaying;         //shared, but not synchronized with playing thread.
   private MediaLibrary library;
   private String lastFMKey;

   public MediaLibraryApp(String author, String authorKey) throws RemoteException {
      super(author);
      this.lastFMKey = authorKey;
      library = new MediaLibraryImpl();
      stopPlaying = false;
      // set exit on close for window
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // register this object as an action listener for menu item clicks. This will cause
      // my actionPerformed method to be called every time the user selects a menuitem.
      for(int i=0; i<userMenuItems.length; i++){
         for(int j=0; j<userMenuItems[i].length; j++){
            userMenuItems[i][j].addActionListener(this);
         }
      }
      // register this object as an action listener for the Search button. This will cause
      // my actionPerformed method to be called every time the user clicks the Search button
      searchJButt.addActionListener(this);
      try{
         //tree.addTreeWillExpandListener(this);  // add if you want to get called with expansion/contract
         tree.addTreeSelectionListener(this);
         rebuildTree();
      }catch (Exception ex){
         JOptionPane.showMessageDialog(this,"Handling "+
                                       " constructor exception: " + ex.getMessage());
      }
      try{
         /*
          * display an image just to show how the album or artist image can be displayed in the
          * app's window. setAlbumImage is implemented by MediaLibraryGui class. Call it with a
          * string url to a png file as obtained from an album search.
         */
         setAlbumImage(searchImg);
      }catch(Exception ex){
         System.out.println("unable to open image png");
      }
      setVisible(true);
   }

   /**
    * A method to facilite printing debugging messages during development, but which can be
    * turned off as desired.
    *
    **/

   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

   /**
    * Create and initialize nodes in the JTree of the left pane.
    * buildInitialTree is called by MediaLibraryGui to initialize the JTree.
    * Classes that extend MediaLibraryGui should override this method to 
    * perform initialization actions specific to the extended class.
    * The default functionality is to set base as the label of root.
    * In your solution, you will probably want to initialize by deserializing
    * your library and displaying the categories and subcategories in the
    * tree.
    * @param root Is the root node of the tree to be initialized.
    * @param base Is the string that is the root node of the tree.
    */
   public void buildInitialTree(DefaultMutableTreeNode root, String base){
      //set up the context and base name
      try{
         root.setUserObject(base);
      }catch (Exception ex){
         JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
         ex.printStackTrace();
      }
   }

   /**
    *
    * method to build the JTree of music shown in the left panel of the UI. The
    * field tree is a JTree as defined and initialized by MediaLibraryGui class.
    * it is defined to be protected so it can be accessed by extending classes.
    * This version of the method uses the music library to get the names of
    * tracks. Your solutions will need to replace this structure with one that
    * keeps information particular to both Album and Track (two classes Album.java,
    * and Track.java). Your music library will store and provide access to Album
    * and Track objects.
    * This method is provided to demonstrate one way to add nodes to a JTree based
    * on an underlying storage structure.
    * See also the methods clearTree, valueChanged, and getSubLabelled defined in this class.
 * @throws RemoteException 
    **/
   public void rebuildTree() throws RemoteException{
      tree.removeTreeSelectionListener(this);
      //tree.removeTreeWillExpandListener(this);
      DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
      clearTree(root, model);
      DefaultMutableTreeNode musicNode = new DefaultMutableTreeNode("Music");
      model.insertNodeInto(musicNode, root, model.getChildCount(root));
      // put nodes in the tree for all  registered with the library
      String[] musicList = library.getTitles();
      for (int i = 0; i<musicList.length; i++){
         MediaDescription aMD = library.get(musicList[i]);
         String aMTitle = aMD.title;
         debug("Adding song with title:"+aMD.title);
         DefaultMutableTreeNode toAdd = new DefaultMutableTreeNode(aMTitle);
         DefaultMutableTreeNode subNode = getSubLabelled(musicNode,aMD.album);
         if(subNode!=null){ // if album subnode already exists
            debug("album exists: "+aMD.album);
            //subNode.add(toAdd);
            model.insertNodeInto(toAdd, subNode,
                                 model.getChildCount(subNode));
         }else{ // album node does not exist
            DefaultMutableTreeNode anAlbumNode =
               new DefaultMutableTreeNode(aMD.album);
            debug("no album, so add one with name: "+aMD.album);
            //root.add(aCatNode);
            model.insertNodeInto(anAlbumNode, musicNode,
                                 model.getChildCount(musicNode));
            DefaultMutableTreeNode aSubCatNode = 
               new DefaultMutableTreeNode("aSubCat");
            debug("adding subcat labelled: "+"aSubCat");
            model.insertNodeInto(toAdd,anAlbumNode,
                                 model.getChildCount(anAlbumNode));
         }
      }
      // expand all the nodes in the JTree
      for(int r =0; r < tree.getRowCount(); r++){
         tree.expandRow(r);
      }
      tree.addTreeSelectionListener(this);
      //tree.addTreeWillExpandListener(this);
      //setVisible(true);
   }

   private void clearTree(DefaultMutableTreeNode root, DefaultTreeModel model){
      try{
         DefaultMutableTreeNode next = null;
         int subs = model.getChildCount(root);
         for(int k=subs-1; k>=0; k--){
            next = (DefaultMutableTreeNode)model.getChild(root,k);
            debug("removing node labelled:"+(String)next.getUserObject());
            model.removeNodeFromParent(next);
         }
      }catch (Exception ex) {
         System.out.println("Exception while trying to clear tree:");
         ex.printStackTrace();
      }
   }

   private DefaultMutableTreeNode getSubLabelled(DefaultMutableTreeNode root,
                                                 String label){
      DefaultMutableTreeNode ret = null;
      DefaultMutableTreeNode next = null;
      boolean found = false;
      for(Enumeration<TreeNode> e = root.children();
          e.hasMoreElements();){
         next = (DefaultMutableTreeNode)e.nextElement();
         debug("sub with label: "+(String)next.getUserObject());
         if (((String)next.getUserObject()).equals(label)){
            debug("found sub with label: "+label);
            found = true;
            break;
         }
      }
      if(found)
         ret = next;
      else
         ret = null;
      return (DefaultMutableTreeNode)ret;
   }

   public void treeWillCollapse(TreeExpansionEvent tee) {
      debug("In treeWillCollapse with path: "+tee.getPath());
      tree.setSelectionPath(tee.getPath());
   }

   public void treeWillExpand(TreeExpansionEvent tee) {
      debug("In treeWillExpand with path: "+tee.getPath());
      //DefaultMutableTreeNode dmtn =
      //    (DefaultMutableTreeNode)tee.getPath().getLastPathComponent();
      //System.out.println("will expand node: "+dmtn.getUserObject()+
      //		   " whose path is: "+tee.getPath());
   }

   public void valueChanged(TreeSelectionEvent e) {
      try{
         tree.removeTreeSelectionListener(this);
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();
         if(node!=null){
            String nodeLabel = (String)node.getUserObject();
            debug("In valueChanged. Selected node labelled: "+nodeLabel);
            // is this a terminal node?
            if(node.getChildCount()==0 &&
               (node != (DefaultMutableTreeNode)tree.getModel().getRoot())){
               MediaDescription md = library.get(nodeLabel);
               trackJTF.setText(nodeLabel);
               authorJTF.setText(md.author);
               albumJTF.setText(md.album);
               fileNameJTF.setText(md.fileName);
             }
         }
      }catch (Exception ex){
         ex.printStackTrace();
      }
      tree.addTreeSelectionListener(this);
   }

   public void actionPerformed(ActionEvent e) {
      tree.removeTreeSelectionListener(this);
      if(e.getActionCommand().equals("Exit")) {
         System.exit(0);
      }else if(e.getActionCommand().equals("Save")) {
      
         boolean savRes = true;
         System.out.println("Save "+((savRes)?"successful":"not implemented"));
         
      }else if(e.getActionCommand().equals("Restore")) {
      
         boolean resRes = true;
         try {
			rebuildTree();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         System.out.println("Restore "+((resRes)?"successful": "implemented"));
         
      }else if(e.getActionCommand().equals("AlbumAdd")) {
      	//************************************************
        // copied TrackAdd and changed
          int typeInd = genreJCB.getSelectedIndex();
          MediaDescription aMD = new MediaDescription(trackJTF.getText().trim(),
                                                      authorJTF.getText().trim(),
                                                      albumJTF.getText().trim(),
                                                      fileNameJTF.getText().trim());
          try {
			library.add(aMD);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
          try {
			rebuildTree();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         
          JFileChooser chooser = new JFileChooser();
          chooser.setCurrentDirectory(
             new File(System.getProperty("user.dir")));
          FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                           "mp3 files", "mp3");
          chooser.setFileFilter(filter);
          int returnVal = chooser.showOpenDialog(this);
          if(returnVal == JFileChooser.APPROVE_OPTION) {
             debug("You chose to open the file: " +
                                           chooser.getSelectedFile().getName());
          }
         
      }else if(e.getActionCommand().equals("TrackAdd")) {
        //************************************************
         int typeInd = genreJCB.getSelectedIndex();
         MediaDescription aMD = new MediaDescription(trackJTF.getText().trim(),
                                                     authorJTF.getText().trim(),
                                                     albumJTF.getText().trim(),
                                                     fileNameJTF.getText().trim());
         try {
			library.add(aMD);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         try {
			rebuildTree();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
         JFileChooser chooser = new JFileChooser();
         chooser.setCurrentDirectory(
            new File(System.getProperty("user.dir")));
         FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                          "mp3 files", "mp3");
         chooser.setFileFilter(filter);
         int returnVal = chooser.showOpenDialog(this);
         if(returnVal == JFileChooser.APPROVE_OPTION) {
            debug("You chose to open the file: " +
                                          chooser.getSelectedFile().getName());
         }
       
      }else if(e.getActionCommand().equals("Search")) {
      
         String searchReqURL = pre+artistSearchJTF.getText().trim()+"&album="+albumSearchJTF.getText().trim()+
                               "&api_key="+lastFMKey+"&format=json";
         System.out.println("calling fetchAsyncURL with url: "+searchReqURL);
         fetchAsyncURL(searchReqURL);
         
      }else if(e.getActionCommand().equals("Tree Refresh")) {
      
         try {
			rebuildTree();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         
      }else if(e.getActionCommand().equals("TrackRemove")) {
        //************************************************

          DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

          TreePath[] paths = tree.getSelectionPaths();
          if (paths != null) {
              for (TreePath path : paths) {
                  DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                      path.getLastPathComponent();
                  if (node.getParent() != null) {
                      model.removeNodeFromParent(node);
                  }
              }
          }

         
      }else if(e.getActionCommand().equals("AlbumRemove")) {
        //************************************************
          
          DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

          TreePath[] paths = tree.getSelectionPaths();
          if (paths != null) {
              for (TreePath path : paths) {
                  DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                      path.getLastPathComponent();
                  if (node.getParent() != null) {
                      model.removeNodeFromParent(node);
                  }
              }
          }
              
         
         
      }else if(e.getActionCommand().equals("AlbumPlay") || e.getActionCommand().equals("TrackPlay")){
         try{
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                            tree.getLastSelectedPathComponent();
            if(node!=null){
               String nodeLabel = (String)node.getUserObject();
               MediaDescription md = library.get(nodeLabel);
               String fileName = md.fileName;
               String path = "file://"+System.getProperty("user.dir")+
                             "/MediaFiles/" + fileName;
               this.playMedia(path, md.title);
            }
         }catch(Exception ex){
            System.out.println("Execption trying to play media:");
            ex.printStackTrace();
         }
      }
      tree.addTreeSelectionListener(this);
   }

   /**
    *
    * A method to do asynchronous url request printing the result to System.out
    * @param aUrl the String indicating the query url for the lastFM api search
    *
    **/
   public void fetchAsyncURL(String aUrl){
      try{
         HttpClient client = HttpClient.newHttpClient();
         HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(aUrl))
            .timeout(Duration.ofMinutes(1))
            .build();
         client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(System.out::println)
            .join();
      }catch(Exception ex){
         System.out.println("Exception in fetchAsyncUrl request: "+ex.getMessage());
      }
   }

   /**
    *
    * a method to make a web request. Note that this method will block execution
    * for up to 20 seconds while the request is being satisfied. Better to use a
    * non-blocking request.
    * @param aUrl the String indicating the query url for the lastFM api search
    * @return the String result of the http request.
    *
    **/
   public String fetchURL(String aUrl) {
      StringBuilder sb = new StringBuilder();
      URLConnection conn = null;
      InputStreamReader in = null;
      try {
         URL url = new URL(aUrl);
         conn = url.openConnection();
         if (conn != null)
            conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
         if (conn != null && conn.getInputStream() != null) {
            in = new InputStreamReader(conn.getInputStream(),
                                       Charset.defaultCharset());
            BufferedReader br = new BufferedReader(in);
            if (br != null) {
               int ch;
               // read the next character until end of reader
               while ((ch = br.read()) != -1) {
                  sb.append((char)ch);
               }
               br.close();
            }
         }
         in.close();
      } catch (Exception ex) {
         System.out.println("Exception in url request:"+ ex.getMessage());
      } 
      return sb.toString();
   }

   public boolean sezToStop(){
      return stopPlaying;
   }

   public static void main(String args[]) {
      String name = "first.last";
      String key = "use-your-last.fm-key-here";
      if (args.length >= 2){
         //System.out.println("java -cp classes:lib/json.lib ser321.assign2.lindquist."+
         //                   "MediaLibraryApp \"Lindquist Music Library\" lastFM-Key");
         name = args[0];
         key = args[1];
      }
      try{
         //System.out.println("calling constructor name "+name);
         MediaLibraryApp mla = new MediaLibraryApp(name,key);
      }catch (Exception ex){
         ex.printStackTrace();
      }
   }
}
