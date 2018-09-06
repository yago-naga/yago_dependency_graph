package demo;

import java.io.File;
import java.io.IOException;

import javatools.administrative.D;
import javatools.administrative.Parameters;

/**
 * A class for extracting common parameter initialization for the web demo
 *
 * @author Joanna Biega
 *
 */
public class YagoWebInitializer {

  public static String initFile = "yago.ini";

  public static void init() throws IOException {
    D.p("Initializing from", initFile);
    Parameters.init(initFile);

    //If the default ini file suggests the usage of a custom init file
    //and the custom init file exists - reload the parameters
    String customInit = Parameters.get("customInitFile", null);
    if (customInit != null) {
      File customInitFile = new File(customInit);
      if (customInitFile.exists()) {
        Parameters.reset();
        D.p("Initializing from", customInit);
        Parameters.init(customInit);
        initFile = customInit;
      }
    }

    //Restore types for TransitiveTypeExtractor
    // TODO: commented following line for transition YAGO2s to YAGO3. Is this ok?
    //TransitiveTypeExtractor.freeMemory();

  }
}
