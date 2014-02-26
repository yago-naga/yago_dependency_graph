package servlets;

import java.io.File;
import java.io.IOException;

import javatools.administrative.Parameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import demo.YagoWebInitializer;

/**
 * Servlet for editing a theme
 * 
 * @author Joanna Biega
 *
 */
public class EditTheme extends HttpServlet {

	private static final long serialVersionUID = 518317864953874524L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		YagoWebInitializer.init();
		
		//TODO: add more generic way of finding all possible data folders
		String themeName = request.getParameter("themeName") + ".ttl";
		String themeFolder = Parameters.get("yagoFolder");
		if (request.getParameter("themeName").contains(".")) {
			themeName = request.getParameter("themeName");
			themeFolder = Parameters.get("dataFolder");
		}
		
		String themeFilename = String.format("%s/%s", themeFolder, themeName);
		File themeFile = new File(themeFilename);
		if (!themeFile.exists()) {
			request.setAttribute("response", "Theme file could not be found.");
			httpResponse(request, response);
			return;
		}
		
		//This is not a terribly elegant solution, but the most elegant we can think of
		//bearing in mind the size of the edited files.
		String command = Parameters.get("themeEditCommand", "gedit ");
		try {
			Runtime.getRuntime().exec(String.format("%s %s", command, themeFilename));
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("response", "Please specify <strong>themeEditCommand</strong> " +
					"in your initialization file that is suitable for your platform.");
		}
		
		httpResponse(request, response);
	}
	
	protected void httpResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/WEB-INF/template/themeEdit.jsp").forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
