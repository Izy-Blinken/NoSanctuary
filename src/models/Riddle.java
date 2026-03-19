/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

public class Riddle {

    public int id;
    public String question;
    public String answer;
    public String clue; 
    public boolean solved = false;

    public Riddle(int id, String question, String answer, String clue) {
        
        this.id = id;
        this.question = question;
        this.answer = answer.toLowerCase().trim();
        this.clue = clue;
    }
}
