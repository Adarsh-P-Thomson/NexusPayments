// MongoDB Initialization Script for NexusPay
// This script runs automatically when the Docker container is first created

// Switch to the transactions database
db = db.getSiblingDB('nexuspay_transactions');

// Create user for the database (if not exists)
// Note: The root user is created by environment variables in docker-compose
// We create a specific user for the application if needed
try {
    db.createUser({
        user: "nexuspay",
        pwd: "nexuspay_dev",
        roles: [
            {
                role: "readWrite",
                db: "nexuspay_transactions"
            }
        ]
    });
} catch (e) {
    // User might already exist, ignore error
    print("User creation skipped: " + e);
}

// Create transactions collection with sample data
db.createCollection("transactions");

// Create indexes for better query performance
db.transactions.createIndex({ "userId": 1, "transactionDate": -1 });
db.transactions.createIndex({ "billId": 1 });
db.transactions.createIndex({ "status": 1 });
db.transactions.createIndex({ "transactionDate": -1 });

// Insert sample transaction data
db.transactions.insertMany([
    {
        "userId": 1,
        "billId": null,
        "transactionId": "TXN-" + new Date().getTime() + "-001",
        "amount": NumberDecimal("9.99"),
        "status": "SUCCESS",
        "paymentMethod": "CREDIT_CARD",
        "transactionDate": new Date(new Date().setMonth(new Date().getMonth() - 1)),
        "failureReason": null,
        "retryCount": 0,
        "metadata": {
            "cardLast4": "4242",
            "cardType": "VISA",
            "gatewayResponse": "approved"
        },
        "createdAt": new Date(new Date().setMonth(new Date().getMonth() - 1)),
        "updatedAt": new Date(new Date().setMonth(new Date().getMonth() - 1))
    },
    {
        "userId": 2,
        "billId": null,
        "transactionId": "TXN-" + new Date().getTime() + "-002",
        "amount": NumberDecimal("29.99"),
        "status": "SUCCESS",
        "paymentMethod": "CREDIT_CARD",
        "transactionDate": new Date(new Date().setMonth(new Date().getMonth() - 1)),
        "failureReason": null,
        "retryCount": 0,
        "metadata": {
            "cardLast4": "5555",
            "cardType": "MASTERCARD",
            "gatewayResponse": "approved"
        },
        "createdAt": new Date(new Date().setMonth(new Date().getMonth() - 1)),
        "updatedAt": new Date(new Date().setMonth(new Date().getMonth() - 1))
    },
    {
        "userId": 3,
        "billId": null,
        "transactionId": "TXN-" + new Date().getTime() + "-003",
        "amount": NumberDecimal("99.99"),
        "status": "FAILED",
        "paymentMethod": "CREDIT_CARD",
        "transactionDate": new Date(new Date().setDate(new Date().getDate() - 5)),
        "failureReason": "Insufficient funds",
        "retryCount": 1,
        "metadata": {
            "cardLast4": "3782",
            "cardType": "AMEX",
            "gatewayResponse": "declined"
        },
        "createdAt": new Date(new Date().setDate(new Date().getDate() - 5)),
        "updatedAt": new Date(new Date().setDate(new Date().getDate() - 5))
    }
]);

print("MongoDB initialization completed successfully!");
print("Created transactions collection with " + db.transactions.count() + " sample records");
