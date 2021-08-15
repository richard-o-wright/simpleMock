package net.wrightnz.simple.testing;

import java.util.Objects;

/**
 * @author Richard Wright
 */
public class ExampleWithNoNullConstructorClass {

    private String str1;
    private String str2;

    public ExampleWithNoNullConstructorClass(String str1, String str2){
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExampleWithNoNullConstructorClass{");
        sb.append("str1='").append(str1).append('\'');
        sb.append(", str2='").append(str2).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ExampleWithNoNullConstructorClass that = (ExampleWithNoNullConstructorClass) o;

        if (!Objects.equals(str1, that.str1)) return false;
        return Objects.equals(str2, that.str2);
    }

    @Override
    public int hashCode() {
        int result = str1 != null ? str1.hashCode() : 0;
        result = 31 * result + (str2 != null ? str2.hashCode() : 0);
        return result;
    }
}
