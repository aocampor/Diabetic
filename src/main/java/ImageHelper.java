/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author idarraga
 */
public class ImageHelper {
    
    String fnPrefix;
    int DRlevel;
    
    // constructor
    public ImageHelper(String fnPre, int DRlev){
        this.fnPrefix = fnPre;
        this.DRlevel = DRlev;
    }
    
    public String getFilenamePrefix() { return this.fnPrefix; }
    public int getDRLevel() { return this.DRlevel; }
    
}
