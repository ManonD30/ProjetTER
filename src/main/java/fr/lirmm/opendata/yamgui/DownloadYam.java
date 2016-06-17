package fr.lirmm.opendata.yamgui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadYam extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// allow user to download result.rdf
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

            /*response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(System.getProperty("user.dir"));*/
            
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));
            
		InputStream is = new FileInputStream(
				prop.getProperty("workdir") + "/yam_files/Yam++.zip");
		OutputStream os = response.getOutputStream();
		response.setHeader("Content-Disposition",
				"attachment;filename=Yam++.zip");
		int count;
		byte buf[] = new byte[4096];
		while ((count = is.read(buf)) > -1)
			os.write(buf, 0, count);
		is.close();
		os.close();
	}
}
