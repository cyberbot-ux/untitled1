// Class to represent a single card transaction with details like date, type, quantity, and amounts
public class Card_Transaction {

    // Date of the transaction batch
    private String batch_date;

    // Type of card used (e.g., Visa, Mastercard)
    private String card_type;

    // Number of transactions in the batch
    private int quantity;

    // Gross amount before deductions
    private double gross_amount;

    // Net amount after deductions
    private double net_amount;

    // Fee deducted from the gross amount
    private double fee;

    // Constructor to initialize a transaction object with all values
    public Card_Transaction(String batch_date, String card_type, int quantity,
                           double gross_amount, double net_amount, double fee) {
        this.batch_date = batch_date;
        this.card_type = card_type;
        this.quantity = quantity;
        this.gross_amount = gross_amount;
        this.net_amount = net_amount;
        this.fee = fee;
    }

    // Returns the batch date of this transaction
    public String get_batch_date() {
        return batch_date;
    }

    // Returns the card type of this transaction
    public String get_card_type() {
        return card_type;
    }

    // Returns the number of transactions
    public int get_quantity() {
        return quantity;
    }

    // Returns the gross amount
    public double get_gross_amount() {
        return gross_amount;
    }

    // Returns the net amount
    public double get_net_amount() {
        return net_amount;
    }

    // Returns the fee charged
    public double get_fee() {
        return fee;
    }

    // Custom string representation for easy debugging and logging
    @Override
    public String toString() {
        return String.format("CardTransaction{date=%s, type=%s, qty=%d, gross=%.2f, net=%.2f, fee=%.2f}",
                batch_date, card_type, quantity, gross_amount, net_amount, fee);
    }
}
