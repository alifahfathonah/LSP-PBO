/*
 * ATM Example system - file SimCashDispenser.java
 *
 * copyright (c) 2001 - Russell C. Bjork
 *
 */
 
package simulation;

import banking.Money;

import java.awt.*;
import java.awt.event.*;

/** Simulate the cash dispenser
 */
class SimCashDispenser extends Panel
{
    /** Constructor
     */
    SimCashDispenser()
    {
        setLayout(new GridLayout(1,1));
        label = new Label("Rp XXX.XX", Label.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 24));
        label.setForeground(new Color(0, 64, 0));
        add(label);
        label.setVisible(false);
    }
    
    /** Animate dispensing cash to a customer
     *
     *  @param amount the amount of cash to dispense
     *
     *  Precondition: amount is <= cash on hand
     */
    public void animateDispensingCash(Money amount)
    {
        label.setText(amount.toString());
        for (int size = 3; size <= 24; size += 1)
        { 
            label.setFont(new Font("SansSerif", Font.PLAIN, size));
            label.setVisible(true);
            try
            { 
                Thread.sleep(250);
            }
            catch (InterruptedException e)
            { }
            label.setVisible(false);
        }
    }
    
    /** Label that shows the amount of money being dispensed - starts out
     *  small and grows to simulate cash coming out of the machine
     */
    private Label label;
}                               
    
    
