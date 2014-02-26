package demo;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javatools.administrative.Announce;
import javatools.filehandlers.FileLines;
import basics.Theme;
import fromWikipedia.Extractor;

/**
 * Identifies the themes that are to be highlighted for a certain fact.
 * 
 * @author Fabian M. Suchanek
 *
 */
public class Provenance {

  /** returns the set of themes that contain the given fact id
   * @throws IOException */
  public static Set<Theme> provenance(String factId, List<Extractor> extractors, File yagoFolder) throws IOException {
    Set<Theme> result = new HashSet<>();
    // We use a hack here: instead of using FactSource, we run through the file line by line.
    // This is 10 times faster.
    factId="#@ "+factId;
    Announce.progressStart("Searching fact", extractors.size());
    for (Extractor e : extractors) {
      for (Theme t : e.output()) {
        File file = t.file(yagoFolder);
        if (!file.exists()) continue;
        FileLines lines = new FileLines(file);
        for (String line : lines) {
          if (line.equals(factId)) {
            //D.p(line,factId,t,file);
            result.add(t);
            break;
          }
        }
        lines.close();
      }
      Announce.progressStep();
    }
    Announce.progressDone();
    return (result);
  }
  
  public static String encodedProvenance(String factId, List<Extractor> extractors, File yagoFolder) throws IOException {
	  return MakeSvg.seq(provenance(factId, extractors, yagoFolder));
  }

  /** For testing purposes*/
  public static void main(String[] args) throws Exception {
    System.out.print(provenance("<id_1ck7aov_oyl_1irwnwm>", MakeSvg.extractors(new File("yago.ini")), new File("c:/fabian/data/yago2s")));
  }
}
