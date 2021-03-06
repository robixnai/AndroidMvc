package com.shipdream.lib.android.mvc.samples.simple.controller.internal;

import com.shipdream.lib.android.mvc.Mvc;
import com.shipdream.lib.android.mvc.MvcComponent;
import com.shipdream.lib.android.mvc.event.bus.EventBus;
import com.shipdream.lib.android.mvc.event.bus.annotation.EventBusC;
import com.shipdream.lib.android.mvc.event.bus.annotation.EventBusV;
import com.shipdream.lib.android.mvc.event.bus.internal.EventBusImpl;
import com.shipdream.lib.poke.Provides;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by kejun on 12/07/2016.
 */

public class BaseTest {
    @BeforeClass
    public static void beforeClass() {
        ConsoleAppender console = new ConsoleAppender(); //create appender
        //configure the appender
        String PATTERN = "%d [%p] %C{1}: %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.DEBUG);
        console.activateOptions();
        //add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(console);
    }

    protected EventBus eventBusC;
    protected EventBus eventBusV;
    protected ExecutorService executorService;
    private MvcComponent testComponent;

    protected void prepareGraph(MvcComponent testComponent) throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        eventBusC = new EventBusImpl();
        eventBusV = new EventBusImpl();
        executorService = mock(ExecutorService.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = (Runnable) invocation.getArguments()[0];
                runnable.run();
                Future future = mock(Future.class);
                when(future.isDone()).thenReturn(true); //by default execute immediately succeed.
                when(future.isCancelled()).thenReturn(false);
                return future;
            }
        }).when(executorService).submit(any(Runnable.class));

        testComponent = new MvcComponent("TestOverrideComponent");
        testComponent.register(new Object(){
            @Provides
            @EventBusC
            public EventBus createEventBusC() {
                return eventBusC;
            }

            @Provides
            @EventBusV
            public EventBus createEventBusV() {
                return eventBusV;
            }

            @Provides
            public ExecutorService createExecutorService() {
                return executorService;
            }
        });

        Mvc.graph().getRootComponent().attach(testComponent, true);

        prepareGraph(testComponent);

        Mvc.graph().inject(this);
    }

    @After
    public void tearDown() throws Exception {
        Mvc.graph().getRootComponent().detach(testComponent);
    }
}
