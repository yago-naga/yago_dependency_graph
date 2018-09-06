package demo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import extractors.DataExtractor;
import extractors.Extractor;
import extractors.MultilingualExtractor;
import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.administrative.Parameters;
import javatools.datatypes.Pair;
import javatools.parsers.Char;
import main.ParallelCaller;
import utils.Theme;

/**
 * Makes an SVG picture of the entire YAGO2s extraction process
 *
 * @author Fabian M. Suchanek
 *
 */
public class MakeSvg {

  // ------------------ Set up --------------------------------

  /** Width of the window (in pixels)*/
  public static final int windowWidth = 2000;

  /** Size of the font (in pixels)*/
  public static final int fontSize = 10;

  /** Width of the theme boxes (in pixels)*/
  public static final int themeWidth = 70;

  /** Height of the theme boxes (in pixels)*/
  public static final int themeHeight = 30;

  /** Height of the source input boxes (in pixels)*/
  public static final int sourceHeight = fontSize * 3 / 2;

  /** Width of the extractor circles (in pixels)*/
  public static final int extractorWidth = 90;

  /** Height of the extractor circles (in pixels)*/
  public static final int extractorHeight = 30;

  /** Horizontal space between input boxes (in pixels)*/
  public static final int themeGap = 50;

  /** Color of the extractor*/
  protected static final String extractorColour = "#00a5d8";

  /** Color of input data sources*/
  protected static final String dataColour = "#ff9b8b";

  /** Color of themes*/
  protected static final String themeColour = "#cdc5ca";

  /** Color of final themes */
  protected static final String finalThemeColour = "#9dbf18";

  /** Color of lines*/
  protected static final String lineColour = "#56565a";

  //------------------ Boiler plate code --------------------------------

  /** Boilerplate SVG code*/
  protected static final String boilerplate = "<svg xmlns='http://www.w3.org/2000/svg'\n" + "    xmlns:xlink='http://www.w3.org/1999/xlink'\n"
      + "    width='" + windowWidth + "' height='@HEIGHT' preserveAspectRation='MidX' font-size='" + fontSize + "' font-family='Arial'>\n"
      + "  <defs>\n" + "    <marker id='arrow'\n" + "      viewBox='0 0 10 10' refX='0' refY='5'\n" + "      markerUnits='strokeWidth'\n"
      + "      markerWidth='4' markerHeight='3'\n" + "      orient='auto'>\n" + "       <path d='M 0 0 L 10 5 L 0 10 z' stroke='" + lineColour
      + "' fill='" + lineColour + "'/>\n" + "    </marker>\n" + "    <marker id='arrowOrange'\n" + "      viewBox='0 0 10 10' refX='0' refY='5'\n"
      + "      markerUnits='strokeWidth'\n" + "      markerWidth='4' markerHeight='3'\n" + "      orient='auto'>\n"
      + "       <path d='M 0 0 L 10 5 L 0 10 z' stroke='" + lineColour + "' fill='" + lineColour + "'/>\n" + "    </marker>\n"
      + "    <marker id='arrowOpaque'\n" + "      viewBox='0 0 10 10' refX='0' refY='5'\n" + "      markerUnits='strokeWidth'\n"
      + "      markerWidth='4' markerHeight='3'\n" + "      orient='auto'>\n" + "       <path d='M 0 0 L 10 5 L 0 10 z' stroke='" + lineColour
      + "' fill='" + lineColour + "' stroke-opacity='0.1' fill-opacity='0.1'/>\n" + "    </marker>\n" + "  </defs>\n" + "  <title> YAGO2s</title>\n";

  //------------------ Creating SVG elements --------------------------------

  /** Appends an SVG attribute to a string builder*/
  public static void attribute(StringBuilder b, String attribute, String value) {
    b.append(" ").append(attribute).append("='").append(Char.encodeAmpersand(value)).append('\'');
  }

  /** Creates an SVG element */
  public static void element(StringBuilder b, String type, Object... attributeValue) {
    b.append("  <").append(type);
    for (int i = 0; i < attributeValue.length; i += 2) {
      if (attributeValue[i] instanceof String[]) {
        for (int j = 0; j < ((String[]) attributeValue[i]).length; j += 2) {
          attribute(b, ((String[]) attributeValue[i])[j].toString(), ((String[]) attributeValue[i])[j + 1].toString());
        }
        i--;
      } else {
        attribute(b, attributeValue[i].toString(), attributeValue[i + 1].toString());
      }
    }
    b.append(" />\n");
  }

  /** Adds a line*/
  public static void line(StringBuilder b, String color, int x1, int y1, int x2, int y2, int strokeWidth, double opacity, String... otherParameters) {
    element(b, "line", "stroke", color, "x1", x1, "y1", y1, "x2", x2, "y2", y2, "stroke-opacity", opacity, otherParameters);
  }

  /** Adds an ellipse*/
  public static void ellipse(StringBuilder b, String fillColor, String lineColor, int strokeWidth, int cx, int cy, int rx, int ry,
      String... otherParameters) {
    element(b, "ellipse", "fill", fillColor, "stroke", lineColor, "stroke-width", 2, "cx", cx, "cy", cy, "rx", rx, "ry", ry, otherParameters);
  }

  /** Adds a text element */
  public static void text(StringBuilder b, String color, int x, int y, String text, String... otherParameters) {
    element(b, "text", "fill", color, "x", x, "y", y, otherParameters);
    b.setLength(b.length() - 3);
    b.append(">").append(Char.encodeAmpersand(text)).append("</text>\n");
  }

  /** Adds a rectangle*/
  public static void rect(StringBuilder b, String fillColor, String strokeColor, int strokeWidth, int x, int y, int width, int height,
      String... otherParameters) {
    element(b, "rect", "fill", fillColor, "stroke", strokeColor, "stroke-width", strokeWidth, "x", x, "y", y, "width", width, "height", height,
        otherParameters);
  }

  /** Makes a concatenated string from items*/
  public static String seq(Iterable<?> things) {
    StringBuilder b = new StringBuilder();
    for (Object thing : things) {
      b.append(thing).append(";");
    }
    if (b.length() > 2) b.setLength(b.length() - 1);
    return (b.toString());
  }

  public static String getElementId(String name, int x, int y) {
    return String.format("%s:%d.%d", name, x, y);
  }

  //------------------ Helper methods --------------------------------

  /** returns the next line of extractors, based on the extractors and themes we have*/
  public static List<Extractor> nextLine(List<Extractor> extractors, Set<Extractor> extractorsWeHave,
      Map<Theme, Pair<Integer, Integer>> themesWeHave) {
    List<Extractor> nextline = new ArrayList<>();
    for (Extractor extractor : extractors) {
      if (extractorsWeHave.contains(extractor)) continue;
      if (themesWeHave.keySet().containsAll(extractor.input())) {
        extractorsWeHave.add(extractor);
        nextline.add(extractor);
      }
    }
    return (nextline);
  }

  /** returns an SVG file for the extractors*/
  public static StringBuilder makeSvg(List<Extractor> extractors, File yagoFolder) {

    // ------- Startup ---------

    StringBuilder result = new StringBuilder(boilerplate);
    int boilerStart = result.length();
    Map<Theme, Pair<Integer, Integer>> themesWeHave = new HashMap<>();
    Set<Extractor> extractorsWeHave = new HashSet<>();
    int y = fontSize * 5;

    // ------- Encode transitive dependencies ---------
    Map<Theme, Set<Integer>> themeDependencies = new HashMap<>();
    Integer extractorCount = 0;

    // ------- Loop until all extractors are done ---------

    while (true) {

      // ------- Find next line of extractors ---------

      List<Extractor> nextline = nextLine(extractors, extractorsWeHave, themesWeHave);
      if (nextline.isEmpty()) break;
      int spacing = windowWidth / nextline.size();
      int x = spacing / 2;
      int numThemes = 0;
      for (Extractor extractor : nextline) {
        numThemes += extractor.output().size();
      }
      int themeSpacing = windowWidth / numThemes;
      int themeX = 0;
      int themeY = y + extractorHeight / 2 + themeGap;

      // ------- Plot next line of extractors ---------

      for (Extractor extractor : nextline) {

        Set<Integer> extractorDependencies = new HashSet<Integer>();
        extractorDependencies.add(extractorCount);

        // Plot input arrows
        for (Theme theme : extractor.input()) {
          StringBuilder line = new StringBuilder();
          // Arrows that go far have no head and are transparent
          if (y - themesWeHave.get(theme).second() > themeGap + themeHeight) {
            line(line, lineColour, themesWeHave.get(theme).first(), themesWeHave.get(theme).second(), x, y - extractorHeight / 2 - 2, 2, 0.1,
                "theme-name", theme.name, "extractor-name", extractor.name(), "extractor-id", getElementId(extractor.name(), x, y), "dependencies",
                seq(themeDependencies.get(theme)));
          } else {
            line(line, lineColour, themesWeHave.get(theme).first(), themesWeHave.get(theme).second(), x, y - extractorHeight / 2 - 2, 2, 1.0,
                "marker-end", "url(#arrow)", "theme-name", theme.name, "extractor-name", extractor.name(), "extractor-id",
                getElementId(extractor.name(), x, y), "dependencies", seq(themeDependencies.get(theme)));
          }
          result.insert(boilerStart, line);
          extractorDependencies.addAll(themeDependencies.get(theme));
        }

        // Plot extractor
        result.append("  <g>\n");
        String inputThemes = seq(extractor.input());

        // TODO: transition from YAGO2s to YAGO3
        File inputDataFile = null;
        if (extractor instanceof DataExtractor) {
          inputDataFile = ((DataExtractor) extractor).inputData;
          inputThemes += inputThemes.isEmpty() ? inputDataFile.getName() : ";" + inputDataFile.getName();
        }
        ellipse(result, extractorColour, lineColour, 2, x, y, extractorWidth / 2, extractorHeight / 2, "input-themes", inputThemes, "output-themes",
            seq(extractor.output()), "extractor-name", extractor.name(), "extractor-id", getElementId(extractor.name(), x, y), "dependencies",
            seq(extractorDependencies), "dependency-id", extractorCount.toString());

        // TODO: transition from YAGO2s to YAGO3
        String name = extractor.getClass().getSimpleName();
        if (extractor instanceof MultilingualExtractor) {
          String language = ((MultilingualExtractor) extractor).language;
          name += (language == null ? "" : "_" + language);
        }
        name += "                        ";
        /*String name = extractor.getClass().getSimpleName() + (extractor.language == null ? "" : "_" + extractor.language)
            + "                        ";*/
        text(result, "black", x - extractorWidth / 2 + fontSize, y - fontSize / 3, name.substring(0, extractorWidth / fontSize * 2 * 4 / 5),
            "input-themes", inputThemes, "output-themes", seq(extractor.output()), "extractor-name", extractor.name(), "extractor-id",
            getElementId(extractor.name(), x, y), "dependencies", seq(extractorDependencies));
        text(result, "black", x - extractorWidth / 2 + fontSize, y + fontSize * 2 / 3, name.substring(extractorWidth / fontSize * 2 * 4 / 5),
            "input-themes", inputThemes, "output-themes", seq(extractor.output()), "extractor-name", extractor.name(), "extractor-id",
            getElementId(extractor.name(), x, y), "dependencies", seq(extractorDependencies));
        result.append("  </g>\n");

        // TODO: transition from YAGO2s to YAGO3
        // Plot data input file
        if (inputDataFile != null) {
          result.append("  <g>\n");
          int inputX = x + extractorWidth / 2 + spacing / 10;
          int inputY = y - extractorHeight / 2 - themeGap / 2;
          name = inputDataFile.getName();
          if (inputDataFile.isDirectory()) {
            name = "Folder: " + name;
          }
          int inputBoxWidth = fontSize * name.length() * 60 / 100;
          rect(result, dataColour, "black", 1, inputX, inputY, inputBoxWidth, sourceHeight, "data-file",
              inputDataFile.isDirectory() ? "false" : "true", "theme-name", name);
          text(result, "black", inputX + fontSize / 4, inputY + fontSize, name, "theme-name", name, "data-file",
              inputDataFile.isDirectory() ? "false" : "true");
          result.append("  </g>\n");
          line(result, lineColour, inputX, inputY + sourceHeight, x + extractorWidth / 2, y - 2, 2, 1.0, "marker-end", "url(#arrowOrange)",
              "theme-name", name, "data-file", inputDataFile.isDirectory() ? "false" : "true");
        } //*/

        // Plot output boxes
        if (!extractor.output().isEmpty()) {
          for (Theme theme : extractor.output()) {
            result.append("  <g>\n");
            rect(result, theme.isFinal() ? finalThemeColour : themeColour, "black", 1, themeX, themeY, themeWidth, themeHeight, "theme-name",
                theme.name, "dependencies", seq(extractorDependencies));
            name = theme.name + "                                ";
            int charsPerLine = themeWidth / fontSize * 100 / 60;
            text(result, "black", themeX + fontSize / 4, themeY + fontSize, name.substring(0, charsPerLine), "theme-name", theme.name, "dependencies",
                seq(extractorDependencies));
            text(result, "black", themeX + fontSize / 4, themeY + fontSize * 2, name.substring(charsPerLine, charsPerLine * 2), "theme-name",
                theme.name, "dependencies", seq(extractorDependencies));
            text(result, "black", themeX + fontSize / 4, themeY + fontSize * 3, name.substring(charsPerLine * 2), "theme-name", theme.name,
                "dependencies", seq(extractorDependencies));
            result.append("  </g>");
            line(result, lineColour, x, y + extractorHeight / 2, themeX + themeWidth / 2, themeY - 2, 2, 1.0, "marker-end", "url(#arrow)",
                "output-themes", theme.name, "dependencies", seq(extractorDependencies));
            themesWeHave.put(theme, new Pair<>(themeX + themeWidth / 2, themeY + themeHeight));
            themeX += themeSpacing;

            themeDependencies.put(theme, extractorDependencies);
          }
        }
        x += spacing;

        extractorCount += 1;
      }
      y += extractorHeight + themeGap + themeHeight + themeGap;
    }
    result.append("</svg>");

    // Check remainders
    for (Extractor e : extractors) {
      Set<Theme> weNeed = new HashSet<>(e.input());
      weNeed.removeAll(themesWeHave.keySet());
      if (!weNeed.isEmpty()) Announce.warning("Could not call", e.name(), "because of missing", weNeed);
    }
    result.replace(result.indexOf("@HEIGHT"), result.indexOf("@HEIGHT") + "@HEIGHT".length(), "" + y);
    return (result);
  }

  /** Makes an SVG file for the extractors*/
  public static void makeSvg(List<Extractor> extractors, File yagoFolder, File svgFile) throws IOException {
    Writer w = new FileWriter(svgFile);
    w.write(makeSvgString(extractors, yagoFolder));
    w.close();
  }

  /** Makes an SVG string for the extractors*/
  public static String makeSvgString(List<Extractor> extractors, File yagoFolder) throws IOException {
    return makeSvg(extractors, yagoFolder).toString();
  }

  /** creates and returns the list of all extractors in the ini file
   * @throws IOException */
  public static List<Extractor> extractors(File iniFile) throws IOException {
    D.p("Initializing from", iniFile);
    Parameters.init(iniFile);
    //File outputFolder = Parameters.getOrRequestAndAddFile("yagoFolder", "the folder where YAGO should be created");
    ParallelCaller.createWikipediaList(Parameters.getList("languages"), Parameters.getList("wikipedias"));
    List<Extractor> extractors = ParallelCaller.extractors(Parameters.getList("extractors"));
    List<Extractor> followUps = new ArrayList<>();
    List<Extractor> extractorsCurrent = new ArrayList<>(extractors);
    while (!extractorsCurrent.isEmpty()) {
      for (Extractor extractor : extractorsCurrent)
        followUps.addAll(extractor.followUp());

      extractors.addAll(followUps);
      extractorsCurrent.clear();
      extractorsCurrent.addAll(followUps);
      followUps.clear();
    }
    return (extractors);
  }

  /** Test*/
  public static void main(String[] args) throws Exception {
    String initFile = args.length == 0 ? "custom_yago.ini" : args[0];
    makeSvg(extractors(new File(initFile)), new File("/tmp/yago_tmp"), new File("/tmp/yago3.svg"));
  }
}
