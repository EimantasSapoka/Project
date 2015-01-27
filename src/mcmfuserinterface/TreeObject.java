/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcmfuserinterface;

/**
 *
 * @author Eimantas
 */
public class TreeObject implements TreeObjectInterface {
    String name;
    TreeObject(String s){
        this.name = s;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
    
}
