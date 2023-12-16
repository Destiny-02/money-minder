package com.destiny.budgeting.model

import java.io.Serializable

data class SummaryGroup(
    var thisMonth: List<SummaryItem>,
    var last30Days: List<SummaryItem>,
    var monthlyAverage: List<SummaryItem>
) : Serializable {
    constructor() : this(emptyList(), emptyList(), emptyList())
}
