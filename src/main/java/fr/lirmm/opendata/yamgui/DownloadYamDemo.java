package fr.lirmm.opendata.yamgui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadYamDemo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// allow user to download result.rdf
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
            
                Properties prop = new Properties();
                prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties"));

		InputStream is = new FileInputStream(
				prop.getProperty("workdir") + "/yam_files/Yam++Demo.avi");
		OutputStream os = response.getOutputStream();
		response.setHeader("Content-Disposition",
				"attachment;filename=Yam++Demo.avi");
		int count;
		byte buf[] = new byte[4096];
		while ((count = is.read(buf)) > -1)
			os.write(buf, 0, count);
		is.close();
		os.close();
	}
}
