package servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.administrative.Parameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import basics.Theme;

import demo.MakeSvg;
import demo.YagoWebInitializer;

import main.ParallelCaller;
import fromWikipedia.Extractor;

/**
 * Servlet for running the YAGO2s extractors
 * 
 * @author Joanna Biega
 *
 */
public class RunExtractor extends HttpServlet {

	private static final long serialVersionUID = -2027440050352700342L;
	
	public static final Set<String> themesToBeFiltered = new HashSet<String>(Arrays.asList("yagoTransitiveType", "redirectFacts"));
	
	public static final Set<String> forbiddenPatternThemes = new HashSet<String>(Arrays.asList("hardWiredFacts", "wordnetWords"));
	

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Announce.setLevel(Announce.Level.DEBUG);
		YagoWebInitializer.init();
		
		File yagoFolder = null;
		try {
			yagoFolder = new File(Parameters.get("yagoFolder"));
		} catch (Exception e) {
			request.setAttribute("response", "Input file not found");
			httpResponse(request, response);
		}
		
		String extractorName = (String) (request.getParameter("extractorName") != null ? request.getParameter("extractorName") : "");
		
		//Run the whole system
		if (extractorName.isEmpty()) {
			
			//Running the whole system currently disabled
			
//			String[] args = {YagoWebInitializer.initFile};
//			try {
//				ParallelCaller.main(args);
//				waitForExtractionFinish();
//			} catch (Exception e) {
//				e.printStackTrace();
//				request.setAttribute("response", "Extractor call failed.");
//				httpResponse(request, response);
//			}
			
		} else {
		//Run single extractor
			
//			List<Extractor> extractors = ParallelCaller.extractors(Parameters.getList("extractors"));
			List<Extractor> extractors = MakeSvg.extractors(new File(YagoWebInitializer.initFile));
			
			D.p(extractors);
			
			Extractor extractor = null;
			for(Extractor e: extractors) {
				if (e.name().equals(extractorName)) {
					extractor = e;
				}
			}
			
			if (extractor != null) {
				
				//filter the defined "big" themes
				for(Theme t : extractor.input()) {
					if (themesToBeFiltered.contains(t.name)) {
						Set<String> patternThemeSet = new HashSet<String>();
						for(Theme pattern : extractor.input()) {
							patternThemeSet.add(pattern.name);
						}
						
						patternThemeSet.removeAll(themesToBeFiltered);
						patternThemeSet.removeAll(forbiddenPatternThemes);
						
						doThemeFiltering(t.name, patternThemeSet);
					}
				}
				
				try {
					extractor.extract(yagoFolder, "Demo extraction");
				} catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("response", "Extractor call failed.");
				}
				
				//bring back the original themes
				for(Theme t : extractor.input()) {
					if (themesToBeFiltered.contains(t.name)) {
						undoThemeFiltering(t.name);
					}
				}
				
			} else {
				D.p("Extractor " + extractorName + " not found.");
				request.setAttribute("response", "Extractor " + extractorName + " not found.");
				httpResponse(request, response);
				return;
			}
		}
		
		request.setAttribute("response", "Extraction was succesful. You can now explore the output themes.");
		httpResponse(request, response);
	}
	
	//A trick enabling running the extractors effectively
	protected void doThemeFiltering(String filteredTheme, Set<String> patternThemeSet) {
		
		D.p(String.format("Copying from %s/%s.ttl to %s/%s_orig.ttl", Parameters.get("yagoFolder"), filteredTheme, Parameters.get("yagoFolder"), filteredTheme));
		
		String dir = Parameters.get("yagoFolder");
		File source = new File(String.format("%s/%s.ttl", dir, filteredTheme));
		File destination = new File(String.format("%s/%s_orig.ttl", dir, filteredTheme));
		try {
			Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File filteredFile = new File(String.format("%s/%s.ttl", dir, filteredTheme));
		try {
			filteredFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		for (String patternTheme : patternThemeSet) {
			//awk - print object and subject from pattern files if they denote an entity
			//grep - filter the desired file so that is contains only entries related to entities output by the awk program
			//sort the ouput the remove duplicates
			//append the result to the replacement file
			D.p(String.format("Filtering %s_orig.ttl using %s.ttl as an entity pattern file", filteredTheme, patternTheme));
			
			String[] awk = {
					"/bin/sh",
					"-c",
					String.format("awk '{ if (match($1, /^</)) print substr($1, index($1,\"<\") + 1, index($1,\">\") - 2); if (match($1, /^</) && match($3, /^</)) print substr($3, index($3,\"<\") + 1, index($3,\">\") - 2); }' %s/%s.ttl " +
					"| sort -u > %s/%s_tmp.ttl",
					dir, patternTheme, dir, patternTheme)
			};
			
			try {
				Process proc = Runtime.getRuntime().exec(awk);
				
				InputStream stderr = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
	            BufferedReader br = new BufferedReader(isr);
	            String line = null;
	            while ( (line = br.readLine()) != null)
	                System.out.println(line);
				
				proc.waitFor();
				D.p(proc.exitValue());
			} catch (Exception e) {
				e.printStackTrace();
				//Output some warning
			}
			
			String[] sed = {
					"/bin/sh",
					"-c",
					String.format("cat %s/%s_tmp.ttl | sed 's/\\_/ /g' >> %s/%s_tmp.ttl",
					dir, patternTheme, dir, patternTheme)
			};
			
			try {
				Process proc = Runtime.getRuntime().exec(sed);
				
				InputStream stderr = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
	            BufferedReader br = new BufferedReader(isr);
	            String line = null;
	            while ( (line = br.readLine()) != null)
	                System.out.println(line);
				
				proc.waitFor();
				D.p(proc.exitValue());
			} catch (Exception e) {
				e.printStackTrace();
				//Output some warning
			}
			
			String[] fgrep = {
					"/bin/sh",
					"-c",
					String.format("fgrep -f %s/%s_tmp.ttl %s/%s_orig.ttl >> %s/%s.ttl; rm %s/%s_tmp.ttl", 
							dir, patternTheme, dir, filteredTheme, dir, filteredTheme, dir, patternTheme)
			};
			
			try {
				Process proc = Runtime.getRuntime().exec(fgrep);
				
				InputStream stderr = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
	            BufferedReader br = new BufferedReader(isr);
	            String line = null;
	            while ( (line = br.readLine()) != null)
	                System.out.println(line);
				
				proc.waitFor();
				D.p(proc.exitValue());
			} catch (Exception e) {
				e.printStackTrace();
				//Output some warning
			}
		}
	}
	
	protected void undoThemeFiltering(String filteredTheme) {
//		String command = String.format("cd %s && mv %s_orig.ttl %s.ttl", 
//				Parameters.get("yagoFolder"), filteredTheme, filteredTheme);
		
		D.p(String.format("Restoring from %s_orig.ttl to %s.ttl", filteredTheme, filteredTheme));
		
		String dir = Parameters.get("yagoFolder");
		File source = new File(String.format("%s/%s_orig.ttl", dir, filteredTheme));
		File destination = new File(String.format("%s/%s.ttl", dir, filteredTheme));
		
		try {
			Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void waitForExtractionFinish() throws InterruptedException {
		while (ParallelCaller.isExtractionRunning()) {
			Thread.sleep(1000);
		}
	}
	
	protected void httpResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/WEB-INF/template/extracted.jsp").forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
