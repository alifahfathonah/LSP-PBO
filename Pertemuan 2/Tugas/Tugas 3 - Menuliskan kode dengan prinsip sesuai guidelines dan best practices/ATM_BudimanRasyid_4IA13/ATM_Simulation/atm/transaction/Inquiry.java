/*
 * ATM Example system - file Inquiry.java   
 *
 * copyright (c) 2001 - Russell C. Bjork
 *
 */

package atm.transaction;

import atm.ATM;
import atm.Session;
import atm.physical.*;
import banking.AccountInformation;
import banking.Card;
import banking.Message;
import banking.Money;
import banking.Status;
import banking.Receipt;

/**
 * Representation for a balance inquiry transaction
 */
public class Inquiry extends Transaction {
    /**
     * Constructor
     *
     * @param atm     the ATM used to communicate with customer
     * @param session the session in which the transaction is being performed
     * @param card    the customer's card
     * @param pin     the PIN entered by the customer
     */
    public Inquiry(ATM atm, Session session, Card card, int pin) {
        super(atm, session, card, pin);
    }

    /**
     * Get specifics for the transaction from the customer
     *
     * @return message to bank for initiating this transaction
     * @exception CustomerConsole.Cancelled if customer cancelled this transaction
     */
    protected Message getSpecificsFromCustomer() throws CustomerConsole.Cancelled {
        from = atm.getCustomerConsole().readMenuChoice("Cek Saldo", AccountInformation.ACCOUNT_NAMES);
        return new Message(Message.INQUIRY, card, pin, serialNumber, from, -1, new Money(0));
    }

    /**
     * Complete an approved transaction
     *
     * @return receipt to be printed for this transaction
     */
    protected Receipt completeTransaction() {
        return new Receipt(this.atm, this.card, this, this.balances) {
            {
                detailsPortion = new String[2];
                detailsPortion[0] = "CEK SALDO DARI: " + AccountInformation.ACCOUNT_ABBREVIATIONS[from];
                detailsPortion[1] = "";
            }
        };
    }

    /**
     * Account to inquire about
     */
    private int from;

}