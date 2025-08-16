/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThread extends Thread{
    Integer a;
    Integer b;
    public CountThread (Integer a, Integer b){
        this.a=a;
        this.b=b;
    }

    public void run(){
        for(Integer i=a+1;i<b;i++){
            System.out.println(i);
        }


    }
    
}
