package ru.stqa.selenium.factory;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

public class WebDriverFactory {

  private static WebDriverFactoryInternal factoryInternal = new SingletonStorage();

  public static void setMode(WebDriverFactoryMode newMode) {
    if (! factoryInternal.isEmpty()) {
      throw new Error("Mode can't be changed because there are active WebDriver instances");
    }
    factoryInternal = createFactoryInternal(newMode);
  }

  private static WebDriverFactoryInternal createFactoryInternal(WebDriverFactoryMode mode) {
    switch (mode) {
      case SINGLETON:
        return new SingletonStorage();
      case THREADLOCAL_SINGLETON:
        return new ThreadLocalSingletonStorage();
      case UNRESTRICTED:
        return new UnrestrictedStorage();
      default:
        throw new Error("Unsupported browser factory mode: " + mode);
    }
  }

  public static void setDefaultHub(String defaultHub) {
    factoryInternal.setDefaultHub(defaultHub);
  }

  // Factory

  public static WebDriver getDriver(String hub, Capabilities capabilities) {
    return factoryInternal.getDriver(hub, capabilities);
  }

  public static WebDriver getDriver(Capabilities capabilities) {
    return factoryInternal.getDriver(capabilities);
  }

  public static void dismissDriver(WebDriver driver) {
    factoryInternal.dismissDriver(driver);
  }

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        factoryInternal.dismissAll();
      }
    });
  }

}
