package subway.shortestpathfinder.domain;

public final class ShortestPathFeeCalculator {
    private static final int BASE_FEE = 1250;
    private static final int BASE_DISTANCE = 10;
    private static final int RANGE_OF_FIVE_INCREASE = 40;
    private static final int START_DISTANCE_OF_EIGHT_INCREASE = BASE_DISTANCE + RANGE_OF_FIVE_INCREASE;
    private static final int FIVE_INCREASE = 5;
    private static final int EIGHT_INCREASE = 8;
    
    private ShortestPathFeeCalculator() { }
    
    public static long calculateFee(final Long distance) {
        return BASE_FEE
                + calculateWithSpecifiedIncrement(Math.min(RANGE_OF_FIVE_INCREASE, distance - BASE_DISTANCE), FIVE_INCREASE)
                + calculateWithSpecifiedIncrement(distance - START_DISTANCE_OF_EIGHT_INCREASE, EIGHT_INCREASE);
    }
    
    private static long calculateWithSpecifiedIncrement(final long distance, final int increase) {
        if (isDistanceNegative(distance)) {
            return 0;
        }
        
        return (long) ((Math.ceil((distance - 1) / increase) + 1) * 100);
    }
    
    private static boolean isDistanceNegative(final long distance) {
        return distance <= 0;
    }
}
