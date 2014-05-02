/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package peatland;

/**
 *
 * @author chris
 */
public class simState {
    private int day;
    private int year;
    
    public simState(int d, int y){
        this.day = d;
        this.year = y;
    }
    
    public simState(){
        this(0,0);
    }
    
   public int getDay(){
        return this.day;
    }
   
   public void setDay(int d){
       this.day = d;
   }
   
   public int getYear(){
       return this.year;
   }
   
   public void setYear(int y){
       this.year = y;
   }
   
   public void resetState(){
       this.year = 0;
       this.day = 0;
   }
    
}


