package com.ciphercloud.upgrade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ciphercloud.upgrade.definitions.PreviewReportStatistics;
import com.ciphercloud.upgrade.definitions.ResourceKey;
import com.ciphercloud.upgrade.definitions.ResourceKeySpec;
import com.ciphercloud.upgrade.definitions.SystemChangeDef;


public class PreviewReport
{
	private static final List<PreviewReportStatistics> reportStatistics = new ArrayList<PreviewReportStatistics>();
	private static Logger logger = Logger.getLogger(PreviewReport.class.getName());
	private static String previewFilePath = "D:\\CET-Project\\zips\\upgrade";

	/*
	 * "Usage : java PreviewReport <old-build-path> <new-build-path> <system-chaneg-def-file>
	 */
	public static void generatePreview(String oldBuildPath, String newBuildPath,List<SystemChangeDef> sysChangeDefs)
	{
		captureChanges(oldBuildPath,newBuildPath,sysChangeDefs);		
		generatePreviewFile();
	}

	/* Read all the systemChangedefinitions in the systemChangedef.xml and 
	 *  for each call respective method to handle values in that file
	 */
	private static void captureChanges(String oldBuildPath, String newBuildPath,List<SystemChangeDef> sysChangeDefs)
	{
		for(SystemChangeDef systemChangeDef : sysChangeDefs)
		{
			String systemDeftype = systemChangeDef.getSystemDefType();

			if(systemDeftype.equalsIgnoreCase("xml"))
			{
				captureXmlChanges(oldBuildPath,newBuildPath,systemChangeDef);
			}
		}
	}

	/* Read xml files and get values from xpath and 
	 * store corresponding properties values in reportStatistics
	 */
	private static void captureXmlChanges(String oldBuildPath, String newBuildPath, SystemChangeDef systemChangeDef)
	{
		String resourceContainer = systemChangeDef.getResourceContainer();
		String oldBuildFilePath = oldBuildPath+File.separator+resourceContainer;
		String newBuildFilePath = newBuildPath+File.separator+resourceContainer;

		try
		{
			File oldBuildFile = new File(oldBuildFilePath);
			File newBuilFile  = new File(newBuildFilePath);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document oldBuildFileDoc = dBuilder.parse(oldBuildFile);
			Document newBuildFileDoc = dBuilder.parse(newBuilFile);

			oldBuildFileDoc.getDocumentElement().normalize();
			newBuildFileDoc.getDocumentElement().normalize();

			XPath xPath =  XPathFactory.newInstance().newXPath();

			for(ResourceKey resourceKey : systemChangeDef.getResourceKey())
			{
				PreviewReportStatistics reportStats = new PreviewReportStatistics();
				reportStats.setFilePath(resourceContainer);
				reportStats.setScope(resourceKey.getOwner());
				
				String action = resourceKey.getAction();
				if(action.equalsIgnoreCase("UpdateValue"))
				{
					ResourceKeySpec resourceKeySpec = resourceKey.getResourceKeySpec();
					String keyXpath = resourceKeySpec.getKey();
					
					Node oldBuildFileNode = (Node) xPath.compile(keyXpath).evaluate(oldBuildFileDoc, XPathConstants.NODE);
					Node newBuildFileNode = (Node) xPath.compile(keyXpath).evaluate(newBuildFileDoc, XPathConstants.NODE);

					String oldBuildNodeContent = oldBuildFileNode.getTextContent();
					String newBuildNodeContent = newBuildFileNode.getTextContent();
	
					reportStats.setPreviousValue(oldBuildNodeContent);
					reportStats.setNewValue(newBuildNodeContent);
					reportStats.setProperty(keyXpath);
				}
				else
				{
					/*
					 * TODO : add reportstatics according to new structre.
					 */
				}
				reportStatistics.add(reportStats);
			}

		}
		catch(Exception e)
		{
			logger.error("Exception while reading the file:");
			e.printStackTrace();
		}

	}

	/* Generate preview.html file from 'reportStatistics'
	 */
	private static void generatePreviewFile()
	{
		OutputStream previewFileOutputStream = null;
		try
		{
			StringBuilder previewBuilder = new StringBuilder();
			previewBuilder.append("<html><head><style>table {border-collapse: collapse;border:5px solid #946f00;width: 100%;height: 100%;table-layout: fixed;}table, th, td {border: 1px solid green;padding: 10px;}th {font-size: 14px;	background-color: rgba(0, 128, 0, 0.22);color:#946f00;	text-align: center;	font-variant: normal; font-style: italic;	font-weight: bold;	font-family: georgia,garamond,serif;}td {font-size: 12px;	text-align: left;	font-variant: normal;	font-family: georgia,garamond,serif;width:auto;overflow:auto;background:white;}</style></head><body><table><tr><th style=\"width:5%\">Scope</th><th style=\"width:5%\">Org</th><th style=\"width:10%\">FilePath</th><th style=\"width:20%\">Property</th><th style=\"width:30%\">Previous Value</th><th style=\"width:30%\">New Value</th></tr>");

			for (PreviewReportStatistics previewReportStatistics : reportStatistics) {
				String org = previewReportStatistics.getOrg();
				if (org == null) {
					org = "N/A";
				}
				previewBuilder.append("<tr><td>" + previewReportStatistics.getScope() + "</td><td>" + org + "</td><td>"
						+ previewReportStatistics.getFilePath() + "</td><td>" + previewReportStatistics.getProperty() + "</td><td>"
						+ StringEscapeUtils.escapeHtml4(String.valueOf(previewReportStatistics.getPreviousValue())) + "</td><td>"
						+ StringEscapeUtils.escapeHtml4(String.valueOf(previewReportStatistics.getNewValue())) + "</td></tr>");
			}
			previewBuilder.append("</table></body></html>");

			File previewFile = new File(previewFilePath + File.separator + "preview.html");


			previewFileOutputStream = new FileOutputStream(previewFile);
			IOUtils.write(previewBuilder.toString(), previewFileOutputStream);

			logger.info("Generated preview file: "+previewFile.getAbsolutePath());
		}
		catch (Exception e) 
		{
			logger.error("Cannot generate preview report file"+e.getStackTrace().toString());
			//throw new Exception("Cannot generate preview report file", e);
			//UpgradeException("Cannot generate preview report file", e);
		}
		finally
		{
			if (previewFileOutputStream != null) {
				try
				{
					previewFileOutputStream.close();
				} 
				catch (Exception ignore) { // NOSONAR
					// ignore
				}
			}
		}
	}

}