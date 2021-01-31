package com.example.moneymap;

public class Transaction {
    public String account;
    public String category;
    public String note;
    public String amount;
    public String type;
    public TransactionDate transactionDate;

    public Transaction(){}

    public Transaction(String account, String category, String note, String amount, String type, TransactionDate transactionDate){
        this.account = account;
        this.category = category;
        this.note = note;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
    }
}
