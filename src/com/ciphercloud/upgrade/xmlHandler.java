package com.ciphercloud.upgrade;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ciphercloud.upgrade.definitions.NewNodeInfo;
import com.ciphercloud.upgrade.definitions.ResourceKey;
import com.ciphercloud.upgrade.definitions.ResourceKeySpec;
import com.ciphercloud.upgrade.definitions.SystemChangeDef;

public class xmlHandler
{
	private static Logger logger = Logger.getLogger(xmlHandler.class.getName());
	private static String updateAction = "UpdateValue";
	private static String addAction = "AddNode";
	private static String remoceAction = "RemoveNode";

	public static void handleXmlFile(String oldBuildPath,String newBuildPath,SystemChangeDef sysChnagedef)
	{
		String resourceContainerPath = sysChnagedef.getResourceContainer();
		logger.info("Processing system definitions for the file:" + resourceContainerPath);
		try
		{	
			String resourceContainer = sysChnagedef.getResourceContainer();
			String oldBuildFilePath = oldBuildPath+File.separator+resourceContainer;
			//	String newBuildFilePath = newBuildPath+File.separator+resourceContainer;

			File oldBuildFile = new File(oldBuildFilePath);
			//	File newBuilFile  = new File(newBuildFilePath);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document oldBuildFileDoc = dBuilder.parse(oldBuildFile);
			//	Document newBuildFileDoc = dBuilder.parse(newBuilFile);

			oldBuildFileDoc.getDocumentElement().normalize();
			//	newBuildFileDoc.getDocumentElement().normalize();

			List<ResourceKey> resourceKeys = sysChnagedef.getResourceKey();
			for(ResourceKey resourceKey : resourceKeys)
			{	
				/*
				 * TODO : Handle: 1. Values in attributes
				 * 				  2. Delegation classes
				 * 				  3. History	 
				 * 				  4. Option for : what is unique identifier for the node for updateValue ?
				 * 					(assuming only node value right now)
				 */

				/*
				 * Absolute Xpath can't be used blindly as it may depend upon the order/location of the nodes in the xml file;
				 * in case of nodes with same tag names under same parent node.		
				 */

				String action = resourceKey.getAction();
				// Based on the action value take the corresponding action.

				if(action.equalsIgnoreCase(updateAction))
				{
					updateValue(resourceKey,oldBuildFileDoc);
				}
				else if(action.equalsIgnoreCase(remoceAction))
				{
					removeNode(resourceKey,oldBuildFileDoc);
				}
				else if(action.equalsIgnoreCase(addAction))
				{
					addNode(resourceKey,oldBuildFileDoc);
				}
			}

			updateDocument(oldBuildFileDoc,oldBuildFilePath);
		}
		catch (Exception e) 
		{
			logger.error("Cannot process system definitions for file:" + resourceContainerPath, e);
			//throw new UpgradeException("Cannot process system definitions for file" + resourceContainerPath, e);
		}
	}

	/*
	 *  Add new node to given parent(xpath), with given tagName and value
	 */
	private static void addNode(ResourceKey resourceKey, Document oldBuildFileDoc) throws XPathExpressionException 
	{

		/*
		 * TODO : Handling parent node that can't be identified uniquely, using xpath relying on the order of nodes. 
		 */
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String action = resourceKey.getAction();
		NewNodeInfo newNodeInfo = resourceKey.getNewNodeInfo();
		if(newNodeInfo == null)
		{
			logger.error("Error in sysChangeDef.xml. NewNodeInfo information is requied for action:"+action);
			// Goto next resource key
			return;
		}
		String parentKey = newNodeInfo.getParentKey();
		String tagName = newNodeInfo.getNodeTag();
		String nodeValue = newNodeInfo.getNodeValue();
		String type = newNodeInfo.getType();

		if(type == null || parentKey == null || tagName == null || nodeValue == null)
		{
			String missingField = null;
			if(type == null)
				missingField = "type";
			else if(parentKey == null)
				missingField = "parentKey";
			else if(tagName == null)
				missingField = "tagName";
			else if(nodeValue == null)
				missingField = "nodeName";

			logger.error("Error in sysChangeDef.xml." +missingField+" information is requied for action:"+action);
		}
		else if(type.equalsIgnoreCase("text"))
		{
			/*
			 * TODO: handle tags with all types of values.
			 */
			Node parentNode = (Node) xPath.compile(parentKey).evaluate(oldBuildFileDoc, XPathConstants.NODE);

			Node newNode = oldBuildFileDoc.createElement(tagName);
			newNode.setTextContent(nodeValue);

			parentNode.appendChild(newNode);

			logger.info("Added new node:\""+tagName+"\" Parent Node key:\""+parentKey+"\"");
		}								
	}

	/*
	 * Remove a node with given tag(xpath) and value(to resolve the duplicate nodes with same tag name).
	 */
	private static void removeNode(ResourceKey resourceKey, Document oldBuildFileDoc) throws XPathExpressionException 
	{
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String action = resourceKey.getAction();

		ResourceKeySpec resourceKeySpec = resourceKey.getResourceKeySpec();
		if(resourceKeySpec == null)
		{
			logger.error("Error in sysChangeDef.xml. ResourceKeySpec information is requied for action:"+action);
			// Goto next resourceKey
			return;
		}

		String keyXpath = resourceKeySpec.getKey();
		String oldValue = resourceKeySpec.getOldValue();
		//String newValue = resourceKeySpec.getNewValue();

		if(keyXpath == null || oldValue == null)
		{
			String missingField = null;
			if(keyXpath == null)
				missingField = "key";
			else if(oldValue == null)
				missingField = "oldValue";

			logger.error("Error in sysChangeDef.xml." +missingField+" information is requied for action:"+action);
		}
		else
		{
			NodeList oldBuildFileNodes = (NodeList) xPath.compile(keyXpath).evaluate(oldBuildFileDoc, XPathConstants.NODESET);

			if (oldBuildFileNodes != null) 
			{	
				for(int i=0;i<oldBuildFileNodes.getLength();i++)
				{
					Node curNode = oldBuildFileNodes.item(i);
					String curValue = curNode.getTextContent();

					if(curValue.equals(oldValue))
					{
						// This is the node to be Removed
						Node parentNode = curNode.getParentNode();
						parentNode.removeChild(curNode);
					}
				}						
			}
			logger.info("Removed Node; key:\""+keyXpath+"\" value:\""+oldValue+"\"");
		}
	}

	/*
	 * Replace/update/overwrite the value of a node in existing build with new value.
	 * Depending/using the old value of the node to identify the correct node.
	 */
	private static void updateValue(ResourceKey resourceKey, Document oldBuildFileDoc) throws XPathExpressionException
	{
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String action = resourceKey.getAction();
		ResourceKeySpec resourceKeySpec = resourceKey.getResourceKeySpec();
		if(resourceKeySpec == null)
		{
			logger.error("Error in sysChangeDef.xml. ResourceKeySpec information is requied for action:"+action);
			// Goto next resourceKey
			return;
		}
		String keyXpath = resourceKeySpec.getKey();
		String oldValue = resourceKeySpec.getOldValue();
		String newValue = resourceKeySpec.getNewValue();

		if(keyXpath == null || oldValue == null || newValue == null)
		{
			String missingField = null;
			if(keyXpath == null)
				missingField = "key";
			else if(oldValue == null)
				missingField = "oldValue";
			else if(newValue == null)
				missingField = "newValue";

			logger.error("Error in sysChangeDef.xml." +missingField+" information is requied for action:"+action);
		}
		else
		{
			NodeList oldBuildFileNodes = (NodeList) xPath.compile(keyXpath).evaluate(oldBuildFileDoc, XPathConstants.NODESET);

			if (oldBuildFileNodes != null) 
			{	
				for(int i=0;i<oldBuildFileNodes.getLength();i++)
				{
					Node curNode = oldBuildFileNodes.item(i);
					String curValue = curNode.getTextContent();

					if(curValue.equals(oldValue))
					{
						// This is the node to be changed
						curNode.setTextContent(newValue);
					}
				}						
			}
			logger.info("Updated value at key:"+keyXpath+"\n\t\tOld value:\""+oldValue+"\" New value:\""+newValue+"\"");
		}

	}

	/*
	 *  write/transfer the content into the xml file
	 */
	private static void updateDocument(Document destinationDocument, String destinationResourcePath)
	{
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(destinationDocument);
			StreamResult result = new StreamResult(new File(destinationResourcePath));

			transformer.transform(source, result);
		}
		catch (Exception e)
		{
			logger.error("Cannot update destination document" + destinationResourcePath, e);
			//throw new UpgradeException("Cannot update destination document" + destinationResourcePath, e);
		}

	}
}
