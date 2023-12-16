package com.destiny.budgeting.server.db

import com.destiny.budgeting.server.model.SummaryItem

class MockData {
    companion object {
        fun getSummaryItems(): List<SummaryItem> {
            return listOf(
                SummaryItem("Rent", "Needs", "Weekly rent. Excludes bonds.", -1000.0f),
                SummaryItem("Groceries", "Needs", "Groceries and snacks.", -300.0f),
                SummaryItem("Dining Out", "Wants", "Eating out and food delivery.", -200.0f),
                SummaryItem("Home", "Needs", "Home accessories e.g. kitchen, cleaning and toiletries.", -400.0f),
                SummaryItem("Clothes", "Wants", "Clothes shopping.", -150.0f),
                SummaryItem("Tech", "Wants", "Tech-related shopping.", -500.0f),
                SummaryItem("Shopping", "Wants", "General shopping.", -250.0f),
                SummaryItem("One-off", "Excluded", "Large, long-term or one-off purchases or payments.", -1000.0f),
                SummaryItem("Social", "Wants", "Tickets to social outings, gifts and donations. Excludes subscriptions to streaming services and eating out.", -300.0f),
                SummaryItem("Subscriptions", "Wants", "Streaming services, Headspace, Google One and Spotify. Excludes phone.", -100.0f),
                SummaryItem("Utilities", "Needs", "Phone, Wi-Fi, electricity, gas and water.", -200.0f),
                SummaryItem("Dental", "Wants", "Dental cleaning and procedures.", -150.0f),
                SummaryItem("Eyes", "Wants", "Glasses and eye tests.", -100.0f),
                SummaryItem("Medical", "Excluded", "Medicine, doctor and hospital visits. Excludes dental and eyesight.", -500.0f),
                SummaryItem("Beauty", "Wants", "Makeup, beauty products, haircuts and hair removal.", -200.0f),
                SummaryItem("Transport", "Needs", "Public and private transport.", -300.0f),
                SummaryItem("Salary", "Income", "Monthly paycheques and bonus payouts. Excludes reimbursements.", 5000.0f),
                SummaryItem("Other Income", "Income", "Interest on savings and the sale of goods.", 100.0f),
                SummaryItem("Tax", "Excluded", "Tax on interest, overseas currency conversion expenses and bank fees.", -200.0f),
                SummaryItem("Transfer", "Excluded", "Transfers between personal accounts. Excludes transfers to other people e.g. reimbursements.", 0.0f),
                SummaryItem("Miscel", "Excluded", "For transactions that are unknown or can be excluded from analysis.", -50.0f)
            )
        }
    }
}
