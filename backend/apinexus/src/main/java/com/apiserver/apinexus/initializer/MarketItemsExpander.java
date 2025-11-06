package com.apiserver.apinexus.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class MarketItemsExpander implements CommandLineRunner {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if we already have expanded products
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM market_items", Integer.class);
        
        if (count != null && count > 30) {
            System.out.println("Market items already expanded. Skipping initialization.");
            return;
        }
        
        System.out.println("Expanding market items database...");
        
        String[] items = {
            // Electronics (15 items)
            "('Mechanical Keyboard', 'RGB mechanical gaming keyboard with blue switches', 'Electronics', 129.99, 99.99, 45, TRUE)",
            "('Gaming Mouse', 'High-precision gaming mouse with 16000 DPI', 'Electronics', 69.99, 54.99, 60, TRUE)",
            "('Webcam 4K', '4K webcam with auto-focus and noise reduction', 'Electronics', 149.99, 119.99, 35, TRUE)",
            "('USB-C Hub', '7-in-1 USB-C hub with HDMI and ethernet', 'Electronics', 49.99, 39.99, 80, TRUE)",
            "('Laptop Stand', 'Aluminum adjustable laptop stand', 'Electronics', 39.99, 31.99, 55, TRUE)",
            "('Portable SSD 1TB', 'External SSD with USB 3.2 Gen 2', 'Electronics', 119.99, 95.99, 40, TRUE)",
            "('Monitor 27 inch', '27-inch 4K IPS monitor with HDR', 'Electronics', 399.99, 319.99, 25, TRUE)",
            "('Wireless Charger', 'Fast wireless charging pad 15W', 'Electronics', 29.99, 23.99, 100, TRUE)",
            "('Bluetooth Speaker', 'Portable waterproof Bluetooth speaker', 'Electronics', 79.99, 63.99, 70, TRUE)",
            "('Noise Cancelling Headphones', 'Over-ear ANC headphones with 30hr battery', 'Electronics', 249.99, 199.99, 30, TRUE)",
            "('Graphics Tablet', 'Digital drawing tablet with pen', 'Electronics', 89.99, 71.99, 40, TRUE)",
            "('Ring Light', 'LED ring light with tripod stand', 'Electronics', 44.99, 35.99, 50, TRUE)",
            "('Cable Organizer Kit', 'Premium cable management system', 'Electronics', 24.99, 19.99, 120, TRUE)",
            "('Power Bank 20000mAh', 'High-capacity portable charger', 'Electronics', 59.99, 47.99, 65, TRUE)",
            "('Smart LED Bulbs 4-Pack', 'WiFi enabled RGB smart bulbs', 'Electronics', 54.99, 43.99, 75, TRUE)",
            
            // Food & Beverages (10 items)
            "('Gourmet Chocolate Box', 'Assorted Belgian chocolates premium collection', 'Food & Beverages', 34.99, 27.99, 90, TRUE)",
            "('Organic Honey 500g', 'Raw unfiltered organic wildflower honey', 'Food & Beverages', 18.99, 15.99, 110, TRUE)",
            "('Artisan Pasta Set', 'Italian artisan pasta variety pack', 'Food & Beverages', 28.99, 23.99, 85, TRUE)",
            "('Premium Olive Oil', 'Extra virgin cold-pressed olive oil 750ml', 'Food & Beverages', 24.99, 19.99, 95, TRUE)",
            "('Specialty Coffee Sampler', 'World coffee sampler 6-pack', 'Food & Beverages', 39.99, 31.99, 70, TRUE)",
            "('Herbal Tea Collection', 'Premium herbal tea assortment 20 bags', 'Food & Beverages', 22.99, 18.99, 100, TRUE)",
            "('Protein Bars 12-Pack', 'High-protein energy bars variety pack', 'Food & Beverages', 29.99, 24.99, 130, TRUE)",
            "('Almond Butter Organic', 'Creamy organic almond butter 500g', 'Food & Beverages', 16.99, 13.99, 105, TRUE)",
            "('Matcha Powder Premium', 'Ceremonial grade matcha green tea powder', 'Food & Beverages', 32.99, 26.99, 60, TRUE)",
            "('Dark Chocolate 85%', 'Organic dark chocolate bars 5-pack', 'Food & Beverages', 19.99, 15.99, 115, TRUE)",
            
            // Sports & Fitness (12 items)
            "('Resistance Bands Set', 'Premium resistance bands 5-piece set', 'Sports & Fitness', 24.99, 19.99, 85, TRUE)",
            "('Foam Roller', 'High-density foam roller for muscle recovery', 'Sports & Fitness', 29.99, 23.99, 70, TRUE)",
            "('Gym Gloves', 'Weight lifting gloves with wrist support', 'Sports & Fitness', 19.99, 15.99, 95, TRUE)",
            "('Jump Rope Speed', 'Professional speed jump rope', 'Sports & Fitness', 14.99, 11.99, 110, TRUE)",
            "('Yoga Blocks Set', 'EVA foam yoga blocks 2-pack', 'Sports & Fitness', 17.99, 14.99, 80, TRUE)",
            "('Kettlebell 20lb', 'Cast iron kettlebell with grip', 'Sports & Fitness', 44.99, 35.99, 50, TRUE)",
            "('Exercise Ball 65cm', 'Anti-burst exercise stability ball', 'Sports & Fitness', 22.99, 18.99, 65, TRUE)",
            "('Cycling Gloves', 'Padded cycling gloves half-finger', 'Sports & Fitness', 24.99, 19.99, 75, TRUE)",
            "('Ankle Weights Pair', 'Adjustable ankle weights 5lb each', 'Sports & Fitness', 29.99, 23.99, 60, TRUE)",
            "('Ab Roller Wheel', 'Ab roller with knee pad', 'Sports & Fitness', 19.99, 15.99, 90, TRUE)",
            "('Workout Towel Set', 'Microfiber gym towels 3-pack', 'Sports & Fitness', 16.99, 13.99, 100, TRUE)",
            "('Gym Bag Duffel', 'Large capacity gym duffel bag', 'Sports & Fitness', 39.99, 31.99, 55, TRUE)",
            
            // Health & Wellness (10 items)
            "('Multivitamin Gummies', 'Adult multivitamin gummy 90-day supply', 'Health & Wellness', 24.99, 19.99, 120, TRUE)",
            "('Omega-3 Fish Oil', 'High-potency omega-3 supplement 180 caps', 'Health & Wellness', 34.99, 27.99, 95, TRUE)",
            "('Collagen Peptides', 'Hydrolyzed collagen powder 500g', 'Health & Wellness', 39.99, 31.99, 75, TRUE)",
            "('Vitamin D3 5000 IU', 'High-strength vitamin D3 softgels', 'Health & Wellness', 18.99, 15.99, 110, TRUE)",
            "('Magnesium Citrate', 'Magnesium supplement 400mg 120 caps', 'Health & Wellness', 21.99, 17.99, 100, TRUE)",
            "('Probiotic Complex', 'Multi-strain probiotic 30 billion CFU', 'Health & Wellness', 44.99, 35.99, 65, TRUE)",
            "('Turmeric Curcumin', 'Turmeric with black pepper extract', 'Health & Wellness', 26.99, 21.99, 85, TRUE)",
            "('Ashwagandha Capsules', 'Organic ashwagandha root extract', 'Health & Wellness', 22.99, 18.99, 90, TRUE)",
            "('Green Superfood Powder', 'Organic greens powder blend 300g', 'Health & Wellness', 49.99, 39.99, 60, TRUE)",
            "('Apple Cider Vinegar Gummies', 'ACV gummies with the mother 60-count', 'Health & Wellness', 19.99, 15.99, 105, TRUE)",
            
            // Home & Office (15 items)
            "('Ergonomic Office Chair', 'Mesh back ergonomic desk chair', 'Home & Office', 249.99, 199.99, 30, TRUE)",
            "('Standing Desk Converter', 'Height adjustable desk riser', 'Home & Office', 159.99, 127.99, 25, TRUE)",
            "('Monitor Arm Mount', 'Dual monitor arm desk mount', 'Home & Office', 89.99, 71.99, 40, TRUE)",
            "('Desk Organizer Set', 'Bamboo desk organizer 5-piece set', 'Home & Office', 34.99, 27.99, 70, TRUE)",
            "('Wireless Mouse Pad', 'Large desk mat with wireless charging', 'Home & Office', 44.99, 35.99, 60, TRUE)",
            "('Bookshelf 5-Tier', 'Modern ladder bookshelf', 'Home & Office', 79.99, 63.99, 35, TRUE)",
            "('File Cabinet 3-Drawer', 'Mobile file cabinet with lock', 'Home & Office', 119.99, 95.99, 28, TRUE)",
            "('Whiteboard 48x36', 'Magnetic dry erase board with tray', 'Home & Office', 64.99, 51.99, 45, TRUE)",
            "('Paper Shredder', 'Cross-cut paper shredder 12-sheet', 'Home & Office', 79.99, 63.99, 38, TRUE)",
            "('Desk Plant Set', 'Low-maintenance succulent plant set', 'Home & Office', 29.99, 23.99, 80, TRUE)",
            "('Wall Clock Modern', 'Silent non-ticking wall clock 12-inch', 'Home & Office', 24.99, 19.99, 90, TRUE)",
            "('Surge Protector', 'Smart surge protector with USB ports', 'Home & Office', 34.99, 27.99, 75, TRUE)",
            "('Humidifier Ultrasonic', 'Cool mist humidifier for office', 'Home & Office', 49.99, 39.99, 55, TRUE)",
            "('Air Purifier Compact', 'HEPA air purifier for desk', 'Home & Office', 89.99, 71.99, 42, TRUE)",
            "('Document Scanner', 'Portable document scanner wireless', 'Home & Office', 149.99, 119.99, 32, TRUE)",
            
            // Stationery (12 items)
            "('Fountain Pen Luxury', 'Executive fountain pen with case', 'Stationery', 79.99, 63.99, 45, TRUE)",
            "('Bullet Journal Kit', 'Dotted journal with stencils and pens', 'Stationery', 32.99, 26.99, 70, TRUE)",
            "('Gel Pen Set 24-Pack', 'Vibrant colored gel pens assortment', 'Stationery', 19.99, 15.99, 100, TRUE)",
            "('Sticky Notes Mega Pack', 'Assorted sticky notes 12-pack', 'Stationery', 14.99, 11.99, 120, TRUE)",
            "('Planner 2025', 'Weekly/monthly planner leather cover', 'Stationery', 34.99, 27.99, 80, TRUE)",
            "('Highlighter Set', 'Pastel highlighters 10-pack', 'Stationery', 12.99, 10.99, 110, TRUE)",
            "('Binder Clips Assorted', 'Heavy-duty binder clips 100-pack', 'Stationery', 9.99, 7.99, 150, TRUE)",
            "('Washi Tape Set', 'Decorative washi tape 20-roll set', 'Stationery', 18.99, 15.99, 85, TRUE)",
            "('Pencil Case Large', 'Multi-compartment pencil pouch', 'Stationery', 16.99, 13.99, 95, TRUE)",
            "('Index Cards 500-Pack', 'Ruled index cards 3x5 inch', 'Stationery', 11.99, 9.99, 130, TRUE)",
            "('Stamp Set Custom', 'Customizable stamp set with ink pad', 'Stationery', 24.99, 19.99, 65, TRUE)",
            "('Desk Calendar 2025', 'Large desk calendar with notes section', 'Stationery', 14.99, 11.99, 90, TRUE)"
        };
        
        int added = 0;
        for (String item : items) {
            try {
                String sql = "INSERT INTO market_items (item_name, description, category, normal_price, premium_price, stock_quantity, is_available) VALUES "
                    + item + " ON CONFLICT DO NOTHING";
                int rows = jdbcTemplate.update(sql);
                added += rows;
            } catch (Exception e) {
                System.err.println("Error adding item: " + e.getMessage());
            }
        }
        
        Integer finalCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM market_items", Integer.class);
        
        System.out.println("=== Market Items Expansion Complete ===");
        System.out.println("New items added: " + added);
        System.out.println("Total items in database: " + finalCount);
        System.out.println("========================================");
    }
}
