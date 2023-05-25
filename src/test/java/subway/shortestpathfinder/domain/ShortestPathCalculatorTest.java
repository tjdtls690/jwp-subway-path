package subway.shortestpathfinder.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class ShortestPathCalculatorTest {
    @ParameterizedTest(name = "{displayName} : partialDistance = {0}, resultFee = {1}")
    @CsvSource(value = {"10,1250", "11,1350", "15,1350", "16,1450", "50,2050", "51,2150", "58,2150", "59,2250", "66,2250", "67,2350"})
    void 요금을_계산한다(final Long distance, final Long resultFee) {
        // when
        final long fee = ShortestPathCalculator.calculateFee(distance);
        
        // then
        assertThat(fee).isEqualTo(resultFee);
    }
}
