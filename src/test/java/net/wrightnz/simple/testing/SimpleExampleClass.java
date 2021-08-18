package net.wrightnz.simple.testing;

/**
 * @author Richard Wright
 */
public class SimpleExampleClass {

    private String str1;

    public SimpleExampleClass(String str1){
        this.str1 = str1;
    }

    public SimpleExampleClass(){
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public ReturnedClass getReturned() {
        return new ReturnedClass();
    }

    public static class ReturnedClass {

        public String getAnswer(){
            return "answer";
        }
    }
}
