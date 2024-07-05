package com.recruitment.temperatures;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class ReplaceCamelCase extends DisplayNameGenerator.Standard {

  @Override
  public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
    return this.replaceCapitals(testMethod.getName());
  }

  private String replaceCapitals(String name) {
    name = name.replaceAll("([A-Z])", " $1");
    name = name.replaceAll("([0-9]+)", " $1");
    name = name.substring(0, 1).toUpperCase() + name.substring(1);
    return name;
  }
}
