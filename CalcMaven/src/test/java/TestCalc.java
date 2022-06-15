import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestCalc {

  @Test
  public void testAdd(){
    CalcResources calc = new CalcResources();
    assertEquals(10, calc.add(2,8));
    assertEquals(5, calc.add(-5,10));
  }

  @Test
  public void testSub() {
    CalcResources calc = new CalcResources();
    assertEquals(2, calc.subtract(12,10));
    assertEquals(7, calc.subtract(5, -2));
  }

  @Test
  public void testMult() {
    CalcResources calc = new CalcResources();
    assertEquals(15, calc.multiply(3,5));
    assertEquals(-20, calc.multiply(-4, 5));
  }

  @Test
  public void testDiv() throws Exception {
    CalcResources calc = new CalcResources();
    assertEquals(2, calc.divide(10, 5));
    assertEquals(0.5, calc.divide(1, 2));
    //may need more testing for returning doubles
  }
}
