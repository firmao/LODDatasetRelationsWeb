package web.servlet.matching;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MatchingServlet
 */
@WebServlet("/MatchingServlet")
public class MatchingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MatchingServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		if (request.getParameter("query") != null) {
			Set<String> ret = new LinkedHashSet<String>();
			String dataset = request.getParameter("query").trim();
			boolean bDataset = ((request.getParameter("opt") != null) && ((request.getParameter("opt").contentEquals("dataset"))));
			boolean bProperties = ((request.getParameter("opt") != null) && ((request.getParameter("opt").contentEquals("properties"))));
			boolean bSparql = ((request.getParameter("opt") != null) && ((request.getParameter("opt").contentEquals("sparql"))));
			
			String str[] = dataset.split(",");
			if (str.length > 1) {
				Set<String> props = new LinkedHashSet<String>();
				if (DatabaseMain.isSparql(dataset)) {
					ret = DatabaseMain.searchDB(dataset);
				} else {
					for (String p : str) {
						props.add(p.trim());
					}
					ret = DatabaseMain.searchDB(props);
				}
			} else {
				if(bDataset) {
					dataset = dataset.replaceAll("http://", "");
					dataset = dataset.replaceAll("https://", "");
					dataset = dataset.replaceAll("/sparql", "_sparql");
					ret = DatabaseMain.searchDB(dataset);
				} else if(bProperties) {
					Set<String> props = new LinkedHashSet<String>();
					props.add(dataset);
					ret = DatabaseMain.searchDB(props);
				}
			}
			StringBuffer bRet = new StringBuffer();
			if (ret != null && ret.size() > 0) {
				bRet.append("<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n"
						+ "table {\n" + "  font-family: arial, sans-serif;\n" + "  border-collapse: collapse;\n"
						+ "  width: 100%;\n" + "}\n" + "\n" + "td, th {\n" + "  border: 1px solid #dddddd;\n"
						+ "  text-align: left;\n" + "  padding: 8px;\n" + "}\n" + "\n" + "tr:nth-child(even) {\n"
						+ "  background-color: #dddddd;\n" + "}\n" + "</style>\n" + "</head>\n" + "<body>"
						+ "<script src=\"https://www.kryogenix.org/code/browser/sorttable/sorttable.js\"></script>");
				bRet.append("<h1>Input: </h1><textArea cols=\"100\" rows=\"3\">" + dataset + "</textArea>");
				bRet.append("<h1>Output:</h1>");
				int count = 0;
				for (String element : ret) {
					String r = element.toLowerCase();
					r = r.replaceAll("_sparql", "/sparql");
					r = r.replaceAll("out_tests2/", "");
					if (r.contains("{")) {
						bRet.append("<h3>Exact Match</h3>"
								+ "<br>Json file containing: Property->DatasetsMatch");
						String s1 = r.replaceAll(",", "\",\"");
//						s1=s1.replaceAll("[", "[\"");
//						s1=s1.replaceAll("]", "\"]");
//						s1=s1.replaceAll("=", "\"=");
//						s1=s1.replaceAll("{", "{\"");
						bRet.append("<textArea rows='8' cols='80'>" + s1 + "</textArea>");
						//String s1 = r.replaceAll("=", "<b>=</b>");
//						String sR[] = r.split("=");
//						for (String s : sR) {
//							if (!s.contains("[")) {
//								bRet.append(s);
//							} else {
//								bRet.append("<b>" + s + "<b><br>xxxxxxxxxxxxx<br>");
//							}
////							count++;
//						}
						//response.getOutputStream().println(s1);
						// response.getOutputStream().println(r.replaceAll(sR[0], "<b>"+sR[0]+"</b>="));
						bRet.append("<h3>Similar Match</h3>");
						bRet.append("<table class=\"sortable\">\n" + "  <tr>\n" + "    <th>Property_Source</th>\n"
										+ "    <th>Property_Target</th>\n" + "    <th>Dataset_Source</th>\n"
										+ "    <th>Dataset_Target</th>\n" + "  </tr>");
					} else {
						String sR = r.replaceAll("ANDRE_", "");
						String s[] = sR.split("\t");
						if (s.length > 3) {
							String pS = "<a href='"+ s[0] +"'>" + s[0] + "</a>";
							String pT = "<a href='"+ s[1] +"'>" + s[1] + "</a>";
							String dS = getLink(s[2]);
							String dT = getLink(s[3]);
							bRet.append("<tr>\n" + "    <td>" + pS + "</td>\n" + "    <td>" + pT + "</td>\n"
											+ "    <td>" + dS + "</td>\n" + "    <td>" + dT + "</td>\n"
											+ "  </tr>");
						} else {
							if (count == 0) {
								//bRet.append("<table><tr><th>" + s[0] + "</th><th>"	+ s[1] + "</th><th>" + s[2] + "</th></tr>");
								bRet.append("<table class=\"sortable\"><tr><th>Dataset</th><th>#Exact Match</th><th>#Similarity > 0.9</th></tr>");
							} else {
								String ds = getLink(s[0]);
								bRet.append("<tr>\n" + "    <td>" + ds + "</td>\n" + "    <td>"
										+ s[1] + "</td>\n" + "    <td>" + s[2] + "</td>\n" + "  </tr>");
							}
							count++;
						}
					}
				}
				bRet.append("</table></body></html>");
				// ret.forEach(response.getWriter()::println);
			}
			response.getOutputStream().println(bRet.toString());
			// response.getWriter().append("Served at:
			// ").append(request.getParameter("query"));
		}
	}

	private String getLink(String ds) {
		if(ds.contains("sparql")) {
			return "<a href='http://"+ ds +"'>http://" + ds + "</a>";
		} else {
			return "<a href='http://139.18.13.76:8082/dirHDTLaundromat/decompressed/"+ ds.substring(0, 2) + "/" + ds.replaceAll(".hdt", "/") +"'>" + ds + "</a>";
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
