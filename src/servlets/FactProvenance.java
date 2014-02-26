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

import main.ParallelCaller;

import demo.MakeSvg;
import demo.Provenance;
import demo.YagoWebInitializer;
import fromWikipedia.Extractor;

/**
 * Servlet for displaying fact provenance
 * 
 * @author Joanna Biega
 *
 */
public class FactProvenance extends HttpServlet {
	
	private static final long serialVersionUID = 3117415250928030747L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		YagoWebInitializer.init();
		
		String factId = request.getParameter("factId");
		String provenance = Provenance.encodedProvenance(
				factId, 
				MakeSvg.extractors(new File(YagoWebInitializer.initFile)),
				new File(Parameters.get("yagoFolder")));
		
		request.setAttribute("provenance", provenance);
		
		//create a list of the desired extractors
		List<Extractor> extractors = MakeSvg.extractors(new File(YagoWebInitializer.initFile));
		//generate the SVG image
		String result = MakeSvg.makeSvgString(extractors, new File(Parameters.get("yagoFolder")));
		
		request.setAttribute("svgImage", result);
		
		httpResponse(request, response);
	}
	
	protected void httpResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/WEB-INF/template/factProvenance.jsp").forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
