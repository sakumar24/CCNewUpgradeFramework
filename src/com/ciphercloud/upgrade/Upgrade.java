package com.ciphercloud.upgrade;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ciphercloud.upgrade.definitions.SystemChangeDef;
import com.ciphercloud.upgrade.definitions.SystemChangeDefs;

public class Upgrade
{
	private static List<SystemChangeDef> sysChangeDefs = null;
	private static Logger logger = Logger.getLogger(Upgrade.class.getName());

	public static void main(String[] args)
	{
		if(args.length < 3)
		{
			System.out.println("Usage : java Upgrade <old-build-path> <new-build-path> <system-chaneg-def-file>");
			logger.error("Required parameters are not available, exiting.");
			System.exit(0);
		}
		String newBuildPath = args[0];
		String oldBuildPath = args[1];
		String sysChnageDefFilePath = args[2];

		logger.info("Input Parameteres:"+args[0]+" "+args[1]+" "+args[2]);
		
		readSystemChangeDef(sysChnageDefFilePath);
	
		/*
		 * TODO : fix preview as per the latest systemChangeDef.xml
		 * PreviewReport.generatePreview(oldBuildPath,newBuildPath,sysChangeDefs);
		 */
		applyChanges(oldBuildPath,newBuildPath);

	}

	/*
	 * Read the differences using sysChangeDefinitions and 
	 * call corresponding function based on file type 
	 */
	private static void applyChanges(String oldBuildPath, String newBuildPath) 
	{
		for(SystemChangeDef systemChangeDef : sysChangeDefs)
		{
			String systemDeftype = systemChangeDef.getSystemDefType();

			if(systemDeftype.equalsIgnoreCase("xml"))
			{
				xmlHandler.handleXmlFile(oldBuildPath,newBuildPath,systemChangeDef);
			}
		}
	}

	/* Read/unmarshal the systemChangeDef.xml and store the definitions in sysChangeDefs
	 */
	private static void readSystemChangeDef(String sysChnageDefFilePath)
	{

		//logger.info("Processing system definitions from the file:" + sysChnageDefFilePath);
		try
		{
			File sysChnageDefFile = new File(sysChnageDefFilePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(SystemChangeDefs.class);  

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
			SystemChangeDefs sysChnageDefsObj = (SystemChangeDefs) jaxbUnmarshaller.unmarshal(sysChnageDefFile);  

			sysChangeDefs = sysChnageDefsObj.systemChangeDef;  
		}
		catch (Exception e)
		{
			logger.error("Cannot read systemChangeDef.xml file"+e);
			//UpgradeInitialException("Cannot read system-defs.xml file", e);
		}
	}
}
