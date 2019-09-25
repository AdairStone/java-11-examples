package itx.rxjava.test;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import itx.rxjava.DataService;
import itx.rxjava.DataServiceImpl;
import itx.rxjava.dto.DataItem;
import itx.rxjava.dto.DataQuery;
import itx.rxjava.test.consumer.SynchronousDataObserver;
import itx.rxjava.test.consumer.SynchronousDataSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(DataServiceTest.class);

    private ExecutorService executor;

    @BeforeClass
    public void init() {
        this.executor = Executors.newFixedThreadPool(4);
        LOG.info("test init");
    }

    @Test
    public void testDataServiceWithBackPressureComplete() throws InterruptedException {
        DataService dataService = new DataServiceImpl(executor);
        Flowable<DataItem> dataFlow = dataService.getDataFlowWithBackPressure(new DataQuery("query1-back-pressure-complete", 10));
        LOG.info("query submitted");
        SynchronousDataSubscriber dataSubscriber = new SynchronousDataSubscriber();
        dataFlow.subscribe(dataSubscriber);

        dataSubscriber.request(10);
        dataSubscriber.await(10, TimeUnit.SECONDS);
        LOG.info("evaluating test results");

        Assert.assertTrue(dataSubscriber.getErrors().size() == 0);
        Assert.assertTrue(dataSubscriber.getResults().size() == 10);
        Assert.assertTrue(dataSubscriber.isCompleted());
        Assert.assertNotNull(dataSubscriber.getSubscription());
    }

    @Test
    public void testDataServiceWithBackPressureIncomplete() throws InterruptedException {
        DataService dataService = new DataServiceImpl(executor);
        Flowable<DataItem> dataFlow = dataService.getDataFlowWithBackPressure(new DataQuery("query2-back-pressure-incomplete", 10));
        LOG.info("query submitted");
        SynchronousDataSubscriber dataSubscriber = new SynchronousDataSubscriber();
        dataFlow.subscribe(dataSubscriber);

        dataSubscriber.request(5);
        dataSubscriber.await(2, TimeUnit.SECONDS);
        LOG.info("evaluating test results");

        Assert.assertTrue(dataSubscriber.getErrors().size() == 0);
        Assert.assertTrue(dataSubscriber.getResults().size() == 5);
        Assert.assertFalse(dataSubscriber.isCompleted());
        Assert.assertNotNull(dataSubscriber.getSubscription());
    }

    @Test
    public void testDataService() throws InterruptedException {
        DataService dataService = new DataServiceImpl(executor);
        Observable<DataItem> dataFlow = dataService.getDataFlow(new DataQuery("query3-complete", 10));
        LOG.info("query submitted");
        SynchronousDataObserver dataObserver = new SynchronousDataObserver();
        dataFlow.subscribe(dataObserver);

        dataObserver.await(10, TimeUnit.SECONDS);
        LOG.info("evaluating test results");

        Assert.assertTrue(dataObserver.getErrors().size() == 0);
        Assert.assertTrue(dataObserver.getResults().size() == 10);
        Assert.assertTrue(dataObserver.isCompleted());
    }

    @AfterClass
    public void shutdown() {
        LOG.info("test shutdown");
        executor.shutdown();
    }

}