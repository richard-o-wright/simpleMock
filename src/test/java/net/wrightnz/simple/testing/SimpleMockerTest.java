package net.wrightnz.simple.testing;

import java.awt.Point;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Richard Wright
 */
class SimpleMockerTest {

    @Test
    void mock() {
        ExampleInterface example = SimpleMocker.mock(ExampleInterface.class);
        String result = example.doSomething(1, "foo");
        assertNull(result);
    }

    @Test
    void testMock() {
        String expected = "Expected";
        Map<String, Object> responses = new HashMap<>();
        responses.put("doSomething", expected);
        ExampleInterface example = SimpleMocker.mock(ExampleInterface.class, responses);
        String result = example.doSomething(1, "foo");
        assertEquals(expected, result);
    }

    @Test
    void testMockMethods() {
        String expected = "Fish";
        // Mock a method
        MockMethod<String> meth1 = new MockMethod<>("doSomething", expected);
        // Mock an Interface
        ExampleInterface example = SimpleMocker.mock(ExampleInterface.class, meth1);
        // Call the mocked method on the mocked interface.
        example.doSomething(1, "");
        String result = example.doSomething(1, "test");
        // Check the mock method was called n number of times.
        assertEquals(2, meth1.getInvocationCount());
        // Check the expected mock result was also returned.
        assertEquals(expected, result);
    }

    @Test
    void testMockClassMethods() {
        int expected = 0;
        // Mock an Class
        ExampleClass example = SimpleMocker.mock(ExampleClass.class);
        // Call the mocked method on the mocked interface.
        int result = example.zero(10);

        // Check the expected mock result was also returned.
        assertEquals(expected, result);
    }

    // @Test
    void testMockClassWithContructorMethods() {
        try {
            // Mock an Class
            ExampleWithConstructorClass example = SimpleMocker.mock(ExampleWithConstructorClass.class);
            fail("Exception should be thrown before reaching this point");
        } catch (FailedToMockException e) {
            System.out.println(e.getMessage());
            boolean result = e.getMessage().contains(
                    "it's currently only possible to mock null contructor classes with SimpleMock"
            );
            assertTrue(result);
        }
    }


    // @Test
    void testMockClassWithContructorsMethods() {
        Point expected = new Point(0, 0);
        // Mock an Class
        ExampleWithConstructorsClass example = SimpleMocker.mock(ExampleWithConstructorsClass.class);
        // Call the mocked method on the mocked interface.
        Point result = example.zeroPoint(new Point(10, 10));

        // Check the expected mock result was also returned.
        assertEquals(expected, result);
    }

}
