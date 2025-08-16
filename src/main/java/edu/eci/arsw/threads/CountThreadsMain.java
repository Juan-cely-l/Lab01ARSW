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
public class CountThreadsMain {
    
    public static void main(String a[]){
        CountThread t1= new CountThread(0,99);
        t1.run();
        System.out.println(" -------------------------------------------------------------------------------------------------- ");

        CountThread t2=new CountThread(99,199);
        t2.run();
        System.out.println(" -------------------------------------------------------------------------------------------------- ");

        CountThread t3=new CountThread(200,299);
        t3.run();
    }
    
}
