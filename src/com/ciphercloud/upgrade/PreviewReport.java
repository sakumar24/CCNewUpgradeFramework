package com.ciphercloud.upgrade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.ciphercloud.upgrade.definitions.ResourceKey;
import com.ciphercloud.upgrade.definitions.ResourceKeySpec;
import com.ciphercloud.upgrade.definitions.SystemChangeDef;
import com.ciphercloud.upgrade.definitions.SystemChangeDefs;


public class PreviewReport
{
	private static Logger logger = Logger.getLogger(PreviewReport.class.getName());
	public static String defaultReportValue = "N/A";

	/*
	 * "Usage : java PreviewReport <old-build-path> <new-build-path> <system-chaneg-def-file>
	 */
	public static void generatePreview(String oldBuildPath, String newBuildPath,List<SystemChangeDef> sysChangeDefs)
	{	
		try
		{
			StringBuilder previewBuilder = new StringBuilder();
			SystemChangeDefs previewSysChangeDefs = new SystemChangeDefs();
			
			previewSysChangeDefs.setSystemDef(sysChangeDefs);
			
			System.out.println("PREVIEW OF CHANGES:");
			for(int i=0;i<180;i++)
				System.out.format("-");
			System.out.println();

			System.out.format("%8s |%5s |%30s |%50s |%15s |%25s |%30s |\n","Owner","org","FilePath","Property","Action","PreiousValue","NewValue");

			for(int i=0;i<180;i++)
				System.out.format("-");
			System.out.println();

			for (SystemChangeDef sysChangeDef : sysChangeDefs) 
			{
				String org = sysChangeDef.getClassification();
				if (org == null) {
					org = defaultReportValue;
				}
				String file =  sysChangeDef.getResourceContainer();
				String fileType = sysChangeDef.getSystemDefType();
				String filePath = oldBuildPath+File.separator+file;

				List<ResourceKey> resourceKeys = sysChangeDef.getResourceKey();
				for(ResourceKey resourceKey : resourceKeys)
				{	
					String owner = resourceKey.getOwner();
					String action = resourceKey.getAction();
					String newValue = defaultReportValue;
					String oldValue = defaultReportValue;
					String key = defaultReportValue;

					ResourceKeySpec resourceKeySpec = resourceKey.getResourceKeySpec();
					key = resourceKeySpec.getKey();

					if(action.equalsIgnoreCase(Upgrade.remoceAction) || action.equalsIgnoreCase(Upgrade.updateAction))	
					{	
						if(fileType.equalsIgnoreCase(Upgrade.XML))
							oldValue = XmlHandler.getPropertyValue(key, filePath);
						else if(fileType.equalsIgnoreCase(Upgrade.PROPERTIES) || fileType.equalsIgnoreCase(Upgrade.CFG))
							oldValue = PropertiesFileHandler.getPropertyValue(key, filePath);
					}		

					if(action.equalsIgnoreCase(Upgrade.updateAction) || action.equals(Upgrade.addAction))
						newValue = resourceKeySpec.getNewValue();


					System.out.format("%8s |%5s |%30s |%50s |%15s |%25s |%30s |\n",owner,org,file,key,action,oldValue,newValue);
				}
			}
			for(int i=0;i<180;i++)
				System.out.format("-");
			System.out.println();

			previewBuilder.append("</table></body></html>");

			File previewFile = new File(Upgrade.previewFilePath);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(SystemChangeDefs.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(previewSysChangeDefs, previewFile);
			
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
	}
}