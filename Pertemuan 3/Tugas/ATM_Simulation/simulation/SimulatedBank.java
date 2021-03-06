/*
 * ATM Example system - file SimulatedBank.java 
 *
 * copyright (c) 2001 - Russell C. Bjork
 *
 */
 
package simulation;
import banking.AccountInformation;
import banking.Balances;
import banking.Card;
import banking.Message;
import banking.Money;
import banking.Status;

/** Simulation of the bank.  A set of simulated accounts is initalized at startup.
 */
public class SimulatedBank
{
    /** Simulate the handling of a message
     *
     *  @param message the message to send
     *  @param balances (out) balances in customer's account as reported
     *         by bank
     *  @return status code returned by bank
     */
    public Status handleMessage(Message message, Balances balances)
    {       
        int cardNumber = message.getCard().getNumber(); 
        if (cardNumber < 1 || cardNumber > PIN.length)
            return new Failure("Kartu tidak valid");
    
        if (message.getPIN() != PIN [ cardNumber ] )
            return new InvalidPIN();
    
        switch(message.getMessageCode())
        {
            case Message.WITHDRAWAL:
            
                return withdrawal(message, balances);
                            
            case Message.INITIATE_DEPOSIT:
            
                return initiateDeposit(message);
            
            case Message.COMPLETE_DEPOSIT:
            
                return completeDeposit(message, balances);
            
            case Message.TRANSFER:
            
                return transfer(message, balances);
            
            case Message.INQUIRY:
            
                return inquiry(message, balances);
        }
        
        // Need to keep compiler happy
        
        return null;
    }

    /** Simulate processing of a withdrawal
     *
     *  @param message the message describing the withdrawal requested
     *  @param balances (out) balances in account after withdrawal
     *  @return status code derived from current values
     */
    private Status withdrawal(Message message, Balances balances)
    {
        int cardNumber = message.getCard().getNumber();
        
        int accountNumber = ACCOUNT_NUMBER [ cardNumber ] [ message.getFromAccount() ];

        if (accountNumber == 0)
            return new Failure("Rekening Yang ada Masukkan Salah");
    
        Money amount = message.getAmount();
        
        Money limitRemaining = new Money(DAILY_WITHDRAWAL_LIMIT);
    
        limitRemaining.subtract(WITHDRAWALS_TODAY[ cardNumber ]);

        

        if (! amount.lessEqual(limitRemaining))
            return new Failure("Batas penarikan tunai harian anda telah terlewati");

        if (! amount.lessEqual(AVAILABLE_BALANCE [ accountNumber ]))
             return new Failure("Maaf, saldo Anda tidak mencukupi");

        // Update withdrawals today and account balances once we know everything is
        // OK
            
        WITHDRAWALS_TODAY [ cardNumber ].add(amount);
        BALANCE [ accountNumber ].subtract(amount);
        AVAILABLE_BALANCE [ accountNumber ].subtract(amount);
        
        // Return updated balances
        
        balances.setBalances(BALANCE [ accountNumber ], 
                             AVAILABLE_BALANCE [ accountNumber ]);
        
        return new Success();
    } 
    
    /** Simulate initiation of a deposit. At this point, the bank only approves
     *  the validity of the deposit - no changes to the records are made until
     *  the envelope is actually inserted 
     *
     *  @param message the message describing the deposit requested
     *  @return status code derived from current values
     */
    private Status initiateDeposit(Message message)
    {
        int cardNumber = message.getCard().getNumber(); 
    
        int accountNumber = ACCOUNT_NUMBER [ cardNumber ] [ message.getToAccount() ];
        if (accountNumber == 0)
            return new Failure("Rekening Yang ada Masukkan Salah");
            
        // Don't update anything yet
            
        return new Success();
    }    
    
    /** Simulate completion of a deposit
     *
     *  @param message the message describing the deposit requested
     *  @param balances (out) balances (not updated until completed)
     *  @return status code - must always be success in this case
     */
    private Status completeDeposit(Message message, Balances balances)
    {
        int cardNumber = message.getCard().getNumber(); 
        
        int accountNumber = ACCOUNT_NUMBER [ cardNumber ] [ message.getToAccount() ];
        if (accountNumber == 0)
            return new Failure("Rekening Yang ada Masukkan Salah");
            
        // Now we can update the balance
        
        Money amount = message.getAmount();
        BALANCE [ accountNumber ].add(amount);
        
        // Return updated balances
        
        balances.setBalances(BALANCE [ accountNumber ], 
                             AVAILABLE_BALANCE [ accountNumber ]);
        
        return new Success();
    }    
    
    /** Simulate processing of a transfer
     *
     *  @param message the message describing the transfer requested
     *  @param balances (out) balances in "to" account after transfer
     *  @return status code derived from current values
     */
    private Status transfer(Message message, Balances balances)
    {
        int cardNumber = message.getCard().getNumber(); 
    
        int fromAccountNumber = ACCOUNT_NUMBER [ cardNumber ] [ message.getFromAccount() ];
        if (fromAccountNumber == 0)
            return new Failure("Rekening Yang ada Masukkan Salah");
    
        int toAccountNumber = ACCOUNT_NUMBER [ cardNumber ] [ message.getToAccount() ];
        if (toAccountNumber == 0)
            return new Failure("Rekening Yang ada Masukkan Salah");
        if (fromAccountNumber == toAccountNumber)
            return new Failure("Tidak dapat melakukan transfer\n"+
                                "ke rekening yang sama");
    
        Money amount = message.getAmount();
        Money limitRemaining = new Money(DAILY_TRANSFER_LIMIT);
        limitRemaining.subtract(TRANSFER_TODAY[ cardNumber ]);
        if (! amount.lessEqual(limitRemaining))
            return new Failure("Batas transfer harian anda telah terlewati");
        
        if (! amount.lessEqual(AVAILABLE_BALANCE [ fromAccountNumber ]))
             return new Failure("Maaf, saldo Anda tidak mencukupi");

        // Update account balances once we know everything is OK
        TRANSFER_TODAY [ cardNumber ].add(amount);
        BALANCE [ fromAccountNumber ].subtract(amount);
        AVAILABLE_BALANCE [ fromAccountNumber ].subtract(amount);
        BALANCE [ toAccountNumber ].add(amount);
        AVAILABLE_BALANCE [ toAccountNumber ].add(amount);
        
        // Return updated balances
        
        balances.setBalances(BALANCE [ toAccountNumber ], 
                             AVAILABLE_BALANCE [ toAccountNumber ]);
        
        return new Success();
    } 
    
    /** Simulate processing of an inquiry
     *
     *  @param message the message describing the inquiry requested
     *  @param balances (out) balances in account
     *  @return status code derived from current values
     */
    private Status inquiry(Message message, Balances balances)
    {
        int cardNumber = message.getCard().getNumber(); 

        int accountNumber = ACCOUNT_NUMBER [ cardNumber ] [ message.getFromAccount() ];
       
        if (accountNumber == 0)
            return new Failure("Rekening Yang ada Masukkan Salah");
        
        // Return requested balances
        
        balances.setBalances(BALANCE [ accountNumber ], 
                             AVAILABLE_BALANCE [ accountNumber ]);
        
        return new Success();
    }
     
    /** Representation for status of a transaction that succeeded
     */
    private static class Success extends Status
    {
        public boolean isSuccess()
        {
            return true;
        }
        
        public boolean isInvalidPIN()
        {
            return false;
        }
        
        public String getMessage()
        {
            return null;
        }
    }
    
    /** Representation for status of a transaction that failed (for reason other than
     *  invalid PIN)
     */
    private static class Failure extends Status
    {
        /** Constructor
         *
         *  @param message description of the failure
         */
        public Failure(String message)
        {
            this.message = message;
        }
        
        public boolean isSuccess()
        {
            return false;
        }
        
        public boolean isInvalidPIN()
        {
            return false;
        }
        
        public String getMessage()
        {
            return message;
        }
        
        private String message;
    }

    /** Representation for status of a transaction that failed due to an invalid PIN
     */
    private static class InvalidPIN extends Failure
    {
        /** Constructor
         *
         *  @param message description of the failure
         */
        public InvalidPIN()
        {
            super("PIN ANDA SALAH");
        }
        
        public boolean isInvalidPIN()
        {
            return true;
        }
    }
    
    /** PIN for each card.  (Valid card numbers start with 1)
     */
    private static final int PIN [] =
    { 
        0,  // dummy for nonexistent card 0
        422442, 
        123456,
    };

    /** Array of account numbers associated with each card.  For each card,
     *  there can be three different types of account, which correspond to
     *  the names in class AccountInformation.  0 means no account of this
     *  type.   (Valid card numbers start with 1)
     */
    private static final int ACCOUNT_NUMBER [] [] =
    { 
        { 0, 0, 0 },    // dummies for nonexistent card 0
        { 1, 2, 0 },
        { 1, 0, 3 }
    };

    /** Withdrawals so far today on each card.   (Valid card numbers start with 1)
     */ 
    private static Money WITHDRAWALS_TODAY [] =
    {
        new Money(0),   // dummy for nonexistent card 0
        new Money(0),
        new Money(0)
    };

    private static Money TRANSFER_TODAY [] =
    {
        new Money(0),   // dummy for nonexistent card 0
        new Money(0),
        new Money(0)
    };
    
    /** Maximum daily withdrawal limit for any one card.  
     */
    private static final Money WITHDRAWAL_LIMIT = new Money(5000000);

    private static final Money DAILY_WITHDRAWAL_LIMIT = new Money(5000000);

    private static final Money DAILY_TRANSFER_LIMIT = new Money(10000000);
    
    /** Balance for each account (will change as program runs, hence not a
     *  static final.
     */
    private Money BALANCE [] =
    {
        new Money(0) // dummy for nonexistent account 0
            , new Money(100000000)
            , new Money(50000000)
//            , new Money(5000) 
    }; 
    
    /** Available alance for each account (will change as program runs, hence
     *  not a static final.
     */
    private Money AVAILABLE_BALANCE [] =
    { 
        new Money(0) // dummy for nonexistent account 0
            , new Money(5000000)
            , new Money(10000000)
//            , new Money(5000) 
    };
}    
