package servlets;

import java.io.File;
import java.io.IOException;

import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javatools.administrative.Announce;
import javatools.administrative.Parameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import demo.YagoWebInitializer;

/**
 * Servlet for resetting themes
 * 
 * @author Joanna Biega
 *
 */
public class ResetTheme extends HttpServlet {

	private static final long serialVersionUID = 596720207346899179L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		YagoWebInitializer.init();
		
		String resetType = request.getParameter("resetType");
		String typeData = "data";
		String typeTheme = "theme";
		String typeAll = "all";
		
		String forbiddenFilesPattern = "^\\..*"; //hidden files
		
		//Resetting all the theme and data files
		if (resetType.equals(typeAll)) {
			Announce.doing("Resetting all demo input files");

			//copy all theme files
			List<String> notReset = copyAllYagoFiles(Parameters.get("pristineYagoFolder"), Parameters.get("yagoFolder"), forbiddenFilesPattern);
			//copy all data files
			notReset.addAll(copyAllYagoFiles("data", Parameters.get("dataFolder"), forbiddenFilesPattern));
			
			//generate notification
			String msg = null;
			if (notReset.isEmpty()){
				msg = "All files were reset successfully.";
				Announce.done();
			}
			else {
				msg = "The following files could not be reset:\n";
				for (String s : notReset) {
					msg += s + "\n";
				}
				Announce.done(msg);
			}
			request.setAttribute("response", msg);
		}
		//reset single theme or data file
		else {
			File source = null;
			File destination = null;
			if (resetType.equals(typeTheme)) {
				destination = new File(String.format("%s/%s.ttl", Parameters.get("yagoFolder"), request.getParameter("themeName")));
				source = new File(String.format("%s/%s.ttl", Parameters.get("pristineYagoFolder"), request.getParameter("themeName")));
			} else if (resetType.equals(typeData)) {
				destination = new File(String.format("%s/%s", Parameters.get("dataFolder"), request.getParameter("themeName")));
				source = new File(String.format("%s/%s", "data", request.getParameter("themeName")));
			}
			else {
				request.setAttribute("response", "Incorrect request.");
				httpResponse(request, response);
				return;
			}
			
			try {
				copyYagoFile(source, destination);
			} catch (Exception e) {
				request.setAttribute("response", e.getMessage());
				Announce.warning(request.getParameter("themeName"), ": ", e.getMessage());
				httpResponse(request, response);
				return;
			}
			
			request.setAttribute("response", "File was reset successfully.");
		}
		
		httpResponse(request, response);
	}
	
	protected List<String> copyAllYagoFiles(String sourceDirName, String destDirName, String forbiddenFilesPattern) {
		List<String> notReset = new ArrayList<String>();
		
		File sourceDir = new File(sourceDirName);
		for (File source : sourceDir.listFiles()) {
			//Omitting unwanted files
			if (source.getName().matches(forbiddenFilesPattern)) {
				continue;
			}
			File destination = new File(String.format("%s/%s", destDirName, source.getName()));
			
			try {
				copyYagoFile(source, destination, false);
			} catch (Exception e) {
				notReset.add(source.getName());
			}
		}
		
		return notReset;
	}
	
	protected void copyYagoFile(File source, File destination) throws Exception {
		copyYagoFile(source, destination, true);
	}
	
	protected void copyYagoFile(File source, File destination, Boolean checkDestinationExists) throws Exception {
		if (checkDestinationExists && !destination.exists()) {
			throw new Exception("File could not be found.");
		}
		if (!source.exists()) {
			throw new Exception("Source file could not be found.");
		}
		
		Announce.doing(String.format("Copying from %s to %s", source, destination));
		Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Announce.done();
	}
	
	protected void httpResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/WEB-INF/template/themeReset.jsp").forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
