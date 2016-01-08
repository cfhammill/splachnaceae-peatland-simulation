/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author chris
 */
public class simParams {
    private generalParams gp;
    private sParams amp;
    private sParams pens;
    
    public simParams(generalParams gp, sParams amp, sParams pens){
        this.gp = gp;
        this.amp = amp;
        this.pens = pens;
    }
    
    public void setGP(generalParams gp){
        this.gp = gp;
    }
    
    public generalParams getGP(){
        return this.gp;
    }
    
    public void setAmp(sParams amp){
        this.amp = amp;
    }
    
    public sParams getAmp(){
        return this.amp;
    }
    
    public void setPens(sParams pens){
        this.pens = pens;
    }
    
    public sParams getPens(){
        return this.pens;
    }
    
}

