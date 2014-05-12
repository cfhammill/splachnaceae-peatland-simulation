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
    private double radMax;
    private int daysPerYear;
    private double poopRate;
    private double area;
    
    public generalParams(){}
    
    public generalParams(double radM, int dpy, double pr){
        radMax = radM;
        daysPerYear = dpy;
        poopRate = pr;
        area = radMax * Math.PI * 2;
    }
    
    public double getRadius(){
        return radMax;
    }
    
    public void setArea(double area){
        radMax = area / (2 * Math.PI);
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
    
    public double getArea(){
        return area;
    }

}
