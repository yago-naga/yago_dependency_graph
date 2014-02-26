package servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javatools.administrative.D;
import javatools.administrative.Parameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import demo.MakeSvg;
import demo.YagoWebInitializer;

import main.ParallelCaller;
import fromWikipedia.Extractor;

/**
 * A simple servlet for serving SVG visualization of YAGO2s extraction process
 * 
 * @author Joanna Biega
 *
 */
public class SvgBrowser extends HttpServlet {

	private static final long serialVersionUID = -8269581428141896338L;

	/** Called upon a GET request by server */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		YagoWebInitializer.init();
	
		//create a list of the desired extractors
		List<Extractor> extractors = MakeSvg.extractors(new File(YagoWebInitializer.initFile));
		
		//generate the SVG image
		String result = MakeSvg.makeSvgString(extractors, new File(Parameters.get("yagoFolder")));
		
		request.setAttribute("svgImage", result);
		getServletContext().getRequestDispatcher("/WEB-INF/template/index.jsp").forward(request, response);
	}
	
	/** Called for POST requests. Calls doGet */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
