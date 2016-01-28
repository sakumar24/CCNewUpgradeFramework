package com.ciphercloud.upgrade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.ciphercloud.upgrade.definitions.ResourceKey;
import com.ciphercloud.upgrade.definitions.ResourceKeySpec;
import com.ciphercloud.upgrade.definitions.SystemChangeDef;

public class PropertiesFileHandler 
{
	private static Logger logger = Logger.getLogger(PropertiesFileHandler.class.getName());
	private static String updateAction = "UpdateValue";
	private static String addAction = "Add";
	private static String remoceAction = "Remove";

	public static void handlePropertiesFile(String existingInstallationPath,String newBuildPath,SystemChangeDef sysChnagedef)
	{
		String resourceContainerPath = sysChnagedef.getResourceContainer();
		logger.info("Processing system definitions for the file:" + resourceContainerPath);
		System.out.println("Processing system chnage definitions for the file:" + resourceContainerPath);

		try
		{

			String resourceContainer = sysChnagedef.getResourceContainer();
			String existingInstallationFilePath = existingInstallationPath+File.separator+resourceContainer;

			File existingInstallationFile = new File(existingInstallationFilePath);

			PropertiesConfiguration destinationProperties = new PropertiesConfiguration();

			if(!existingInstallationFile.exists())
			{
				logger.error(existingInstallationFile.getAbsolutePath()+" File doesn't exixts.");
				return;
			}
			destinationProperties.load(existingInstallationFile);

			List<ResourceKey> resourceKeys = sysChnagedef.getResourceKey();
			for(ResourceKey resourceKey : resourceKeys)
			{	
				String action = resourceKey.getAction();
				// Based on the action value take the corresponding action.

				if(action.equalsIgnoreCase(updateAction))
				{
					updateValue(resourceKey,destinationProperties);
				}
				else if(action.equalsIgnoreCase(remoceAction))
				{
					removeProperty(resourceKey,destinationProperties);
				}
				else if(action.equalsIgnoreCase(addAction))
				{
					addNode(resourceKey,destinationProperties);
				}
			}

			updateDocument(destinationProperties,existingInstallationFile);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void addNode(ResourceKey resourceKey, PropertiesConfiguration destinationProperties) 
	{
		String action = resourceKey.getAction();
		ResourceKeySpec newNodeInfo = resourceKey.getResourceKeySpec();
		if(newNodeInfo == null)
		{
			logger.error("Error in sysChangeDef.xml. NewNodeInfo information is requied for action:"+action);
			// Goto next resource key
			return;
		}
		//String parentKey = newNodeInfo.getParentKey();
		String key = newNodeInfo.getKey();
		String value = newNodeInfo.getNewValue();
		String type = newNodeInfo.getType();

		if(type == null || key == null || value == null)
		{
			String missingField = null;
			if(type == null)
				missingField = "type";
			else if(key == null)
				missingField = "tagName";
			else if(value == null)
				missingField = "nodeName";

			logger.error("Error in sysChangeDef.xml." +missingField+" information is requied for action:"+action);
		}
		else if(type.equalsIgnoreCase("text"))
		{
			/*
			 * TODO: handle tags with all types of values.
			 */
			destinationProperties.addProperty(key, value);
			logger.info("Added new property. key:\""+key+"\" Parent Node value:\""+value+"\"");
		}				
	}

	private static void removeProperty(ResourceKey resourceKey, PropertiesConfiguration destinationProperties)
	{
		String action = resourceKey.getAction();
		ResourceKeySpec resourceKeySpec = resourceKey.getResourceKeySpec();
		if(resourceKeySpec == null)
		{
			logger.error("Error in sysChangeDef.xml. ResourceKeySpec information is requied for action:"+action);
			// Goto next resourceKey
			return;
		}
		String key = resourceKeySpec.getKey();
		String oldValue = resourceKeySpec.getOldValue();
		//String newValue = resourceKeySpec.getNewValue();

		if(key == null || oldValue == null)
		{
			String missingField = null;
			if(key == null)
				missingField = "key";
			else if(oldValue == null)
				missingField = "oldValue";

			logger.error("Error in sysChangeDef.xml." +missingField+" information is requied for action:"+action);
		}
		else
		{
			boolean done = false;

			String destinationPropertyValue = destinationProperties.getString(key);

			//confirming before changing the value
			if(destinationProperties != null &&  destinationPropertyValue.equals(oldValue))
			{			
				destinationProperties.clearProperty(key);
				done = true;
			}

			if(done)
				logger.info("Removed Property; key:\""+key+"\" value:\""+oldValue+"\"");
			else
				logger.info("Could not find the Property for remove action. key:\""+key+"\" value:\""+oldValue+"\"");
		}
	}

	private static void updateDocument(PropertiesConfiguration destinationProperties, File existingInstallationFile) throws FileNotFoundException, ConfigurationException
	{
		OutputStream destinationFileOutputStream = new FileOutputStream(existingInstallationFile);
		destinationProperties.save(destinationFileOutputStream);
	}

	private static void updateValue(ResourceKey resourceKey, PropertiesConfiguration destinationProperties)
	{
		String action = resourceKey.getAction();
		ResourceKeySpec resourceKeySpec = resourceKey.getResourceKeySpec();
		if(resourceKeySpec == null)
		{
			logger.error("Error in sysChangeDef.xml. ResourceKeySpec information is requied for action:"+action);
			// Goto next resourceKey
			return;
		}
		String key = resourceKeySpec.getKey();
		String oldValue = resourceKeySpec.getOldValue();
		String newValue = resourceKeySpec.getNewValue();

		if(key == null || oldValue == null || newValue == null)
		{
			String missingField = null;
			if(key == null)
				missingField = "key";
			else if(oldValue == null)
				missingField = "oldValue";
			else if(newValue == null)
				missingField = "newValue";

			logger.error("Error in sysChangeDef.xml." +missingField+" information is requied for action:"+action);
		}
		else
		{
			boolean done = false;

			String destinationPropertyValue = destinationProperties.getString(key);

			//confirming before changing the value
			if(destinationProperties != null &&  destinationPropertyValue.equals(oldValue))
			{				
				destinationProperties.setProperty(key, newValue);
				done = true;
			}
			if(done)
				logger.info("Updated value at key:"+key+"\tOld value:\""+destinationPropertyValue+"\" New value:\""+newValue+"\"");
			else
				logger.info("Could not find the node for update value. key:"+key+"\tOld value:\""+oldValue+"\" New value:\""+newValue+"\"");
		}	
	}
}