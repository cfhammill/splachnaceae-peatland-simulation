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
public class generalParams {
    private int xMax;
    private int yMax;
    private int daysPerYear;
    private double poopRate;
    private int area;
    
    public generalParams(){}
    
    public generalParams(int xm, int ym, int dpy, double pr){
        xMax = xm;
        yMax = ym;
        daysPerYear = dpy;
        poopRate = pr;
        area = xMax * yMax;
    }
    
    public void setSize(int xm, int ym){
        xMax = xm;
        yMax = ym;
    }
    
    public int[] getSize(){
        int[] s = {xMax,yMax};
        return s;
    }
    
    public void setDPY(int dpy){
        daysPerYear = dpy;
    }
    
    public int getDPY(){
        return daysPerYear;
    }
    
    public void setPR(int pr){
        poopRate = pr;
    }
    
    public double getPR(){
        return poopRate;
    }
    
    public int getArea(){
        return area;
    }

}
