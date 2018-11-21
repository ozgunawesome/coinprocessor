package services.ozzy.coinprocessor;

import com.binance.api.client.BinanceApiRestClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import services.ozzy.coinprocessor.tasks.BinanceTasks;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class BinanceTasksTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BinanceApiRestClient client;

    @InjectMocks
    private BinanceTasks exchange;

    @Test
    public void testCoinDivision() throws IllegalAccessException {
    }
}
