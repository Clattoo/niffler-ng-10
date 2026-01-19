package guru.qa.niffler.test.grpc;

import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.CalculateResponse;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyGrpcParameterizedTest extends BaseGrpcTest {

    static Stream<ConversionTestCase> conversionProvider() {
        return Stream.of(
                new ConversionTestCase(CurrencyValues.USD, CurrencyValues.RUB, 10.0),
                new ConversionTestCase(CurrencyValues.EUR, CurrencyValues.RUB, 50.0),
                new ConversionTestCase(CurrencyValues.RUB, CurrencyValues.USD, 1000.0),
                new ConversionTestCase(CurrencyValues.EUR, CurrencyValues.USD, 123.45),
                new ConversionTestCase(CurrencyValues.USD, CurrencyValues.USD, 77.77) // same currency
        );
    }

    @ParameterizedTest(name = "{0} → {1}, amount={2}")
    @MethodSource("conversionProvider")
    @DisplayName("Проверка конвертации валют через gRPC")
    void testCurrencyConversion(ConversionTestCase testCase) {
        CurrencyResponse currenciesResponse = blockingStub.getAllCurrencies(com.google.protobuf.Empty.getDefaultInstance());
        double fromRate = getRate(currenciesResponse, testCase.from);
        double toRate = getRate(currenciesResponse, testCase.to);

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(testCase.from)
                .setDesiredCurrency(testCase.to)
                .setAmount(testCase.amount)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);

        double expected = testCase.amount * fromRate / toRate;
        expected = Math.round(expected * 100.0) / 100.0;

        assertEquals(expected, response.getCalculatedAmount(), 0.0001,
                () -> "Conversion failed: " + testCase.from + " → " + testCase.to);
    }

    private double getRate(CurrencyResponse response, CurrencyValues currency) {
        return response.getAllCurrenciesList().stream()
                .filter(c -> c.getCurrency() == currency)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Currency not found: " + currency))
                .getCurrencyRate();
    }

    record ConversionTestCase(CurrencyValues from, CurrencyValues to, double amount) {
    }
}
