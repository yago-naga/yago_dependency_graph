package servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javatools.administrative.Parameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import demo.YagoWebInitializer;

import basics.Fact;
import basics.FactSource;

/**
 * Servlet for displaying theme preview
 * 
 * @author Joanna Biega
 *
 */
public class PreviewTheme extends HttpServlet {

	private static final long serialVersionUID = 603290464548626936L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		YagoWebInitializer.init();
		
		String themeFilename = String.format("%s/%s.ttl", Parameters.get("yagoFolder"), request.getParameter("themeName"));
		File themeFile = new File(themeFilename);
		if (!themeFile.exists()) {
			request.setAttribute("response", "Theme file could not be found.");
			httpResponse(request, response);
			return;
		}
		
		FactSource previewedSource = FactSource.from(themeFile);
		ArrayList<Fact> factList = new ArrayList<Fact>(1000); 
		int counter = 0;
		for (Fact f: previewedSource) {
			if (counter > 1000) {
				break;
			}
			factList.add(counter, f);
			counter++;
		}
		request.setAttribute("themeName", request.getParameter("themeName"));
		request.setAttribute("facts", factList);
		
		httpResponse(request, response);
	}
	
	protected void httpResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/WEB-INF/template/themePreview.jsp").forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
