package com.tngtech.junit.dataprovider.resolver;

public enum ResolveStrategy {
    /**
     * Strategy which stops after the first resolver returns valid dataproviders (= non-empty list) or no further
     * resolvers are available.
     */
    UNTIL_FIRST_MATCH,

    /**
     * Loops over all resolvers and aggregates all resulting dataproviders.
     */
    AGGREGATE_ALL_MATCHES,
}