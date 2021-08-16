package net.wrightnz.simple.testing;

import org.apache.bcel.generic.Type;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class Parameters {

  private String[] names;
  private Type[] types;

  public Parameters() {
  }

  public Parameters(Parameter[] params) {
    List<String> paramNames = new ArrayList<>();
    List<Type> paramTypes = new ArrayList<>();
    for (Parameter param : params) {
      paramNames.add(param.getName());
      paramTypes.add(Type.getType(param.getType()));
    }
    this.types = paramTypes.toArray(new Type[0]);
    this.names = paramNames.toArray(new String[0]);
  }

  public String[] getNames() {
    return names;
  }

  public void setNames(String[] names) {
    this.names = names;
  }

  public Type[] getTypes() {
    return types;
  }

  public void setTypes(Type[] types) {
    this.types = types;
  }

}
