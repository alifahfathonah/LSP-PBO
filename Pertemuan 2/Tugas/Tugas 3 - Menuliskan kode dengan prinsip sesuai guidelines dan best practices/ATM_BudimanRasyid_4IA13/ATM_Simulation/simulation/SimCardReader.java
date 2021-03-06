/*
 * ATM Example system - file SimCardReader.java
 *
 * copyright (c) 2001 - Russell C. Bjork
 *
 */
 
package simulation;

import atm.ATM;
import banking.Card;

import java.awt.*;
import java.awt.event.*;

/** Simulate the card reader
 */
class SimCardReader extends Button
{
    /** Constructor
     *
     *  @param simulation the Simulation object
     */
    SimCardReader(final Simulation simulation)
    {
        super("Silahkan Masukkan Kartu Anda");

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                simulation.cardInserted();
            }
        });
        
        // Not available until machine is turned on
        
        setVisible(false);
    }
    
    /** Animate card going into the machine
     */
    void animateInsertion()
    {
        originalBounds = getBounds();
        Rectangle currentBounds =
           new Rectangle(originalBounds.x, originalBounds.y,
                         originalBounds.width, originalBounds.height);
                         
        while (currentBounds.width > 0 && currentBounds.height > 0)
        { 
            setBounds(currentBounds.x, currentBounds.y,
                      currentBounds.width, currentBounds.height);
            repaint();
            try 
            { 
                Thread.sleep(100);
            } 
            catch (InterruptedException e) 
            { }
            
            currentBounds.height -= 1;
            currentBounds.width = 
              (originalBounds.width * currentBounds.height) / originalBounds.height;
            currentBounds.x =
              originalBounds.x + (originalBounds.width - currentBounds.width) / 2;
            currentBounds.y =
              originalBounds.y + (originalBounds.height - currentBounds.height) / 2;
        }
          
        setVisible(false);  
    }
    
    /** Animate ejecting the card that is currently inside the reader.  
     */
    void animateEjection()
    {
        setLabel("Mengeluarkan Kartu");
        setVisible(true);
        
        Rectangle currentBounds = 
           new Rectangle(originalBounds.x + originalBounds.width / 2,
                         originalBounds.y + originalBounds.height / 2,
                         originalBounds.width / originalBounds.height, 1);
        
        while (currentBounds.height <= originalBounds.height &&
               currentBounds.width <= originalBounds.width)
        { 
            setBounds(currentBounds.x, currentBounds.y,
                      currentBounds.width, currentBounds.height);
            repaint();
            try 
            { 
                Thread.sleep(100);
            } 
            catch (InterruptedException e) 
            { }
            currentBounds.height += 1;
            currentBounds.width = 
              (originalBounds.width * currentBounds.height) / originalBounds.height;
            currentBounds.x =
              originalBounds.x + (originalBounds.width - currentBounds.width) / 2;
            currentBounds.y =
              originalBounds.y + (originalBounds.height - currentBounds.height) / 2;
        }
          
        setLabel("Silahkan Masukkan Kartu Anda");
    }

    /** Animate retaining the card that is currently inside the reader for action by the
     *  bank. 
     */
    void animateRetention()
    {
        setLabel("Silahkan Masukkan Kartu Anda");
        setVisible(true);
    }

    /** To animate card insertion/ejection, we change the bounds of this button.
     *  These are the original bounds we ultimately restore to
     */ 
    private Rectangle originalBounds;
}                               
    
    
