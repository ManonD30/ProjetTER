<%@page import="java.util.Comparator"%>
<%@ page
  import="java.io.File,java.io.FilenameFilter,java.util.Arrays"%>
  <%
    /**
     * jQuery File Tree JSP Connector Version 1.0 Copyright 2008 Joshua Gould 21
     * April 2008
     */
    String dir = request.getParameter("dir");
    if (dir == null) {
      return;
    }

    if (dir.charAt(dir.length() - 1) == '\\') {
      dir = dir.substring(0, dir.length() - 1) + "/";
    } else if (dir.charAt(dir.length() - 1) != '/') {
      dir += "/";
    }

    dir = java.net.URLDecoder.decode(dir, "UTF-8");

    if (new File(dir).exists()) {
      out.print("<ul class=\"jqueryFileTree\" style=\"display: none;\">");

      File[] filesArray = new File(dir).listFiles();
      Arrays.sort(filesArray, new Comparator<File>() {
        public int compare(File f1, File f2) {
          return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
        }
      });

      // All dirs
      for (File file : filesArray) {
        if (file.isDirectory()) {
          out.print("<li class=\"directory collapsed\"><a href=\"#\" rel=\"" + file.getAbsolutePath() + "/\">"
                  + file.getName() + "</a></li>");
        }
      }
      // All files
      for (File file : filesArray) {
        if (!file.isDirectory()) {
          int dotIndex = file.getName().lastIndexOf('.');
          String ext = dotIndex > 0 ? file.getName().substring(dotIndex + 1) : "";
          String downloadUrl = "download?ddl=" + file.getAbsolutePath() + "&filename=" + file.getName();
          out.print("<li class=\"file ext_" + ext + "\"><a href=\"" + downloadUrl + "\" rel=\"" + file.getAbsolutePath() + "\">"
                  + file.getName() + "</a></li>");
        }
      }
      out.print("</ul>");
    }
  %>