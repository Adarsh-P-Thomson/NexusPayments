// Initialize MongoDB for NexusPay
db = db.getSiblingDB('events');

// Create collections with proper indexing
db.createCollection('usage_events');
db.createCollection('billing_events');
db.createCollection('audit_events');

// Create indexes for optimal query performance
db.usage_events.createIndex({ "tenantId": 1, "timestamp": -1 });
db.usage_events.createIndex({ "tenantId": 1, "customerId": 1, "timestamp": -1 });
db.usage_events.createIndex({ "eventType": 1, "timestamp": -1 });
db.usage_events.createIndex({ "processed": 1, "timestamp": 1 });

db.billing_events.createIndex({ "tenantId": 1, "timestamp": -1 });
db.billing_events.createIndex({ "eventType": 1, "timestamp": -1 });
db.billing_events.createIndex({ "aggregationId": 1 });

db.audit_events.createIndex({ "tenantId": 1, "timestamp": -1 });
db.audit_events.createIndex({ "userId": 1, "timestamp": -1 });
db.audit_events.createIndex({ "action": 1, "timestamp": -1 });

// Create users for services
db.createUser({
  user: "metering_service",
  pwd: "metering_pass",
  roles: [
    { role: "readWrite", db: "events" }
  ]
});

db.createUser({
  user: "billing_service",
  pwd: "billing_pass",
  roles: [
    { role: "readWrite", db: "events" }
  ]
});

print("MongoDB initialization completed!");