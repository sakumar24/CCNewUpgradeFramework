package com.ciphercloud.upgrade;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ciphercloud.upgrade.definitions.SystemChangeDef;
import com.ciphercloud.upgrade.definitions.SystemChangeDefs;

public class Upgrade
{
	public static String updateAction = "UpdateValue";
	public static String addAction = "Add";
	public static String remoceAction = "Remove";
	
	public static String previewFilePath = "D:\\CET-Project\\zips\\upgrade";
	public static final String PROPERTIES = "properties";
	public static final String CFG = "CFG";
	public static final String XML = "XML";
	private static List<SystemChangeDef> sysChangeDefs = null;
	private static Logger logger = Logger.getLogger(Upgrade.class.getName());

	public static void main(String[] args)
	{
		/*
		 * Give any path for new-build-path as we are not using it for now.
		 */
		if(args.length < 4)
		{
			System.out.println("Usage : java Upgrade <existing-Installation-Path> <new-build-path> <system-chaneg-def-file-path> "
								+ "<y for preview; n for no preview>");
			logger.error("Required parameters are not available, exiting.");
			System.exit(0);
		}
		String newBuildPath = args[0];
		String existingInstallationPath = args[1];
		String sysChnageDefFilePath = args[2];
		String preview = args[3];

		logger.info("Input Parameteres:"+args[0]+" "+args[1]+" "+args[2]);

		readSystemChangeDef(sysChnageDefFilePath);

		if(preview.equalsIgnoreCase("y"))
		{
			System.out.println("Generating preview as upgrade intiated with preview value 'y'.");
			logger.info("Generating preview as upgrade intiated with preview value 'y'.");

			PreviewReport.generatePreview(existingInstallationPath,newBuildPath,sysChangeDefs);
			System.out.println("Enter Y/y to proceed with the upgrade, OR enter any other key to abort.");

			String proceed;

			Scanner scanIn = new Scanner(System.in);
			proceed = scanIn.nextLine();

			if(proceed.equalsIgnoreCase("y"))
			{
				applyChanges(existingInstallationPath,newBuildPath);
			}
			else
			{
				System.out.println("Upgrade Aborted.");
				logger.info("Aborting upgrade after preview.");
			}
			scanIn.close();            
		}
		else
		{
			System.out.println("Upgrade intiated with preview value 'N'.");
			logger.info("Upgrade intiated with preview value 'N'.");

			applyChanges(existingInstallationPath,newBuildPath);
		}
	}

	/* Read the differences using sysChangeDefinitions and 
	 * call corresponding function based on file type 
	 */
	private static void applyChanges(String existingInstallationPath, String newBuildPath) 
	{
		System.out.println("Applying upgrade changes...");
		for(SystemChangeDef systemChangeDef : sysChangeDefs)
		{
			String typeOfFile = systemChangeDef.getSystemDefType();

			if(typeOfFile.equalsIgnoreCase(XML))
			{
				XmlHandler.handleXmlFile(existingInstallationPath,newBuildPath,systemChangeDef);
			}
			else if (typeOfFile.equalsIgnoreCase(CFG) || typeOfFile.equalsIgnoreCase(PROPERTIES)) 
			{
				PropertiesFileHandler.handlePropertiesFile(existingInstallationPath,newBuildPath,systemChangeDef);
			}
		}
		System.out.println("\n** All changes applied successfully. System is now upgraded. **");
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