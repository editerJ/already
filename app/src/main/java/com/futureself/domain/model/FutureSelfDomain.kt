package com.futureself.domain.model

enum class DomainCategory(
    val key: String,
    val englishLabel: String
) {
    WORK("work", "Work"),
    HEALTH("health", "Health"),
    FAMILY("family", "Family"),
    FINANCE("finance", "Finance"),
    SELF("self", "Self");

    companion object {
        fun fromKey(key: String): DomainCategory = entries.firstOrNull { it.key == key } ?: SELF
    }
}

enum class YearHorizon(val years: Int) {
    ONE(1),
    THREE(3),
    FIVE(5),
    TEN(10);

    companion object {
        fun fromYears(years: Int): YearHorizon = entries.firstOrNull { it.years == years } ?: FIVE
    }
}

enum class WoopStep {
    OBSTACLE,
    EMOTION,
    PLAN
}
