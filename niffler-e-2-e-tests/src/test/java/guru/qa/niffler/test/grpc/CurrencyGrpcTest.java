package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.CalculateResponse;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.niffler.grpc.CurrencyValues.*;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyGrpcTest extends BaseGrpcTest{

    @Test
    @DisplayName("Все валюты возвращаются")
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        assertEquals(4, allCurrenciesList.size());
    }

    @Test
    @DisplayName("В ответе есть все ожидаемые валюты")
    void allCurrenciesShouldContainExpectedCurrencies() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> currencies = response.getAllCurrenciesList();
        assertEquals(4, currencies.size());

        List<CurrencyValues> values = currencies.stream()
                .map(Currency::getCurrency)
                .toList();

        assertEquals(List.of(RUB, KZT, EUR, USD), values);
    }

    @Test
    @DisplayName("Все курсы валют положительные")
    void currencyRatesShouldBePositive() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());

        response.getAllCurrenciesList().forEach(currency ->
                assertTrue(currency.getCurrencyRate() > 0,
                        "Rate should be positive for " + currency.getCurrency())
        );
    }

    @Test
    @DisplayName("Конвертация валюты в саму себя возвращает ту же сумму")
    void calculateSameCurrencyShouldReturnSameAmount() {
        double amount = 50.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(USD)
                .setDesiredCurrency(USD)
                .setAmount(amount)
                .build();

        final CalculateResponse response = blockingStub.calculateRate(request);

        assertEquals(amount, response.getCalculatedAmount(), 0.0001);
    }

    @Test
    @DisplayName("Нулевая сумма возвращает 0")
    void zeroAmountShouldReturnZero() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(EUR)
                .setDesiredCurrency(RUB)
                .setAmount(0)
                .build();

        final CalculateResponse response = blockingStub.calculateRate(request);

        assertEquals(0, response.getCalculatedAmount(), 0.0001);
    }

    @Test
    @DisplayName("UNSPECIFIED валюта вызывает ошибку")
    void unspecifiedCurrencyShouldFail() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(UNSPECIFIED)
                .setDesiredCurrency(RUB)
                .setAmount(10)
                .build();

        assertThrows(StatusRuntimeException.class,
                () -> blockingStub.calculateRate(request));
    }
}
