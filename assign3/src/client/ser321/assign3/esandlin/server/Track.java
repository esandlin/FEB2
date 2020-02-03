package ser321.assign3.esandlin.server;

import java.io.Serializable;

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
public class Track implements Serializable{
    
    private String title;
    private String artist;
    //private time duration;
    private int rank;
    
    /**
     * auto generated 
     */
    private static final long serialVersionUID = -1951140985058849513L;

    /*
     * Constructor
     */
    public Track(String trackName, String artist, int duration, int rank_Order) {
        this.title = trackName;
        this.artist = artist;
        //this.duration = duration;
        this.rank = rank_Order;
        
    }
    
 	/*
 	 * - the title of the track.
 	 */
	public String track_Name() {
        
	    return this.title;
	}
	
	/*
	 * - the artist or group name.
	 */
	public String artist() {
        
	    return this.artist;
	}
	
	/*
	 * - an integer indicating the track number of this track. You should number all tracks in an album ignoring (don't reset to 1) multiple disks.
	 */
	public int rank_Order() {
	    
	    return this.rank;
	}
	
	/*
	 * - a string indicating the track's run time in the form: mm:ss
	 */
	public void duration() {
	    
	    
	}

 }
 