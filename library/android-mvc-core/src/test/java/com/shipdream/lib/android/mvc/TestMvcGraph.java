/*
 * Copyright 2016 Kejun Xia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shipdream.lib.android.mvc;

import com.shipdream.lib.poke.Consumer;
import com.shipdream.lib.poke.Graph;
import com.shipdream.lib.poke.Provider;
import com.shipdream.lib.poke.Provides;
import com.shipdream.lib.poke.exception.ProvideException;
import com.shipdream.lib.poke.exception.ProviderConflictException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class TestMvcGraph extends BaseTest{
    interface Os {
    }

    @Qualifier
    @Documented
    @Retention(RUNTIME)
    @interface Apple {
    }

    @Qualifier
    @Documented
    @Retention(RUNTIME)
    @interface Google {
    }

    static class iOS implements Os {

    }

    static class Android implements Os {

    }

    static class DeviceModule {
        @Provides
        @Singleton
        public Os provide() {
            return new Android();
        }

        @Provides
        @Singleton
        @Apple
        public Os provideIos() {
            return new iOS();
        }

        @Provides
        @Singleton
        @Google
        public Os provideAndroid() {
            return new Android();
        }
    }

    class Device {
        @Inject
        private Os android;

        @Inject
        @Apple
        private Os os;
    }

    @Test
    public void use_method_should_retain_and_release_instance_without_qualifier_correctly() throws ProvideException, ProviderConflictException {
        graph.getRootComponent().register(new DeviceModule());

        //OsReferenceCount = 0
        graph.use(Os.class, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //First time to create the instance.
                //OsReferenceCount = 1
                Assert.assertTrue(instance instanceof Android);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 0

        final Device device = new Device();
        graph.inject(device);  //OsReferenceCount = 1
        //New instance created and cached

        graph.use(Os.class, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //Since reference count is greater than 0, cached instance will be reused
                //OsReferenceCount = 2
                Assert.assertTrue(device.android == instance);
                Assert.assertTrue(instance instanceof Android);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 1

        graph.release(device);  //OsReferenceCount = 0
        //Last instance released, so next time a new instance will be created

        graph.use(Os.class, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //OsReferenceCount = 1
                //Since the cached instance is cleared, the new instance is a newly created one.
                Assert.assertTrue(device.android != instance);
                Assert.assertTrue(instance instanceof Android);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 0

        graph.use(Os.class, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //OsReferenceCount = 1
                //Since the cached instance is cleared, the new instance is a newly created one.
                Assert.assertTrue(device.android != instance);
                Assert.assertTrue(instance instanceof Android);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 0
        //Cached instance cleared again

        graph.use(Os.class, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //OsReferenceCount = 1
                graph.inject(device);
                //Injection will reuse the cached instance and increment the reference count
                //OsReferenceCount = 2

                //Since the cached instance is cleared, the new instance is a newly created one.
                Assert.assertTrue(device.android == instance);
                Assert.assertTrue(instance instanceof Android);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 1

        graph.release(device);  //OsReferenceCount = 0
    }

    @Test
    public void use_method_should_retain_and_release_instance_correctly() throws ProvideException, ProviderConflictException {
        graph.getRootComponent().register(new DeviceModule());

        @Apple
        class NeedIoS {

        }

        Annotation iosQualifier = NeedIoS.class.getAnnotation(Apple.class);

        //OsReferenceCount = 0
        graph.use(Os.class, iosQualifier, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //First time to create the instance.
                //OsReferenceCount = 1
                Assert.assertTrue(instance instanceof iOS);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 0

        final Device device = new Device();
        graph.inject(device);  //OsReferenceCount = 1
        //New instance created and cached

        graph.use(Os.class, iosQualifier, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //Since reference count is greater than 0, cached instance will be reused
                //OsReferenceCount = 2
                Assert.assertTrue(device.os == instance);
                Assert.assertTrue(instance instanceof iOS);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 1

        graph.release(device);  //OsReferenceCount = 0
        //Last instance released, so next time a new instance will be created

        graph.use(Os.class, iosQualifier, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //OsReferenceCount = 1
                //Since the cached instance is cleared, the new instance is a newly created one.
                Assert.assertTrue(device.os != instance);
                Assert.assertTrue(instance instanceof iOS);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 0

        graph.use(Os.class, iosQualifier, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //OsReferenceCount = 1
                //Since the cached instance is cleared, the new instance is a newly created one.
                Assert.assertTrue(device.os != instance);
                Assert.assertTrue(instance instanceof iOS);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 0
        //Cached instance cleared again

        graph.use(Os.class, iosQualifier, new Consumer<Os>() {
            @Override
            public void consume(Os instance) {
                //OsReferenceCount = 1
                graph.inject(device);
                //Injection will reuse the cached instance and increment the reference count
                //OsReferenceCount = 2

                //Since the cached instance is cleared, the new instance is a newly created one.
                Assert.assertTrue(device.os == instance);
                Assert.assertTrue(instance instanceof iOS);
            }
        });
        //Reference count decremented by use method automatically
        //OsReferenceCount = 1

        graph.release(device);  //OsReferenceCount = 0
    }

    @Test
    public void should_delegate_mvc_graph_properly() throws ProvideException, ProviderConflictException {
        // Arrange
        Graph graphMock = mock(Graph.class);

        graph.graph = graphMock;

        // Act
        Graph.Monitor monitor = mock(Graph.Monitor.class);
        graph.registerMonitor(monitor);
        // Verify
        verify(graphMock).registerMonitor(eq(monitor));

        // Arrange
        reset(graphMock);
        // Act
        graph.unregisterMonitor(monitor);
        // Verify
        verify(graphMock).unregisterMonitor(eq(monitor));

        // Arrange
        reset(graphMock);
        // Act
        graph.clearMonitors();
        // Verify
        verify(graphMock).clearMonitors();

        // Arrange
        reset(graphMock);
        Provider.DereferenceListener providerFreedListener = mock(Provider.DereferenceListener.class);
        // Act
        graph.registerDereferencedListener(providerFreedListener);
        // Verify
        verify(graphMock).registerDereferencedListener(eq(providerFreedListener));

        // Arrange
        reset(graphMock);
        // Act
        graph.unregisterDereferencedListener(providerFreedListener);
        // Verify
        verify(graphMock).unregisterDereferencedListener(eq(providerFreedListener));

        // Arrange
        reset(graphMock);
        // Act
        graph.clearDereferencedListeners();
        // Verify
        verify(graphMock).clearDereferencedListeners();
    }

    @Test (expected = IllegalStateException.class)
    public void should_throw_out_exceptions_when_registering_component()
            throws ProvideException, ProviderConflictException, Graph.IllegalRootComponentException {
        // Arrange
        MvcComponent badComponent = mock(MvcComponent.class);
        MvcGraph mvcGraph = new MvcGraph();
        mvcGraph.setRootComponent(badComponent);

        Object obj = new Object();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IllegalStateException();
            }
        }).when(badComponent).register(any(Object.class));

        // Act
        mvcGraph.getRootComponent().register(obj);
    }

    //TODO: should restore
//    @Test
//    public void should_be_able_save_and_restore_state_correctly()
//            throws ProvideException, ProviderConflictException {
//        Bean beanMock = mock(Bean.class);
//
//        List<Bean> beans = new ArrayList();
//        beans.add(beanMock);
//        graph.rootComponent.beans = beans;
//
//        final BeanKeeper beanKeeperMock = mock(BeanKeeper.class);
//
//        // Act
//        graph.rootComponent.saveState(beanKeeperMock);
//
//        // Verify
//        verify(beanKeeperMock).saveState(beanMock);
//
//        // Arrange
//        reset(beanKeeperMock);
//
//        Object stateMock = mock(Object.class);
//        when(beanKeeperMock.restoreState(any(Class.class))).thenReturn(stateMock);
//
//        graph.rootComponent.restoreState(beanKeeperMock);
//
//        // Verify
//        verify(beanMock).restoreModel(eq(stateMock));
//    }

    interface UnimplementedInterface{}

    @Test(expected = MvcGraph.Exception.class)
    public void should_raise_mvc_graph_exception_when_inject_on_poke_exception() {
        class View {
            @Inject
            UnimplementedInterface unimplementedInterface;
        }
        graph.inject(new View());
    }

    @Test(expected = MvcGraph.Exception.class)
    public void should_raise_mvc_graph_exception_when_release_on_poke_exception() {
        class View {
            @Inject
            UnimplementedInterface unimplementedInterface;
        }
        View view = new View();
        view.unimplementedInterface = new UnimplementedInterface() {
        };
        graph.release(view);
    }

    @Test(expected = MvcGraph.Exception.class)
    public void should_raise_mvc_graph_exception_when_use_on_poke_exception() {
        class View {
            @Inject
            UnimplementedInterface unimplementedInterface;
        }
        graph.use(UnimplementedInterface.class, new Consumer<UnimplementedInterface>() {
            @Override
            public void consume(UnimplementedInterface instance) {
            }
        });
    }
}
