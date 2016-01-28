package com.ciphercloud.upgrade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.ciphercloud.upgrade.definitions.NewNodeInfo;
import com.ciphercloud.upgrade.definitions.ResourceKey;
import com.ciphercloud.upgrade.definitions.ResourceKeySpec;
import com.ciphercloud.upgrade.definitions.SystemChangeDef;


public class PreviewReport
{
	private static Logger logger = Logger.getLogger(PreviewReport.class.getName());
	public static String defaultReportValue = "N/A";
	
	/*
	 * "Usage : java PreviewReport <old-build-path> <new-build-path> <system-chaneg-def-file>
	 */
	public static void generatePreview(String oldBuildPath, String newBuildPath,List<SystemChangeDef> sysChangeDefs)
	{	
		OutputStream previewFileOutputStream = null;
		try
		{
			StringBuilder previewBuilder = new StringBuilder();
			System.out.println("PREVIEW OF CHANGES:");
			for(int i=0;i<180;i++)
				System.out.format("-");
			System.out.println();
			
			System.out.format("%8s |%5s |%30s |%50s |%15s |%25s |%30s |\n","Owner","org","FilePath","Property","Action","PreiousValue","NewValue");
			
			for(int i=0;i<180;i++)
				System.out.format("-");
			System.out.println();
			
			previewBuilder.append("<html><head><style>table {border-collapse: collapse;border:5px solid #946f00;width: 100%;height: 100%;table-layout: "
					+ "fixed;}table, th, td {border: 1px solid green;padding: 10px;}th {font-size: 14px;	background-color: rgba(0, 128, 0, 0.22);color:#946f00;	"
					+ "text-align: center;	font-variant: normal; font-style: italic;	font-weight: bold;	font-family: georgia,garamond,serif;}td "
					+ "{font-size: 12px;	text-align: left;	font-variant: normal;	font-family: georgia,garamond,serif;width:auto;overflow:auto;background:white;}"
					+ "</style></head><body><table><tr><th style=\"width:5%\">Owner</th><th style=\"width:5%\">Org</th><th style=\"width:10%\">FilePath</th>"
					+ "<th style=\"width:20%\">Property</th><th style=\"width:30%\">Previous Value</th><th style=\"width:30%\">New Value</th></tr>");

			
			for (SystemChangeDef sysChangeDef : sysChangeDefs) 
			{
				String org = sysChangeDef.getClassification();
				if (org == null) {
					org = defaultReportValue;
				}
				String file =  sysChangeDef.getResourceContainer();
				
				List<ResourceKey> resourceKeys = sysChangeDef.getResourceKey();
				for(ResourceKey resourceKey : resourceKeys)
				{	
					String owner = resourceKey.getOwner();
					String action = resourceKey.getAction();
					String newValue = defaultReportValue;
					String oldValue = defaultReportValue;
					String key = defaultReportValue;
					
					if(action.equals(Upgrade.addAction))
					{
						NewNodeInfo newNodeInfo = resourceKey.getNewNodeInfo();
						key = newNodeInfo.getNodeTag();
						newValue = newNodeInfo.getNodeValue();
					}
					else
					{	
						ResourceKeySpec resourceKeySpec = resourceKey.getResourceKeySpec();
						key = resourceKeySpec.getKey();
						oldValue = resourceKeySpec.getOldValue();
						if(action.equalsIgnoreCase(Upgrade.updateAction))
							newValue = resourceKeySpec.getNewValue();
					}
										
					previewBuilder.append("<tr><td>" + owner + "</td><td>" 
							+ org + "</td><td>"
							+ file + "</td><td>" 
							+ key + "</td><td>"
							+ StringEscapeUtils.escapeHtml4(String.valueOf(oldValue)) + "</td><td>"
							+ StringEscapeUtils.escapeHtml4(String.valueOf(newValue)) + "</td></tr>");
				
					System.out.format("%8s |%5s |%30s |%50s |%15s |%25s |%30s |\n",owner,org,file,key,action,oldValue,newValue);
				}
			}
			for(int i=0;i<180;i++)
				System.out.format("-");
			System.out.println();
			
			previewBuilder.append("</table></body></html>");

			File previewFile = new File(Upgrade.previewFilePath + File.separator + "preview.html");

			previewFileOutputStream = new FileOutputStream(previewFile);
			IOUtils.write(previewBuilder.toString(), previewFileOutputStream);

			logger.info("Generated preview file: "+previewFile.getAbsolutePath());
			System.out.println("Generated preview file: "+previewFile.getAbsolutePath());
		}
		catch (Exception e) 
		{
			logger.error("Cannot generate preview report file:");
			e.printStackTrace();
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